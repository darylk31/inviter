package invite.hfad.com.inviter.EventObjectModel;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventEditAdminActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private Query attendeeQuery;
    private DatabaseReference attendeeRef;
    private DatabaseReference userRef;
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit_admin);
        recyclerView = findViewById(R.id.edit_admin_recycler);
        context = getApplicationContext();
        String eventID = getIntent().getStringExtra("eventID");
        attendeeQuery = Utils.getDatabase().getReference().child(Utils.EVENT_DATABASE).child(eventID).child(Utils.EVENT_ATTENDEE);
        attendeeRef = Utils.getDatabase().getReference().child(Utils.EVENT_DATABASE).child(eventID).child(Utils.EVENT_ATTENDEE);
        userRef = Utils.getDatabase().getReference().child(Utils.USER);

    }

    @Override
    public void onStart(){
        super.onStart();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        getAttendees();
    }

    public void getAttendees(){
        FirebaseRecyclerAdapter<Boolean, EditAdminViewHolder> editAdminRecyclerAdapter = new FirebaseRecyclerAdapter<Boolean, EditAdminViewHolder>(
                Boolean.class,
                R.layout.edit_admin_item_layout,
                EditAdminViewHolder.class,
                attendeeQuery) {

            @Override
            protected void populateViewHolder(final EditAdminViewHolder viewHolder, Boolean model, int position) {

                String username = this.getRef(position).getKey();
                viewHolder.setSwitch(model);

                userRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        viewHolder.setName(user.getDisplayname());
                        viewHolder.setPicture(user.getPhotoUrl());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                viewHolder.admin_switch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(viewHolder.admin_switch.isChecked()){
                            Toast.makeText(context, "You cannot remove an admin.", Toast.LENGTH_SHORT);
                        } else{
                            viewHolder.admin_switch.setChecked(true);
                            Toast.makeText(context, viewHolder.name + " is now an admin.", Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(editAdminRecyclerAdapter);
    }

    public static class EditAdminViewHolder extends RecyclerView.ViewHolder {

        View cardView;
        Switch admin_switch;
        TextView name;

        public EditAdminViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
            admin_switch = cardView.findViewById(R.id.edit_admin_switch);
            name = cardView.findViewById(R.id.tv_eventMembersName);

        }

        public void setName(String username){
            name.setText(username);
        }

        public void setSwitch(Boolean admin){
            admin_switch.setChecked(admin);
        }

        public void setPicture(String url){
            CircleImageView picture = cardView.findViewById(R.id.civ_eventMembers);
            if (url == null) {
                Glide.with(context)
                        .load(R.drawable.profile_image)
                        .into(picture);
            }
            else {
                Glide.with(context)
                        .load(url)
                        .into(picture);
            }
        }


    }
}
