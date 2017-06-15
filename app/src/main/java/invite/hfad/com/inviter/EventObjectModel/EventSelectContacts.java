package invite.hfad.com.inviter.EventObjectModel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import invite.hfad.com.inviter.Contacts.FriendsAdapter;
import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;

public class EventSelectContacts extends AppCompatActivity {

    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_select_contacts);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.selectfriends_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(new SelectContactsAdapter(getApplicationContext()));

        event = getIntent().getParcelableExtra("myEvent");
        System.out.println("EventSelectContacts Event:" + event.toString());
        Toast.makeText(this,event.getCreator(),Toast.LENGTH_LONG).show();
    }

    public void onButtonClick(View view){
        /*
        Set event object arraylist to whatever the list is
         */
        FirebaseDatabase.getInstance()
                .getReference()
                .push()
                .setValue(event);
        //TODO
        //Should really open event right after an event is created
        Intent intent = new Intent(this, UserAreaActivity.class);
        startActivity(intent);
        finish();
    }
}
