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
import android.widget.TextView;

import invite.hfad.com.inviter.R;

public class FriendsFragment extends Fragment {

    SQLiteDatabase db;
    RecyclerView friendsrecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        FriendsAdapter adapter = new FriendsAdapter(getContext());
        if (adapter.getItemCount() == 0){
            TextView tv_friends = (TextView) getView().findViewById(R.id.tv_friends);
            tv_friends.setText("You have no friends, get inviting!");
        }
        else {
            friendsrecycler = (RecyclerView) getView().findViewById(R.id.friends_recycler);
            RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
            friendsrecycler.setAdapter(adapter);
            friendsrecycler.setLayoutManager(manager);
        }
    }
}
