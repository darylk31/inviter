package invite.hfad.com.inviter;


import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        RecyclerView homeRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_home, container, false);

        UserDatabase db = new UserDatabase();
        LinkedList<Event> events;
        events = db.getEvents();

        HomeAdapter adapter = new HomeAdapter(events);
        homeRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        homeRecycler.setLayoutManager(layoutManager);
        return homeRecycler;
    }
}
