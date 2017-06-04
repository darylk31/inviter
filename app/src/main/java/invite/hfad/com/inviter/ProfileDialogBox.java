package invite.hfad.com.inviter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Jimmy on 6/3/2017.
 */

public class ProfileDialogBox extends Dialog implements android.view.View.OnClickListener{
    private String username;
    private DatabaseReference mDatabase;


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
        final TextView profile_displayname = (TextView)findViewById(R.id.profile_displayname);
        final CircleImageView profile_picture = (CircleImageView)findViewById(R.id.profile_image);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Usernames username = dataSnapshot.getValue(Usernames.class);
                    profile_displayname.setText(username.getDisplayname());
                    /*
                    //Need to add default profile pictures
                    Glide.with(getOwnerActivity())
                            .load(username.getPhotoUrl())
                            .into(profile_picture);
                            */
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
