package invite.hfad.com.inviter;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;
//testing again
public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    //Reset Password button
    public void onResetPasswordButton(View view){
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        Toast toast = Toast.makeText(this,"Success", Toast.LENGTH_LONG);
        toast.show();
        startActivity(intent);
    }
}

//Testing test

//Merging to main