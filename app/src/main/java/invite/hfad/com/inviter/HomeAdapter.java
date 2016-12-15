package invite.hfad.com.inviter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daryl on 9/21/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {


    private String[] event_names;
    private String[] event_days;
    private String[] event_ids;
    private Cursor cursor;
    private SQLiteDatabase event_db;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public HomeAdapter(Context context){
        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(context);
            SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
            Cursor cursor = event_db.rawQuery("SELECT * FROM " + "EVENTS" + " ORDER BY date(" + "DAY" + ") ASC", null);
            this.event_db = event_db;
            this.cursor = cursor;

        } catch (SQLiteException e){
            e.printStackTrace();
            Toast toast = Toast.makeText(context, "Error: Database unavailable", Toast.LENGTH_SHORT);
            toast.show();}

        storeEvents();
        cursor.close();
        event_db.close();
    }


    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_layout, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;
        TextView event_name_text = (TextView) cardView.findViewById(R.id.event_name);
        TextView event_month_text = (TextView) cardView.findViewById(R.id.event_month);
        TextView event_day_text = (TextView) cardView.findViewById(R.id.event_day);
        event_name_text.setText(event_names[position]);
        event_day_text.setText(event_days[position]);
        event_month_text.setText(event_days[position]);
        /*
        //...only if unread messages
        ImageView notification = (ImageView)cardView.findViewById(R.id.notification);
        notification.setImageResource(R.drawable.chat_24dp);
        */

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventPage.class);
                intent.putExtra("event_id", event_ids[position]);
                v.getContext().startActivity(intent);
            }
        });
    }





    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    public void storeEvents() {
        String[] names = new String[getItemCount()];
        String[] dates = new String[getItemCount()];
        String[] ids = new String[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            cursor.moveToPosition(i);
            names[i] = cursor.getString(2);
            dates[i] = cursor.getString(1);
            ids[i] =  cursor.getString(0);
        }
        this.event_names = names;
        this.event_days = dates;
        this.event_ids = ids;
    }
}
