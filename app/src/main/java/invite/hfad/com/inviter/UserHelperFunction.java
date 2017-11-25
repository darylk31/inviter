package invite.hfad.com.inviter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Jimmy on 11/4/2017.
 */

public class UserHelperFunction {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static DatabaseReference mDatabase = Utils.getDatabase().getReference();
    private static String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

    public void UserHelperFunction(){

    }

    public static void addUsername(String username){
        mDatabase.child(Utils.USER).child(firebaseUser).child(Utils.CONTACTS).child(username).setValue(false);
        mDatabase.child(Utils.USER).child(username).child(Utils.INBOX).child(Utils.USER_ADD_REQUEST).child(firebaseUser).setValue(firebaseUser);
    }

    public static void removeUsername(String username){
        mDatabase.child(Utils.USER).child(firebaseUser).child(Utils.CONTACTS).child(username).removeValue();
        mDatabase.child(Utils.USER).child(username).child(Utils.INBOX).child(Utils.USER_ADD_REQUEST).child(firebaseUser).removeValue();
        //Find personal chat id and remove it
        mDatabase.child(Utils.USER).child(firebaseUser).child(Utils.PERSONAL_CHATS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String personalchat_key = (String) dataSnapshot.getValue();
                    mDatabase.child(Utils.CHAT_DATABASE).child(personalchat_key).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //Remove chat off both users
        mDatabase.child(Utils.USER).child(firebaseUser).child(Utils.PERSONAL_CHATS).child(username).removeValue();
        mDatabase.child(Utils.USER).child(username).child(Utils.PERSONAL_CHATS).child(firebaseUser).removeValue();
    }

    public static boolean isContact(String username){
        String exists = mDatabase.child(Utils.USER).child(firebaseUser).child(Utils.CONTACTS).child(username).getKey();
        System.out.println(exists);
        return !exists.equalsIgnoreCase("");
    }
}
