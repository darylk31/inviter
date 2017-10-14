package invite.hfad.com.inviter.EventObjectModel;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import invite.hfad.com.inviter.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventEditAdmin extends Fragment {


    public EventEditAdmin() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_edit_admin, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

    }
}
