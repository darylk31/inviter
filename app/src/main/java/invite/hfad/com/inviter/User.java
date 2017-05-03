package invite.hfad.com.inviter;

import android.support.v4.util.Pair;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by Jimmy on 3/24/2017.
 */

public class User {

    private String username;
    private String firstname;
    private String lastname;
    //private String profilepicture;
    private String email;
    private String password;

    /*
    public User() {
    }

    public User(String userName,String firstname, String lastname, String email, String password) {
        this.username = userName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;

    }
    */


    public void setFirstname(String firstname){
        this.firstname = firstname;
    }

    public void setLastname(String lastname){
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;}

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    /*
    public String getProfilepicture() {
        return profilepicture;
    }
    */

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}