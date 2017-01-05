package invite.hfad.com.inviter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import static android.R.attr.checked;
import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class MakeEventActivity extends Activity {

    private String yearData;
    private String monthData;
    private String dayData;
    private String titleData;
    private String descriptionData;
    private String hourData;
    private String minuteData;
    private boolean allDayData;
    private String endHourData;
    private String endMinuteData;
    private String reminderUpdate;
    private String repeatUpdate;
    private String endYearData;
    private String endMonthData;
    private String endDayData;
    boolean[] checkedReminder = new boolean[]{
            false, // 30 Minutes
            false, // 1 Hour
            false, // 1 Day
            false, // Custom
    };
    boolean[] checkedRepeat = new boolean[]{
            false, // Daily
            false, // Weekly
            false, // Monthly
            false, // Weekdays
            false, // Weekends
            false, // lol
    };

    private String dateData;
    private String timeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_event);
        //onStartTimeDateClick();
        setScreenSize();
        onStartTimeClick();
        onStartDateDialog();
        onEndTimeClick();
        onEndDateClick();
    }

    public void setScreenSize() {
        //Set Screen Size on create
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        LinearLayout l1 = (LinearLayout) findViewById(R.id.layout1);
        int actionbar = getStatusBarHeight();
        l1.getLayoutParams().height = height - actionbar;
    }


    public void onInvite(View view) {
        final EditText etTitle = (EditText) findViewById(R.id.etTitle);
        final EditText etDescription = (EditText) findViewById(R.id.etDescription);
        titleData = etTitle.getText().toString().trim();
        descriptionData = etDescription.getText().toString().trim();
        UserDatabaseHelper helper = new UserDatabaseHelper(this.getApplicationContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        if (titleData.equals("")) {
            etTitle.setError("Require Title");
        } else {
            helper.insert_event(db, dateData, titleData, descriptionData, timeData, allDayData);
            Intent i = new Intent(MakeEventActivity.this, HomeActivity.class);
            //Toast test
            Toast.makeText(MakeEventActivity.this, "Successfully added Event", Toast.LENGTH_LONG).show();
            startActivity(i);
        }
    }



    /*
    //OLD FUNCTION
    public void onStartTimeDateClick() {
        final EditText etDate = (EditText) findViewById(R.id.etDate);
        final EditText etTime = (EditText) findViewById(R.id.etTime);
        final boolean[] clicked = {false};
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Gets instance of calender with the current date
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                if (clicked[0]) {
                    mYear = Integer.parseInt(yearData);
                    mMonth = Integer.parseInt(monthData);
                    mDay = Integer.parseInt(dayData);
                }
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(MakeEventActivity.this, R.style.test, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        yearData = Integer.toString(year);
                        monthData = Integer.toString(monthOfYear);
                        dayData = Integer.toString(dayOfMonth);
                        String dateString = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                            String output = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH).format(date);
                            dateData = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
                            etDate.setText(output);
                            clicked[0] = true;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Date");
                mDatePicker.show();
            }

        });
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int mMinute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MakeEventActivity.this, R.style.test, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hourData = Integer.toString(hourOfDay);
                        minuteData = Integer.toString(minute);
                        //timeData = String.format("%02d:%02d", hourOfDay, minute);
                        int hour = hourOfDay % 12;
                        if (hour == 0)
                            hour = 12;
                        String timeText = String.format("%02d:%02d %s", hour, minute, hourOfDay < 12 ? "AM" : "PM");
                        timeData = timeText;
                        etTime.setText(timeText);
                    }
                }, mHour, mMinute, true);
                mTimePicker.setTitle("Time");
                mTimePicker.show();
            }
        });
    }
    */

    public void onStartTimeClick(){
        final TextView tvTime = (TextView) findViewById(R.id.tvStartTime);
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int mMinute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MakeEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hourData = Integer.toString(hourOfDay);
                        minuteData = Integer.toString(minute);
                        //timeData = String.format("%02d:%02d", hourOfDay, minute);
                        int hour = hourOfDay % 12;
                        if (hour == 0)
                            hour = 12;
                        String timeText = String.format("%02d:%02d %s", hour, minute, hourOfDay < 12 ? "AM" : "PM");
                        timeData = timeText;
                        tvTime.setText(timeText);
                    }
                }, mHour, mMinute, false);
                mTimePicker.setTitle("Time");
                mTimePicker.show();
            }
        });
    }

    public void onStartDateDialog() {
        final DatePicker datePicker = (DatePicker) findViewById(R.id.dpDatePicker);

        final TextView tvDate = (TextView) findViewById(R.id.tvDateDisplay);
        final int day = datePicker.getDayOfMonth();
        final int month = datePicker.getMonth() + 1;
        final int year = datePicker.getYear();
        String dateString = String.format("%d-%d-%d", year, month, day);
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            dateData = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
            String output = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(date);
            tvDate.setText(output);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datePicker.init(year, month, day,
                new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day){

                String dateString = String.format("%d-%d-%d", year, month + 1, day);
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                    dateData = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date);
                    String output = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(date);
                    tvDate.setText(output);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void onResizeScreen(View v) {
        ImageButton ibAdditional = (ImageButton) findViewById(R.id.ibAdditionalSetting);
        LinearLayout layout2 = (LinearLayout) findViewById(R.id.layout2);
        ibAdditional.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.EventScrollLayout));
        scrollview.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 500);
        setNewScreenSize();
    }

    public void setNewScreenSize() {
        //Set Screen Size on create
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        LinearLayout l1 = (LinearLayout) findViewById(R.id.layout1);
        int actionbar = getStatusBarHeight();
        double scale = (height - actionbar) * 0.9;
        l1.getLayoutParams().height = (int) scale;
    }


    /**
     * Get the action bar height
     *
     * @return - height in dp of the action bar
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Get bool value of all day event or not.
     *
     * @return - true if event is all day
     */
    public void onAllDayClick(View v) {
        CheckBox checkbox = (CheckBox) findViewById(R.id.cbAllDay);
        allDayData = checkbox.isChecked();
        LinearLayout layout = (LinearLayout) findViewById(R.id.Additional_Time_Layout);
        if (allDayData) {
            layout.setVisibility(View.GONE);
            endHourData = "";
            endMinuteData = "";
        } else {
            layout.setVisibility(View.VISIBLE);
        }
    }

    public void onEndTimeClick() {
        final EditText etEndTime = (EditText) findViewById(R.id.etEndTime);
        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int endHour = 0;
                int endMinute = 0;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MakeEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timepicker, int selectedHour, int selectedMinute) {
                        endHourData = Integer.toString(selectedHour);
                        endMinuteData = Integer.toString(selectedMinute);
                        int hour = selectedHour % 12;
                        if (hour == 0)
                            hour = 12;
                        String timeText = String.format("%02d:%02d %s", hour, selectedMinute, selectedHour < 12 ? "AM" : "PM");
                        etEndTime.setText(timeText);
                    }
                }, endHour, endMinute, true);
                mTimePicker.setTitle("End Time");
                mTimePicker.show();
            }
        });
    }

    public void onEndDateClick() {
        final EditText etEndDate = (EditText) findViewById(R.id.etEndDate);
        etEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean[] clicked = {false};
                //Gets instance of calender with the current date
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                if (yearData != null && monthData != null && dayData != null) {
                    mYear = Integer.parseInt(yearData);
                    mMonth = Integer.parseInt(monthData);
                    mDay = Integer.parseInt(dayData);
                }
                if (endYearData != null && endDayData != null && endDayData != null) {
                    mYear = Integer.parseInt(endYearData);
                    mMonth = Integer.parseInt(endMonthData);
                    mDay = Integer.parseInt(endDayData);
                }
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(MakeEventActivity.this, R.style.test, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endYearData = Integer.toString(year);
                        endMonthData = Integer.toString(monthOfYear);
                        endDayData = Integer.toString(dayOfMonth);
                        String dateString = String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth);
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                            String output = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH).format(date);
                            etEndDate.setText(output);
                            clicked[0] = true;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Date");
                mDatePicker.show();
            }

        });

    }

    public void onReminderClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MakeEventActivity.this);
        builder
                //Set multiple choice options.
                .setMultiChoiceItems(R.array.Reminder_Options, checkedReminder,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.Reminder_Options));
                                reminderUpdate = "";
                                checkedReminder[which] = isChecked;
                                for (int i = 0; i < checkedReminder.length; i++) {
                                    boolean checked = checkedReminder[i];
                                    if (checked) {
                                        reminderUpdate += Lines.get(i);
                                        reminderUpdate += "\n";
                                    }
                                }
                            }
                        })
                //Set action button
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //User Clicked OK, so save the mSelectedItems results.
                        TextView tv = (TextView) findViewById(R.id.tvReminder);
                        tv.setText(reminderUpdate);


                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //User clicked cancel, so do not save
                    }
                })
                .setTitle("lol");
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void onRepeatClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MakeEventActivity.this);
        builder
                //Set multiple choice options.
                .setMultiChoiceItems(R.array.Repeat_Options, checkedRepeat,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                List<String> Lines = Arrays.asList(getResources().getStringArray(R.array.Repeat_Options));
                                repeatUpdate = "";
                                checkedRepeat[which] = isChecked;
                                for (int i = 0; i < checkedRepeat.length; i++) {
                                    boolean checked = checkedRepeat[i];
                                    if (checked) {
                                        repeatUpdate += Lines.get(i);
                                        repeatUpdate += "\n";
                                    }
                                }


                            }
                        })
                //Set action button
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //User Clicked OK, so save the mSelectedItems results.
                        TextView tv = (TextView) findViewById(R.id.tvRepeat);
                        tv.setText(repeatUpdate);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //User clicked cancel, so do not save
                    }
                })
                .setTitle("lol");
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void googleLocationFragment() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

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
            Intent intent = new Intent(MakeEventActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MakeEventActivity.this);
            builder.setTitle("Discard");
            builder.setMessage("All event information will be discarded, are you sure?");
            builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MakeEventActivity.this, HomeActivity.class);
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