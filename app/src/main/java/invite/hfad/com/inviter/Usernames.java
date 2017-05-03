package invite.hfad.com.inviter;

/**
 * Created by Jimmy on 4/29/2017.
 */

public class Usernames {
    private String username;
    private String email;

    public Usernames(){

    }

    public Usernames(String username, String email){
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
