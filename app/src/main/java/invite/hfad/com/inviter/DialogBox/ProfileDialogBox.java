package invite.hfad.com.inviter.DialogBox;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.Utils;

/**
 * Created by Jimmy on 6/3/2017.
 */

public class ProfileDialogBox extends Dialog implements android.view.View.OnClickListener{
    private String username;
    private DatabaseReference mDatabase;
    private User user;
    TextView profile_displayname;
    CircleImageView profile_picture;
    Button message;
    Button add_friend;
    Button remove_friend;


    public ProfileDialogBox(Activity a,String username){
        super(a);
        this.username = username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_profile_dialog);
        TextView profile_username = (TextView)findViewById(R.id.profile_username);
        profile_username.setText(username);
        profile_displayname = (TextView)findViewById(R.id.profile_displayname);
        profile_picture = (CircleImageView)findViewById(R.id.profile_image);
        message = (Button) findViewById(R.id.message_user);
        add_friend = (Button) findViewById(R.id.add_user);
        remove_friend = (Button) findViewById(R.id.unfriend_user);

        mDatabase = Utils.getDatabase().getReference();
        mDatabase.child(Utils.USER).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    user = dataSnapshot.getValue(User.class);
                    profile_displayname.setText(user.getDisplayname());
                    if (user.getPhotoUrl() == null){
                        Glide.with(getContext())
                                .load(R.drawable.profile_image)
                                .into(profile_picture);
                    }
                    //Need to add default profile pictures
                    else {
                        Glide.with(getContext())
                                .load(user.getPhotoUrl())
                                .into(profile_picture);
                    }
                    checkForContacts();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkForContacts(){
        mDatabase.child(Utils.USER).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(Utils.CONTACTS).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getValue().equals(true)){
                        message.setVisibility(View.VISIBLE);
                        remove_friend.setVisibility(View.VISIBLE);
                        add_friend.setVisibility(View.GONE);
                        System.out.println(dataSnapshot.getValue() + " true " + username);
                    } else{
                        message.setVisibility(View.GONE);
                        remove_friend.setVisibility(View.GONE);
                        add_friend.setVisibility(View.VISIBLE);
                        add_friend.setClickable(false);
                        System.out.println(dataSnapshot.getValue() + " false " +username);
                    }
                } else{
                    message.setVisibility(View.GONE);
                    remove_friend.setVisibility(View.GONE);
                    add_friend.setVisibility(View.VISIBLE);
                    System.out.println("non exisiting");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            default:
                break;
        }
        dismiss();
    }
}
