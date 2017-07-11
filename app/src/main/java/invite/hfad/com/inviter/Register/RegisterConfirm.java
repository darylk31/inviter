package invite.hfad.com.inviter.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.EmailAddress;
import invite.hfad.com.inviter.LoginActivity;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Usernames;


public class RegisterConfirm extends AppCompatActivity {

    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvEmail;
    private TextView tvUsername;

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;

    private DatabaseReference onlineDatabase;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private ProgressDialog progressDialog;

    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_confirm);
        setTitle("Confirm");
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        bundle = getIntent().getExtras();
        getDisplayInformation();
        setDisplayInformation();
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    private void getDisplayInformation() {
        tvFirstName = (TextView) findViewById(R.id.tvFirstNameInput);
        tvLastName = (TextView) findViewById(R.id.tvLastNameInput);
        tvEmail = (TextView) findViewById(R.id.tvEmailInput);
        tvUsername = (TextView) findViewById(R.id.tvUsernameInput);
        firstname = bundle.getString("firstname");
        lastname = bundle.getString("lastname");
        username = bundle.getString("username");
        email = bundle.getString("email-address");
        password = bundle.getString("password");
    }

    private void setDisplayInformation() {
        tvFirstName.setText(firstname);
        tvLastName.setText(lastname);
        tvEmail.setText(email);
        tvUsername.setText(username);
    }


    /**
     * Multiple checks before creating user.
     * 1. Check firebase connection
     * 2. Check username is available
     * 3. Check email is available
     * Pushes user data onto firebase (user,email,username)
     * Creates a user with email and password
     * Opens Login Activity
     *
     * @param v
     */
    public void onNextButton(View v) {
        onlineDatabase = FirebaseDatabase.getInstance().getReference(".info/connected");
        onlineDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    mDatabase.child("Usernames").child(username.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!(dataSnapshot.exists())) {
                                final String emailString = email.substring(0, email.indexOf('.'));
                                mDatabase.child("Email-Address").child(emailString.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!(dataSnapshot.exists())) {
                                            //Create user
                                            createUser(emailString);
                                        } else {
                                            Toast.makeText(RegisterConfirm.this, "Oops looks like there was an error with the Email Address. \n Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterConfirm.this, "Oops looks like there was an error with the Usernames. \n Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    Toast.makeText(RegisterConfirm.this, "Please check your connection.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    private void createUser(String e){
        final String emailString = e;
        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterConfirm.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterConfirm.this, "An error occurred", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        } else {
                            String uid = mAuth.getCurrentUser().getUid();

                            User firebaseUser = new User(uid,username, firstname, lastname, email, password);
                            Usernames firebaseUsernames = new Usernames(uid,username, email);
                            EmailAddress firebaseEmailAddress = new EmailAddress(uid,email, username);
                            setUserProfile(firebaseUser);
                            mAuth.signOut();
                            mDatabase.child("Users").child(uid).setValue(firebaseUser);
                            mDatabase.child("Email-Address").child(emailString.toLowerCase()).setValue(firebaseEmailAddress);
                            mDatabase.child("Usernames").child(username.toLowerCase()).setValue(firebaseUsernames);
                            Thread thread = new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(2500); // As I am using LENGTH_SHORT in Toast
                                        startActivity(new Intent(RegisterConfirm.this, LoginActivity.class));
                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            Toast.makeText(RegisterConfirm.this, "Registration Complete.", Toast.LENGTH_SHORT).show();
                            thread.start();
                        }
                    }
                });
    }



    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Register");
        progressDialog.setMessage("Registering Account...");
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

    private void setUserProfile(User firebaseUser){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(firebaseUser.getUsername())
                .setPhotoUri(Uri.parse(firebaseUser.getPhotoUrl()))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("REGISTER_CONFIRM", "User profile updated.");
                        }
                    }
                });
    }

}
