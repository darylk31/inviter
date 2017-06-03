package invite.hfad.com.inviter.Register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;

public class RegisterEmail extends AppCompatActivity {
    private EditText email;
    private DatabaseReference onlineDatabase;
    private DatabaseReference mDatabase;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);
        setTitle("Email");
        bundle = getIntent().getExtras();
        this.email = (EditText) findViewById(R.id.etEmailAddress);

        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void onNextButton(View v) {
        if (!isValidEmail(email.getText().toString())) {
            Toast.makeText(RegisterEmail.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
            return;
        }
        onlineDatabase = FirebaseDatabase.getInstance().getReference(".info/connected");
        onlineDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    String emailString = email.getText().toString();
                    emailString = emailString.substring(0,emailString.indexOf('.'));
                    mDatabase.child("Email-Address").child(emailString.toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!(dataSnapshot.exists())) {
                                Intent intent = new Intent(RegisterEmail.this, RegisterPassword.class);
                                intent.putExtra("firstname", bundle.getString("firstname"));
                                intent.putExtra("lastname", bundle.getString("lastname"));
                                intent.putExtra("username",bundle.getString("username"));
                                intent.putExtra("email-address", email.getText().toString());
                                startActivity(intent);
                            } else {
                                Toast.makeText(RegisterEmail.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } else {
                    {
                        Toast.makeText(RegisterEmail.this, "Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });


    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
