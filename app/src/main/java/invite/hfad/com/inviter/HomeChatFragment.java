package invite.hfad.com.inviter;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private int MAX_NUMBER_OF_NOTIFICATION_TAB = 12;


    public HomeChatFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_home_chat, container, false);
        chatRecycler = mainView.findViewById(R.id.home_chat_recycler);
        chatRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        chatRecycler.setLayoutManager(linearLayoutManager);

        auth = FirebaseAuth.getInstance();
        eventTableRef = Utils.getDatabase().getReference().child("Events");
        userEventQuery = Utils.getDatabase().getReference().child("Users").child(auth.getCurrentUser().getDisplayName())
                .child(Utils.USER_EVENTS).orderByChild(Utils.EVENT_LAST_MODIFIED).limitToFirst(MAX_NUMBER_OF_NOTIFICATION_TAB);
        userEventQuery.keepSynced(true);
        userEventRef = Utils.getDatabase().getReference().child("Users").child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENTS);
        downloadChats();
        return mainView;
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
                        viewHolder.setLastTime(event.getLast_modified());
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
