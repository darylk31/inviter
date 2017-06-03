package invite.hfad.com.inviter.Inbox;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.Contact;
import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.LoginActivity;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {


    private ArrayList<User> friendlist;
    private ArrayList<Event> eventlist;
    private int friendrequests;
    private int eventrequests;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public InboxAdapter(ArrayList<User> friendlist, ArrayList<Event> eventlist) {
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.friendlist = friendlist;
        friendrequests = friendlist.size();
        this.eventlist = eventlist;
        eventrequests = eventlist.size();
    }

    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                CardView friend_cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_friend_item, parent, false);
                return new ViewHolder(friend_cv);
            case 1:
                CardView event_cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_event_item, parent, false);
                return new ViewHolder(event_cv);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(final InboxAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                final TextView friendtext = (TextView) cardView.findViewById(R.id.friend_request);
                friendtext.setText(friendlist.get(position).getUsername() + " would like to add you!");
                final Button acceptbutton = (Button) cardView.findViewById(R.id.accept_button);
                final Button declinebutton = (Button) cardView.findViewById(R.id.decline_button);

                acceptbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        Toast.makeText(v.getContext(), friendlist.get(pos).getUsername() + " is now added to your friends list!", Toast.LENGTH_SHORT).show();
                        acceptbutton.setVisibility(Button.GONE);
                        declinebutton.setVisibility(Button.GONE);
                        //Add them onto my contacts
                        Contact myContact = new Contact(friendlist.get(pos).getUid(),true);
                        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Contacts").child(friendlist.get(pos).getUid()).setValue(myContact);
                        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").child("Add_Request").child(friendlist.get(pos).getUid()).removeValue();
                        friendlist.remove(pos);
                    }
                });
                declinebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        Toast.makeText(v.getContext(), friendlist.get(pos).getUsername() + "'s request denied.", Toast.LENGTH_SHORT).show();
                        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").child("Add_Request").child(friendlist.get(pos).getUid()).removeValue();
                        friendlist.remove(pos);
                        acceptbutton.setVisibility(Button.GONE);
                        declinebutton.setVisibility(Button.GONE);
                    }
                });
            case 1:
                int pos = holder.getAdapterPosition() - friendrequests;
                TextView eventname = (TextView) cardView.findViewById(R.id.event_name);
                TextView eventday = (TextView) cardView.findViewById(R.id.event_day);
        }
    }

    @Override
    public int getItemCount() {
        return eventrequests + friendrequests;
    }

    @Override
    public int getItemViewType(int position){
        if (position < friendrequests){
            return 0;
        }
        else return 1;
    }
}
