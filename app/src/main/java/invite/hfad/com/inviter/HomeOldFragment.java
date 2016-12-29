package invite.hfad.com.inviter;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeOldFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        RecyclerView homeRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_home, container, false);
        HomeAdapter adapter = new HomeAdapter(getActivity().getApplicationContext(), false);
        homeRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        homeRecycler.setLayoutManager(layoutManager);
        return homeRecycler;
    }


}
