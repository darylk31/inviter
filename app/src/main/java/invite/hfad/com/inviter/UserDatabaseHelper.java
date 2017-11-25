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
import java.util.Date;
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

        db.execSQL("CREATE TABLE CALENDAR " +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "FIREBASE_EID TEXT);");
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
        values.put(CalendarContract.Calendars._ID, MY_CALENDAR_ID);
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        values.put(CalendarContract.Calendars.NAME, "Inviter (" + accountName + ")");
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, Utils.APP_PACKAGE);
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, R.color.colorPrimary);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_READ);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, ACCOUNT_TYPE_LOCAL);

        return context.getContentResolver().insert(target, values);
    }

    public static boolean checkForLocalCalendar(Context context, String accountName) {
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.OWNER_ACCOUNT
        };

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{accountName, Utils.APP_PACKAGE, accountName};
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
        } else {
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        }
        return cur != null;
    }

    public static void remove_local_calendar(Context context, String accountName) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{accountName, ACCOUNT_TYPE_LOCAL, accountName};
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //TODO: ask for permission.
        } else {
            cr.delete(uri, selection, selectionArgs);
        }
    }

    public static void insert_event(
            SQLiteDatabase db, Event event, Context context, String name) {
        ContentValues eventValues = new ContentValues();
        eventValues.put("EID", event.getEventId());
        eventValues.put("CREATOR", event.getCreator());
        eventValues.put("DAY", event.getStartDate());
        eventValues.put("ENDDAY", event.getEndDate());
        eventValues.put("TITLE", event.getEvent_name());
        eventValues.put("DESCRIPTION", event.getDescription());
        eventValues.put("LOCATION", event.getLocation());
        db.insert("EVENTS", null, eventValues);

        ContentValues calendarValues = new ContentValues();
        calendarValues.putNull("_id");
        calendarValues.put("FIREBASE_EID", event.getEventId());
        db.insert("CALENDAR", null, calendarValues);

        insert_event_calendar(db, event, context, name);
    }


    private static void insert_event_calendar(SQLiteDatabase db, Event event, Context context, String accountName) {
        try {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Cursor cursor = db.rawQuery("SELECT * FROM CALENDAR WHERE FIREBASE_EID LIKE '" + event.getEventId() + "';", null);
            cursor.moveToFirst();
            int i = cursor.getInt(0);
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(Events.CALENDAR_ID, MY_CALENDAR_ID);
            values.put(Events._ID, i);
            values.put(Events.TITLE, event.getEvent_name());
            values.put(Events.DESCRIPTION, event.getDescription());
            values.put(Events.EVENT_LOCATION, event.getLocation());

            Calendar beginTime = Calendar.getInstance();
            Date start_date = Utils.convertStringtoDate(event.getStartDate());
            beginTime.setTime(start_date);
            long startMillis = beginTime.getTimeInMillis();
            long endMillis = 0;
            if (event.getEndDate() != null) {
                Calendar endTime = Calendar.getInstance();
                Date end_date = Utils.convertStringtoDate(event.getEndDate());
                endTime.setTime(end_date);
                endMillis = endTime.getTimeInMillis();
            } else{
                if (beginTime.get(Calendar.HOUR) == 0 && beginTime.get(Calendar.MINUTE) == 0){
                    values.put(Events.ALL_DAY, true);
                    Calendar endTime = Calendar.getInstance();
                    endTime.setTime(start_date);
                    endTime.add(Calendar.DATE, 1);
                    endTime.set(Calendar.HOUR, 0);
                    endTime.set(Calendar.MINUTE, 0);
                    endMillis = endTime.getTimeInMillis();
                } else {
                    Calendar endTime = Calendar.getInstance();
                    endTime.setTime(start_date);
                    endTime.add(Calendar.HOUR, 1);
                    endMillis = endTime.getTimeInMillis();
                }
            }
            values.put(Events.DTSTART, startMillis);
            values.put(Events.DTEND, endMillis);

            TimeZone timeZone = TimeZone.getDefault();
            values.put(Events.EVENT_TIMEZONE, timeZone.getID());
            context.getContentResolver().insert(Events.CONTENT_URI, values);
        } catch (Exception e){
            System.out.println("UserDatabaseHelper insert_event_calendar:" + e);
        }
    }

    public static void delete_event(SQLiteDatabase db, String id, Context context) {
        delete_event_calendar(db, id, context);
        db.execSQL("DELETE FROM EVENTS WHERE EID LIKE '" + id + "';");
        db.execSQL("DELETE FROM CALENDAR WHERE FIREBASE_EID LIKE '" + id + "';");
    }

    private static void delete_event_calendar(SQLiteDatabase db, String firebase_eid, Context context){
        try{
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Cursor cursor = db.rawQuery("SELECT _id FROM CALENDAR WHERE FIREBASE_EID LIKE '" + firebase_eid + "';", null);
            cursor.moveToFirst();
            int id = cursor.getInt(0);
            Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, id );
            context.getContentResolver().delete(deleteUri, null, null);
        }
        catch (Exception e){
            System.out.println("UserDatabaseHelper delete_event_calendar:" + e);
        }
    }

    public static void update_event(SQLiteDatabase db, String eid, Event event, Context context) {
        ContentValues eventValues = new ContentValues();
        eventValues.put("DAY", event.getStartDate());
        eventValues.put("ENDDAY", event.getEndDate());
        eventValues.put("TITLE", event.getEvent_name());
        eventValues.put("DESCRIPTION", event.getDescription());
        eventValues.put("LOCATION", event.getLocation());
        db.update("EVENTS", eventValues, "EID='" + eid + "'", null);

        Cursor cursor = db.rawQuery("SELECT _id FROM CALENDAR WHERE FIREBASE_EID LIKE '" + eid + "';", null);
        cursor.moveToFirst();
        int id = cursor.getInt(0);
        Uri updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, MY_CALENDAR_ID);
        values.put(Events._ID, id);
        values.put(Events.TITLE, event.getEvent_name());
        values.put(Events.DESCRIPTION, event.getDescription());
        values.put(Events.EVENT_LOCATION, event.getLocation());

        Calendar beginTime = Calendar.getInstance();
        Date start_date = Utils.convertStringtoDate(event.getStartDate());
        beginTime.setTime(start_date);
        long startMillis = beginTime.getTimeInMillis();
        long endMillis = 0;
        if (event.getEndDate() != null) {

            Calendar endTime = Calendar.getInstance();
            Date end_date = Utils.convertStringtoDate(event.getEndDate());
            endTime.setTime(end_date);
            endMillis = endTime.getTimeInMillis();
        } else{
            if (beginTime.get(Calendar.HOUR) == 0 && beginTime.get(Calendar.MINUTE) == 0){
                values.put(Events.ALL_DAY, true);
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(start_date);
                endTime.add(Calendar.DATE, 1);
                endTime.set(Calendar.HOUR, 0);
                endTime.set(Calendar.MINUTE, 0);
                endMillis = endTime.getTimeInMillis();
            } else {
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(start_date);
                endTime.add(Calendar.HOUR, 1);
                endMillis = endTime.getTimeInMillis();
            }
        }
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        TimeZone timeZone = TimeZone.getDefault();
        values.put(Events.EVENT_TIMEZONE, timeZone.getID());
        context.getContentResolver().update(updateUri, values, null, null);
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