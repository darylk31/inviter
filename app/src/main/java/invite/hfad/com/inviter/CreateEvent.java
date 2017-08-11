package invite.hfad.com.inviter;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import invite.hfad.com.inviter.EventObjectModel.EventSelectContacts;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().hide();
        getViews();
        onStartDateSelect();
        onStartTimeClick();
        onButtonClick();
        googleLocationFragment();
    }

    private void getViews(){
        startCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        dateTextDisplay = (TextView) findViewById(R.id.tvDateDisplay);
        timeTextDisplay = (TextView) findViewById(R.id.tvStartTimeDisplay);
        titleDisplay = (EditText) findViewById(R.id.etTitle);
        descriptionDisplay = (EditText) findViewById(R.id.etDescription);
        button = (FloatingActionButton) findViewById(R.id.bEventSelectContacts);
    }

    private void onStartDateSelect(){
        startCalendarView.setSelectedDate(Calendar.getInstance());
        String output = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(new Date());
        dateTextDisplay.setText(output);
        startCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                try {
                    String converted_date = String.format("%04d-%02d-%02d",date.getYear(),date.getMonth()+1,date.getDay());
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

    private void onStartTimeClick(){
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
                        startTimeData = String.format("%02d:%02d:%02d",hourOfDay,minute,00);
                        timeTextDisplay.setText(timeText);
                    }
                }, mHour, mMinute, false);
                mTimePicker.setTitle("Time");
                mTimePicker.show();
            }
        });
    }

    private void onButtonClick(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleDisplay.getText().toString().trim();
                String description = descriptionDisplay.getText().toString().trim();
                String date = startDateData + " " + startTimeData;
                Event event = new Event(date,endDateData,title,description, FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),new ArrayList<String>());
                Intent intent = new Intent(CreateEvent.this , EventSelectContacts.class);
                intent.putExtra("myEvent",(Parcelable) event);
                startActivity(intent);
            }
        });
    }

    public void googleLocationFragment() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                this.getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

/*
* The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
* set a filter returning only results with a precise address.
*/
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                //Log.i(TAG, "Place: " + place.getName());//get place details here
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                //Log.i(TAG, "An error occurred: " + status);
            }
        });
    }


    @Override
    public void onBackPressed(){
        final EditText etTitle = (EditText) findViewById(R.id.etTitle);
        if(etTitle.getText().toString().equals("")) {
            Intent intent = new Intent(CreateEvent.this, UserAreaActivity.class);
            startActivity(intent);
        }
        else {
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
