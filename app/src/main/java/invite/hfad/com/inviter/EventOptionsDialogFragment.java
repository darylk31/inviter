package invite.hfad.com.inviter;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventOptionsDialogFragment extends DialogFragment {


    DatabaseReference mDatabase;
    FirebaseAuth auth;
    String event_id;
    private View rootView;
    Event event;

    Button edit_event;
    Button edit_admin;
    Button leave_event;
    Button delete_event;
    public EventOptionsDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDatabase = Utils.getDatabase().getReference();
        auth = FirebaseAuth.getInstance();
        event_id = getArguments().getString("id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.fragment_event_options_dialog, container, false);
        ButtonClickListener();
        return rootView;
    }

    private void ButtonClickListener(){
        edit_event = (Button) rootView.findViewById(R.id.edit_event);
        edit_admin = (Button) rootView.findViewById(R.id.edit_admin);
        leave_event = (Button) rootView.findViewById(R.id.leave_event);
        delete_event = (Button) rootView.findViewById(R.id.delete_event);

        mDatabase.child(Utils.EVENT_DATABASE).child(event_id).child("creator").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String event_creator = dataSnapshot.getValue(String.class);
                    if(auth.getCurrentUser().getDisplayName().equals(event_creator)) {
                        edit_event.setVisibility(View.VISIBLE);
                        edit_admin.setVisibility(View.VISIBLE);
                        delete_event.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}
