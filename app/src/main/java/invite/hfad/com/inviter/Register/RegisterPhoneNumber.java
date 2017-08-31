package invite.hfad.com.inviter.Register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import invite.hfad.com.inviter.R;

public class RegisterPhoneNumber extends AppCompatActivity {

    private boolean mVerificationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_number);
    }

}
