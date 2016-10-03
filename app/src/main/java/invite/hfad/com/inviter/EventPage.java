package invite.hfad.com.inviter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;




public class EventPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        int id = getIntent().getIntExtra("event_id", 0);
        //find event with event_id and display
        //...
        }
    }

