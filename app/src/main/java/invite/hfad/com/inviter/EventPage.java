package invite.hfad.com.inviter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class EventPage extends Activity {
    String id;
    String event_string;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        this.id = getIntent().getStringExtra("event_id");

        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(this.getApplicationContext());
            SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
            Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE _id=" + id, null);
            cursor.moveToLast();
            TextView event_name = (TextView) findViewById(R.id.tv_eventpagename);
            this.event_string = cursor.getString(2);
            event_name.setText(event_string);
            TextView event_date = (TextView) findViewById(R.id.tv_eventpagedate);
            String event_day = cursor.getString(1);
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(event_day);
                String output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                String output_month = new SimpleDateFormat("MMM",Locale.ENGLISH).format(date);
                String output_year = new SimpleDateFormat("yyyy",Locale.ENGLISH).format(date);
                event_date.setText(output_month + " " + output_day + ", " + output_year);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            TextView event_time = (TextView) findViewById(R.id.tv_eventpagetime);
            event_time.setText(cursor.getString(4));
            cursor.close();
            event_db.close();

        } catch (SQLiteException e){
            e.printStackTrace();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Error: Event unavailable", Toast.LENGTH_SHORT);
            toast.show();}


        this.toolbar = (Toolbar) findViewById(R.id.event_toolbar);
        toolbar.inflateMenu(R.menu.menu_eventpage);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item){
                switch (item.getItemId()){
                    case R.id.invite_eventpage:
                        Intent invite_intent = new Intent (EventPage.this, ContactsActivity.class);
                        startActivity(invite_intent);
                        break;
                    case R.id.edit_eventpage:
                        Intent edit_intent = new Intent (EventPage.this, MakeEventActivity.class);
                        edit_intent.putExtra("Edit Id", id);
                        startActivity(edit_intent);
                        break;
                    case R.id.delete_eventpage:
                        //Alert dialog to confirm
                        deleteEvent();
                        Intent delete_intent = new Intent (EventPage.this, HomeActivity.class);
                        startActivity(delete_intent);
                        break;
                }
                return true;
            }
    });


        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        appBarLayout.setExpanded(false);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                int collapsed_num = appBarLayout.getTotalScrollRange();

                if (verticalOffset == -collapsed_num ) {
                    collapsingToolbarLayout.setTitleEnabled(true);
                    collapsingToolbarLayout.setTitle(event_string);
                }
                else {
                    collapsingToolbarLayout.setTitleEnabled(false);}
            }}
        );

    }

    public void deleteEvent(){
        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(this.getApplicationContext());
            SQLiteDatabase event_db = eventDatabaseHelper.getWritableDatabase();
            UserDatabaseHelper.delete_event(event_db, id);
            event_db.close();}
        catch (SQLiteException e){
            e.printStackTrace();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Error: Unable to delete", Toast.LENGTH_SHORT);
            toast.show();}

        Toast toast = Toast.makeText(this.getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
        toast.show();}
    }


