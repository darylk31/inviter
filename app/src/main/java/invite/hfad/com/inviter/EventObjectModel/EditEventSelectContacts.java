package invite.hfad.com.inviter.EventObjectModel;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import org.w3c.dom.Text;

import java.util.ArrayList;

import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.UserDatabaseHelper;
import invite.hfad.com.inviter.Utils;

public class EditEventSelectContacts extends AppCompatActivity {

    private Event event;
    private EditSelectContactsAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private SQLiteDatabase db;
    private TextView selected_list;
    private RecyclerView recyclerView;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = Utils.getDatabase().getReference();
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_event_select_contacts);
        id = getIntent().getStringExtra("event_id");

        selected_list = (TextView) findViewById(R.id.tvSelectedContacts);
        recyclerView = (RecyclerView)findViewById(R.id.selectfriends_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        remove_attendee();
    }

    public void onButtonClick(View view){
        final String newKey = mDatabase.push().getKey();
        event.setEventId(newKey);
        mDatabase.child(Utils.EVENT_DATABASE).child(newKey).setValue(event);
        //Set yourself as an admin
        mDatabase.child(Utils.EVENT_DATABASE).child(newKey).child(Utils.EVENT_ADMIN).child(auth.getCurrentUser().getDisplayName()).setValue(true);
        mDatabase.child(Utils.EVENT_DATABASE).child(newKey).child(Utils.EVENT_ATTENDEE).child(auth.getCurrentUser().getDisplayName()).setValue(true);
        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENTS).child(newKey).setValue(newKey);
        //Iterate through arraylist
        //Check to see if they're on each others contacts
        //If so add to new event id to event request inbox
        //If not TODO::
        for(final String id: adapter.getArrayList()){
            mDatabase.child(Utils.USER).child(id).child(Utils.CONTACTS).child(auth.getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        mDatabase.child(Utils.USER).child(id).child(Utils.INBOX).child(Utils.EVENT_REQUEST).child(newKey).setValue(auth.getCurrentUser().getDisplayName());
                        mDatabase.child(Utils.EVENT_DATABASE).child(newKey).child(Utils.INVITEDID).child(id).setValue(false);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void remove_attendee(){
        final ArrayList<String> attendee_list = new ArrayList<>();
        final long[] attendee_count = {0};
        DatabaseReference databaseReference = Utils.getDatabase().getReference().child(Utils.EVENT_DATABASE)
                .child(id).child(Utils.EVENT_ATTENDEE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    attendee_count[0] = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    attendee_list.add(snapshot.getKey());
                }

                if (attendee_count[0] == attendee_list.size()){
                    remove_pending(attendee_list);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void remove_pending(final ArrayList<String> remove_list){
        final ArrayList<String> pending_list = new ArrayList<>();
        final long[] pending_count = {0};
        DatabaseReference databaseReference = Utils.getDatabase().getReference().child(Utils.EVENT_DATABASE)
                .child(id).child(Utils.INVITEDID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    pending_count[0] = dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   pending_list.add(snapshot.getKey());
                }

                if (pending_count[0] == pending_list.size()){
                    remove_list.addAll(pending_list);
                    adapter = new EditSelectContactsAdapter(getApplicationContext(), selected_list, remove_list);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
