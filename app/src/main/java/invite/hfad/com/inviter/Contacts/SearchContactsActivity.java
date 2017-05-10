package invite.hfad.com.inviter.Contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.FirebaseUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.Usernames;

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
        adapter = new SearchContactsAdapter(getApplicationContext(),"6");
        search_recycler.setAdapter(adapter);

        getUsernames();
    }

    private void getUsernames(){
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.i("well", " this worked");

                Toast.makeText(SearchContactsActivity.this,"TEST2",Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.i("well", " this worked");
                /*
                 adapter = new SearchContactsAdapter(getApplicationContext(),newText);
                search_recycler.setAdapter(adapter);
                search_recycler.invalidate();
                */
                Toast.makeText(SearchContactsActivity.this,"TEST",Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }




}
