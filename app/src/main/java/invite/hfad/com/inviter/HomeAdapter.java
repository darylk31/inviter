package invite.hfad.com.inviter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.ViewGroup;
import android.support.v7.widget.CardView;

/**
 * Created by Daryl on 9/21/2016.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private String[] event_names;
    private String[] event_days;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public HomeAdapter(String[] event_names, String[] event_days) {
        this.event_names = event_names;
        this.event_days = event_days;

    }


    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView event_name_text = (TextView)cardView.findViewById(R.id.event_name);
        event_name_text.setText(event_names[position]);
        TextView event_day_text = (TextView)cardView.findViewById(R.id.event_day);
        event_day_text.setText(event_days[position]);

    }

    @Override
    public int getItemCount() {
        return event_names.length;
    }
}
