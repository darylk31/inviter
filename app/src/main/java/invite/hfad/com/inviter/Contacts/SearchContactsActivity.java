package invite.hfad.com.inviter.Contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;

import invite.hfad.com.inviter.R;

public class SearchContactsActivity extends AppCompatActivity {

    private SearchView search;
    private RecyclerView search_recycler;
    private SearchContactsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        search = (SearchView) findViewById(R.id.searchview_contacts);
        search.onActionViewExpanded();
        search_recycler = (RecyclerView) findViewById(R.id.searchcontacts_recycler);
        adapter = new SearchContactsAdapter(getApplicationContext());
        search_recycler.setAdapter(adapter);
    }





}
