package invite.hfad.com.inviter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etPhoneNumber = (EditText)findViewById(R.id.etPhoneNumber);
        final EditText etName = (EditText)findViewById(R.id.etUsername);
        final EditText etUsername = (EditText)findViewById(R.id.etUsername);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);

        final Button bRegister = (Button) findViewById(R.id.bRegister);
    }
}
