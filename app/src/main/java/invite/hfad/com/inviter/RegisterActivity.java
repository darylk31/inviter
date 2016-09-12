package invite.hfad.com.inviter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    //Sign Up button
    public void onSignUpButton(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        Toast toast = Toast.makeText(this,"Success",Toast.LENGTH_LONG);
        toast.show();
        startActivity(intent);
    }
}