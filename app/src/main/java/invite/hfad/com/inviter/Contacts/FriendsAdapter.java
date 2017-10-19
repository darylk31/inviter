package invite.hfad.com.inviter.Contacts;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import invite.hfad.com.inviter.DialogBox.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserDatabaseHelper;

/**
 * Created by Daryl on 5/11/2017.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    Cursor cursor;
    SQLiteDatabase db;
    String[] username;
    String[] displayname;
    int[] accept;
    Context context;


    public FriendsAdapter(Context context){
        try {
            SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM FRIENDS WHERE ACCEPT=1 ORDER BY LOWER(DISPLAY);", null);
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
        if (accept[position] == 0){
            TextView uname = (TextView) cardView.findViewById(R.id.tvFriendsDisplayName);
            uname.setText(username[position] + " (Pending)");
        }
        else {
        TextView dname = (TextView) cardView.findViewById(R.id.tvFriendsDisplayName);
        dname.setText(displayname[position]);
        TextView uname = (TextView) cardView.findViewById(R.id.tvFriendsUserName);
        uname.setText(username[position]);}
    }

    @Override
    public int getItemCount() {
        if (cursor == null)
        {return 0;}
        else
            return cursor.getCount();
    }


    private void storeFriends(){
        String[] username = new String[getItemCount()];
        String[] display = new String[getItemCount()];
        int[] accept = new int[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            cursor.moveToPosition(i);
            accept[i] = cursor.getInt(2);
            display[i] = cursor.getString(1);
            username[i] = cursor.getString(0);
        }
        this.username = username;
        this.displayname = display;
        this.accept = accept;
    }
}

