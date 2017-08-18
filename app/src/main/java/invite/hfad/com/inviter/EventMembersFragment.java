package invite.hfad.com.inviter;

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

import java.util.List;


public class EventMembersFragment extends Fragment {

    private String id;
    private List<String> invitedId;
    private String[] displayNames;
    private String[] displayPictures;
    private String[] userNames;
    private String creator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        id = getArguments().getString("event_id");
        return inflater.inflate(R.layout.fragment_event_members, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        final DatabaseReference event_ref = FirebaseDatabase.getInstance().getReference().child(Utils.EVENT_DATABASE).child(id);
        event_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                creator = event.getCreator();
                event_ref.child(Utils.INVITEDID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                            invitedId.add(dataSnapshot.getKey());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                displayNames = new String[invitedId.size()];
                displayPictures = new String[invitedId.size()];
                userNames = new String[invitedId.size()];
                for (int i = 0; i < invitedId.size(); i++) {
                    String userId = invitedId.get(i);
                    DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    final int finalI = i;
                    users_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            displayNames[finalI] = user.getDisplayname();
                            displayPictures[finalI] = user.getPhotoUrl();
                            userNames[finalI] = user.getUsername();

                            RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.eventMembers_recycler);
                            EventMembersAdapter adapter = new EventMembersAdapter(userNames, displayNames, displayPictures, creator, getContext());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
