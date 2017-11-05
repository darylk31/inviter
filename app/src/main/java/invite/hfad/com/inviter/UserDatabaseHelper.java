package invite.hfad.com.inviter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
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

/**
 * Created by Daryl on 9/22/2016.
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_Name = "User_DB";
    private static final int DB_Version = 1;

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
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2017, 10, 19, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.TITLE, event.getEvent_name());
        values.put(Events.DESCRIPTION, event.getDescription());
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, startMillis);
        values.put(Events.CALENDAR_ID, 1);
        TimeZone timeZone = TimeZone.getDefault();
        values.put(Events.EVENT_TIMEZONE, timeZone.getID());
        //long eventID = Long.parseLong(uri.getLastPathSegment());
        //System.out.println("This event id" + eventID);
        Uri uri = asSyncAdapter(CalendarContract.CONTENT_URI, "Inviter", "Inviter");
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        uri = cr.insert(Events.CONTENT_URI, values);
    }

    static Uri asSyncAdapter(Uri uri, String account, String accountType) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, account)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType).build();
    }

    public static void delete_event(SQLiteDatabase db,
                                    String id) {
        db.execSQL("DELETE FROM EVENTS WHERE EID LIKE '" + id + "';");
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