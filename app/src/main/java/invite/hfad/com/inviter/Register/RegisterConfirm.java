package invite.hfad.com.inviter.Register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import invite.hfad.com.inviter.EmailAddress;
import invite.hfad.com.inviter.LoginActivity;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Utils;


public class RegisterConfirm extends AppCompatActivity {

    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvEmail;
    private TextView tvUsername;
    private TextView tvPhoneNumber;

    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;

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
        mDatabase = Utils.getDatabase().getReference();
    }

    private void getDisplayInformation() {
        tvFirstName = (TextView) findViewById(R.id.tvFirstNameInput);
        tvLastName = (TextView) findViewById(R.id.tvLastNameInput);
        tvEmail = (TextView) findViewById(R.id.tvEmailInput);
        tvUsername = (TextView) findViewById(R.id.tvUsernameInput);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumberInput);
        firstname = bundle.getString("firstname");
        lastname = bundle.getString("lastname");
        username = bundle.getString("username");
        email = bundle.getString("email-address");
        password = bundle.getString("password");
        phoneNumber = bundle.getString("phone-number");
    }

    private void setDisplayInformation() {
        tvFirstName.setText(firstname);
        tvLastName.setText(lastname);
        tvEmail.setText(email);
        tvUsername.setText(username);
        if(phoneNumber != null){
            tvPhoneNumber.setText(phoneNumber);
        }

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
        SafetyNet.getClient(this).verifyWithRecaptcha("6Lex7DUUAAAAACNj2_pDIlUghcGSpp9Lg8FQPwfB")
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            handleSiteVerify(response.getTokenResult());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error message:" + e.getMessage());
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            //Log.d(TAG, "Error message: " +
                            //        CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            //Log.d(TAG, "Unknown type of error: " + e.getMessage());
                        }
                    }
                });
    }

    private void handleSiteVerify(String response){
        RequestParams params = new RequestParams();
        params.put("secret", "6Lex7DUUAAAAAEQobx8P3v0YZNKGAr_zkyt3Y2vY");
        params.put("response", response);
        SafetyNetClient.post("/recaptcha/api/siteverify", params,new JsonHttpResponseHandler() {
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonResponse){
                try {
                    String jsonResult = jsonResponse.getString("success");
                    if(jsonResult=="true"){
                        createUser();
                    } else{
                        Toast.makeText(getApplicationContext(),"Sorry something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    public static class SafetyNetClient {
        private static final String BASE_URL = "https://www.google.com";

        private static AsyncHttpClient client = new AsyncHttpClient();

        public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.post(getAbsoluteUrl(url), params, responseHandler);
        }

        private static String getAbsoluteUrl(String relativeUrl) {
            return BASE_URL + relativeUrl;
        }
    }

    private void createUser(){
        mDatabase.child(Utils.USER).child(username.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(dataSnapshot.exists())) {
                    final String emailString = email.substring(0, email.indexOf('.'));
                    mDatabase.child(Utils.EMAIL).child(emailString.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!(dataSnapshot.exists())) {
                                //Create user
                                createFirebaseUser(emailString);
                            } else {
                                Toast.makeText(getApplicationContext(), "Oops looks like there was an error with the Email Address. \n Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Oops looks like there was an error with the Usernames. \n Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private void createFirebaseUser(String e){
        final String emailString = e;
        showProgressDialog();
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterConfirm.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        } else {
                            String uid = mAuth.getCurrentUser().getUid();
                            User firebaseUser = new User(uid,username,username, firstname, lastname, email);
                            if(phoneNumber != null){
                                firebaseUser = new User(uid,username,username, firstname, lastname, email,phoneNumber);
                            }
                            EmailAddress firebaseEmailAddress = new EmailAddress(uid,email, username);
                            setUserProfile(firebaseUser);
                            mAuth.signOut();
                            if(phoneNumber != null && !phoneNumber.isEmpty()){
                                mDatabase.child(Utils.DATABASE_PHONE_NUMBER).child(phoneNumber).setValue(username);
                            }
                            mDatabase.child(Utils.USER).child(username).setValue(firebaseUser);
                            mDatabase.child(Utils.EMAIL).child(emailString.toLowerCase()).setValue(firebaseEmailAddress);
                            Toast.makeText(getApplicationContext(), "Registration Complete.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterConfirm.this, LoginActivity.class));
                            finish();
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
