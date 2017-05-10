package invite.hfad.com.inviter.Contacts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import invite.hfad.com.inviter.R;

/**
 * Created by Daryl on 5/9/2017.
 */

public class SearchContactsAdapter extends RecyclerView.Adapter<SearchContactsAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public SearchContactsAdapter(Context context){
    }

    @Override
    public SearchContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(SearchContactsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView contact_name = (TextView) cardView.findViewById(R.id.tvPContactName);
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
