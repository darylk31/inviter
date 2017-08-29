package invite.hfad.com.inviter.EventObjectModel;

import android.content.Context;
import android.net.Uri;
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


public class EventPendingFragment extends Fragment {

    private String id;
    private RecyclerView pending_recycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("event_id");
        return inflater.inflate(R.layout.fragment_event_pending, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        pending_recycler = (RecyclerView) getView().findViewById(R.id.eventPending_recycler);
        pending_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        final ArrayList<String> pendingId = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pending_ref = ref.child(Utils.EVENT_DATABASE).child(id).child(Utils.INVITEDID);
        pending_ref.addListenerForSingleValueEvent(new ValueEventListener() {
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

        private void populatePending(ArrayList<String> pending_array) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
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
