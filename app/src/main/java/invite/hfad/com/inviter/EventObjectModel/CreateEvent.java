package invite.hfad.com.inviter.EventObjectModel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import invite.hfad.com.inviter.Event;
import invite.hfad.com.inviter.R;
import invite.hfad.com.inviter.UserAreaActivity;
import invite.hfad.com.inviter.Utils;

public class CreateEvent extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().hide();
        getViews();
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
    }

    private void onStartDateSelect() {
        Calendar c = Calendar.getInstance();
        startCalendarView.setSelectedDate(c);
        String converted_date =  new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new Date());
        startDateData = converted_date;
        String output = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(new Date());
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
                mTimePicker = new TimePickerDialog(CreateEvent.this, new TimePickerDialog.OnTimeSetListener() {
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
                String title = titleDisplay.getText().toString().trim();
                if(titleDisplay.getText().toString().trim().equalsIgnoreCase("")){
                    titleDisplay.setError("Missing event name.");
                    return;
                }
                String description = descriptionDisplay.getText().toString().trim();
                String date = startDateData + " " + startTimeData;
                try {
                    if (new SimpleDateFormat("yyyy-MM-dd").parse(startDateData).before(yesterday())) {
                        Toast.makeText(getApplicationContext(), "Date has passed.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Event event = new Event(date, endDateData, title, description, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),location, Utils.getCurrentDate());
                Intent intent = new Intent(CreateEvent.this, EventSelectContacts.class);
                intent.putExtra("myEvent", (Parcelable) event);
                startActivity(intent);
            }
        });
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public void setUpGoogleLocation() {
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ContextCompat.checkSelfPermission(CreateEvent.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateEvent.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION);}
                    else {
                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                        startActivityForResult(builder.build(CreateEvent.this), PLACE_PICKER_REQUEST);
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
                startActivityForResult(builder.build(CreateEvent.this), PLACE_PICKER_REQUEST);
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
                tvLocation.setText(place.getName() + " @ " + place.getAddress());}
                else {
                    tvLocation.setText(place.getAddress());
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
            Intent intent = new Intent(CreateEvent.this, UserAreaActivity.class);
            startActivity(intent);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateEvent.this);
            builder.setTitle("Discard");
            builder.setMessage("All event information will be discarded, are you sure?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CreateEvent.this, UserAreaActivity.class);
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
