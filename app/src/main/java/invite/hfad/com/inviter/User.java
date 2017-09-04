package invite.hfad.com.inviter;

import java.util.Map;

/**
 * Created by Jimmy on 3/24/2017.
 */

public class User {

    private String username;
    private String firstname;
    private String lastname;
    private String photoUrl;
    private String email;
    private String displayname;
    private String phoneNumber;
    private String uid;
    private Map<String,Boolean> contacts;
    private Map<String,String> inbox;

    private final String defaultURL = "https://firebasestorage.googleapis.com/v0/b/inviter-96012.appspot.com/o/default%2Fdownload.jpg?alt=media&token=72fb0556-f0a9-440f-8d9e-4d29311847a1";

    public User() {
    }


    public User(String uid, String userName, String displayname, String firstname, String lastname, String email) {
        this.uid = uid;
        this.username = userName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.displayname = displayname;
        this.photoUrl = defaultURL;
    }

    public User(String uid, String userName, String displayname, String firstname, String lastname, String email, String phoneNumber) {
        this.uid = uid;
        this.username = userName;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.displayname = displayname;
        this.photoUrl = defaultURL;
        this.phoneNumber = phoneNumber;
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

    public Map<String, Boolean> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, Boolean> contacts) {
        this.contacts = contacts;
    }

    public Map<String, String> getInbox() {
        return inbox;
    }

    public void setInbox(Map<String, String> inbox) {
        this.inbox = inbox;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}