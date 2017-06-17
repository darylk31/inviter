package invite.hfad.com.inviter.EventObjectModel;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import invite.hfad.com.inviter.Contacts.FriendsAdapter;
import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.UserDatabaseHelper;

public class EventSelectContacts extends AppCompatActivity {

    private Event event;
    private SelectContactsAdapter adapter;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_select_contacts);
        TextView selected_list = (TextView) findViewById(R.id.tvSelectedContacts);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.selectfriends_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new SelectContactsAdapter(getApplicationContext(), selected_list);
        recyclerView.setAdapter(adapter);

        event = getIntent().getParcelableExtra("myEvent");
        System.out.println("EventSelectContacts Event:" + event.toString());
        Toast.makeText(this,event.getCreator(),Toast.LENGTH_LONG).show();
    }

    public void onButtonClick(View view){
        Toast.makeText(this,adapter.getArrayList().toString(),Toast.LENGTH_LONG).show();
        event.setInvitedId(adapter.getArrayList());
        FirebaseDatabase.getInstance()
                .getReference()
                .child("Events")
                .push()
                .setValue(event);
        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        UserDatabaseHelper.insert_event(db, event);

        /*
        //TODO
        //Should really open event right after an event is created
        Intent intent = new Intent(this, UserAreaActivity.class);
        startActivity(intent);
        finish();
        */
    }
}
