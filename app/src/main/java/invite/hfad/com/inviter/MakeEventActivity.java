package invite.hfad.com.inviter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MakeEventActivity extends Activity {

    private String yearData;
    private String monthData;
    private String dayData;
    private String titleData;
    private String descriptionData;
    private String hourData;
    private String minuteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_event);

        final EditText etDate = (EditText)findViewById(R.id.etDate);
        final EditText etTime = (EditText)findViewById(R.id.etTime);

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //Gets instance of calender with the current date
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;

                mDatePicker = new DatePickerDialog(MakeEventActivity.this,R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        yearData = Integer.toString(year);
                        monthData = Integer.toString(monthOfYear);
                        dayData = Integer.toString(dayOfMonth);
                        String dateString = String.format("%d-%d-%d", year,monthOfYear +1,dayOfMonth);
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                            String output = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH).format(date);
                            etDate.setText(output);
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
                mTimePicker = new TimePickerDialog(MakeEventActivity.this,R.style.MyTimePickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
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

    //make discard warning for back button
    public void onInvite(View view){
        final EditText etTitle = (EditText)findViewById(R.id.etTitle);
        final EditText etDescription = (EditText)findViewById(R.id.etDescription);
        titleData = etTitle.getText().toString();
        descriptionData = etDescription.getText().toString();

        Intent i = new Intent(MakeEventActivity.this, ContactsActivity.class);

        i.putExtra("<yearData>", yearData);
        i.putExtra("<monthData>", monthData);
        i.putExtra("<dayData>",dayData);
        i.putExtra("hourData",hourData);
        i.putExtra("<minuteData>",minuteData);
        i.putExtra("<titleData>",titleData);
        i.putExtra("<descriptionData>",descriptionData);
        startActivity(i);


    }



    public void onTimeClick(){

    }
}