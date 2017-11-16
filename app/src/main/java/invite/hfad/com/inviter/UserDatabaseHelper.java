package invite.hfad.com.inviter;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.TimeZone;

import static android.provider.CalendarContract.ACCOUNT_TYPE_LOCAL;
import static android.provider.CalendarContract.CONTENT_URI;

/**
 * Created by Daryl on 9/22/2016.
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_Name = "User_DB";
    private static final int DB_Version = 1;
    private static final int MY_CALENDAR_ID = 333;

    public UserDatabaseHelper(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE EVENTS ("
                + "EID TEXT PRIMARY KEY, "
                + "CREATOR TEXT, "
                + "DAY TEXT, "
                + "ENDDAY TEXT,"
                + "TITLE TEXT, "
                + "DESCRIPTION TEXT, "
                + "LOCATION TEXT);");

        db.execSQL("CREATE TABLE FRIENDS ("
                + "USERNAME TEXT PRIMARY KEY, "
                + "DISPLAY TEXT, "
                + "ACCEPT INTEGER);");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, DB_Version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, DB_Version);
    }


    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion < newVersion) {
            db.execSQL("CREATE TABLE EVENTS ("
                    + "EID TEXT PRIMARY KEY, "
                    + "DAY TEXT, "
                    + "TITLE TEXT, "
                    + "DESCRIPTION TEXT, "
                    + "TIME TEXT, "
                    + "ENDDAY TEXT, "
                    + "ENDTIME TEXT, "
                    + "ALLDAY INTEGER,"
                    + "REMINDER INTEGER);");
        }
    }

    public static Uri createLocalCalendar(Context context, String accountName) {
        Uri target = Uri.parse(CalendarContract.Calendars.CONTENT_URI.toString());
        target = target.buildUpon().appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, Utils.APP_PACKAGE).build();


        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, Utils.APP_PACKAGE);
        values.put(CalendarContract.Calendars.NAME, "Inviter (" + accountName + ")");
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, Utils.APP_PACKAGE);
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0x00FF00);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_READ);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.CAN_PARTIALLY_UPDATE, 1);

        Uri newCalendar = context.getContentResolver().insert(target, values);
        return newCalendar;
    }

    public static boolean checkForLocalCalendar(Context context, String accountName) {
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
        };

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{accountName, Utils.APP_PACKAGE, accountName};
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //TODO: ask for permission.
        } else {
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        }
        return cur != null;
    }

    public static void deleteLocalCalendar(Context context, String accountName){
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{accountName, Utils.APP_PACKAGE, accountName};
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //TODO: ask for permission.
        } else {
           cr.delete(uri, selection, selectionArgs);
        }
    }
    public static void insert_event_calendar(Event event, Context context, String accountName){
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2017, 10, 21, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        ContentValues values = new ContentValues();
        values.put(Events._ID, event.getEventId());
        values.put(Events.TITLE, event.getEvent_name());
        values.put(Events.DESCRIPTION, event.getDescription());
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, startMillis);
        values.put(Events.CALENDAR_ID, MY_CALENDAR_ID);
        TimeZone timeZone = TimeZone.getDefault();
        values.put(Events.EVENT_TIMEZONE, timeZone.getID());
        Uri uri = context.getContentResolver().insert(asSyncAdapter(Events.CONTENT_URI, accountName), values);
    }


    public static void insert_event(
            SQLiteDatabase db, Event event,
            Context context) {
        ContentValues eventValues = new ContentValues();
        eventValues.put("EID", event.getEventId());
        eventValues.put("CREATOR", event.getCreator());
        eventValues.put("DAY", event.getStartDate());
        eventValues.put("ENDDAY", event.getEndDate());
        eventValues.put("TITLE", event.getEvent_name());
        eventValues.put("DESCRIPTION", event.getDescription());
        eventValues.put("LOCATION", event.getLocation());
        db.insert("EVENTS", null, eventValues);
    }

    static Uri asSyncAdapter(Uri uri, String accountName) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, Utils.APP_PACKAGE)
                .build();
    }

      public static void delete_event(SQLiteDatabase db,
                                    String id, Context context) {
        db.execSQL("DELETE FROM EVENTS WHERE EID LIKE '" + id + "';");
        ContentResolver cr = context.getContentResolver();
    }

    public static void update_event(SQLiteDatabase db, String id, Event event) {
        ContentValues eventValues = new ContentValues();
        eventValues.put("DAY", event.getStartDate());
        eventValues.put("ENDDAY", event.getEndDate());
        eventValues.put("TITLE", event.getEvent_name());
        eventValues.put("DESCRIPTION", event.getDescription());
        eventValues.put("LOCATION", event.getLocation());
        db.update("EVENTS", eventValues, "EID='" + id + "'", null);
    }



    public static void updateContacts(final SQLiteDatabase db) {
        db.execSQL("DELETE FROM FRIENDS;");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference mDatabase = Utils.getDatabase().getReference();
        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child("Contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot contact_snapshot : dataSnapshot.getChildren()) {
                        mDatabase.child(Utils.USER).child(contact_snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (contact_snapshot.getValue(boolean.class)) {
                                        insert_friend(db, user, 1);
                                    } else {
                                        insert_friend(db, user, 0);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void insert_friend(SQLiteDatabase db,
                                     User user, int accept) {
        ContentValues friendValues = new ContentValues();
        friendValues.put("USERNAME", user.getUsername());
        friendValues.put("DISPLAY", user.getDisplayname());
        friendValues.put("ACCEPT", accept);
        db.insert("FRIENDS", null, friendValues);
    }

    public static void delete_friend(SQLiteDatabase db,
                                     String username) {
        db.execSQL("DELETE FROM FRIENDS WHERE USERNAME LIKE '" + username + "';");
    }


}