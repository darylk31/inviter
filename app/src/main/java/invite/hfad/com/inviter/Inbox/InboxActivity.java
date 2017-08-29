package invite.hfad.com.inviter.Inbox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.Utils;


public class InboxActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private RecyclerView recycler;

    private ArrayList<User> friendlist;
    private ArrayList<Event> eventlist;
    private ArrayList<String> invitedbylist;

    private TextView tv_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        getSupportActionBar().setTitle("Inbox");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = Utils.getDatabase().getReference();
        recycler = (RecyclerView) findViewById(R.id.inbox_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        auth = FirebaseAuth.getInstance();
        mDatabase = Utils.getDatabase().getReference();
        tv_empty = (TextView)findViewById(R.id.tv_emptyInbox);

        friendlist = new ArrayList<>();
        eventlist = new ArrayList<>();
        invitedbylist = new ArrayList<>();
        searchinbox();
        searchevents();
    }

    public void searchinbox() {
        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child("Inbox").child(Utils.USER_ADD_REQUEST).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            tv_empty.setVisibility(View.INVISIBLE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                mDatabase.child(Utils.USER).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        friendlist.add(user);
                                        InboxAdapter adapter = new InboxAdapter(friendlist, eventlist, invitedbylist);
                                        recycler.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


    }

    public void searchevents(){
        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child("Inbox").child("Event_Request").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            tv_empty.setVisibility(View.INVISIBLE);
                            for (final DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String invitedby = snapshot.getValue(String.class);
                                invitedbylist.add(invitedby);
                                mDatabase.child(Utils.EVENT_DATABASE).child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Event event = dataSnapshot.getValue(Event.class);
                                            //TODO:: Delete if event is expired
                                            eventlist.add(event);
                                        } else{
                                            mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENT_REQUEST).child(snapshot.getKey()).removeValue();
                                        }
                                        InboxAdapter adapter = new InboxAdapter(friendlist, eventlist, invitedbylist);
                                        recycler.setAdapter(adapter);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, UserAreaActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
