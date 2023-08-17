import java.util.ArrayList;
import com.fasterxml.jackson.annotation.*;

public class ErrorResponse{
    boolean err;
    ArrayList<String> error;

    public ErrorResponse(@JsonProperty("err") boolean err, @JsonProperty("error") ArrayList<String> error){
        this.err=err;
        this.error=error;
    }

    //gettters
    public boolean getErr(){
        return this.err;
    }
    public ArrayList<String> getError(){
        return this.error;
    }
    
    //setters
    public void setErr(@JsonProperty("err") boolean err){
        this.err=err;
    }
    public void setError(@JsonProperty("error") ArrayList<String> error){
        this.error=error;
    }
}
