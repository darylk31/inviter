package invite.hfad.com.inviter;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InboxFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView inboxRecycler = (RecyclerView) inflater.inflate(R.layout.fragment_inbox, container, false);
        /*
        UserDatabaseHelper db = new UserDatabaseHelper();
        LinkedList<Event> invites;
        invites = db.getInvites();

        InboxAdapter adapter = new InboxAdapter(invites);
        inboxRecycler.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        inboxRecycler.setLayoutManager(layoutManager);
        */
        return inboxRecycler;


    }

}
