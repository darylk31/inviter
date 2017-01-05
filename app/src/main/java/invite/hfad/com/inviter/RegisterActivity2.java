package invite.hfad.com.inviter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/*
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
*/

public class RegisterActivity2 extends AppCompatActivity {

    private String email;
    private String phoneNumber;
    private String username;
    private String name;
    private String password;
    private ProgressDialog progressDialog;
    //private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        setTitle("Registration");
        this.email = (String) getIntent().getExtras().get("Email");
        this.phoneNumber = (String) getIntent().getExtras().get("PhoneNumber");
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
        //firebaseAuth = FirebaseAuth.getInstance();
    }


    private void onRegisterButton(View view){
        EditText etUsername = (EditText) findViewById(R.id.etUsername);
        this.username = etUsername.getText().toString().trim();

        EditText etName = (EditText) findViewById(R.id.etName);
        this.name = etName.getText().toString().trim();

        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        EditText etConfirm = (EditText) findViewById(R.id.etConfirmPassword);
        if (etConfirm == etPassword) {
            this.password = etPassword.getText().toString();
        }

        //...BACKEND: register user in database
        /*
        if (check_username(username)) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast toast = Toast.makeText(RegisterActivity2.this, "Register success!", Toast.LENGTH_SHORT);
                                toast.show();
                                Intent intent = new Intent(RegisterActivity2.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }
        */
    }

    private boolean check_username(String username){
        //...BACKEND: check if username is taken
        return true;
    }
}
