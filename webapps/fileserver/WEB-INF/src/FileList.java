import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileList extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean err = false;
        boolean sent = false;
        ArrayList<String> error = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayList<String> files = new ArrayList<String>();
        String outstr="";
        try {
            if (req.isUserInRole("fuser")) {
                String dburl = "jdbc:postgresql://localhost:5432/fileserver";
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection(dburl, "postgres", "1234");
                con.setAutoCommit(false);
                try (PreparedStatement st = con.prepareStatement("Select filename from files where username=?")) {
                    st.setFetchSize(20);
                    st.setString(1, req.getRemoteUser());
                    try (ResultSet rs = st.executeQuery()) {
                        while (rs.next()) {
                            files.add(rs.getString(1));
                        }
                    } catch (Exception e) {
                        err = true;
                        con.rollback();
                        error.add(e.getMessage());
                        System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
                    }
                } catch (Exception e) {
                    err = true;
                    con.rollback();
                    error.add(e.getMessage());
                    System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
                } finally {
                    con.setAutoCommit(true);
                    con.close();
                }
            }
            else {
                throw new Exception("Admin user not allowed!");
            }
            
        } catch (Exception e) {
            err = true;
            error.add(e.getMessage());
            System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" + e.getStackTrace());
        }
        ListFileResponse lfr=new ListFileResponse(files, err, error);
        outstr=mapper.writeValueAsString(lfr);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out=resp.getWriter();
        out.print(outstr);
        out.flush();
        sent=true;
    }
}

class ListFileResponse {
    ArrayList<String> files;
    boolean err;
    ArrayList<String> error;

    public ListFileResponse(@JsonProperty("files") ArrayList<String> files, @JsonProperty("err") boolean err,
            @JsonProperty("error") ArrayList<String> error) {
        this.files = files;
        this.err = err;
        this.error = error;
    }

    public ArrayList<String> getFiles() {
        return files;
    }

    public void setFiles(@JsonProperty("files") ArrayList<String> files) {
        this.files = files;
    }

    public void setErr(@JsonProperty("err") boolean err) {
        this.err = err;
    }

    public void setError(@JsonProperty("error") ArrayList<String> error) {
        this.error = error;
    }

    public boolean isErr() {
        return err;
    }

    public ArrayList<String> getError() {
        return error;
    }
}