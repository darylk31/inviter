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
            System.out.println("LOOKAT ME" + auth.getCurrentUser().getDisplayName());
            startActivity(new Intent(LoginActivity.this, UserAreaActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
    }

    public void onLogin(View v){
        showProcessDialog();
        if(!validateForm())
            return;
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            performLogin(email,password);
        } else{
            mDatabase.child(Utils.USERNAMES).child(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null){
                                User user = dataSnapshot.getValue(User.class);
                                performLogin(user.getEmail(),password);
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
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else {
                    String userId = auth.getCurrentUser().getDisplayName();
                    SharedPreferences.Editor editor = getSharedPreferences("UserPref", 0).edit();
                    editor.putString("userID", userId);
                    editor.commit();

                    final UserDatabaseHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
                    final SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    progressDialog.setMessage("Retrieving account details...");

                    final long[] childrenCount = {0};
                    final int[] eventCount = {0};
                    mDatabase.child(Utils.USER).child(auth.getCurrentUser().getUid()).child(Utils.USER_EVENTS).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                System.out.println("Login accessed events table.");
                                childrenCount[0] = dataSnapshot.getChildrenCount();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    mDatabase.child("Events").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                System.out.println("Login accessed children.");
                                                Event event = dataSnapshot.getValue(Event.class);
                                                databaseHelper.insert_event(db, event);
                                                eventCount[0]++;
                                                System.out.println("Login Count Event:" + eventCount[0] + "Children:" + childrenCount[0]);

                                                if (eventCount[0] == childrenCount[0]) {
                                                    db.close();
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(LoginActivity.this, UserAreaActivity.class);
                                                    startActivity(intent);
                                                }
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
            Toast.makeText(LoginActivity.this, "Wrong EmailAddress or password", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Press Back again to Exit.",
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