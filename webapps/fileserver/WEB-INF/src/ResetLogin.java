import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ResetLogin extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getSession().isNew()){
            resp.sendRedirect(getServletContext().getContextPath()+"/home.jsp");
        }else{
            req.getSession().invalidate();
            resp.sendRedirect(getServletContext().getContextPath()+"/home.jsp");
        }
    }
}
