package invite.hfad.com.inviter.Contacts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.Contact;
import invite.hfad.com.inviter.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.Usernames;

/**
 * Created by Daryl on 5/9/2017.
 */

public class SearchUsernameAdapter extends RecyclerView.Adapter<SearchUsernameAdapter.ViewHolder> {

    private Usernames firebaseUsername;
    private ArrayList<Usernames> usernameList;


    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public SearchUsernameAdapter(Context context, ArrayList<Usernames> usernameList){
        this.mContext = context;
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.usernameList = usernameList;
        if(firebaseUsername == null)
            return;

    }

    @Override
    public SearchUsernameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcontact_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final SearchUsernameAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        final Button addSearchContactButton = (Button) cardView.findViewById(R.id.bAddSearchContact);
        addSearchContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = holder.getAdapterPosition();
                //base on the position we get their username
                //and then check where they're from
                //and then complete action
                //in this case we add the user to our contacts
                //set the value to true
                //add the ourselves to the user set to false
                System.out.println(usernameList.get(i).getUsername());
                addSearchContactButton.setText("Added");
                addSearchContactButton.setEnabled(false);
                addFirebaseUser(usernameList.get(i));
            }
        });
        TextView display_name = (TextView) cardView.findViewById(R.id.tvSearchDisplayName);
        TextView user_name = (TextView) cardView.findViewById(R.id.tvSearchUserName);
        display_name.setText(usernameList.get(position).getDisplayname());
        user_name.setText(usernameList.get(position).getUsername());
        cardViewClick(holder);
    }

    @Override
    public int getItemCount() {
        return usernameList.size();
    }


    private void cardViewClick(final SearchUsernameAdapter.ViewHolder holder){
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = holder.getAdapterPosition();
                if(mContext instanceof SearchContactsActivity){
                    System.out.println(usernameList.get(i).getUid());
                    ProfileDialogBox profileDialogBox = new ProfileDialogBox((SearchContactsActivity) mContext,usernameList.get(i).getUsername());
                    profileDialogBox.show();
                }
            }
        });
    }

    private void addFirebaseUser(final Usernames addUsername){
      mDatabase.child("Users").child(addUsername.getUid()).child("Inbox").child("Add_Request").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Contacts").child(addUsername.getUid()).setValue(addUsername.getDisplayname());
                    mDatabase.child("Users").child(addUsername.getUid()).child("Inbox").child("Add_Request").child(auth.getCurrentUser().getUid()).setValue(auth.getCurrentUser().getDisplayName());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        }


}
