package invite.hfad.com.inviter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import invite.hfad.com.inviter.EventObjectModel.EventViewPager;

/**
 * Created by Daryl on 9/21/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {


    private List<String> event_names;
    private List<String> event_days;
    private List<String> event_ids;
    private Cursor cursor;
    private Date date;
    private SQLiteDatabase event_db;
    final int TYPE_HEADER = 0;
    final int TYPE_EVENT = 1;
    private Context context;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public HomeAdapter(Context context, Boolean newEvents) {
        this.context = context;
        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(context);
            SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
            if (newEvents) {
                Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE DAY >= date('now', 'localtime') ORDER BY DAY ASC", null);
                this.event_db = event_db;
                this.cursor = cursor;
                storeEvents();
                cursor.close();
                event_db.close();
            } else {
                Cursor cursor = event_db.rawQuery("SELECT * FROM " + "EVENTS " + "WHERE DAY < date('now','localtime') " + "ORDER BY date(" + "DAY" + ") ASC", null);
                this.event_db = event_db;
                this.cursor = cursor;
                storeEvents();
                cursor.close();
                event_db.close();
            }

        } catch (SQLiteException e) {
        }
    }


    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                CardView header_cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.month_header_layout, parent, false);
                return new ViewHolder(header_cv);


            case TYPE_EVENT:
                CardView event_cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_layout, parent, false);
                return new ViewHolder(event_cv);
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        switch (getItemViewType(position)) {

            case TYPE_HEADER:
                TextView month_header_text = (TextView) cardView.findViewById(R.id.tv_homeMonth);
                String header_month = "";
                try {
                    date = new SimpleDateFormat("yyyy-MM").parse(event_days.get(position));
                    header_month = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                    month_header_text.setText(header_month);
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, month_color(header_month)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case TYPE_EVENT:
                TextView event_name_text = (TextView) cardView.findViewById(R.id.event_name);
                TextView event_day_text = (TextView) cardView.findViewById(R.id.event_day);
                TextView event_dayOfWeek_text = (TextView) cardView.findViewById(R.id.event_dayOfWeek);
                TextView event_time_text = (TextView) cardView.findViewById(R.id.event_time);
                event_name_text.setText(event_names.get(position));
                String event_day = event_days.get(position);
                String output_day = "";
                String output_dayOfWeek = "";
                String output_time = "";
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event_day);
                    output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                    output_dayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(date);
                    output_time = new SimpleDateFormat("KK:mm a", Locale.ENGLISH).format(date);
                    if (output_time.equals("00:00 AM")) {
                        output_time = null;
                    }
                } catch (ParseException e) {}

                event_day_text.setText(output_day);
                event_dayOfWeek_text.setText(output_dayOfWeek);
                event_time_text.setText(output_time);

        /*
        //...only if unread messages
        ImageView notification = (ImageView)cardView.findViewById(R.id.notification);
        notification.setImageResource(R.drawable.chat_24dp);
        */

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), EventViewPager.class);
                        intent.putExtra("event_id", event_ids.get(position));
                        v.getContext().startActivity(intent);
                    }
                });
            }
        }



    @Override
    public int getItemCount() {
        if (cursor == null) {
            return 0;
        }
        return event_names.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (event_names.get(position) == null) {
            return TYPE_HEADER;
        } else return TYPE_EVENT;
    }


    public void storeEvents() {
        List<String> names = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        if (cursor.moveToFirst()) {
            String headerMonth = cursor.getString(2).substring(0, 7);
            names.add(null);
            dates.add(headerMonth);
            ids.add(null);
            while (!cursor.isAfterLast()){
                if (!cursor.getString(2).substring(0, 7).equals(headerMonth)) {
                    headerMonth = cursor.getString(2).substring(0, 7);
                    names.add(null);
                    dates.add(headerMonth);
                    ids.add(null);
                } else{
                names.add(cursor.getString(4));
                dates.add(cursor.getString(2));
                ids.add(cursor.getString(0));
                cursor.moveToNext();}
            }
        }
        this.event_names = names;
        this.event_days = dates;
        this.event_ids = ids;
    }

    public int month_color(String month){
        switch (month){
            case "January":
                return R.color.colorJanuary;

            case "February":
                return R.color.colorFebruary;
            case "March":
                return R.color.colorMarch;
            case "April":
                return R.color.colorApril;
            case "May":
                return R.color.colorMay;
            case "June":
                return R.color.colorJune;
            case "July":
                return R.color.colorJuly;
            case "August":
                return R.color.colorAugust;
            case "September":
                return R.color.colorSeptember;
            case "October":
                return R.color.colorOctober;
            case "November":
                return R.color.colorNovember;
            case "December":
                return R.color.colorDecember;
        }
        return 0;
    }
}
