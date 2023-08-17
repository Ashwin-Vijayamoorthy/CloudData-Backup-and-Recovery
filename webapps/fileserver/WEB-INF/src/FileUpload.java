import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.SecureRandom;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;


@MultipartConfig(
  fileSizeThreshold = 1024 * 32, // 32 KB
  maxFileSize = 1024 * 1024 * 10,      // 10 MB
  maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class FileUpload extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Reached Start");
        int PART_SIZE = 32 * 1024;
        int BUFFER_SIZE = 2 * 1024;
        String FILE_DIR="C:\\ashwin\\dev\\fserver";
        boolean alreadyExist=false;
        String fileName="";
        ArrayList<DBFilePart> farray=new ArrayList<DBFilePart>();
        ArrayList<String> decoys=new ArrayList<String>();
        int filecount=0;
        for (Part part : req.getParts()) {
            try{
                fileName = part.getSubmittedFileName();
                alreadyExist=checkFileExistsUser(req.getRemoteUser(),fileName);
                if(alreadyExist){
                    break;
                }
                System.out.println("Submitted file name  : "+fileName);
                InputStream input = part.getInputStream();
                
                SecretKey secretKey = null;
                IvParameterSpec iv = null;
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                secretKey = keyGen.generateKey();
                byte[] ivBytes = new byte[16];
                SecureRandom random = new SecureRandom();
                random.nextBytes(ivBytes);
                iv = new IvParameterSpec(ivBytes);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

                UUID opfname=UUID.randomUUID();
                Random rand2 = new Random();
                int randomNum = rand2.nextInt(3) + 1;

                String opfile=FILE_DIR+"\\f"+randomNum+"\\"+opfname.toString()+".cryptpart";
                File file=new File(opfile);
			    file.getParentFile().mkdirs();
			    file.createNewFile();
                FileOutputStream fout=new FileOutputStream(opfile);
			    FileChannel channel = fout.getChannel();
                ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);

                //out channel
                //opfname
                //randomNum
                //opfile
                //create dris and file
                //new secretKey
                //new iv
                //new cipher
                //add to farray

                byte[] buffer=new byte[BUFFER_SIZE];
                farray.add(new DBFilePart(opfname, "f"+randomNum, secretKey, iv));
                filecount++;
                boolean newStart=false;
                while(true){
                    int bytesread=0;
                    if((bytesread=input.read(buffer))!=-1){
                        if(newStart){
                            KeyGenerator keyGen2 = KeyGenerator.getInstance("AES");
                            keyGen.init(128);
                            secretKey = keyGen2.generateKey();
                            ivBytes = new byte[16];
                            SecureRandom random2 = new SecureRandom();
                            random2.nextBytes(ivBytes);
                            iv = new IvParameterSpec(ivBytes);
                            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

                            opfname=UUID.randomUUID();
                            rand2 = new Random();
                            randomNum = rand2.nextInt(3) + 1;

                            opfile=FILE_DIR+"\\f"+randomNum+"\\"+opfname.toString()+".cryptpart";
                            file=new File(opfile);
                            file.getParentFile().mkdirs();
                            file.createNewFile();

                            fout.close();
                            channel.close();
                            fout=new FileOutputStream(opfile);
                            channel = fout.getChannel();
                            buff.clear();
                            farray.add(new DBFilePart(opfname, "f"+randomNum, secretKey, iv));
                            filecount++;
                            newStart=false;
                        }
                        byte[] outputBytes = cipher.update(buffer,0,bytesread);
                        Arrays.fill(buffer, (byte)0);
                        channel.write(ByteBuffer.wrap(outputBytes));
                        if(channel.position()<PART_SIZE){
                            continue;
                        }else{
                            byte[] finalBytes = cipher.doFinal();
                            channel.write(ByteBuffer.wrap(finalBytes));
                            newStart=true;
                        }
                    }else{
                        byte[] finalBytes = cipher.doFinal();
                        channel.write(ByteBuffer.wrap(finalBytes));
                        channel.close();
                        fout.close();
                        break;
                    }
                }
            }catch(Exception e){
                System.out.println(e+", "+e.getMessage());
            }
        }
        //decoy file
        try{
            for(int j=0;j<((int)Math.ceil((double)filecount/3));j++){
                SecretKey secretKey = null;
                IvParameterSpec iv = null;
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(128);
                secretKey = keyGen.generateKey();
                byte[] ivBytes = new byte[16];
                SecureRandom random = new SecureRandom();
                random.nextBytes(ivBytes);
                iv = new IvParameterSpec(ivBytes);
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                
                String fname=UUID.randomUUID().toString();
                Random rand2 = new Random();
                int randomNum = rand2.nextInt(3) + 1;
                String outputFile=FILE_DIR+"\\f"+randomNum+"\\"+fname+".cryptpart";
                decoys.add("\\f"+randomNum+"\\"+fname+".cryptpart");
                File file=new File(outputFile);
                file.getParentFile().mkdirs();
                file.createNewFile();
                int bytesWritten = 0;
                FileOutputStream fout=new FileOutputStream(outputFile);
                FileChannel channel2 = fout.getChannel();
                ByteBuffer buff=ByteBuffer.allocate(1024*2);
                Random rand = new Random();
                while (bytesWritten < PART_SIZE) {
                    buff.clear();
                    for (int k= 0; k < BUFFER_SIZE; k++) {
                        buff.put((byte) rand.nextInt(256));
                    }
                    buff.flip();
                    byte[] inputBytes = new byte[buff.remaining()];
                    buff.get(inputBytes);
                    byte[] outputBytes = cipher.update(inputBytes);
                    channel2.write(ByteBuffer.wrap(outputBytes));
                    bytesWritten += BUFFER_SIZE;
                }
                byte[] finalBytes = cipher.doFinal();
                channel2.write(ByteBuffer.wrap(finalBytes));
                channel2.close();
                fout.close();
            }
        }catch(Exception e){
            System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
        }
        //db update
        if(!alreadyExist && !fileName.equals("")){
            for(DBFilePart part:farray){
                part.printThis();
            }
            UUID[] fpartnamearr=generateArrayFilePartUUIDs(farray);
            byte[][] seckeyarr=generateArrayKeys(farray);
            byte[][] ivarr=generateArrayIvParams(farray);
            String[] foldarr=generateArrayFolderNames(farray);
            try{
                String dburl="jdbc:postgresql://localhost:5432/fileserver";
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection(dburl, "postgres", "1234");
                con.setAutoCommit(false);
                Array Fpnarr=con.createArrayOf("uuid",fpartnamearr);
                Array Karr=con.createArrayOf("bytea",seckeyarr);
                Array Ivarr=con.createArrayOf("bytea",ivarr);
                Array Foldarr=con.createArrayOf("text", foldarr);
                try (PreparedStatement st = con.prepareStatement("insert into files(username,filename,foldername,parts,seckeys,ivs) values(?,?,?,?,?,?)")) {
                    st.setString(1,req.getRemoteUser());
                    st.setString(2,fileName);
                    st.setArray(3, Foldarr);
                    st.setArray(4, Fpnarr);
                    st.setArray(5, Karr);
                    st.setArray(6, Ivarr);
                    st.executeUpdate();
                    con.commit();
                } catch (Exception e) {
                    con.rollback();
                    System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
                }finally{
                    con.setAutoCommit(true);
                    con.close();
                }
            }catch(Exception e){
                System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
            }
        }
        try{
            String dburl="jdbc:postgresql://localhost:5432/fileserver";
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(dburl, "postgres", "1234");
            con.setAutoCommit(false);
            for(int i=0;i<decoys.size();i++){
                try (PreparedStatement st = con.prepareStatement("insert into decoys(filename) values(?)")) {
                    st.setString(1, decoys.get(i));
                    st.executeUpdate();
                } catch (Exception e) {
                    con.rollback();
                    System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
                }
            }
            con.commit();
            con.setAutoCommit(true);
            con.close();
        }catch(Exception e){
            System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
        }
        resp.sendRedirect(getServletContext().getContextPath()+"/home.jsp");
    }

    public UUID[] generateArrayFilePartUUIDs(ArrayList<DBFilePart> fileParts) {
        UUID[] uuids = new UUID[fileParts.size()];
        int i = 0;
        for (DBFilePart filePart : fileParts) {
            uuids[i++] = filePart.filePartName;
        }
        return uuids;
    }
    public byte[][] generateArrayKeys(ArrayList<DBFilePart> parts) {
        byte[][] keys=new byte[parts.size()][];
        int i = 0;
        for (DBFilePart part : parts) {
            keys[i++] = part.key.getEncoded();
        }
        return keys;
    }
    public byte[][] generateArrayIvParams(ArrayList<DBFilePart> parts) {
        byte[][] ivs=new byte[parts.size()][];
        int i = 0;
        for (DBFilePart part : parts) {
            ivs[i++] = part.iv.getIV();
        }
        return ivs;
    }
    public String[] generateArrayFolderNames(ArrayList<DBFilePart> parts){
        String[] folderNames=new String[parts.size()];
        int i=0;
        for(DBFilePart part:parts){
            folderNames[i++]=part.folderName;
        }
        return folderNames;
    }

    public static boolean checkFileExistsUser(String uname,String fname) throws Exception{
        boolean exist=false;
        String dburl="jdbc:postgresql://localhost:5432/fileserver";
        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(dburl, "postgres", "1234");
        try (PreparedStatement st = con.prepareStatement("SELECT Exists(select*from files where username=? and filename=?)")) {
            st.setString(1,uname);
            st.setString(2,fname);
            try (ResultSet rs = st.executeQuery()) {
                if(rs.next()){
                    exist=rs.getBoolean(1);
                    System.out.println("Exists : "+exist);
                }
                
            }catch(Exception e){
                con.close();
                System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
            }
        } catch (Exception e) {
            con.close();
            System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
        }
        return exist;
    }
}

class DBFilePart{
    UUID filePartName;
    String folderName;
    SecretKey key;
    IvParameterSpec iv;

    public DBFilePart(UUID fpname,String foldname,SecretKey k,IvParameterSpec i){
        this.filePartName=fpname;
        this.folderName=foldname;
        this.key=k;
        this.iv=i;
    }

    public void printThis(){
        System.out.println("UUID : "+this.filePartName.toString()+"\tfoldername : "+folderName+"\tSeckey : "+this.key.toString()+"\tIV : "+this.iv.toString());
    }
}

//part.write("C:\\upload\\" + fileName);
//     InputStream inputStream = req.getInputStream();
    // String fileName = req.getHeader("Content-Disposition").split(";")[2].split("=")[1].replace("\"", "");
    // Path filePath = Paths.get("uploads", fileName);
    //     System.out.println(fileName);
    //     System.out.println(filePath);
    //     System.out.println(filePath.getFileName());
    //     System.out.println(filePath.getFileName().toString());
    //     resp.sendRedirect(getServletContext().getContextPath()+"./home.jsp");