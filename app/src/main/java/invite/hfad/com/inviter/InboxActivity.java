package invite.hfad.com.inviter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        getSupportActionBar().setTitle("Inbox");
    }
}
