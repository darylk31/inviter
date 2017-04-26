package invite.hfad.com.inviter;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by Jimmy on 3/24/2017.
 */

public class User {

    private String username;
    private String firstname;
    private String lastname;
    private String profilpicture;
    private String email;
    private String password;
    //TODO
    //CONTACTS
    //EVENTS
    //INVITES

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName,String firstname, String lastname, String email, String password) {
        this.username = userName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    private void setFirstname(String firstname){
        this.firstname = firstname;
    }

    private void setLastname(String lastname){
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getProfilpicture() {
        return profilpicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}