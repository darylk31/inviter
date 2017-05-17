package invite.hfad.com.inviter.Contacts;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserDatabaseHelper;

public class FriendsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView friends = (RecyclerView) inflater.inflate(R.layout.fragment_friends, container, false);
        FriendsAdapter adapter = new FriendsAdapter(getContext());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        friends.setLayoutManager(manager);
        friends.setAdapter(adapter);
        return friends;
    }

}
