package invite.hfad.com.inviter.Contacts;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.FirebaseUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import invite.hfad.com.inviter.Utils;

public class SearchContactsActivity extends AppCompatActivity {

    private SearchView search;
    private RecyclerView search_recycler;
    private SearchContactsAdapter adapter;

    private Usernames usernameMatch;

    private Usernames firebaseUsername;
    private ArrayList<Usernames> usernameList;


    private DatabaseReference mDatabase;
    private FirebaseAuth auth;




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
        //adapter = new SearchContactsAdapter(getApplicationContext(),"");
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
                checkFirebaseDatabase(query);
                adapter = new SearchContactsAdapter(getApplicationContext(), usernameList);


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
                                System.out.println("added");
                                System.out.println(usernameList.get(0).getDisplayname());
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

    /**

    private void fireBaseTest(String query) {
        if(query.equals(""))
            return;
        mDatabase = Utils.getDatabase().getReference();
        FirebaseRecyclerAdapter<String, ItemViewHolder> adapter = new FirebaseRecyclerAdapter<String, ItemViewHolder>(
                String.class, R.layout.searchcontact_list_item, ItemViewHolder.class, mDatabase.child("Usernames").child(query)) {
            protected void populateViewHolder(final ItemViewHolder viewHolder, String model, int position) {
                String key = this.getRef(position).getKey();
                System.out.println("Key:" + key + " Model: " + model);
                TextView displayname = (TextView) viewHolder.itemView.findViewById(R.id.tvSearchDisplayName);
                displayname.setText(model);
            }
        };
        search_recycler.setAdapter(adapter);


    }
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }
     */






}
