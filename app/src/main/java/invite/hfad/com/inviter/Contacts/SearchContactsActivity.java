package invite.hfad.com.inviter.Contacts;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
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
    private SearchUsernameAdapter adapter;

    private Usernames usernameMatch;

    private Usernames firebaseUsername;
    private ArrayList<Usernames> usernameList;
    private ArrayList<Usernames> contactList;


    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    private LinearLayout usernameSearchLayoutWrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_search_contacts);
        search = (SearchView) findViewById(R.id.searchview_contacts);
        search.onActionViewExpanded();

        search_recycler = (RecyclerView) findViewById(R.id.searchcontacts_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        search_recycler.setLayoutManager(mLayoutManager);

        usernameSearchLayoutWrapper = (LinearLayout) findViewById(R.id.searchview_username_wrapper);
        usernameSearchLayoutWrapper.setVisibility(View.GONE);
        //adapter = new SearchUsernameAdapter(getApplicationContext(),"");
        //search_recycler.setAdapter(adapter);


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
                if(query.equals(""))
                    return;
                usernameSearchLayoutWrapper.setVisibility(View.GONE);
                System.out.println("GONE");
                checkFirebaseDatabase(query);
                adapter = new SearchUsernameAdapter(SearchContactsActivity.this, usernameList);
            }
        });
    }

    private void checkFirebaseDatabase(String query){

        //TODO:
        //If they're on my contacts they don't show up
        usernameList = new ArrayList<Usernames>();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Usernames").child(query).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    firebaseUsername = dataSnapshot.getValue(Usernames.class);
                    System.out.println(firebaseUsername.getUid());
                    mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Contacts").child(firebaseUsername.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                usernameList.add(firebaseUsername);
                                System.out.println(usernameList.get(0).getDisplayname());
                                System.out.println("VISIBLE");
                                usernameSearchLayoutWrapper.setVisibility(View.VISIBLE);
                            }
                            search_recycler.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }








}
