package invite.hfad.com.inviter;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Daryl on 8/1/2017.
 */

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private String[] event_names;
    private String[] event_times;
    private String[] event_ids;
    private Cursor cursor;

    public CalendarAdapter(Cursor cursor){
        this.cursor = cursor;
        storeEvents();

    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CalendarAdapter.ViewHolder holder, final int position) {
        final CardView cardView = holder.cardView;
        TextView event_name = (TextView)cardView.findViewById(R.id.calendar_nameTV);
        TextView event_time = (TextView)cardView.findViewById(R.id.calendar_timeTV);
        event_name.setText(event_names[position]);
        //TODO: get time.
        DateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        try {
            Date date = format.parse(event_times[position]);
            event_time.setText(date.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventViewPager.class);
                intent.putExtra("event_id", event_ids[position]);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (cursor == null){
            return 0;
        }
        return cursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public void storeEvents() {
        String[] names = new String[getItemCount()];
        String[] times = new String[getItemCount()];
        String[] ids = new String[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            cursor.moveToPosition(i);
            names[i] = cursor.getString(4);
            times[i] = cursor.getString(2);
            ids[i] = cursor.getString(0);
        }
        this.event_names = names;
        this.event_times = times;
        this.event_ids = ids;
    }
}
