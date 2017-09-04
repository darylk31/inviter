package invite.hfad.com.inviter.Register;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.Utils;

public class RegisterPhoneNumber extends AppCompatActivity {

    private TextView phoneNumberText;
    private Button nextButton;
    private Button skipButton;
    private Bundle bundle;
    private DatabaseReference mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_number);
        bundle = getIntent().getExtras();
        mdatabase = Utils.getDatabase().getReference();
        getViews();
        buttonClickListener();
        setUpPhoneNumber();
    }

    private void getViews(){
        phoneNumberText = (TextView) findViewById(R.id.tvPhoneNumber);
        nextButton = (Button) findViewById(R.id.bNext);
        skipButton = (Button) findViewById(R.id.bSkip);
    }

    private void setUpPhoneNumber(){
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();
        phoneNumberText.setText(mPhoneNumber);
    }

    private void buttonClickListener(){
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!phoneNumberText.getText().equals("")){
                    mdatabase.child(Utils.DATABASE_PHONE_NUMBER).child(phoneNumberText.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                Intent intent = new Intent(RegisterPhoneNumber.this, RegisterConfirm.class);
                                intent.putExtra("firstname", bundle.getString("firstname"));
                                intent.putExtra("lastname", bundle.getString("lastname"));
                                intent.putExtra("email-address", bundle.getString("email-address"));
                                intent.putExtra("username", bundle.getString("username"));
                                intent.putExtra("password",bundle.getString("password"));
                                intent.putExtra("phone-number", phoneNumberText.getText().toString());
                                startActivity(intent);
                            } else{
                                Toast.makeText(RegisterPhoneNumber.this,"Sorry phone number in use", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(RegisterPhoneNumber.this, RegisterPassword.class);
                    intent.putExtra("firstname", bundle.getString("firstname"));
                    intent.putExtra("lastname", bundle.getString("lastname"));
                    intent.putExtra("username",bundle.getString("username"));
                    intent.putExtra("email-address", bundle.getString("email-address"));
                    intent.putExtra("password",bundle.getString("password"));
                    intent.putExtra("phone-number", "");
                    startActivity(intent);
            }
        });

    }

}
