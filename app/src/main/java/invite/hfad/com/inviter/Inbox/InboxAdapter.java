package invite.hfad.com.inviter.Inbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.UserDatabaseHelper;
import invite.hfad.com.inviter.UserEvents;
import invite.hfad.com.inviter.Utils;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {


    private ArrayList<User> friendlist;
    private ArrayList<Event> eventlist;
    private ArrayList<String> invitedbylist;
    private int friendrequests;
    private int eventrequests;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private Context context;
    private long read_message = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public InboxAdapter(ArrayList<User> friendlist, ArrayList<Event> eventlist, ArrayList<String> invitedbylist, Context context) {
        auth = FirebaseAuth.getInstance();
        this.context = context;
        mDatabase = Utils.getDatabase().getReference();
        this.friendlist = friendlist;
        friendrequests = friendlist.size();
        this.eventlist = eventlist;
        eventrequests = eventlist.size();
        this.invitedbylist = invitedbylist;
        System.out.println("InvitedByList: " + invitedbylist);
        System.out.println("EventList: " + eventlist);
        System.out.println("FriendList: " + friendlist);
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
        int viewType = getItemViewType(position);
        CardView cardView = holder.cardView;
        switch (viewType) {
            case 0:
                final TextView friendtext = (TextView) cardView.findViewById(R.id.friend_request);
                friendtext.setText(friendlist.get(position).getUsername() + " would like to add you!");
                final Button friendaccept = (Button) cardView.findViewById(R.id.friend_accept_button);
                final Button frienddecline = (Button) cardView.findViewById(R.id.friend_decline_button);

                friendaccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        Toast.makeText(context, friendlist.get(pos).getUsername() + " is now added to your friends list!", Toast.LENGTH_SHORT).show();
                        //Add them onto my contacts
                        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.CONTACTS).child(friendlist.get(pos).getUsername()).setValue(true);
                        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.INBOX).child(Utils.USER_ADD_REQUEST).child(friendlist.get(pos).getUsername()).removeValue();
                        //Set invited user contact to true
                        mDatabase.child(Utils.USER).child(friendlist.get(pos).getUsername()).child(Utils.CONTACTS).child(auth.getCurrentUser().getDisplayName()).setValue(true);
                        removefriendrequest(pos);
                    }
                });
                frienddecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        Toast.makeText(context, friendlist.get(pos).getUsername() + "'s request denied.", Toast.LENGTH_SHORT).show();
                        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child("Inbox").child(Utils.USER_ADD_REQUEST).child(friendlist.get(pos).getUsername()).removeValue();
                        mDatabase.child(Utils.USER).child(friendlist.get(pos).getUsername()).child("Contacts").child(auth.getCurrentUser().getDisplayName()).removeValue();
                        removefriendrequest(pos);
                    }
                });
                break;

            case 1:
                final int pos = holder.getAdapterPosition() - friendrequests;
                TextView eventname = (TextView) cardView.findViewById(R.id.inbox_event_name);
                eventname.setText(eventlist.get(pos).getEvent_name());
                TextView eventday = (TextView) cardView.findViewById(R.id.inbox_event_day);
                TextView eventMonth = (TextView) cardView.findViewById(R.id.inbox_event_month);
                try{
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(eventlist.get(pos).getStartDate());
                    eventMonth.setText(new SimpleDateFormat("MMM", Locale.ENGLISH).format(date));
                    eventday.setText(new SimpleDateFormat("dd", Locale.ENGLISH).format(date));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                TextView invitedby = (TextView) cardView.findViewById(R.id.inbox_event_inviteby);
                invitedby.setText("Invitation from: " + invitedbylist.get(pos));

                final Button eventaccept = (Button) cardView.findViewById(R.id.event_accept_button);
                final Button eventdecline = (Button) cardView.findViewById(R.id.event_decline_button);

                eventaccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int pos = holder.getAdapterPosition() - friendrequests;

                        DatabaseReference eventTableRef = mDatabase.child(Utils.EVENT_DATABASE).child(eventlist.get(pos).getEventId());
                        final DatabaseReference userEventRef = mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENTS)
                                .child(eventlist.get(pos).getEventId());

                        //Add to attendee list
                        //Set value to false defaulty not an admin
                        eventTableRef.child(Utils.EVENT_ATTENDEE).child(auth.getCurrentUser().getDisplayName()).setValue(false);

                        eventTableRef.child(Utils.CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    read_message = dataSnapshot.getChildrenCount();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        //Remove off invited id list
                        eventTableRef.child(Utils.INVITEDID).child(auth.getCurrentUser().getDisplayName()).removeValue();

                        //Adds to Users events
                        UserEvents userEvents = new UserEvents(eventlist.get(pos).getLast_modified(),read_message,eventlist.get(pos).getEventId(),Utils.TYPE_EVENT);
                        userEventRef.setValue(userEvents);

                        //Removes off users inbox
                        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.INBOX).child(Utils.EVENT_REQUEST)
                                .child(eventlist.get(pos).getEventId()).removeValue();

                        //Writes to SQL
                        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(context);
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        UserDatabaseHelper.insert_event(db, eventlist.get(pos));

                        
                        Toast.makeText(context, eventlist.get(pos).getEvent_name() + " is added!", Toast.LENGTH_SHORT).show();
                        removeeventrequest(pos);
                    }
                });

                eventdecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition() - friendrequests;
                        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child("Inbox").child("Event_Request")
                                .child(eventlist.get(pos).getEventId()).removeValue();
                        Toast.makeText(context, " Event request declined.", Toast.LENGTH_SHORT).show();
                        removeeventrequest(pos);
                    }
                });
        }
    }


    @Override
    public int getItemCount() {
        return eventrequests + friendrequests;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < friendrequests) {
            return 0;
        } else return 1;
    }

    public void removefriendrequest(int pos){
        friendlist.remove(pos);
        friendrequests--;
        InboxAdapter.this.notifyItemRemoved(pos);
        InboxAdapter.this.notifyItemRangeChanged(pos, getItemCount());
        InboxAdapter.this.notifyDataSetChanged();

    }

    public void removeeventrequest(int pos){
        eventlist.remove(pos);
        invitedbylist.remove(pos);
        eventrequests--;
        InboxAdapter.this.notifyItemRemoved(pos + friendrequests);
        InboxAdapter.this.notifyItemRangeChanged(pos + friendrequests, getItemCount());
        InboxAdapter.this.notifyDataSetChanged();

    }

}
