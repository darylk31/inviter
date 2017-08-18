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
    public static final String CHAT = "Chat";
    public static final String EVENT_DATABASE = "Events";
    public static final String PIN = "Pin";

    //USER TABLE STUFF
    public static final String USER_PHOTO_URL = "photoUrl";
    public static final String USER_USERNAME = "username";
    public static final String USER_EVENTS = "Events";
    public static final String USER_ADD_REQUEST = "Add_Request";
    public static final String USER_EVENT_REQUEST = "Event_Request";

    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final String INVITEDID = "Invited_Id";
    public static final String EVENT_ADMIN = "Admins";

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}