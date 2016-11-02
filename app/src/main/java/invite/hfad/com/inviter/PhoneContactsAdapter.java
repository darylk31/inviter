package invite.hfad.com.inviter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daryl on 10/30/2016.
 */
public class PhoneContactsAdapter extends BaseAdapter {
    Context mContext;
    List<PhoneContactsFragment.PhoneContact> phoneContacts;

    public PhoneContactsAdapter (Context mContext, List<PhoneContactsFragment.PhoneContact> mContact){
        this.mContext = mContext;
        this.phoneContacts = mContact;
    }
    @Override
    public int getCount() {
        return phoneContacts.size();
    }

    @Override
    public Object getItem(int position) {
        return phoneContacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.contacts_list_item, null);
        TextView contactname = (TextView)view.findViewById(R.id.tvContactName);
        TextView contactnumber = (TextView)view.findViewById(R.id.tvContactNumber);

        contactname.setText(phoneContacts.get(position).contact_name);
        contactnumber.setText(phoneContacts.get(position).contact_number);

        view.setTag(phoneContacts.get(position).contact_name);
        return view;
    }
}
