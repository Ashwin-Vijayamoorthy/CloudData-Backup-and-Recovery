import com.fasterxml.jackson.annotation.*;

public class User {
    private String uid;
    private String uname;

    // constructor
    public User(@JsonProperty("uid") String uid,
    @JsonProperty("uname") String uname) {
        this.uid = uid;
        this.uname = uname;
    }

    // getters
    public String getUid() {
        return this.uid;
    }

    public String getUname() {
        return this.uname;
    }

    // setters
    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
