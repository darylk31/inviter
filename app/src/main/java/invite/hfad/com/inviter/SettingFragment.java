package invite.hfad.com.inviter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

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
    SwitchPreference Phone_Number;
    EditTextPreference First_Name;
    EditTextPreference Last_Name;
    EditTextPreference Display_Name;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Preference SignOut;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        mDatabase = Utils.getDatabase().getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        getViews();
        //Grabs user object
        Gson gson = new Gson();
        sharedPref = getActivity().getSharedPreferences(Utils.APP_PACKAGE, MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(Utils.APP_PACKAGE, 0).edit();
        String json = sharedPref.getString("userObject", "");
        user = gson.fromJson(json, User.class);
        setUpSettings();
        settingOnClick();
    }

    private void getViews(){
        Username = (Preference) findPreference("pref_key_username");
        Email = (Preference) findPreference("pref_key_email");
        Phone_Number = (SwitchPreference) findPreference("pref_key_phonenumber_toggle");
        First_Name = (EditTextPreference) findPreference("pref_key_first_name");
        Last_Name = (EditTextPreference) findPreference("pref_key_last_name");
        Display_Name = (EditTextPreference) findPreference("pref_key_display_name");
        SignOut = findPreference("pref_key_Logout");
    }
    private void setUpSettings(){
        Username.setSummary(user.getUsername());
        Email.setSummary(user.getEmail());
        Display_Name.setSummary(user.getDisplayname());
        Display_Name.setText(user.getDisplayname());
        First_Name.setSummary(user.getFirstname());
        First_Name.setText(user.getFirstname());
        Last_Name.setSummary(user.getLastname());
        Last_Name.setText(user.getLastname());
        Phone_Number.setChecked(sharedPref.getBoolean("phoneNumberOnline",false));
    }

    private void settingOnClick(){
        First_Name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                preference.setSummary(o.toString());
                user.setFirstname(o.toString());
                First_Name.setText(o.toString());
                mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_FIRSTNAME).setValue(o.toString());
                Gson gson = new Gson();
                String json = gson.toJson(user);
                editor.putString("userObject",json);
                editor.commit();
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
                Gson gson = new Gson();
                String json = gson.toJson(user);
                editor.putString("userObject",json);
                editor.commit();
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
                Gson gson = new Gson();
                String json = gson.toJson(user);
                editor.putString("userObject",json);
                editor.commit();
                return false;
            }
        });
        Phone_Number.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();
                if(!(Boolean) o){
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_PACKAGE, MODE_PRIVATE).edit();
                    if(user.getPhoneNumber()!= null) {
                        mDatabase.child(Utils.DATABASE_PHONE_NUMBER).child(user.getPhoneNumber()).removeValue();
                    }
                    if(mPhoneNumber != null) {
                        mDatabase.child(Utils.DATABASE_PHONE_NUMBER).child(mPhoneNumber).removeValue();
                    }
                    editor.putBoolean("phoneNumberOnline", false);
                    Phone_Number.setChecked(false);
                    editor.commit();

                } else {
                    if(mPhoneNumber != null){
                        if(user.getPhoneNumber().equals(mPhoneNumber)){
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Utils.APP_PACKAGE, MODE_PRIVATE).edit();
                            user.setPhoneNumber(mPhoneNumber);
                            mDatabase.child(Utils.DATABASE_PHONE_NUMBER).child(user.getPhoneNumber()).setValue(user.getUsername());
                            editor.putBoolean("phoneNumberOnline", true);
                            Phone_Number.setChecked(true);
                            Gson gson = new Gson();
                            String json = gson.toJson(user);
                            editor.putString("userObject",json);
                            editor.commit();
                            System.out.println("Phone number on");
                            editor.commit();
                        }
                        //Assuming different phone number.
                        //Remove current number off database
                        //Replace current phone number with current sim card number
                        else{
                            mDatabase.child(Utils.DATABASE_PHONE_NUMBER).child(user.getPhoneNumber()).removeValue();
                            mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_PHONENUMBER).setValue(mPhoneNumber);
                            user.setPhoneNumber(mPhoneNumber);
                            Gson gson = new Gson();
                            String json = gson.toJson(user);
                            editor.putString("userObject",json);
                            editor.commit();
                        }
                    }
                    //If phone number doesn't exists then do set to false with toast
                    else {
                        Phone_Number.setChecked(false);
                        Toast.makeText(getActivity(),"Cannot find phone number",Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });

        SignOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                auth.signOut();
                sharedPref.edit().clear().commit();
                SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getActivity().getApplicationContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                db.delete("EVENTS", null, null);
                db.delete("FRIENDS", null, null);
                db.close();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return false;
            }
        });
        
    }

}
