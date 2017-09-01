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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserDatabaseHelper;

/**
 * Created by Daryl on 6/14/2017.
 */

public class EditSelectContactsAdapter extends RecyclerView.Adapter<EditSelectContactsAdapter.ViewHolder> {

    Cursor cursor;
    SQLiteDatabase db;
    LinkedList<String> username;
    LinkedList<String> displayname;
    Context context;
    ArrayList<String> invited;
    TextView selected_list;
    ArrayList<String> selected_names;
    ArrayList<String> remove_list;



    public EditSelectContactsAdapter(Context context, TextView list, ArrayList<String> remove_list){
        this.remove_list = remove_list;
        try {
            this.context = context;
            SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(context);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM FRIENDS WHERE ACCEPT=1 ORDER BY USERNAME;", null);
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
    public EditSelectContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.selectfriends_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(final EditSelectContactsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        final ImageView selected = (ImageView) cardView.findViewById(R.id.ivSelected);
        selected.setVisibility(View.INVISIBLE);
        final TextView dname = (TextView) cardView.findViewById(R.id.tvFriendsDisplayName);
        final TextView uname = (TextView) cardView.findViewById(R.id.tvFriendsUserName);
        dname.setText(displayname.get(position));
        uname.setText(username.get(position));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected.getVisibility() == View.INVISIBLE){
                    selected.setVisibility(View.VISIBLE);
                    invited.add(uname.getText().toString());
                    selected_names.add(dname.getText().toString());
                    //invited.add(username.get(holder.getAdapterPosition()));
                    //selected_names.add(displayname.get(holder.getAdapterPosition()));
                    selected_list.setText(TextUtils.join(", ", Arrays.asList(selected_names)));
                }
                else {
                    selected.setVisibility(View.INVISIBLE);
                    invited.remove(uname.getText().toString());
                    selected_names.remove(dname.getText().toString());
                    //invited.remove(username[holder.getAdapterPosition()]);
                    //selected_names.remove(displayname[holder.getAdapterPosition()]);
                    if (selected_names.isEmpty()){
                        selected_list.setText("Just Me");
                    }
                    else
                    selected_list.setText(TextUtils.join(", ", Arrays.asList(selected_names)));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (cursor == null)
        {return 0;}
        else
            return username.size();
    }


    private void storeFriends() {
        LinkedList<String> username = new LinkedList<>();
        LinkedList<String> display = new LinkedList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (remove_list.contains(cursor.getString(0))) {
                //already invited, do not need to display.
                remove_list.remove(cursor.getString(0));
            }
            else {
                display.add(cursor.getString(1));
                username.add(cursor.getString(0));
            }
        }
        this.username = username;
        this.displayname = display;
    }

    public List<String> getArrayList(){
        return invited;
    }
}
