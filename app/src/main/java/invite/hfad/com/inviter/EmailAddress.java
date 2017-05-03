package invite.hfad.com.inviter;

/**
 * Created by Jimmy on 5/2/2017.
 */

public class EmailAddress {

    private String email;
    private String username;

    public EmailAddress(){}

    public EmailAddress(String email, String username){
        this.email = email;
        this.username = username;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
