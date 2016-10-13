package invite.hfad.com.inviter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daryl on 9/21/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private String[] event_names;
    private String[] event_days;
    private Integer[] event_ids;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public HomeAdapter(LinkedList<Event> events) {
        LinkedList<Event> sortedEvents = sortEvents(events);
        showEvents(sortedEvents);
    }


    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView event_name_text = (TextView)cardView.findViewById(R.id.event_name);
        event_name_text.setText(event_names[position]);
        TextView event_day_text = (TextView)cardView.findViewById(R.id.event_day);
        event_day_text.setText(event_days[position]);
        final int id = event_ids[position];
        //...only if unread messages
        ImageView notification = (ImageView)cardView.findViewById(R.id.notification);
        notification.setImageResource(R.drawable.chat_24dp);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventPage.class);
                intent.putExtra("event_id", id);
                v.getContext().startActivity(intent);
            }
        });
        };

    @Override
    public int getItemCount() {
        return event_names.length;
    }


    public void showEvents(List<Event> events) {
        String[] names = new String[events.size()];
        String[] dates = new String[events.size()];
        Integer[] ids = new Integer[events.size()];
        for (int i = 0; i < events.size(); i++) {
            names[i] = events.get(i).getEvent_name();
            dates[i] = events.get(i).getDay();
            ids[i] = events.get(i).getEventId();
        }
        this.event_names = names;
        this.event_days = dates;
        this.event_ids = ids;
    }


    public LinkedList<Event> sortEvents(LinkedList<Event> events) {
        //...sort in date order
        return events;
    }
}
