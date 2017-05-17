package invite.hfad.com.inviter.Contacts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.Usernames;

/**
 * Created by Daryl on 5/9/2017.
 */

public class SearchContactsAdapter extends RecyclerView.Adapter<SearchContactsAdapter.ViewHolder> {

    private Usernames firebaseUsername;
    private ArrayList<Usernames> usernames;
    private ArrayList<String> displaynames;
    private ArrayList<String> usernameDisplay;
    HashMap<String,Boolean> contacts;
    HashMap<String,Boolean> addContacts;

    private DatabaseReference mDatabase;
    private FirebaseAuth auth;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public SearchContactsAdapter(Context context,String username){
        auth = FirebaseAuth.getInstance();
        this.usernames = new ArrayList<Usernames>();
        searchDatabase(username);
        if(firebaseUsername == null)
            return;
    }

    @Override
    public SearchContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcontact_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final SearchContactsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView display_name = (TextView) cardView.findViewById(R.id.tvSearchDisplayName);
        TextView user_name = (TextView) cardView.findViewById(R.id.tvSearchUserName);
        display_name.setText(usernames.get(position).getDisplayname());
        user_name.setText(usernames.get(position).getUsername());
        actionTextClick(holder);
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }

    private void searchDatabase(String username){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Usernames").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    firebaseUsername = dataSnapshot.getValue(Usernames.class);
                    usernames.add(firebaseUsername);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void actionTextClick(final SearchContactsAdapter.ViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = holder.getAdapterPosition();
                //base on the position we get their username
                //and then check where they're from
                //and then complete action
                //in this case we add the user to our contacts
                //set the value to true
                //add the ourselves to the user set to false
                addFirebaseUser(usernames.get(i));
                System.out.println(i);
            }
        });
    }

    private void addFirebaseUser(Usernames addUsername){
        contacts =  new HashMap<String,Boolean>();
        contacts.put(addUsername.getUid(),true);
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("contacts").setValue(contacts);

        addContacts = new HashMap<String,Boolean>();
        addContacts.put(auth.getCurrentUser().getUid(),false);
        mDatabase.child("Users").child(addUsername.getUid()).child("inbox").setValue(addContacts);
    }




}
