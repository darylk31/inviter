package invite.hfad.com.inviter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
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
    EditTextPreference Phone_Number;
    EditTextPreference First_Name;
    EditTextPreference Last_Name;
    EditTextPreference Display_Name;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mDatabase = Utils.getDatabase().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        getViews();
        //Grabs user object
        mDatabase.child(Utils.USER).child(firebaseUser.getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);;
                    setUpSettings();
                    settingOnClick();
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
        Phone_Number = (EditTextPreference) findPreference("pref_key_phonenumber");
        First_Name = (EditTextPreference) findPreference("pref_key_first_name");
        Last_Name = (EditTextPreference) findPreference("pref_key_last_name");
        Display_Name = (EditTextPreference) findPreference("pref_key_display_name");
    }
    private void setUpSettings(){
        Username.setSummary(firebaseUser.getDisplayName());
        Email.setSummary(firebaseUser.getEmail());
        Display_Name.setSummary(user.getDisplayname());
        Display_Name.setText(user.getDisplayname());
        First_Name.setSummary(user.getFirstname());
        First_Name.setText(user.getFirstname());
        Last_Name.setSummary(user.getLastname());
        Last_Name.setText(user.getLastname());
        if(user.getPhoneNumber() != null){
            Phone_Number.setSummary(user.getPhoneNumber());
            Phone_Number.setText(user.getPhoneNumber());
        }
    }

    private void settingOnClick(){
        First_Name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString());
                user.setFirstname(o.toString());
                First_Name.setText(o.toString());
                mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_FIRSTNAME).setValue(o.toString());
                return false;
            }
        });
        Last_Name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString());
                user.setLastname(o.toString());
                Last_Name.setText(o.toString());
                mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_LASTNAME).setValue(o.toString());
                return false;
            }
        });
        Display_Name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString());
                user.setDisplayname(o.toString());
                Display_Name.setText(o.toString());
                mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_DISPLAYNAME).setValue(o.toString());
                return false;
            }
        });
        Phone_Number.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString());
                return false;
            }
        });
        
    }



}
