package invite.hfad.com.inviter;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CalendarFragment extends Fragment {
    MaterialCalendarView calendarView;
    RecyclerView recyclerView;
    String[] event_names;
    String[] event_ids;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        view = getView();
        calendarView = (MaterialCalendarView)view.findViewById(R.id.calendarView);
        recyclerView = (RecyclerView)view.findViewById(R.id.calender_recycler);
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String month;
                String day;
                //converted_date in yyyy-MM-dd format.
                if (date.getMonth() >= 10){
                    month = Integer.toString(date.getMonth());}
                else month = "0" + Integer.toString(date.getMonth());

                if (date.getDay() >= 10) {
                    day = Integer.toString(date.getDay());}
                else day = "0" + Integer.toString(date.getDay());

                String converted_date = Integer.toString(date.getYear()) + "-" + month + "-" + day;

                try {
                    SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(getContext());
                    SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
                    Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE DAY LIKE '%" + converted_date + "%';", null);
                    if (cursor != null && cursor.moveToFirst()){
                            updaterecycler(cursor);
                            cursor.close();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        Toast toast = Toast.makeText(getContext(), "Error loading this date, please try again!", Toast.LENGTH_SHORT);
                        toast.show();

                    }
            }
        });

    }

    private void updaterecycler(Cursor cursor){
        int num = cursor.getCount();
        event_names = new String[num];
        event_ids = new String[num];
        for (int i = 0; i < num; i++) {
            cursor.moveToPosition(i);
            event_names[i] = cursor.getString(4);
            event_ids[i] = cursor.getString(0);
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //TODO: make adapter


    }


}
