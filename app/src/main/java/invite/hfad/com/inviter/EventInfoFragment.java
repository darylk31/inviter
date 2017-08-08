package invite.hfad.com.inviter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

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
        id = getArguments().getString("event_id");
        return inflater.inflate(R.layout.fragment_event_info, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        view = getView();
        setEventPicture();
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
        onEventOptions();
        onEventInvite();
    }

    private void setEventPicture(){
        ImageView EventPictureView = (ImageView) view.findViewById(R.id.event_image);

        Glide.with(this)
                .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(EventPictureView);
    }

    public void onEventOptions(){
        getView().findViewById(R.id.tv_eventinfooptions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setItems(R.array.EventInfo_Options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Intent edit_intent = new Intent(getContext(), MakeEventActivity.class);
                                edit_intent.putExtra("Edit Id", id);
                                startActivity(edit_intent);
                                break;
                            case 1:
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Leave Event");
                                builder.setMessage("The event will be removed from your calendar, are you sure?");
                                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(getContext());
                                        SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
                                        UserDatabaseHelper.delete_event(event_db, id);
                                        event_db.close();
                                        //TODO: Delete from firebase.
                                        Intent intent = new Intent(getContext(), UserAreaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                                break;
                            case 3:
                                break;
                            case 4:
                                AlertDialog.Builder delete_builder = new AlertDialog.Builder(getContext());
                                delete_builder.setTitle("Delete Event");
                                delete_builder.setMessage("The event will be deleted, are you sure?");
                                delete_builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(getContext());
                                        SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
                                        UserDatabaseHelper.delete_event(event_db, id);
                                        event_db.close();
                                        //TODO: Delete from firebase.
                                        Intent intent = new Intent(getContext(), UserAreaActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                delete_builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog delete_alert = delete_builder.create();
                                delete_alert.show();
                                break;
                        }
                    }
                });
                builder.create();
                builder.show();
            }
        });


    }

    private void onEventInvite(){
        getView().findViewById(R.id.tv_eventinfoinvite);
    }
}
