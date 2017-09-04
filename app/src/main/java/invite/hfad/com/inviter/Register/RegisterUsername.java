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
import invite.hfad.com.inviter.Utils;

public class RegisterUsername extends AppCompatActivity {

    private EditText username;
    private DatabaseReference onlineDataBase;
    private DatabaseReference mDatabase;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_username);
        setTitle("Username");
        this.username = (EditText) findViewById(R.id.etUsername);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);

        bundle = getIntent().getExtras();

        mDatabase = Utils.getDatabase().getReference();
    }

    public void onNextButton(View v){
        if(!isValidUsername(username.getText().toString())) {
            Toast.makeText(RegisterUsername.this,"Invalid username. \n Please enter length between 6 and 16 with only letters or numbers.",Toast.LENGTH_SHORT).show();
            return;
        }

        onlineDataBase = Utils.getDatabase().getReference(".info/connected");
        onlineDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    mDatabase.child(Utils.USER).child(username.getText().toString().toLowerCase()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!(dataSnapshot.exists())) {
                                    Intent intent = new Intent(RegisterUsername.this, RegisterEmail.class);
                                    intent.putExtra("firstname", bundle.getString("firstname"));
                                    intent.putExtra("lastname", bundle.getString("lastname"));
                                    intent.putExtra("username", username.getText().toString());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(RegisterUsername.this, "Usernames already exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                } //Not connected
                else {
                    Toast.makeText(RegisterUsername.this,"Please check your connection.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }


    private boolean isValidUsername(String username){
        return(username.length() >= 6 && username.length() <= 16 && username.matches("[a-zA-Z0-9]*"));
    }
}
