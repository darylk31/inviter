package invite.hfad.com.inviter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    protected void onLogin(View v){
        Intent intent = new Intent(this, UserAreaActivity.class);
        startActivity(intent);
    }

    protected void onRegister(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onForgotPasswordText(View view){
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void onSettingButton(View view){
        Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
    }

}