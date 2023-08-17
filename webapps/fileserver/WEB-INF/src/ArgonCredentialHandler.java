import org.apache.catalina.CredentialHandler;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class ArgonCredentialHandler implements CredentialHandler{
    public boolean matches(java.lang.String arg0,java.lang.String arg1){
        Argon2PasswordEncoder enc = new Argon2PasswordEncoder(16, 32, 1, 65536, 10);
        return enc.matches(arg0, arg1);
    }
    public java.lang.String mutate(java.lang.String arg0){
        Argon2PasswordEncoder encoder=new Argon2PasswordEncoder(16,32,1,65536,10);
        return encoder.encode(arg0);
    }
}