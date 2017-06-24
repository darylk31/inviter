package invite.hfad.com.inviter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                + "DESCRIPTION TEXT);");

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

        ContentValues eventValues = new ContentValues();
        eventValues.put("EID", event.getEventId());
        eventValues.put("CREATOR", event.getCreator());
        eventValues.put("DAY", event.getStartDate());
        eventValues.put("ENDDAY", event.getEndDate());
        eventValues.put("TITLE", event.getEvent_name());
        eventValues.put("DESCRIPTION", event.getDescription());
        db.insert("EVENTS",null, eventValues);

    }

    public static void delete_event(SQLiteDatabase db,
                                    String id){
        db.execSQL("DELETE FROM EVENTS WHERE EID LIKE '" + id + "';");
    }

    public static void updateContacts(final SQLiteDatabase db, Context context) {
        db.execSQL("DELETE FROM FRIENDS;");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        mDatabase.child("Users").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                insert_friend(db, user);
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
                                     User user){
        ContentValues friendValues = new ContentValues();
        friendValues.put("UID", user.getUid());
        friendValues.put("USERNAME", user.getUsername());
        friendValues.put("DISPLAY", user.getDisplayname());
        friendValues.put("PHOTO", user.getPhototUrl());
        db.insert("FRIENDS", null, friendValues);
    }

    public static void delete_friend(SQLiteDatabase db,
                                     String uid) {
        db.delete("FRIENDS", "uid=" + uid, null);
    }


}