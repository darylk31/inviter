package invite.hfad.com.inviter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity2 extends AppCompatActivity {

    private String email;
    private String username;
    private String name;
    private String password;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        setTitle("Welcome!");
        this.email = getIntent().getStringExtra("Email");
        this.password = getIntent().getStringExtra("Password");
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
        firebaseAuth = FirebaseAuth.getInstance();
    };



    protected void onRegisterButton(View view){
        EditText etUsername = (EditText) findViewById(R.id.etRegister_Username);
        this.username = etUsername.getText().toString();

        EditText etName = (EditText) findViewById(R.id.etName);
        this.name = etName.getText().toString();


        firebaseAuth.signInWithEmailAndPassword(email, password);

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest addName = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            user.updateProfile(addName)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
        }
        else {
            Toast.makeText(RegisterActivity2.this, "Login Error", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(RegisterActivity2.this, UserAreaActivity.class);
        startActivity(intent);
    }
}
