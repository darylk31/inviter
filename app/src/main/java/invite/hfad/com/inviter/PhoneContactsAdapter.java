package invite.hfad.com.inviter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Daryl on 4/7/2017.
 */

public class PhoneContactsAdapter extends RecyclerView.Adapter<PhoneContactsAdapter.ViewHolder> {

    private String[] phoneNumbers;
    private String[] names;
    private Cursor cursor;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PhoneContactsAdapter(Context context) {
        String filter = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + " > 0 and " +
                ContactsContract.CommonDataKinds.Phone.TYPE + "=" + ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        try {
            ContentResolver cr = context.getContentResolver();
            this.cursor = cr.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER},
                    filter,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY + " ASC");
            phoneNumbers = new String[getItemCount()];
            names = new String[getItemCount()];
            storeContacts();
        } catch (Exception e) {
            e.printStackTrace();
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
        TextView contact_name = (TextView) cardView.findViewById(R.id.tvPContactName);
        TextView contact_number = (TextView) cardView.findViewById(R.id.tvPContactNumber);
        contact_name.setText(names[position]);
        contact_number.setText(phoneNumbers[position]);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_number = (TextView) v.findViewById(R.id.tvPContactNumber);
                String number = tv_number.getText().toString();

                TextView tv_name = (TextView) v.findViewById(R.id.tvPContactName);
                String name = tv_name.getText().toString();

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", number);
                smsIntent.putExtra("sms_body", "Hi " + name + ", I would like to invite you to INV to start sharing events! INV is available for free on the Google Play Store.");
                v.getContext().startActivity(smsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    public void storeContacts() {
        String[] cnames = new String[getItemCount()];
        String[] numbers = new String[getItemCount()];
        for (int i = 0; i < getItemCount(); i++) {
            cursor.moveToPosition(i);
            cnames[i] = cursor.getString(1);
            numbers[i] = cursor.getString(2);
        }
        this.phoneNumbers = numbers;
        this.names = cnames;
    }

}
