package invite.hfad.com.inviter.Contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.Contact;
import invite.hfad.com.inviter.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.User;
import invite.hfad.com.inviter.UserDatabaseHelper;
import invite.hfad.com.inviter.Usernames;

/**
 * Created by Daryl on 5/11/2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    Cursor cursor;
    SQLiteDatabase db;
    String[] uid;
    String[] username;
    String[] displayname;
    Context context;


    public FriendsAdapter(Context context){
        try {
            SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM FRIENDS ORDER BY USERNAME;", null);
            this.cursor = cursor;
            this.db = db;
            if (cursor == null){
                db.close();}
            else {
                storeFriends();
                cursor.close();
                db.close();
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
            return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final FriendsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialogBox dialogBox = new ProfileDialogBox((ContactsActivity) context, username[holder.getAdapterPosition()] );
                dialogBox.show();
            }
        });
        TextView dname = (TextView) cardView.findViewById(R.id.tvFriendsDisplayName);
        dname.setText(displayname[position]);
        TextView uname = (TextView) cardView.findViewById(R.id.tvFriendsUserName);
        uname.setText(username[position]);
    }

    @Override
    public int getItemCount() {
        if (cursor == null)
        {return 0;}
        else
            return cursor.getCount();
    }


    private void storeFriends(){
        String[] uid = new String[getItemCount()];
        String[] username = new String[getItemCount()];
        String[] display = new String[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            cursor.moveToPosition(i);
            display[i] = cursor.getString(2);
            username[i] = cursor.getString(1);
            uid[i] = cursor.getString(0);
        }
        this.uid = uid;
        this.username = username;
        this.displayname = display;
    }
}

