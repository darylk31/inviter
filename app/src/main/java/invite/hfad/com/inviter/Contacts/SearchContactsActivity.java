package invite.hfad.com.inviter.Contacts;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
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

import java.util.ArrayList;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.Usernames;

public class SearchContactsActivity extends AppCompatActivity {

    private SearchView search;
    private RecyclerView search_recycler;
    private SearchContactsAdapter adapter;

    private DatabaseReference mDatabase;

    private Usernames usernameMatch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        search = (SearchView) findViewById(R.id.searchview_contacts);
        search.onActionViewExpanded();

        search_recycler = (RecyclerView) findViewById(R.id.searchcontacts_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        search_recycler.setLayoutManager(mLayoutManager);
        //adapter = new SearchContactsAdapter(getApplicationContext(),"");
        //search_recycler.setAdapter(adapter);

       // mDatabase = FirebaseDatabase.getInstance().getReference();

        getUsernames();
    }

    private void getUsernames(){
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.i("well", " this worked");;
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }


            public void callSearch(String query){
                final String n = query;

                        adapter = new SearchContactsAdapter(getApplicationContext(),n);
                        Toast.makeText(SearchContactsActivity.this,"TEST",Toast.LENGTH_SHORT).show();
                search_recycler.setAdapter(adapter);

            }
        });
    }

    private void getFirebaseUsernames(String username){
        mDatabase.child("Usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    usernameMatch = dataSnapshot.getValue(Usernames.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }




}
