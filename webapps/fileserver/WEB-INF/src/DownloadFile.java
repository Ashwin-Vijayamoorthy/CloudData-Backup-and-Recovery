import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DownloadFile extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int BUFFER_SIZE = 2 * 1024;
        String FILE_DIR="C:\\ashwin\\dev\\fserver";
        String filename="";
        boolean paraExist=false;
        if(req.getParameter("filename")!=null){
            paraExist=true;
            filename=req.getParameter("filename");
        }
        if(paraExist){
            try(OutputStream outputStream = resp.getOutputStream()){
                byte[] buffer=new byte[BUFFER_SIZE];
                boolean fileExist=FileUpload.checkFileExistsUser(req.getRemoteUser(), filename);
                if(fileExist){
                    resp.setContentType("application/octet-stream");
                    resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                    String dburl = "jdbc:postgresql://localhost:5432/fileserver";
                    Class.forName("org.postgresql.Driver");
                    Connection con = DriverManager.getConnection(dburl, "postgres","1234");
                    con.setAutoCommit(false);
                    try (PreparedStatement st = con.prepareStatement("Select foldername,parts,seckeys,ivs from files where username=? and filename=?")) {
                        st.setFetchSize(20);
                        st.setString(1, req.getRemoteUser());
                        st.setString(2, filename);
                        try (ResultSet rs = st.executeQuery()) {
                            if (rs.next()) {

                                String[] foldernames=(String[])rs.getArray("foldername").getArray();
                                UUID[] parts=(UUID[])rs.getArray("parts").getArray();
                                ArrayList<SecretKey> seckeys=new ArrayList<SecretKey>();
                                ArrayList<IvParameterSpec> ivs=new ArrayList<IvParameterSpec>();
                                Array arr=rs.getArray("seckeys");
                                ResultSet keyrs=arr.getResultSet();
                                while(keyrs.next()){
                                    byte[] keybyte=keyrs.getBytes(2);
                                    SecretKey key=new SecretKeySpec(keybyte,"AES");
                                    seckeys.add(key);
                                }
                                keyrs.close();
                                arr=rs.getArray("ivs");
                                ResultSet ivrs=arr.getResultSet();
                                while(ivrs.next()){
                                    byte[] ivbyte=ivrs.getBytes(2);
                                    IvParameterSpec iv=new IvParameterSpec(ivbyte);
                                    ivs.add(iv);
                                }
                                ivrs.close();

                                for(int i=0;i<foldernames.length;i++){
                                    String fname=parts[i].toString();
                                    SecretKey secretKey=seckeys.get((int)i);
                                    IvParameterSpec iv=ivs.get((int)i);
                                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                                    cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

                                    String fp=FILE_DIR+"\\"+foldernames[i]+"\\"+fname+".cryptpart";
                                    File f = new File(fp);
                                    ByteBuffer buff=ByteBuffer.allocate(BUFFER_SIZE);
                                    if(f.exists() && !f.isDirectory()) {
                                        FileInputStream fin= new FileInputStream(fp);
                                        FileChannel channel2 = fin.getChannel();
                                        long size =channel2.size();
                                        System.out.println("Size "+i+" : "+size);
                                        while(size>channel2.position()){
                                            buff.clear();
                                            channel2.read(buff);
                                            buff.flip();
                                            byte[] inputBytes = new byte[buff.remaining()];
                                            buff.get(inputBytes);
                                            byte[] outputBytes = cipher.update(inputBytes);
                                            outputStream.write(outputBytes);
                                        }
                                        fin.close();
                                        byte[] finalBytes = cipher.doFinal();
                                        outputStream.write(finalBytes);
                                    }else{
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            con.rollback();
                            System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
                        }
                    } catch (Exception e) {
                        con.rollback();
                        System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
                    } finally {
                        con.setAutoCommit(true);
                        con.close();
                    }
                }else{
                    System.out.println("File not found in db : "+filename);
                }
            }catch(Exception e){
                System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
            }
        }
        
    }
}