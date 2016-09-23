package invite.hfad.com.inviter;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class EventMakerActivity extends AppCompatActivity {

    DatePicker pickerDate;
    TimePicker pickerTime;
    TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_maker);


        info = (TextView)findViewById(R.id.info);
        pickerDate = (DatePicker)findViewById(R.id.pickerdate);
        pickerTime = (TimePicker)findViewById(R.id.pickertime);

        Calendar now = Calendar.getInstance();

        pickerDate.init(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener(){

                    @Override
                    public void onDateChanged(DatePicker view,
                                              int year, int monthOfYear,int dayOfMonth) {
                        Toast.makeText(getApplicationContext(),
                                "onDateChanged", Toast.LENGTH_SHORT).show();

                        info.setText(
                                "Year: " + year + "\n" +
                                        "Month of Year: " + monthOfYear + "\n" +
                                        "Day of Month: " + dayOfMonth);

                    }});
        int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
        if (yearSpinnerId != 0)
        {
            View yearSpinner = pickerDate.findViewById(yearSpinnerId);
            if (yearSpinner != null)
            {
                yearSpinner.setVisibility(View.GONE);
            }
        }

        pickerTime.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        pickerTime.setCurrentMinute(now.get(Calendar.MINUTE));
        pickerTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Toast.makeText(getApplicationContext(),
                        "onTimeChanged", Toast.LENGTH_SHORT).show();

                info.setText(
                        "Hour of Day: " + hourOfDay + "\n" +
                                "Minute: " + minute);

            }});

    }

}

