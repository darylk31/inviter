package invite.hfad.com.inviter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateEvent extends AppCompatActivity {

    MaterialCalendarView startCalendarView;
    TextView dateTextDisplay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        getSupportActionBar().hide();
        getViews();
        onStartDateSelect();
    }

    private void getViews(){
        startCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        dateTextDisplay = (TextView) findViewById(R.id.tvDateDisplay);
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
                    String output = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.ENGLISH).format(newDate);
                    dateTextDisplay.setText(output);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
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
