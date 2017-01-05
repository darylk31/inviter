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
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
    }


    public void onNextButton(View view){
        Intent intent = new Intent(RegisterActivity.this, RegisterActivity2.class);
        if (validEmail(etEmail) || validNumber(etPhoneNumber)) {
            intent.putExtra("Email", etEmail.getText().toString().trim());
            intent.putExtra("PhoneNumber", etPhoneNumber.getText().toString().trim());
            startActivity(intent);
        } else{
            etEmail.setError("Require Email");
            etPhoneNumber.setError("Require Phone");
        }

    }

    private boolean validEmail(EditText e){
        //Check if blank
        String emailData = e.getText().toString().trim();
        if(emailData.equals("")){
            e.setError("Require Email");
            return false;
        }
        //...BACKEND: check if email is in database already
        return true;
    }

    private boolean validNumber(EditText n){

        //Check if blank
        String phoneData = n.getText().toString().trim();
        if(phoneData.equals("")){
            n.setError("Require PhoneNumber");
            return false;
        }
        //...BACKEND: check if number is in database already
        return true;

    }
}