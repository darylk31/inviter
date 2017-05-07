package invite.hfad.com.inviter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import invite.hfad.com.inviter.Register.RegisterConfirm;
import invite.hfad.com.inviter.Register.RegisterName;
import invite.hfad.com.inviter.Register.RegisterUsername;


public class LoginActivity extends AppCompatActivity {

    private String email;
    private String password;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();


        if (auth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, UserAreaActivity.class));
            finish();
        }


        setContentView(R.layout.activity_login);
    }

    public void onLogin(View v){

        showProcessDialog();
        if(!validateForm())
            return;


        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                }
                else {
                    String userId = auth.getCurrentUser().getUid();
                    SharedPreferences.Editor editor = getSharedPreferences("UserPref", 0).edit();
                    editor.putString("userID", userId);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this,UserAreaActivity.class);
                    startActivity(intent);
                    finish();
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