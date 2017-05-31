package invite.hfad.com.inviter.Inbox;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.R;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    private ArrayList<String> inbox;
    //private ArrayList<String> friendlist;
    //private ArrayList<String> eventlist;
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

    public InboxAdapter(ArrayList<String> friendlist, ArrayList<String> eventlist) {
        auth = FirebaseAuth.getInstance();
        //friendlist = new ArrayList<>();
        //eventlist = new ArrayList<>();
        //searchinbox();
        friendrequests = friendlist.size();
        eventrequests = eventlist.size();
        inbox = new ArrayList<>(friendrequests + eventrequests);
        inbox.addAll(friendlist);
        inbox.addAll(eventlist);
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
    public void onBindViewHolder(InboxAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                TextView friendtext = (TextView) cardView.findViewById(R.id.friend_request);
                friendtext.setText(inbox.get(position) + " would like to add you!");
                Button acceptbutton = (Button) cardView.findViewById(R.id.accept_button);
                Button declinebutton = (Button) cardView.findViewById(R.id.decline_button);
            case 1:
                TextView eventname = (TextView) cardView.findViewById(R.id.event_name);
                TextView eventday = (TextView) cardView.findViewById(R.id.event_day);
        }
    }

    @Override
    public int getItemCount() {
        return inbox.size();
    }

    @Override
    public int getItemViewType(int position){
        if (position < friendrequests){
            return 0;
        }
        else return 1;
    }

    private void searchinbox() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").child("Add_Request").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                if(dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            Contact contact = snapshot.getValue(Contact.class);
                            friendlist.add(contact.getUid());
                    }
                }
                */


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
