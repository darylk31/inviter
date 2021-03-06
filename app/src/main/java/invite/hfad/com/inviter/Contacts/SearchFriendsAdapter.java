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
import android.widget.TextView;

import invite.hfad.com.inviter.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserDatabaseHelper;

/**
 * Created by Daryl on 6/14/2017.
 */

public class SearchFriendsAdapter extends RecyclerView.Adapter<SearchFriendsAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;
    private SQLiteDatabase db;
    String[] uid;
    String[] username;
    String[] displayname;
    String[] profile;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public SearchFriendsAdapter(Context context, String name){
        this.context = context;
        SQLiteOpenHelper helper = new UserDatabaseHelper(context);
        this.db = helper.getReadableDatabase();
        String query_string = "SELECT * FROM FRIENDS WHERE USERNAME LIKE '" + name + "%'" +
                " OR DISPLAY LIKE '" + name + "%'";
        try{
            this.cursor = db.rawQuery(query_string, null);
            storeFriends();}
        catch (SQLiteException e) {}
    }

    @Override
    public SearchFriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final SearchFriendsAdapter.ViewHolder holder, int position) {
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
        String[] profile = new String[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            cursor.moveToPosition(i);
            profile[i] = cursor.getString(3);
            display[i] = cursor.getString(2);
            username[i] = cursor.getString(1);
            uid[i] = cursor.getString(0);
        }
        this.uid = uid;
        this.username = username;
        this.displayname = display;
        this.profile = profile;
    }


}
