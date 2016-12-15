package invite.hfad.com.inviter;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("Registration");
        this.etEmail = (EditText)findViewById(R.id.etEmail);
        this.etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
    }


    public void onNextButton(View view){
        if (validEmail(etEmail) && validNumber(etPhoneNumber)) {
            Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
            intent.putExtra("Email", etEmail.getText().toString().trim());
            intent.putExtra("PhoneNumber", etPhoneNumber.getText().toString().trim());
            startActivity(intent);
        }
    }

    private boolean validEmail(EditText e){
        //...BACKEND: check if email is in database already
        return true;
    }

    private boolean validNumber(EditText number){
        //...BACKEND: check if number is in database already
        return true;
    }
}