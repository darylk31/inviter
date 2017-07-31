package invite.hfad.com.inviter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventInfoFragment extends Fragment {

    private String id;
    private String event_string;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_info, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        View view = getView();
        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(this.getContext());
            SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
            Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE EID LIKE '" + id + "';", null);
            cursor.moveToLast();
            TextView event_name = (TextView) view.findViewById(R.id.tv_eventpagename);
            this.event_string = cursor.getString(4);
            event_name.setText(event_string);
            TextView event_date = (TextView) view.findViewById(R.id.tv_eventpagedate);
            String event_day = cursor.getString(2);
            //TODO: Select just date.
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(event_day);
                String output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                String output_month = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date);
                String output_year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);
                event_date.setText(output_month + " " + output_day + ", " + output_year);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //TODO: Select just time.
            TextView event_time = (TextView) view.findViewById(R.id.tv_eventpagetime);
            event_time.setText(cursor.getString(2));
            cursor.close();
            event_db.close();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this.getContext(), "Error: Event unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }



        /*
        toolbar.inflateMenu(R.menu.menu_eventpage);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.invite_eventpage:
                        Intent invite_intent = new Intent(EventPage.this, ContactsActivity.class);
                        startActivity(invite_intent);
                        break;
                    case R.id.edit_eventpage:
                        Intent edit_intent = new Intent(EventPage.this, MakeEventActivity.class);
                        edit_intent.putExtra("Edit Id", id);
                        startActivity(edit_intent);
                        break;
                    case R.id.delete_eventpage:
                        //Alert dialog to confirm
                        deleteEvent();
                        Intent delete_intent = new Intent(EventPage.this, UserAreaActivity.class);
                        startActivity(delete_intent);
                        break;
                }
                return true;
            }


        });
        */
    }
}
