import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FolderView extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        File folder1 = new File("C:\\ashwin\\dev\\fserver\\f1\\");
        File folder2 = new File("C:\\ashwin\\dev\\fserver\\f2\\");
        File folder3 = new File("C:\\ashwin\\dev\\fserver\\f3\\");
        folder1.mkdirs();
        folder2.mkdirs();
        folder3.mkdirs();
        File[] files1 = folder1.listFiles();
        File[] files2 = folder2.listFiles();
        File[] files3 = folder3.listFiles();
        ArrayList<String> filesArr1=new ArrayList<String>();
        ArrayList<String> filesArr2=new ArrayList<String>();
        ArrayList<String> filesArr3=new ArrayList<String>();
        for (File file : files1) {
            if (file.isFile()) {
                filesArr1.add(file.getName());
            }
        }
        for (File file : files2) {
            if (file.isFile()) {
                filesArr2.add(file.getName());
            }
        }
        for (File file : files3) {
            if (file.isFile()) {
                filesArr3.add(file.getName());
            }
        }
        FolderViewResponse fvr=new FolderViewResponse(filesArr1, filesArr2, filesArr3);
        String outstr=mapper.writeValueAsString(fvr);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out=resp.getWriter();
        out.print(outstr);
        out.flush();
    }
}

class FolderViewResponse{
    ArrayList<String> f1;
    ArrayList<String> f2;
    ArrayList<String> f3;
    public FolderViewResponse(@JsonProperty("f1")ArrayList<String> f1,@JsonProperty("f2") ArrayList<String> f2,@JsonProperty("f3") ArrayList<String> f3) {
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
    }
    public ArrayList<String> getF1() {
        return f1;
    }
    public ArrayList<String> getF2() {
        return f2;
    }
    public ArrayList<String> getF3() {
        return f3;
    }
    public void setF1(@JsonProperty("f1")ArrayList<String> f1) {
        this.f1 = f1;
    }
    public void setF2(@JsonProperty("f2")ArrayList<String> f2) {
        this.f2 = f2;
    }
    public void setF3(@JsonProperty("f3")ArrayList<String> f3) {
        this.f3 = f3;
    }
}