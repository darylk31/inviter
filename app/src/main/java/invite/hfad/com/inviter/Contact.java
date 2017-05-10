package invite.hfad.com.inviter;

import java.lang.reflect.Array;

/**
 * Created by Jimmy on 5/6/2017.
 */

public class Contact {

    private String uid;
    private Boolean isContact;

    public Contact(){
    }

    public Contact(String uid){
        this.uid = uid;
        isContact = true;
    }

    public Boolean getContact() {
        return isContact;
    }

    public void setContact(Boolean contact) {
        isContact = contact;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
