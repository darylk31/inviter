package invite.hfad.com.inviter;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jimmy on 5/18/2017.
 */

public class Utils {
    public static final String INBOX = "Inbox";
    public static final String FRIEND_REQUEST = "Friend_Request";
    public static final String EVENT_REQUEST = "Event_Request";
    public static final String USER = "Users";
    public static final String CONTACTS = "Contacts";
    public static final String USERNAMES = "Usernames";

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}