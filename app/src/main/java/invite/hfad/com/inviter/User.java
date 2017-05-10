package invite.hfad.com.inviter;

import android.net.Uri;

/**
 * Created by Jimmy on 3/24/2017.
 */

public class User {

    private String username;
    private String firstname;
    private String lastname;
    private Uri phototUrl;
    private String email;
    private String password;
    private String displayname;
    private String uid;

    private final Uri defaultURL = Uri.parse("https://firebasestorage.googleapis.com/v0/b/inlcude-2df4d.appspot.com/o/default%2Fdownload.jpg?alt=media&token=5cada801-ac07-4534-b4bf-da5ab77b4679");

    public User() {
    }

    public User(String uid, String userName,String firstname, String lastname, String email, String password) {
        this.uid = uid;
        this.username = userName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.displayname = userName;
        this.phototUrl = defaultURL;
    }


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


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public Uri getPhototUrl() {
        return phototUrl;
    }

    public void setPhototUrl(Uri phototUrl) {
        this.phototUrl = phototUrl;
    }

}