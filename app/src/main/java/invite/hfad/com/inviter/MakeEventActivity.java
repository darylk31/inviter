package invite.hfad.com.inviter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TimePicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.view.View;

public class MakeEventActivity extends Activity {

    private String yearData;
    private String monthData;
    private String dayData;
    private String titleData;
    private String descriptionData;
    private String hourData;
    private String minuteData;
    private boolean allDayData;
    private ArrayList<Integer> reminder = new ArrayList<Integer>();
    private String endHourData;
    private String endMinuteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_event);

        onStartTimeDateClick();
        setScreenSize();
        onEndTimeClick();
    }

    public void setScreenSize(){
        //Set Screen Size on create
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int height = metrics.heightPixels;
        LinearLayout l1 = (LinearLayout) findViewById(R.id.layout1);
        int actionbar = getStatusBarHeight();
        l1.getLayoutParams().height = height - actionbar;
    }

    //make discard warning for back button
    public void onInvite(View view){
        final EditText etTitle = (EditText)findViewById(R.id.etTitle);
        final EditText etDescription = (EditText)findViewById(R.id.etDescription);
        titleData = etTitle.getText().toString();
        descriptionData = etDescription.getText().toString();

        Intent i = new Intent(this, ContactsActivity.class);

        i.putExtra("KEY","thebuilder");
        i.putExtra("<yearData>", yearData);
        i.putExtra("<monthData>", monthData);
        i.putExtra("<dayData>",dayData);
        i.putExtra("hourData",hourData);
        i.putExtra("<minuteData>",minuteData);
        i.putExtra("<titleData>",titleData);
        i.putExtra("<descriptionData>",descriptionData);
        startActivity(i);


    }

    public void onStartTimeDateClick(){
        final EditText etDate = (EditText)findViewById(R.id.etDate);
        final EditText etTime = (EditText)findViewById(R.id.etTime);
        final boolean[] clicked = {false};
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //Gets instance of calender with the current date

                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                if(clicked[0]){
                    mYear = Integer.parseInt(yearData);
                    mMonth = Integer.parseInt(monthData);
                    mDay = Integer.parseInt(dayData);
                }
                DatePickerDialog mDatePicker;

                mDatePicker = new DatePickerDialog(MakeEventActivity.this,R.style.test, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        yearData = Integer.toString(year);
                        monthData = Integer.toString(monthOfYear);
                        dayData = Integer.toString(dayOfMonth);
                        String dateString = String.format("%d-%d-%d", year,monthOfYear +1,dayOfMonth);
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                            String output = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH).format(date);
                            etDate.setText(output);
                            clicked[0] = true;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, mYear,mMonth,mDay);
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
                mTimePicker = new TimePickerDialog(MakeEventActivity.this,R.style.test, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        hourData = Integer.toString(hourOfDay);
                        minuteData = Integer.toString(minute);
                        int hour = hourOfDay % 12;
                        if(hour == 0)
                            hour = 12;
                        String timeText = String.format("%02d:%02d %s",hour,minute,hourOfDay < 12 ? "AM" : "PM");
                        etTime.setText(timeText);
                    }
                }, mHour, mMinute, true);
                mTimePicker.setTitle("Time");
                mTimePicker.show();
            }
        });


    }

    public void onResizeScreen(View v){
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
        },500);
        setNewScreenSize();
    }

    public void setNewScreenSize(){
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
     * @return - true if event is all day
     */
    public void onAllDayClick(View v){
        CheckBox checkbox = (CheckBox)findViewById(R.id.cbAllDay);
        allDayData = checkbox.isChecked();
        LinearLayout layout = (LinearLayout) findViewById(R.id.Additional_Time_Layout);
        if(allDayData) {
            layout.setVisibility(View.GONE);
            endHourData = "";
            endMinuteData = "";
        } else{
            layout.setVisibility(View.VISIBLE);
        }
    }

    public void onEndTimeClick(){
        final EditText etEndTime = (EditText)findViewById(R.id.etEndTime);
        etEndTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int endHour = 0;
                int endMinute = 0;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(MakeEventActivity.this, new TimePickerDialog.OnTimeSetListener(){
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
                },endHour,endMinute,true);
                mTimePicker.setTitle("End Time");
                mTimePicker.show();
            }
        });

    }

    public void onEndDateClick(View v){

    }

    public void onReminderClick(View v){
        final ArrayList<Integer> mSelectedItems = reminder;
        AlertDialog.Builder builder = new AlertDialog.Builder(MakeEventActivity.this);

        builder
                //Set multiple choice options.
                .setMultiChoiceItems(R.array.Reminder_Options,null,
                new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked){
                        for(int i = 0 ; i < mSelectedItems.size();i++){
                            if(which == mSelectedItems.get(i)){
                                isChecked = true;
                            }
                        }
                        if(isChecked){
                            //If the user checked the item, add it to the selected items
                            mSelectedItems.add(which);
                        } else if(mSelectedItems.contains(which)){
                            mSelectedItems.remove(Integer.valueOf(which));
                        }
                    }
                })
                //Set action button
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                    //User Clicked OK, so save the mSelectedItems results.
                    reminder = mSelectedItems;

                    }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
            @Override
                public void onClick(DialogInterface dialog, int id){
                //User clicked cancel, so do not save

            }
        })
                .setTitle("lol");
        AlertDialog dialog = builder.create();
        dialog.show();


    }
}