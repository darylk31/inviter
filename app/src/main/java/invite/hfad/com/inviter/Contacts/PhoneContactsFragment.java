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
import android.widget.Toast;

import invite.hfad.com.inviter.R;

public class PhoneContactsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        RecyclerView PhoneContactsRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_phonecontacts, container, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        PhoneContactsRecycler.setLayoutManager(layoutManager);
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS);
        if (permissionCheck == 1) {
            Toast toast = Toast.makeText(getContext(), "Please enable permission to read phone contacts.", Toast.LENGTH_SHORT);
            toast.show();
            return PhoneContactsRecycler;
        }
        else{
        PhoneContactsAdapter adapter = new PhoneContactsAdapter(getActivity().getApplicationContext());
        PhoneContactsRecycler.setAdapter(adapter);
        return PhoneContactsRecycler;}
    }
}
