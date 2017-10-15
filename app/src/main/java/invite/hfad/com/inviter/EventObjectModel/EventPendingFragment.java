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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.DialogBox.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Utils;


public class EventPendingFragment extends Fragment {

    private String id;
    private RecyclerView pending_recycler;
    private View mainView;
    private DatabaseReference eventPendingRef;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("event_id");
        mainView = inflater.inflate(R.layout.fragment_event_pending, container, false);
        pending_recycler = mainView.findViewById(R.id.eventPending_recycler);
        pending_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        eventPendingRef = Utils.getDatabase().getReference().child("Events").child(id).child(Utils.INVITEDID);
        context = getContext();
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Boolean, EventMembersFragment.EventMemberViewHolder> eventMembersAdapter = new FirebaseRecyclerAdapter<Boolean, EventMembersFragment.EventMemberViewHolder>(
                Boolean.class,
                R.layout.member_list_item,
                EventMembersFragment.EventMemberViewHolder.class,
                eventPendingRef) {
            @Override
            protected void populateViewHolder(final EventMembersFragment.EventMemberViewHolder viewHolder, final Boolean admin, int position) {

                final String username = this.getRef(position).getKey();

                Utils.getDatabase().getReference().child(Utils.USER).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        viewHolder.setName(user.getDisplayname());
                        viewHolder.setPicture(user.getPhotoUrl());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ProfileDialogBox profileDialogBox = new ProfileDialogBox(context, username);
                        profileDialogBox.show();
                    }
                });
            }
        };
        pending_recycler.setAdapter(eventMembersAdapter);
    }
}
