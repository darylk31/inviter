package invite.hfad.com.inviter.EventObjectModel;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.UserDatabaseHelper;
import invite.hfad.com.inviter.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventOptionsDialogFragment extends DialogFragment {


    DatabaseReference mDatabase;
    FirebaseAuth auth;
    String event_id;
    private View rootView;

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
        ButtonRoleView();
        edit_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(Utils.EVENT_DATABASE).child(event_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            Event event = dataSnapshot.getValue(Event.class);
                            Intent intent = new Intent(rootView.getContext(),EditEvent.class);
                            intent.putExtra("event",(Parcelable) event);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        edit_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"2",Toast.LENGTH_SHORT).show();
            }
        });
        leave_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =  new AlertDialog.Builder(rootView.getContext());
                builder.setTitle("Leave Event")
                        .setMessage("Are you sure you wish to leave this event?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                mDatabase.child(Utils.EVENT_DATABASE).child(event_id).child(Utils.EVENT_ATTENDEE).child(auth.getCurrentUser().getDisplayName()).removeValue();
                                mDatabase.child(Utils.USER).child(auth.getCurrentUser().getDisplayName()).child(Utils.USER_EVENTS).child(event_id).removeValue();
                                Toast.makeText(rootView.getContext(),"Event Left",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(rootView.getContext(),UserAreaActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        delete_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =  new AlertDialog.Builder(rootView.getContext());
                builder.setTitle("Delete Event")
                .setMessage("Are you sure you wish to delete this event?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                mDatabase.child(Utils.EVENT_DATABASE).child(event_id).removeValue();
                                Toast.makeText(rootView.getContext(),"Sucessfully delete events",Toast.LENGTH_SHORT).show();
                                SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getContext());
                                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                UserDatabaseHelper.delete_event(db, event_id);
                                db.close();
                                Intent intent = new Intent(rootView.getContext(),UserAreaActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });


    }

    private void ButtonRoleView(){

        mDatabase.child(Utils.EVENT_DATABASE).child(event_id).child(Utils.EVENT_ATTENDEE).child(auth.getCurrentUser().getDisplayName()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    if(dataSnapshot.getValue(boolean.class)) {
                        edit_event.setVisibility(View.VISIBLE);
                        delete_event.setVisibility(View.VISIBLE);
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase.child(Utils.EVENT_DATABASE).child(event_id).child("creator").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String event_creator = dataSnapshot.getValue(String.class);
                    if(auth.getCurrentUser().getDisplayName().equals(event_creator)) {
                        edit_admin.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
