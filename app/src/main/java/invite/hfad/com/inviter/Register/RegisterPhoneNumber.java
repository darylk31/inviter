package invite.hfad.com.inviter.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import invite.hfad.com.inviter.R;

public class RegisterPhoneNumber extends AppCompatActivity {

    private EditText phoneNumberText;
    private Button onNextButton;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_number);
        bundle = getIntent().getExtras();
        getViews();
        buttonClickListener();
    }

    private void getViews(){
        phoneNumberText = (EditText) findViewById(R.id.etPhoneNumber);
        onNextButton = (Button) findViewById(R.id.bNext);
    }

    private void buttonClickListener(){
        onNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(phoneNumberText.getText().equals("")){
                    Intent intent = new Intent(RegisterPhoneNumber.this, RegisterPassword.class);
                    intent.putExtra("firstname", bundle.getString("firstname"));
                    intent.putExtra("lastname", bundle.getString("lastname"));
                    intent.putExtra("username",bundle.getString("username"));
                    intent.putExtra("email-address", bundle.getString("email-address"));
                    intent.putExtra("phone-number", phoneNumberText.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }

}
