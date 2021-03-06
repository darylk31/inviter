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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.Utils;
import invite.hfad.com.inviter.UserDatabaseHelper;

public class EventSelectContacts extends AppCompatActivity {

    private Event event;
    private SelectContactsAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = Utils.getDatabase().getReference();
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_event_select_contacts);
        TextView selected_list = (TextView) findViewById(R.id.tvSelectedContacts);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.selectfriends_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new SelectContactsAdapter(getApplicationContext(), selected_list);
        recyclerView.setAdapter(adapter);

        event = getIntent().getParcelableExtra("myEvent");
        System.out.println("EventSelectContacts Event:" + event.toString());
    }

    public void onButtonClick(View view){
        final String newKey = mDatabase.push().getKey();
        event.setEventId(newKey);
        mDatabase.child(Utils.EVENT_DATABASE).child(newKey).setValue(event);
        //Set yourself as an admin
        mDatabase.child(Utils.EVENT_DATABASE).child(newKey).child(Utils.EVENT_ADMIN).child(auth.getCurrentUser().getUid()).setValue(true);
        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getUid()).child(Utils.EVENT_DATABASE).child(newKey).setValue(newKey);
        //Iterate through arraylist
        //Check to see if they're on each others contacts
        //If so add to new event id to event request inbox
        //If not TODO::
        for(final String id: adapter.getArrayList()){
            mDatabase.child(Utils.USER).child(id).child(Utils.CONTACTS).child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        mDatabase.child(Utils.USER).child(id).child(Utils.INBOX).child(Utils.EVENT_REQUEST).child(newKey).setValue(auth.getCurrentUser().getDisplayName());
                        mDatabase.child(Utils.EVENT_DATABASE).child(newKey).child(Utils.INVITEDID).child(id).setValue(false);
                    }//If they're not on each others contacts
                    else{
                        System.out.println(id + "IS NOT YOUR FKEN FRIEND CUNT");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
        db = databaseHelper.getWritableDatabase();
        UserDatabaseHelper.insert_event(db, event);
        Toast.makeText(this, "Successfully added Event", Toast.LENGTH_LONG).show();

        
        Intent intent = new Intent(this, UserAreaActivity.class);
        startActivity(intent);
        finish();
    }
}
