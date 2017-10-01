package invite.hfad.com.inviter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import invite.hfad.com.inviter.EventObjectModel.EventViewPager;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Daryl on 9/21/2016.
 */
public class HomeNotificationAdapter extends RecyclerView.Adapter<HomeNotificationAdapter.ViewHolder> {


    private List<String> event_names;
    private List<String> event_days;
    private ArrayList<Event> event_ids;
    private Cursor cursor;
    private Date date;
    private SQLiteDatabase event_db;
    final int TYPE_HEADER = 0;
    final int TYPE_EVENT = 1;
    private Context context;

    private DatabaseReference mDatabaseReference;
    public SharedPreferences sharedPref;
    private User user;


    TextView event_name_text;
    TextView event_day_text;
    TextView event_dayOfWeek_text;
    TextView event_time_text;
    TextView event_message_text;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public HomeNotificationAdapter(Context context, Boolean newEvents,ArrayList<Event> event_ids) {
        mDatabaseReference = Utils.getDatabase().getReference();
        this.context = context;
        this.event_ids = event_ids;
        for(Event e: event_ids){
            System.out.println(e.getEvent_name());
        }
    }


    @Override
    public HomeNotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView event_cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.home_notification_item_layout, parent, false);
        return new ViewHolder(event_cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        event_name_text = (TextView) cardView.findViewById(R.id.event_name);
        event_day_text = (TextView) cardView.findViewById(R.id.event_day);
        event_dayOfWeek_text = (TextView) cardView.findViewById(R.id.event_dayOfWeek);
        event_message_text = (TextView) cardView.findViewById(R.id.event_last_message);
        Event event = event_ids.get(position);
        if(event.getEvent_name() == null){
            event_name_text.setText("huh");
        } else {
            event_name_text.setText(event.getEvent_name());
        }
        String output_day = "";
        String output_dayOfWeek = "";
        String output_time = "";
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(event.getStartDate());
            output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
            output_dayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(date);
            output_time = new SimpleDateFormat("KK:mm a", Locale.ENGLISH).format(date);
        } catch (ParseException e) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(event.getStartDate());
                output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                output_dayOfWeek = new SimpleDateFormat("EEE", Locale.ENGLISH).format(date);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }
        event_day_text.setText(output_day);
        event_dayOfWeek_text.setText(output_dayOfWeek);
        event_message_text.setText("test");
/*
        mDatabaseReference.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.CHAT).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    FriendlyMessage m = dataSnapshot.getValue(FriendlyMessage.class);
                    event_message_text.setText(m.getText());
                    notifyDataSetChanged();
                    System.out.println(dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        */

        /*
        //...only if unread messages
        ImageView notification = (ImageView)cardView.findViewById(R.id.notification);
        notification.setImageResource(R.drawable.chat_24dp);
        */

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), EventViewPager.class);
                        intent.putExtra("event_id", event_ids.get(position).getEventId());
                        v.getContext().startActivity(intent);
                    }
                });
        }

    public void updateEventList(ArrayList<Event> eventlist){
        this.event_ids = eventlist;
    }

    @Override
    public int getItemCount() {
        return event_ids.size();
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
