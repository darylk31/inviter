package invite.hfad.com.inviter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etCPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Registration");
        this.etEmail = (EditText)findViewById(R.id.etEmail);
        this.etPassword = (EditText)findViewById(R.id.etPassword);
        this.etCPassword = (EditText) findViewById(R.id.etConfirmPassword);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);

        auth = FirebaseAuth.getInstance();


    }


    public void onNextButton(View view){

        final String password = etPassword.getText().toString().trim();
        final String cpassword = etCPassword.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();

        if (validEmail(email) && validPassword(password, cpassword)) {
            showProgress();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "Register failed, please try again.", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "Register failed, please try again.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
                                intent.putExtra("Email", email);
                                intent.putExtra("Password", password);
                                startActivity(intent);
                            }
                        }
                    });

        } else{
            etEmail.setError("Require Email");
            etPassword.setError("Passwords not the same or passwords less than 6 letters.");
        }

    }

    private boolean validEmail(String e){
        return !e.equals("");
    }

    private boolean validPassword(String password, String cpassword){
        return (password.equals(cpassword) && password.length() >= 6);
    }

    private void showProgress(){
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Register");
            progressDialog.setMessage("Attempting to register account...");
            progressDialog.show();
    }
}