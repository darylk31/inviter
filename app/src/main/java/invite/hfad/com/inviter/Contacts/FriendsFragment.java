package invite.hfad.com.inviter.Contacts;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.UserDatabaseHelper;

public class FriendsFragment extends Fragment {

    SQLiteDatabase db;
    RecyclerView friendsrecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        friendsrecycler = (RecyclerView) inflater.inflate(R.layout.fragment_friends, container, false);
        updateFriends();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        friendsrecycler.setLayoutManager(manager);
        return friendsrecycler;
    }

    private void updateFriends(){
        //code to check timestamp
        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getContext());
        db = databaseHelper.getWritableDatabase();
        db.execSQL("DELETE FROM FRIENDS;");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        mDatabase.child("Users").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                storeUser(user);
                                FriendsAdapter adapter = new FriendsAdapter(getContext());
                                friendsrecycler.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void storeUser(User user){
        ContentValues userinfo = new ContentValues();
        userinfo.put("UID", user.getUid());
        userinfo.put("USERNAME", user.getUsername());
        userinfo.put("DISPLAY", user.getDisplayname());
        userinfo.put("PHOTO", user.getPhototUrl());
        db.insert("FRIENDS", null, userinfo);
    }

}
