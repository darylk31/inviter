package invite.hfad.com.inviter;

import android.net.ConnectivityManager;

import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jimmy on 5/18/2017.
 */

public class Utils {

    public static final String APP_PACKAGE = "invite.hfad.com.inviter";
    public static final String APP = "app";


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
    public static final String NOTIFICATIONS = "Notifications";

    //USER TABLE STUFF
    public static final String USER_PHOTO_URL = "photoUrl";
    public static final String USER_EVENTS = "Events";
    public static final String USER_ADD_REQUEST = "Add_Request";
    public static final String USER_EVENT_REQUEST = "Event_Request";
    public static final String USER_TOKEN = "Device_Token";
    public static final String USER_DISPLAYNAME = "displayname";
    public static final String USER_FIRSTNAME = "firstname";
    public static final String USER_LASTNAME = "lastname";
    public static final String USER_PHONENUMBER = "phonenumber";

    public static final String FRIENDLY_MSG_LENGTH = "friendly_msg_length";
    public static final String INVITEDID = "Invited_Id";
    public static final String EVENT_PHOTO = "photoUrl";
    public static final String EVENT_ATTENDEE = "Attendee";
    public static final String EVENT_STARTDATE = "startDate";
    public static final String EVENT_TITLE = "event_name";
    public static final String EVENT_DESCRIPTION = "description";
    public static final String EVENT_LOCATION = "location";
    public static final String EVENT_LAST_MODIFIED = "last_modified";
    public static final String EVENT_READ_MESSAGES = "read_messages";
    public static final String EVENT_CREATOR = "creator";
    public static final int TYPE_EVENT = 0;
    public static final int TYPE_CHAT = 1;

    public static final String DATABASE_PHONE_NUMBER = "Phone_Number";

    public static final String PERSONAL_CHATS = "Personal_Chat";
    public static final String CHAT_DATABASE = "Personal_Chat";
    public static final String CHAT_MEMBERS = "Members";


    public static final String PROMOTION_DATABASE = "Promotion";
    public static final String REGION_DATABASE = "Region";

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

    public static Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public static Date today() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,0);
        return cal.getTime();
    }


    public static String convertDateToText(Date date){
        String ret = "";
        return ret;
    }

    public static Date convertStringtoDate(String string){
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}