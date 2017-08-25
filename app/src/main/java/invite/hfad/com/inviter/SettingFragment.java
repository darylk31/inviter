package invite.hfad.com.inviter;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Jimmy on 9/6/2016.
 */
public class SettingFragment extends PreferenceFragment {
    DatabaseReference mDatabase;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    User user;

    Preference Username;
    Preference Email;
    Preference Phone_Number;
    Preference Name;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mDatabase = Utils.getDatabase().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        getViews();
        //Grabs user object
        mDatabase.child(Utils.USER).child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);;
                    setUpSettings();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getViews(){
        Username = (Preference) findPreference("pref_key_username");
        Email = (Preference) findPreference("pref_key_email");
        Phone_Number = (Preference) findPreference("pref_key_phonenumber");
        Name = (Preference) findPreference("pref_key_name");
    }
    private void setUpSettings(){
        Username.setSummary(firebaseUser.getDisplayName());
        Email.setSummary(firebaseUser.getEmail());
        Name.setSummary(user.getFirstname() + " " + user.getLastname());
    }

    private void settingOnClick(){
        
    }



}
