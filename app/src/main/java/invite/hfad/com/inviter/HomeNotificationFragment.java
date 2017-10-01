package invite.hfad.com.inviter;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeNotificationFragment extends Fragment {


    HomeNotificationAdapter adapter;
    ArrayList<Event> event_ids;

    RecyclerView homeRecycler;
    private final int MAX_NUMBER_OF_NOTIFICATION_TAB = 10;

    TextView message;
    DatabaseReference mDatabaseReference;
    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_notification, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("HomeNotif: On Start");
        event_ids = new ArrayList<>();
        adapter = new HomeNotificationAdapter(getActivity().getApplicationContext(), true, event_ids);
        //Grabs user object
        Gson gson = new Gson();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Utils.APP_PACKAGE, MODE_PRIVATE);
        String json = sharedPref.getString("userObject", "");
        user = gson.fromJson(json, User.class);
        homeRecycler = (RecyclerView) getView().findViewById(R.id.home_notification_recycler);
        message = (TextView) getView().findViewById(R.id.tv_emptyHome);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        homeRecycler.setLayoutManager(layoutManager);
        mDatabaseReference = Utils.getDatabase().getReference();
        getData();
        onDataChange();
    }


    private void onDataChange(){
        mDatabaseReference.child(Utils.USER).child(user.getUsername()).child(Utils.USER_EVENTS).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateAdapter();
                System.out.println(s + " CHANGED");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void getData(){
        mDatabaseReference.child(Utils.USER).child(user.getUsername()).child(Utils.USER_EVENTS).orderByChild(Utils.EVENT_LAST_MODIFIED).limitToFirst(MAX_NUMBER_OF_NOTIFICATION_TAB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    mDatabaseReference.child(Utils.EVENT_DATABASE).child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                event_ids.add(0,dataSnapshot.getValue(Event.class));
                                adapter = new HomeNotificationAdapter(getActivity().getApplicationContext(), true, event_ids);
                                setAdapter();
                            }
                            if (adapter.getItemCount() == 0){
                                message.setVisibility(View.VISIBLE);
                                message.setText("You have no upcoming events.");
                            } else{
                                message.setVisibility(getView().GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    System.out.println("Printing out event uid" + ds.getKey());
                }
                if(!dataSnapshot.exists()) {
                    adapter = new HomeNotificationAdapter(getActivity().getApplicationContext(), true, event_ids);
                    setAdapter();
                    if (adapter.getItemCount() == 0) {
                        message.setVisibility(View.VISIBLE);
                        message.setText("You have no upcoming events.");
                    } else {
                        message.setVisibility(getView().GONE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateAdapter(){
        mDatabaseReference.child(Utils.USER).child(user.getUsername()).child(Utils.USER_EVENTS).orderByChild(Utils.EVENT_LAST_MODIFIED).limitToFirst(MAX_NUMBER_OF_NOTIFICATION_TAB).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    mDatabaseReference.child(Utils.EVENT_DATABASE).child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                event_ids.add(0,dataSnapshot.getValue(Event.class));
                            }
                            if (adapter.getItemCount() == 0){
                                message.setVisibility(View.VISIBLE);
                                message.setText("You have no upcoming events.");
                            } else{
                                message.setVisibility(getView().GONE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    System.out.println("Printing out event uid" + ds.getKey());
                }
                adapter.updateEventList(event_ids);
                adapter.notifyDataSetChanged();
                if(!dataSnapshot.exists()) {
                    adapter = new HomeNotificationAdapter(getActivity().getApplicationContext(), true, event_ids);
                    if (adapter.getItemCount() == 0) {
                        message.setVisibility(View.VISIBLE);
                        message.setText("You have no upcoming events.");
                    } else {
                        message.setVisibility(getView().GONE);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void setAdapter(){
        homeRecycler.setAdapter(adapter);
    }
}
