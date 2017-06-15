package invite.hfad.com.inviter.EventObjectModel;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import invite.hfad.com.inviter.Contacts.ContactsActivity;
import invite.hfad.com.inviter.Contacts.FriendsAdapter;
import invite.hfad.com.inviter.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserDatabaseHelper;

/**
 * Created by Daryl on 6/14/2017.
 */

public class SelectContactsAdapter extends RecyclerView.Adapter<SelectContactsAdapter.ViewHolder> {

    Cursor cursor;
    SQLiteDatabase db;
    String[] uid;
    String[] username;
    String[] displayname;
    String[] profile;
    Context context;
    ArrayList<String> invited;


    public SelectContactsAdapter(Context context){
        try {
            this.context = context;
            SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM FRIENDS ORDER BY USERNAME;", null);
            this.cursor = cursor;
            this.db = db;
            if (cursor == null){
                Toast toast = Toast.makeText(context, "You have no friends, get including!", Toast.LENGTH_SHORT);
                toast.show();
                db.close();}
            else {
                storeFriends();
                cursor.close();
                db.close();
                invited = new ArrayList<>();

            }


        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(context, "Error loading your friends, please try again!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public SelectContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.selectfriends_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final SelectContactsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        final ImageView selected = (ImageView) cardView.findViewById(R.id.ivSelected);
        selected.setVisibility(View.INVISIBLE);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.getVisibility() == View.INVISIBLE){
                    selected.setVisibility(View.VISIBLE);
                    invited.add(uid[holder.getAdapterPosition()]);
                }
                else {
                    selected.setVisibility(View.INVISIBLE);
                    invited.remove(uid[holder.getAdapterPosition()]);
                }
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

    public ArrayList<String> getArrayList(){
        return invited;
    }
}
