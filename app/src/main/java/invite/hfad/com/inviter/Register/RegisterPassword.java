package invite.hfad.com.inviter.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.R;

public class RegisterPassword extends AppCompatActivity {

    private EditText password;
    private EditText confirmPassword;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_password);
        setTitle("Password");
        this.password = (EditText) findViewById(R.id.etPassword);
        this.confirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        bundle = getIntent().getExtras();
    }


    public void onNextButton(View v) {
        if (!isValidPassword(password.getText().toString(), confirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Invalid password. \n Please enter length between 6 and 16 with only letters or numbers.", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(RegisterPassword.this, RegisterPhoneNumber.class);
            intent.putExtra("firstname", bundle.getString("firstname"));
            intent.putExtra("lastname", bundle.getString("lastname"));
            intent.putExtra("email-address", bundle.getString("email-address"));
            intent.putExtra("username", bundle.getString("username"));
            intent.putExtra("password",password.getText().toString());
            intent.putExtra("phone-number",bundle.getString("phone-number"));
            startActivity(intent);
        }
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if(password.equals(null))
            return false;
        return (password.length() >= 6 && password.length() <= 16 && password.equals(confirmPassword));
    }
}
