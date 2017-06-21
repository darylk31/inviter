package invite.hfad.com.inviter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                + "DAY TEXT, "
                + "TITLE TEXT, "
                + "DESCRIPTION TEXT, "
                + "TIME TEXT, "
                + "ENDDAY TEXT, "
                + "ENDTIME TEXT;");

        db.execSQL("CREATE TABLE FRIENDS ("
                + "UID TEXT PRIMARY KEY, "
                + "USERNAME TEXT, "
                + "DISPLAY TEXT, "
                + "PHOTO TEXT);");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, DB_Version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, DB_Version);
    }


    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        if (oldVersion < newVersion) {
            db.execSQL("CREATE TABLE EVENTS ("
                    + "EID TEXT PRIMARY KEY, "
                    + "DAY TEXT, "
                    + "TITLE TEXT, "
                    + "DESCRIPTION TEXT, "
                    + "TIME TEXT, "
                    + "ENDDAY TEXT, "
                    + "ENDTIME TEXT, "
                    + "ALLDAY INTEGER "
                    + "REMINDER INTEGER);");
        }
    }


    public static void insert_event(
            SQLiteDatabase db, Event event){
        /*
        ContentValues eventValues = new ContentValues();
        eventValues.put("DAY", event.getDay());
        eventValues.put("TITLE", event.getEvent_name());
        eventValues.put("DESCRIPTION", event.getDescription());
        eventValues.put("TIME", event.getTime());
        eventValues.put("ENDDAY", event.getEnd_day());
        eventValues.put("ENDTIME", event.getEnd_time());
        db.insert("EVENTS",null, eventValues);
        */
    }

    public static void delete_event(SQLiteDatabase db,
                                    String id){
        db.delete("EVENTS", "_id=" + id, null);
    }

    public static void insert_friend(SQLiteDatabase db,
                                     String uid,
                                     String username,
                                     String display,
                                     String photo){
        ContentValues friendValues = new ContentValues();
        friendValues.put("UID", uid);
        friendValues.put("USERNAME", username);
        friendValues.put("DISPLAY", display);
        friendValues.put("PHOTO", photo);
        db.insert("FRIENDS", null, friendValues);
    }

    public static void delete_friend(SQLiteDatabase db,
                                     String uid) {
        db.delete("FRIENDS", "uid=" + uid, null);
    }
}