package invite.hfad.com.inviter.Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import invite.hfad.com.inviter.LoginActivity;
import invite.hfad.com.inviter.R;

//testing again
public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email = findViewById(R.id.forgotEmailText);
    }

    //Reset Password button
    public void onResetPasswordButton(View view){
        if(email.getText().equals("")){
            email.setError("Email cannot be blank!");
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String emailAddress = email.getText().toString().trim().toLowerCase();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            Toast toast = Toast.makeText(getApplicationContext(),"Password reset sent to " + emailAddress + ".", Toast.LENGTH_LONG);
                            toast.show();
                            startActivity(intent);
                        } else{
                            Toast.makeText(getApplicationContext(),"Error please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}

