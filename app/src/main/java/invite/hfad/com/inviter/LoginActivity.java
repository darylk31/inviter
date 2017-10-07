package invite.hfad.com.inviter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import invite.hfad.com.inviter.Register.RegisterName;


public class LoginActivity extends AppCompatActivity {

    private String email;
    private String password;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        mDatabase = Utils.getDatabase().getReference();
        if (auth.getCurrentUser() != null){
            updateUser();
            startActivity(new Intent(LoginActivity.this, UserAreaActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
    }

    private void updateUser(){
        final SharedPreferences.Editor editor = getSharedPreferences(Utils.APP_PACKAGE, 0).edit();
        String userId = auth.getCurrentUser().getDisplayName();
        editor.putString("userID", userId);
        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    final User user = dataSnapshot.getValue(User.class);
                    Gson gson = new Gson();
                    String json = gson.toJson(user);
                    editor.putString("userObject",json);
                    mDatabase.child(Utils.DATABASE_PHONE_NUMBER).child(user.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() && !user.getPhoneNumber().isEmpty()){
                                editor.putBoolean("phoneNumberOnline",true);
                            } else{
                                editor.putBoolean("phoneNumberOnline",false);
                            }
                            editor.commit();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        String deviceToken = FirebaseInstanceId.getInstance().getToken();
        mDatabase.child(Utils.USER).child(userId).child(Utils.USER_TOKEN).child(deviceToken).setValue(deviceToken);
    }

    public void onLogin(View v){
        showProcessDialog();
        if(!validateForm())
            return;
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            performLogin(email,password);
        } else{
            mDatabase.child(Utils.USER).child(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                User user = dataSnapshot.getValue(User.class);
                                performLogin(user.getEmail(),password);
                            } else{
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    private void performLogin(String email, String password){
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    updateUser();
                    final UserDatabaseHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
                    final SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    progressDialog.setMessage("Retrieving account details...");

                    final long[] childrenCount = {0};
                    final int[] eventCount = {0};
                    mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENTS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                System.out.println("Login accessed events table.");
                                childrenCount[0] = dataSnapshot.getChildrenCount();
                                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    mDatabase.child(Utils.EVENT_DATABASE).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                System.out.println("Login accessed children.");
                                                Event event = dataSnapshot.getValue(Event.class);
                                                databaseHelper.insert_event(db, event);
                                                eventCount[0]++;
                                                System.out.println("Login Count Event:" + eventCount[0] + "Children:" + childrenCount[0]);
                                            }
                                            else
                                            {
                                                //If event doesn't exist anymore delete off my firebase table
                                                mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENTS).child(snapshot.getKey()).removeValue();
                                                childrenCount[0]--;
                                            }

                                            if (eventCount[0] == childrenCount[0]) {
                                                db.close();
                                                progressDialog.dismiss();
                                                Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                            }
                            else {
                                progressDialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                                startActivity(intent);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }


        });
    }

    private boolean validateForm(){
        boolean valid = true;

        EditText etEmail = (EditText) findViewById(R.id.etUsername);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(), "Wrong Email Address or password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            valid = false;
        }
        return valid;
    }

    public void onRegister(View v){
        Intent intent = new Intent(this, RegisterName.class);
        startActivity(intent);
    }

    public void onForgotPasswordText(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    //Exiting on back button
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }

    private void showProcessDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
    }

}