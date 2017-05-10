package invite.hfad.com.inviter.Contacts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.Usernames;

/**
 * Created by Daryl on 5/9/2017.
 */

public class SearchContactsAdapter extends RecyclerView.Adapter<SearchContactsAdapter.ViewHolder> {

    private String username;
    private String[] usernames;
    private String[] displaynames;

    private final int MAX_COUNT = 10;



    private FirebaseUser user;
    private DatabaseReference mDatabase;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public SearchContactsAdapter(Context context,String username){
        this.username = username;
        this.displaynames = new String[5];
        this.usernames = new String[5];

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        searchDatabase();
    }

    @Override
    public SearchContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(SearchContactsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView display_name = (TextView) cardView.findViewById(R.id.tvSearchDisplayName);
        TextView user_name = (TextView) cardView.findViewById(R.id.tvSearchUserName);
        display_name.setText(displaynames[position]);
        user_name.setText(usernames[position]);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private void searchDatabase(){
        displaynames[0] = "something";
        usernames[0] = "else";
        /*
        mDatabase.child("Usernames").child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Usernames dataBaseUser = dataSnapshot.getValue(Usernames.class);
                displaynames[0] = dataBaseUser.getUsername();
                usernames[0] = dataBaseUser.getUsername();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        */
    }
}
