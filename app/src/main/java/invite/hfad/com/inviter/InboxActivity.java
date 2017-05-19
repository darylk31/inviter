package invite.hfad.com.inviter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static invite.hfad.com.inviter.Utils.mDatabase;

public class InboxActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        getSupportActionBar().setTitle("Inbox");
        recycler = (RecyclerView)findViewById(R.id.inbox_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        mDatabase = Utils.getDatabase().getReference();

        //InboxAdapter adapter = new InboxAdapter();
        //recycler.setAdapter(adapter);
    }

    private void setInbox(){
       mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("inbox").addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(DataSnapshot dataSnapshot, String s) {
               String key = dataSnapshot.getKey();
               String value = dataSnapshot.getValue().toString();
               Inbox inbox = new Inbox(key,value);
               InboxAdapter adapter = new InboxAdapter();
               recycler.setAdapter(adapter);
           }

           @Override
           public void onChildChanged(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onChildRemoved(DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(DataSnapshot dataSnapshot, String s) {

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

    }
}
