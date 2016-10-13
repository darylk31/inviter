package invite.hfad.com.inviter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {
    private String[] event_names;
    private String[] event_days;
    private Integer[] event_ids;
    private String[] creator;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public InboxAdapter(LinkedList<Event> invites){
        LinkedList<Event> sortedEvents = sortEvents(invites);
        showEvents(sortedEvents);
    }

    private void showEvents(LinkedList<Event> events) {
        String[] names = new String[events.size()];
        String[] dates = new String[events.size()];
        Integer[] ids = new Integer[events.size()];
        String[] creators = new String[events.size()];
        for (int i = 0; i < events.size(); i++) {
            names[i] = events.get(i).getEvent_name();
            dates[i] = events.get(i).getDay();
            ids[i] = events.get(i).getEventId();
            creators[i] = events.get(i).get_creator();
        }
        this.event_names = names;
        this.event_days = dates;
        this.event_ids = ids;
        this.creator = creators;
    }

    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_item_layout, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(InboxAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView event_name_text = (TextView)cardView.findViewById(R.id.event_name);
        event_name_text.setText(event_names[position]);
        TextView event_day_text = (TextView)cardView.findViewById(R.id.event_day);
        event_day_text.setText(event_days[position]);
        final int id = event_ids[position];
        TextView invited_by = (TextView)cardView.findViewById(R.id.invite_by);
        invited_by.setText("Created By: " + creator[position]);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EventPage.class);
                intent.putExtra("event_id", id);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return event_names.length;
    }

    public LinkedList<Event> sortEvents(LinkedList<Event> events) {
        //...sort in date order
        return events;
    }


}
