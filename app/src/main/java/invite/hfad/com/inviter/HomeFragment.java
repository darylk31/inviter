package invite.hfad.com.inviter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;


public class HomeFragment extends Fragment {

    HomeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        RecyclerView homeRecycler = (RecyclerView) getView().findViewById(R.id.home_recycler);
        adapter = new HomeAdapter(getActivity().getApplicationContext(), true);
        homeRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        homeRecycler.setLayoutManager(layoutManager);
        if (adapter.getItemCount() == 0){
            TextView message = (TextView) getView().findViewById(R.id.tv_emptyHome);
            message.setText("You have no upcoming events.");
        }
    }
}


