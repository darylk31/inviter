package invite.hfad.com.inviter;


import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.EventObjectModel.EventViewPager;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeChatFragment extends Fragment {

    private RecyclerView chatRecycler;
    private View mainView;
    private FirebaseAuth auth;
    private Query userEventQuery;
    private DatabaseReference eventTableRef;
    private DatabaseReference userEventRef;
    private Context context;
    private int MAX_NUMBER_OF_NOTIFICATION_TAB = 10;
    private int CurrentPage = 1;
    private FirebaseRecyclerAdapter homeChatRecyclerAdapter;

    public HomeChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_home_chat, container, false);
        chatRecycler = mainView.findViewById(R.id.home_chat_recycler);
        chatRecycler.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        chatRecycler.setLayoutManager(linearLayoutManager);
        auth = FirebaseAuth.getInstance();
        eventTableRef = Utils.getDatabase().getReference().child("Events");
        userEventQuery = Utils.getDatabase().getReference().child("Users").child(auth.getCurrentUser().getDisplayName())
                .child(Utils.USER_EVENTS).orderByChild(Utils.EVENT_LAST_MODIFIED).limitToLast(MAX_NUMBER_OF_NOTIFICATION_TAB * CurrentPage);
        userEventQuery.keepSynced(true);
        userEventRef = Utils.getDatabase().getReference().child("Users").child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENTS);
        //downloadChats();
        return mainView;
    }

    @Override
    public void onStart(){
        super.onStart();
        downloadChats();
    }

    public void downloadChats(){
        context = getContext();

        FirebaseRecyclerAdapter<UserEvents, HomeChatViewHolder> homeChatRecylerAdapter = new FirebaseRecyclerAdapter<UserEvents, HomeChatViewHolder>(
                UserEvents.class,
                R.layout.home_notification_item_layout,
                HomeChatViewHolder.class,
                userEventQuery)
        {
            @Override
            protected void populateViewHolder(final HomeChatViewHolder viewHolder, final UserEvents event, int position) {
                View cardView = viewHolder.cardView;
                final String eventID;
                eventID = event.getEventID();
                if (eventID == null) {
                    return;
                }

                eventTableRef.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event eventSnapshot = dataSnapshot.getValue(Event.class);
                            viewHolder.setEventName(eventSnapshot.getEvent_name());
                        }
                        else {
                            userEventRef.child(eventID).removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                eventTableRef.child(eventID).child(Utils.EVENT_PHOTO).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String url = dataSnapshot.getValue().toString();
                            viewHolder.setPicture(url, context);
                        }
                        else {
                            viewHolder.setPicture(null, context);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                eventTableRef.child(eventID).child(Utils.CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setUnread(dataSnapshot.getChildrenCount() - event.getRead_messages());

                        //Converting Date to English (Today, Yesterday etc)
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                        try {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(format.parse(event.getLast_modified()));

                            Calendar yesterday = Calendar.getInstance();
                            yesterday.add(Calendar.DAY_OF_YEAR, -1);

                            Calendar today = Calendar.getInstance();

                            if(cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)){
                                Date date = format.parse(event.getLast_modified());
                                int hour = date.getHours() % 12;
                                if (hour == 0)
                                    hour = 12;
                                String timeText = String.format("%02d:%02d %s", hour ,date.getMinutes(), date.getHours() < 12 ? "AM" : "PM");
                                viewHolder.setLastTime(timeText);
                            }
                            else if(cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)){
                                viewHolder.setLastTime("Yesterday");
                            } else{
                                Date newDate = new SimpleDateFormat("yyyy-MM-dd").parse(event.getLast_modified());
                                String dateOutput = new SimpleDateFormat("MMM dd", Locale.ENGLISH).format(newDate);
                                viewHolder.setLastTime(dateOutput);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                eventTableRef.child(eventID).child(Utils.CHAT).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot message_snapshot : dataSnapshot.getChildren()) {
                                FriendlyMessage message = message_snapshot.getValue(FriendlyMessage.class);
                                viewHolder.setLastMessage(message.getDisplayname() + ": " + message.getText());
                            }
                        }
                        else
                            {viewHolder.setLastMessage("Created on: " + event.getLast_modified());}
                        }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), EventViewPager.class);
                        intent.putExtra("event_id", event.getEventID());
                        startActivity(intent);
                    }
                });


            }
        };
        chatRecycler.setAdapter(homeChatRecylerAdapter);
    }


    public static class HomeChatViewHolder extends RecyclerView.ViewHolder{

        View cardView;

        public HomeChatViewHolder(View itemView) {
            super(itemView);
            cardView = itemView;
        }

        public void setPicture(String url, Context context){
            CircleImageView imageView = cardView.findViewById(R.id.home_chat_ImageView);

            if (url == null){
                Glide.with(context)
                        .load(R.drawable.profile_image)
                        .dontAnimate()
                        .into(imageView);

            }
            else
            Glide.with(context)
                        .load(url)
                        .into(imageView);
            }



        public void setEventName(String name){
            TextView eventName = cardView.findViewById(R.id.home_event_name);
            eventName.setText(name);

        }

        public void setLastMessage(String message){
            TextView lastMessage = cardView.findViewById(R.id.home_event_last_message);
            lastMessage.setText(message);

        }

        public void setLastTime(String time){
            TextView lastTime = cardView.findViewById(R.id.home_event_last_time);
            lastTime.setText(time);
        }

        public void setUnread(long unread){
            TextView unread_tv = cardView.findViewById(R.id.home_chat_unread);
            unread_tv.setText(Long.toString(unread));
        }
    }
}
