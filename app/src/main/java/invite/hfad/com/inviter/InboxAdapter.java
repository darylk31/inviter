package invite.hfad.com.inviter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public InboxAdapter(){
        //retrieve all inbox items and store as array
    }

    @Override
    public InboxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_item_layout, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(InboxAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView name = (TextView) cardView.findViewById(R.id.event_name);
        name.setText("Please include me!");
        TextView invited_by = (TextView) cardView.findViewById(R.id.invite_by);
        //invited_by populated by arraylist of names.
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        //return arraylist size.
        return 0;
    }
}
