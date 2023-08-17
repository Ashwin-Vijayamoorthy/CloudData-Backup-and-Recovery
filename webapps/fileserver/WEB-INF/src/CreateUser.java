import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

enum froles {
  fadmin,
  fuser
}

public class CreateUser extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean err=false;
        boolean sent=false;
        ObjectMapper mapper=new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ArrayList<String> error=new ArrayList<>();
        try {
          String dburl = "jdbc:postgresql://localhost:5432/fileserver";
          if((req.getParameter("username")!=null) &&(req.getParameter("password")!=null)){
              Class.forName("org.postgresql.Driver");
              Connection con = DriverManager.getConnection(dburl, "postgres", "1234");
              con.setAutoCommit(false);
              try (PreparedStatement st = con.prepareStatement("Insert into users(username,password) values(?,?)")) {
                st.setString(1, req.getParameter("username"));
                Argon2PasswordEncoder encoder=new Argon2PasswordEncoder(16,32,1,65536,10);
                st.setString(2,encoder.encode(req.getParameter("password")));
                st.executeUpdate();
                boolean fail=false;
                try(PreparedStatement st2 = con.prepareStatement("Insert into roles(username,rolename) values(?,?)")){
                  st2.setString(1, req.getParameter("username"));
                  st2.setObject(2, froles.fuser, Types.OTHER);
                  st2.executeUpdate();
                }catch(Exception e){
                  fail=true;
                  con.rollback();
                  err=true;
                  error.add(e.getMessage());
                  con.close();
                  System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" +e.getStackTrace());
                }
                if(!fail){
                  con.commit();
                  resp.sendRedirect(getServletContext().getContextPath()+"/home.jsp");
                  sent=true;
                }
              } catch (Exception e) {
                con.rollback();
                err=true;
                error.add(e.getMessage());
                con.close();
                System.out.println("Exception : " + e + "\n" + e.getMessage() + "\n" +e.getStackTrace());
            }finally{
              con.setAutoCommit(true);
              con.close();
            }
          }else{
            throw new Exception("Username or password is empty");
          }
        } catch (Exception e) {
          err=true;
          error.add(e.getMessage());
        }
        if(!sent){
          ErrorResponse eres=new ErrorResponse(err, error);
          String outjson=mapper.writeValueAsString(eres);
          resp.setContentType("application/json");
          resp.setCharacterEncoding("UTF-8");
          PrintWriter out=resp.getWriter();
          out.print(outjson);
          out.flush();
          sent=true;
        }
    }
}
