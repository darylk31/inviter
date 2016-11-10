package invite.hfad.com.inviter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.CardView;
import java.util.ArrayList;

/**
 * Created by Daryl on 10/30/2016.
 */
public class PhoneContactsAdapter extends RecyclerView.Adapter<PhoneContactsAdapter.ViewHolder>{
    private String[] contacts_names;
    private String[] contact_numbers;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PhoneContactsAdapter(ArrayList<PhoneContactsFragment.PhoneContact> phoneContacts) {
        for (int i = 0; i < phoneContacts.size(); i++) {
            contacts_names[i] = phoneContacts.get(i).getContact_name();
            contact_numbers[i] = phoneContacts.get(i).getContact_number();
        }
    }



    @Override
    public PhoneContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(PhoneContactsAdapter.ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView name = (TextView) cardView.findViewById(R.id.tvPContactName);
        name.setText(contacts_names[position]);
        TextView number = (TextView) cardView.findViewById(R.id.tvPContactNumber);
        number.setText(contact_numbers[position]);
    }

    @Override
    public int getItemCount() {
        return contacts_names.length;
    }
}
