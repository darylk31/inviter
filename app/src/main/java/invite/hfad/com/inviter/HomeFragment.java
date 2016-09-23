package invite.hfad.com.inviter;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;


public class HomeFragment extends Fragment {

    String[] eventNames;
    String[] eventDays;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        RecyclerView homeRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_home, container, false);


        String[] eventNames = new String[]{"testing", "testing2"};
        String[] eventDays = new String[]{"Oct 01", "Oct 02"};

        HomeAdapter adapter = new HomeAdapter(eventNames, eventDays);
        homeRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        homeRecycler.setLayoutManager(layoutManager);
        return homeRecycler;
    }
}
