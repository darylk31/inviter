package invite.hfad.com.inviter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class ContactsFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private CursorAdapter mAdapter;


    private String yearData;
    private String monthData;
    private String dayData;
    private String titleData;
    private String descriptionData;
    private String hourData;
    private String minuteData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getActivity();
        int layout = R.layout.contacts_list_item;
        Cursor cursor = null;
        int flags = 0; // no auto-requery! Loader requeries.
        mAdapter = new SimpleCursorAdapter (
                context,
                layout,
                cursor,
                FROM,
                TO,
                flags);

        //Retrieve Event Details
        //yearData = getArguments().getString("yearData");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    // columns requested from the database
    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER
    };



    private static final String[] FROM = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER };

    private static final int[] TO = {
            R.id.tvPContactName,
            R.id.tvPContactNumber};

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // load from the "Contacts table"
        Uri contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // no sub-selection, no sort order, simply every row
        // projection says we want just the _id and the name column
        return new CursorLoader(getActivity(),
                contentUri,
                PROJECTION,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        super.onListItemClick(l, v, position, id);

        TextView tv_number = (TextView) v.findViewById(R.id.tvPContactNumber);
        String number = tv_number.getText().toString();

        TextView tv_name = (TextView) v.findViewById(R.id.tvPContactName);
        String name = tv_name.getText().toString();

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", number);
        smsIntent.putExtra("sms_body", "Hi " + name + ", I would like to invite you to INV to start sharing events! INV is available for free on the Google Play Store.");
        startActivity(smsIntent);

    }
}