package invite.hfad.com.inviter.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import invite.hfad.com.inviter.R;

public class RegisterName extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_name);
        setTitle("Name");
        //Getting fields
        this.firstName = (EditText) findViewById(R.id.etFirstName);
        this.lastName = (EditText) findViewById(R.id.etLastName);
    }

    public void onNextButton(View v){
        if(firstName.getText().toString().matches("")){
            Toast.makeText(RegisterName.this,"First name cannot be empty.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(lastName.getText().toString().matches("")){
            Toast.makeText(RegisterName.this,"Last name cannot be empty.",Toast.LENGTH_SHORT).show();
            return;
        }
        String firstname = firstName.getText().toString();
        String lastname = lastName.getText().toString();

        Intent intent = new Intent(RegisterName.this,RegisterUsername.class);
        intent.putExtra("firstname",firstname);
        intent.putExtra("lastname",lastname);
        startActivity(intent);
    }

}
