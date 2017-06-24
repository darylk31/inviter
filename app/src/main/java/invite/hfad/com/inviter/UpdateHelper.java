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

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Jimmy on 6/23/2017.
 */

public class UpdateHelper {

    static SQLiteDatabase db;
    public UpdateHelper(){}

    public static void updateContacts(Context applicationContext) {
        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(applicationContext);
        db = databaseHelper.getWritableDatabase();
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
                                storeUser(user);
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

    private static void storeUser(User user){
        ContentValues userinfo = new ContentValues();
        userinfo.put("UID", user.getUid());
        userinfo.put("USERNAME", user.getUsername());
        userinfo.put("DISPLAY", user.getDisplayname());
        userinfo.put("PHOTO", user.getPhototUrl());
        db.insert("FRIENDS", null, userinfo);
    }





}

