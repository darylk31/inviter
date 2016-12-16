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

    UserDatabaseHelper(Context context) {
        super(context, DB_Name, null, DB_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE EVENTS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "DAY TEXT, "
                + "TITLE TEXT, "
                + "DESCRIPTION TEXT, "
                + "TIME TEXT, "
                + "TEST TEXT, "
                + "ALLDAY INTEGER);");
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
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "DAY TEXT, "
                    + "TITLE TEXT, "
                    + "DESCRIPTION TEXT, "
                    + "TIME TEXT, "
                    + "TEST TEXT, "
                    + "ALLDAY INTEGER);");
        }
    }


    public static void insert_event(SQLiteDatabase db,
                                    String dayData,
                                    String titleData,
                                    String descriptionData,
                                    String timeData,
                                    boolean allDayData){
        ContentValues eventValues = new ContentValues();
        eventValues.put("DAY", dayData);
        eventValues.put("TITLE", titleData);
        eventValues.put("DESCRIPTION", descriptionData);
        eventValues.put("TIME", timeData);
        if (allDayData) {
            eventValues.put("ALLDAY", 1);
            db.insert("EVENTS", null, eventValues);
        }
        else {
            eventValues.put("ALLDAY", 0);
            db.insert("EVENTS", null, eventValues);
        }
    }

    public static void delete_event(SQLiteDatabase db,
                                    String id){
        db.delete("EVENTS", "_id=" + id, null);
    }
}