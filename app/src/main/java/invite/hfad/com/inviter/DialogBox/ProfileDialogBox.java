package invite.hfad.com.inviter.DialogBox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.ChatActivity;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.UserDatabaseHelper;
import invite.hfad.com.inviter.Utils;

/**
 * Created by Jimmy on 6/3/2017.
 */

public class ProfileDialogBox extends Dialog {
    private String username;
    private DatabaseReference mDatabase;
    private User user;
    private Context context;
    TextView profile_displayname;
    CircleImageView profile_picture;
    Button message;
    Button add_friend;
    Button remove_friend;
    LinearLayout button_list;


    public ProfileDialogBox(Context a, String username){
        super(a);
        this.context = a;
        this.username = username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_profile_dialog);
        button_list = (LinearLayout) findViewById(R.id.profile_buttons);
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
                    if (dataSnapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);
                        profile_displayname.setText(user.getDisplayname());
                        if (user.getPhotoUrl() == null) {
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
                        if(FirebaseAuth.getInstance().getCurrentUser().getDisplayName().equals(username)){
                            button_list.setVisibility(View.GONE);

                        } else {
                            checkForContacts();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        buttonClick();
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
                    } else{
                        message.setVisibility(View.GONE);
                        remove_friend.setVisibility(View.GONE);
                        add_friend.setVisibility(View.VISIBLE);
                        add_friend.setText("Pending Friend Request");
                        add_friend.setEnabled(false);
                    }
                } else{
                    message.setVisibility(View.GONE);
                    remove_friend.setVisibility(View.GONE);
                    add_friend.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void buttonClick(){
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(Utils.USER).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(Utils.CHAT).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String ChatId = dataSnapshot.getValue().toString();
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("chat_id", ChatId);
                            intent.putExtra("username",username);
                            context.startActivity(intent);
                        } else {
                            //Make a new chat thing.
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(Utils.USER).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(Utils.CONTACTS).child(username).setValue(false);
                mDatabase.child(Utils.USER).child(username).child(Utils.INBOX).child(Utils.USER_ADD_REQUEST).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).setValue(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                add_friend.setText("Pending Friend Request");
                add_friend.setEnabled(false);
            }
        });
        remove_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(Utils.USER).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(Utils.CONTACTS).child(username).removeValue();
                mDatabase.child(Utils.USER).child(username).child(Utils.CONTACTS).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).removeValue();
                remove_friend.setEnabled(false);
                UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(context);
                UserDatabaseHelper.delete_friend(userDatabaseHelper.getWritableDatabase(),username);

            }
        });
    }

}
