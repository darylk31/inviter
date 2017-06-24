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
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        FriendsAdapter adapter = new FriendsAdapter(getContext());
        friendsrecycler.setAdapter(adapter);
        friendsrecycler.setLayoutManager(manager);
        return friendsrecycler;
    }
}
