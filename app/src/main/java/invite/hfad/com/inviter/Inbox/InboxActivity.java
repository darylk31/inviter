package invite.hfad.com.inviter.Inbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import invite.hfad.com.inviter.Contact;
import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.Inbox.InboxAdapter;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Utils;


public class InboxActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private RecyclerView recycler;

    private ArrayList<User> friendlist;
    private ArrayList<Event> eventlist;
    private ArrayList<String> invitedbylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        getSupportActionBar().setTitle("Inbox");
        recycler = (RecyclerView) findViewById(R.id.inbox_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        friendlist = new ArrayList<>();
        eventlist = new ArrayList<>();
        invitedbylist = new ArrayList<>();
        searchinbox();
        searchevents();
    }

    public void searchinbox() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").child("Add_Request").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                mDatabase.child("Users").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").child("Event_Request").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String invitedby = snapshot.getValue(String.class);
                                invitedbylist.add(invitedby);
                                mDatabase.child("Events").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()){
                                            Event event = dataSnapshot.getValue(Event.class);
                                            //event.getEndDate(); check if event is already over.
                                            eventlist.add(event);
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
}
