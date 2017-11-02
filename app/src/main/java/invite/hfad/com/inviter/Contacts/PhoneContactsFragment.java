package invite.hfad.com.inviter.Contacts;

import android.Manifest;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;

import invite.hfad.com.inviter.R;

public class PhoneContactsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phonecontacts, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        PhoneContactsAdapter adapter = new PhoneContactsAdapter(getContext());
        if (adapter.getItemCount() == 0) {
            TextView permission_message = getView().findViewById(R.id.tv_phoneContacts);
            permission_message.setText("Please enable permission to access phone contacts.");
        } else {
            RecyclerView PhoneContactsRecycler = getView().findViewById(R.id.phoneContacts_recycler);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            PhoneContactsRecycler.setLayoutManager(layoutManager);
            PhoneContactsRecycler.setAdapter(adapter);
        }
    }
    }

