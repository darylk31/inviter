package invite.hfad.com.inviter;

/**
 * Created by Jimmy on 4/29/2017.
 */

public class Usernames {
    private String uid;
    private String username;
    private String email;
    private String displayname;
    private String photoUrl;

    private final String defaultURL = "https://firebasestorage.googleapis.com/v0/b/inviter-96012.appspot.com/o/default%2Fdownload.jpg?alt=media&token=72fb0556-f0a9-440f-8d9e-4d29311847a1";

    public Usernames(){

    }

    public Usernames(String uid, String username, String email){
        this.uid = uid;
        this.username = username;
        this.displayname = username;
        this.email = email;
        photoUrl = defaultURL;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
