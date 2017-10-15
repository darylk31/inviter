package invite.hfad.com.inviter.EventObjectModel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.FriendlyMessage;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.UserDatabaseHelper;
import invite.hfad.com.inviter.Utils;

public class EditEvent extends AppCompatActivity {

    private MaterialCalendarView startCalendarView;
    private TextView dateTextDisplay;
    private TextView timeTextDisplay;
    private EditText titleDisplay;
    private EditText descriptionDisplay;
    private FloatingActionButton button;

    private String startDateData;
    private String startTimeData;
    private String endDateData;
    private String endTimeData;
    private String location;
    private int PLACE_PICKER_REQUEST = 1;
    private int LOCATION_PERMISSION = 11;
    private TextView tvLocation;

    private Event event;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        event = getIntent().getExtras().getParcelable("event");
        mDatabase = Utils.getDatabase().getReference();
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().hide();
        getViews();
        setUpFields();
        onStartDateSelect();
        onStartTimeClick();
        onButtonClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpGoogleLocation();
    }

    private void getViews() {
        startCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        dateTextDisplay = (TextView) findViewById(R.id.tvDateDisplay);
        timeTextDisplay = (TextView) findViewById(R.id.tvStartTimeDisplay);
        titleDisplay = (EditText) findViewById(R.id.etTitle);
        descriptionDisplay = (EditText) findViewById(R.id.etDescription);
        button = (FloatingActionButton) findViewById(R.id.bEventSelectContacts);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
    }

    private void setUpFields(){
        if(event.getEvent_name() != null)
            titleDisplay.setText(event.getEvent_name());
        if(event.getDescription() != null)
            descriptionDisplay.setText(event.getDescription());
        if(event.getLocation() != null)
            tvLocation.setText(event.getLocation());
        //Figure out time
    }

    private void onStartDateSelect() {
        Date eventDate = new Date();
        try {
            eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(event.getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startCalendarView.setSelectedDate(eventDate);
        String converted_date =  new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(eventDate);
        startDateData = converted_date;
        String output = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(eventDate);
        dateTextDisplay.setText(output);
        startCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                try {
                    String converted_date = String.format("%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay());
                    Date newDate = new SimpleDateFormat("yyyy-MM-dd").parse(converted_date);
                    startDateData = converted_date;
                    String output = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(newDate);
                    dateTextDisplay.setText(output);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onStartTimeClick() {
        timeTextDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int mMinute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int hour = hourOfDay % 12;
                        if (hour == 0)
                            hour = 12;
                        String timeText = String.format("%02d:%02d %s", hour, minute, hourOfDay < 12 ? "AM" : "PM");
                        startTimeData = String.format("%02d:%02d:%02d", hourOfDay, minute, 00);
                        timeTextDisplay.setText(timeText);
                    }
                }, mHour, mMinute, false);
                mTimePicker.setTitle("Time");
                mTimePicker.show();
            }
        });
    }

    private void onButtonClick() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event.setEvent_name(titleDisplay.getText().toString().trim());
                event.setDescription(descriptionDisplay.getText().toString().trim());
                event.setStartDate(startDateData + " " + startTimeData);
                if(event.getStartDate() != null)
                    mDatabase.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.EVENT_STARTDATE).setValue(event.getStartDate());

                if(event.getEvent_name() != null)
                    mDatabase.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.EVENT_TITLE).setValue(event.getEvent_name());

                if(event.getDescription() != null)
                    mDatabase.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.EVENT_DESCRIPTION).setValue(event.getDescription());

                if(event.getLocation() != null)
                    mDatabase.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.EVENT_LOCATION).setValue(event.getLocation());

                if(event.getLast_modified() != null)
                    mDatabase.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.EVENT_LAST_MODIFIED).setValue(Utils.getCurrentDate());

                String friendlyMessageId = mDatabase.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.CHAT).push().getKey();
                FriendlyMessage friendlyMessage = new FriendlyMessage(friendlyMessageId,"Event updated",
                        Utils.APP, event.getPhotoUrl(),Utils.getCurrentDate(), null,Utils.APP);
                mDatabase.child(Utils.EVENT_DATABASE).child(event.getEventId()).child(Utils.CHAT).child(friendlyMessageId).setValue(friendlyMessage);

                SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                UserDatabaseHelper.update_event(db, event.getEventId(), event);
                db.close();

                finish();
            }
        });


    }

    public void setUpGoogleLocation() {
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ContextCompat.checkSelfPermission(EditEvent.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditEvent.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION);}
                    else {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(EditEvent.this), PLACE_PICKER_REQUEST);
                    }
            } catch(
            GooglePlayServicesRepairableException e)
            {e.printStackTrace();
            } catch(
            GooglePlayServicesNotAvailableException e)
            {e.printStackTrace();}
        }
    });}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults){
        if (requestCode == LOCATION_PERMISSION){
            if (grantResults.length != 0){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(EditEvent.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }}

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST) {

            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                if (place.getName() != null){
                    tvLocation.setText(place.getName() + " @ " + place.getAddress());
                    event.setLocation(tvLocation.getText().toString());
                }
                else {
                    tvLocation.setText(place.getAddress());
                    event.setLocation(tvLocation.getText().toString());
                }
                location = tvLocation.getText().toString();
                tvLocation.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvLocation.setSelected(true);
                }}, 2000);
            }
        }
    }


    @Override
    public void onBackPressed() {
        final EditText etTitle = (EditText) findViewById(R.id.etTitle);
        if (etTitle.getText().toString().equals("")) {
            Intent intent = new Intent(EditEvent.this, UserAreaActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditEvent.this);
            builder.setTitle("Discard");
            builder.setMessage("All event information will be discarded, are you sure?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(EditEvent.this, UserAreaActivity.class);
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
        }
    }


}
