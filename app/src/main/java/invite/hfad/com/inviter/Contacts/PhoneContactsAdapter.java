package invite.hfad.com.inviter.Contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import invite.hfad.com.inviter.DialogBox.ProfileDialogBox;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.Utils;

/**
 * Created by Daryl on 4/7/2017.
 */

public class PhoneContactsAdapter extends RecyclerView.Adapter<PhoneContactsAdapter.ViewHolder> {

    private ArrayList<String> phoneNumbers;
    private ArrayList<String> names;
    private Cursor cursor;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public PhoneContactsAdapter(Context context) {
        this.context = context;

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
            phoneNumbers = new ArrayList<String>();
            names = new ArrayList<String>();
            storeContacts();
        } catch (Exception e) {

        }
    }


    @Override
    public PhoneContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_item, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(PhoneContactsAdapter.ViewHolder holder, final int position) {

        final CardView cardView = holder.cardView;
        final TextView contact_name = cardView.findViewById(R.id.tvPContactName);
        final TextView contact_number = cardView.findViewById(R.id.tvPContactNumber);
        contact_name.setText(names.get(position));
        contact_number.setText(phoneNumbers.get(position));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv_number = v.findViewById(R.id.tvPContactNumber);
                String number = tv_number.getText().toString();

                TextView tv_name = v.findViewById(R.id.tvPContactName);
                String name = tv_name.getText().toString();

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", number);
                smsIntent.putExtra("sms_body", "Hi " + name + ", I would like to invite you to INV to start sharing events! INV is available for free on the Google Play Store.");
                v.getContext().startActivity(smsIntent);
            }
        });

        //check if in phone numbers table.
        Utils.getDatabase().getReference().child(Utils.DATABASE_PHONE_NUMBER).child(phoneNumbers.get(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String username = dataSnapshot.getValue().toString();
                            contact_number.setText(username);
                            TextView invite_text = cardView.findViewById(R.id.tvPContactInvite);
                            invite_text.setVisibility(View.INVISIBLE);
                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ProfileDialogBox dialogBox = new ProfileDialogBox(context, username);
                                    dialogBox.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

        }


    @Override
    public int getItemCount() {
        try{
            return names.size();}
        catch (Exception e) {
            return 0;
        }
    }


    public void storeContacts() {
        ArrayList cnames = new ArrayList<String>();
        ArrayList numbers = new ArrayList<String>();
        if (cursor == null){
            return;
        }
        else {
            cursor.moveToFirst();
            if (cursor.getString(2) != null) {
                cnames.add(cursor.getString(1));
                numbers.add(cursor.getString(2));
            }
            while (cursor.moveToNext()){
                if (cursor.getString(2) != null) {
                    cnames.add(cursor.getString(1));
                    numbers.add(cursor.getString(2));
                }
            }
            this.phoneNumbers = numbers;
            this.names = cnames;
        }
    }
}
