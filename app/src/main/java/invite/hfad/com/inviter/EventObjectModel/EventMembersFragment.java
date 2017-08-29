package invite.hfad.com.inviter.EventObjectModel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Utils;


public class EventMembersFragment extends Fragment {

    private String id;
    private String creator;
    private RecyclerView attendee_recycler;
    private RecyclerView pending_recycler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("event_id");
        return inflater.inflate(R.layout.fragment_event_members, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        attendee_recycler = (RecyclerView) getView().findViewById(R.id.eventAttendees_recycler);
        pending_recycler = (RecyclerView) getView().findViewById(R.id.eventPending_recycler);
        attendee_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        pending_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        final ArrayList<String> adminId = new ArrayList<>();
        final ArrayList<String> acceptedId = new ArrayList<>();
        final ArrayList<String> pendingId = new ArrayList<>();
        DatabaseReference event_ref = Utils.getDatabase().getReference();
        DatabaseReference attendee = event_ref.child(Utils.EVENT_DATABASE).child(id).child(Utils.EVENT_ATTENDEE);
        attendee.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.getValue(boolean.class)) {
                            adminId.add(snapshot.getKey());
                        } else
                            acceptedId.add(snapshot.getKey());
                    }
                populateAttendee(adminId, acceptedId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference pending = event_ref.child(Utils.EVENT_DATABASE).child(id).child(Utils.INVITEDID);
        pending.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        pendingId.add(snapshot.getKey());
                    }
                populatePending(pendingId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void populateAttendee(ArrayList<String> admin_array, ArrayList<String> attendee_array){
        DatabaseReference databaseReference = Utils.getDatabase().getReference();
        final int admin_size = admin_array.size();
        admin_array.addAll(attendee_array);
        final ArrayList<User> allAttendee_Users = new ArrayList<>();
        for (int i = 0; i < admin_array.size(); i++) {
            databaseReference.child(Utils.USER).child(admin_array.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        allAttendee_Users.add(user);
                    }
                    EventMembersAdapter adapter = new EventMembersAdapter(allAttendee_Users, admin_size, getContext());
                    attendee_recycler.setAdapter(adapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }


    private void populatePending(ArrayList<String> pending_array) {
        DatabaseReference databaseReference = Utils.getDatabase().getReference();
        final ArrayList<User> allPending_Users = new ArrayList<>();
        for (int i = 0; i < pending_array.size(); i++) {
            databaseReference.child(Utils.USER).child(pending_array.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        allPending_Users.add(user);
                    }
                    EventPendingAdapter adapter = new EventPendingAdapter(allPending_Users, getContext());
                    pending_recycler.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }}
}


