package invite.hfad.com.inviter.EventObjectModel;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.DialogBox.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Utils;


public class EventMembersFragment extends Fragment {

    private String id;
    private String creator;
    private RecyclerView attendee_recycler;
    private View mainView;
    private DatabaseReference eventTableRef;
    private Query attendeeRef;
    private static Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("event_id");
        mainView = inflater.inflate(R.layout.fragment_event_members, container, false);
        attendee_recycler = mainView.findViewById(R.id.eventAttendees_recycler);
        attendee_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        eventTableRef = Utils.getDatabase().getReference().child("Events").child(id);
        eventTableRef.child(Utils.EVENT_CREATOR).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                creator = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        attendeeRef = eventTableRef.child(Utils.EVENT_ATTENDEE).orderByValue().startAt(true);
        context = getContext();
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Boolean, EventMemberViewHolder> eventMembersAdapter = new FirebaseRecyclerAdapter<Boolean, EventMemberViewHolder>(
                Boolean.class,
                R.layout.member_list_item,
                EventMemberViewHolder.class,
                attendeeRef) {
            @Override
            protected void populateViewHolder(final EventMemberViewHolder viewHolder, final Boolean admin, int position) {

                final String username = this.getRef(position).getKey();

                Utils.getDatabase().getReference().child(Utils.USER).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (username.equals(creator)){
                            viewHolder.setName(user.getDisplayname() + " (Creator)");
                        } else {
                            if (admin){
                                viewHolder.setName(user.getDisplayname() + " (Admin)");
                            } else{
                                viewHolder.setName(user.getDisplayname());
                            }
                        }
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

        attendee_recycler.setAdapter(eventMembersAdapter);
    }

    public static class EventMemberViewHolder extends RecyclerView.ViewHolder{

        View cardView;
        TextView displayname;
        CircleImageView imageView;

        public EventMemberViewHolder(View itemView){
            super(itemView);
            cardView = itemView;
            displayname = cardView.findViewById(R.id.tv_eventMembersName);
            imageView = cardView.findViewById(R.id.civ_eventMembers);
        }

        public void setPicture(String url){
            if (url == null) {
                Glide.with(context)
                        .load(R.drawable.profile_image)
                        .into(imageView);
            }
            else {
                Glide.with(context)
                        .load(url)
                        .into(imageView);
            }

        }

        public void setName(String name){
            displayname.setText(name);
        }
    }
}


