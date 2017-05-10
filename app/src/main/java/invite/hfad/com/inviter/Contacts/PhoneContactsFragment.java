package invite.hfad.com.inviter.Contacts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import invite.hfad.com.inviter.R;

public class PhoneContactsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        RecyclerView PhoneContactsRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_phonecontacts, container, false);
        PhoneContactsAdapter adapter = new PhoneContactsAdapter(getActivity().getApplicationContext());
        PhoneContactsRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        PhoneContactsRecycler.setLayoutManager(layoutManager);
        return PhoneContactsRecycler;
    }
}
