package invite.hfad.com.inviter.EventObjectModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserDatabaseHelper;

/**
 * Created by Daryl on 6/14/2017.
 */

public class EventSelectContactsAdapter extends RecyclerView.Adapter<EventSelectContactsAdapter.ViewHolder> {

    Cursor cursor;
    SQLiteDatabase db;
    String[] username;
    String[] displayname;
    Context context;
    ArrayList<String> invited;
    TextView selected_list;
    ArrayList<String> selected_names;
    String names;



    public EventSelectContactsAdapter(Context context, TextView list){
        try {
            this.context = context;
            SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM FRIENDS WHERE ACCEPT=1 ORDER BY LOWER(DISPLAY);", null);
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
            this.selected_list = list;
            list.setText("Just Me");
            selected_names = new ArrayList<>();

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
    public EventSelectContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.selectfriends_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final EventSelectContactsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        final CheckBox checkBox = cardView.findViewById(R.id.selectfriend_checkbox);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()){
                    checkBox.setChecked(true);
                    invited.add(username[holder.getAdapterPosition()]);
                    selected_names.add(username[holder.getAdapterPosition()]);
                    names = TextUtils.join(", ", Arrays.asList(selected_names));
                    names = names.substring(1, names.length()-1);
                    selected_list.setText(names);
                }
                else {
                    checkBox.setChecked(false);
                    invited.remove(username[holder.getAdapterPosition()]);
                    selected_names.remove(username[holder.getAdapterPosition()]);
                    if (selected_names.isEmpty()){
                        selected_list.setText("Just Me");
                    }
                    else {
                        names = TextUtils.join(", ", Arrays.asList(selected_names));
                        names = names.substring(1, names.length() - 1);
                        selected_list.setText(names);
                    }
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
        String[] username = new String[getItemCount()];
        String[] display = new String[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            cursor.moveToPosition(i);
            display[i] = cursor.getString(1);
            username[i] = cursor.getString(0);
        }
        this.username = username;
        this.displayname = display;
    }

    public List<String> getArrayList(){
        return invited;
    }
}
