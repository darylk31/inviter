package invite.hfad.com.inviter;

import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Jimmy on 5/18/2017.
 */

public class Utils {

    public static final String APP_PACKAGE = "invite.hfad.com.inviter";

    public static final String INBOX = "Inbox";
    public static final String FRIEND_REQUEST = "Friend_Request";
    public static final String EVENT_REQUEST = "Event_Request";
    public static final String USER = "Users";
    public static final String CONTACTS = "Contacts";
    public static final String USERNAMES = "Usernames";
    public static final String CHAT = "Chat";
    public static final String EVENT_DATABASE = "Events";
    public static final String PIN = "Pin";
    public static final String EMAIL = "Email_Address";

    //USER TABLE STUFF
    public static final String USER_PHOTO_URL = "photoUrl";
    public static final String USER_USERNAME = "username";
    public static final String USER_EVENTS = "Events";
    public static final String USER_ADD_REQUEST = "Add_Request";
    public static final String USER_EVENT_REQUEST = "Event_Request";

    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final String INVITEDID = "Invited_Id";
    public static final String EVENT_ADMIN = "Admins";
    public static final String EVENT_ATTENDEE = "Attendee";
    public static final String EVENT_STARTDATE = "startDate";
    public static final String EVENT_TITLE = "event_name";
    public static final String EVENT_DESCRIPTION = "description";
    public static final String EVENT_LOCATION = "location";
    public static final String DATABASE_PHONE_NUMBER = "Phone_Number";

    public static final String USER_DISPLAYNAME = "displayname";
    public static final String USER_FIRSTNAME = "firstname";
    public static final String USER_LASTNAME = "lastname";
    public static final String USER_PHONENUMBER = "phonenumber";

    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

    public static String getCurrentDate(){
        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(cal.getTime());
    }

}