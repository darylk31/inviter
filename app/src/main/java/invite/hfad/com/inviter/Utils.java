package invite.hfad.com.inviter;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jimmy on 5/18/2017.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}