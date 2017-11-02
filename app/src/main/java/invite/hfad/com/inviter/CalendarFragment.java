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
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class CalendarFragment extends Fragment {
    MaterialCalendarView calendarView;
    RecyclerView recyclerView;
    TextView emptyMessage;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        view = getView();
        calendarView = view.findViewById(R.id.calendarView);
        recyclerView = view.findViewById(R.id.calender_recycler);
        emptyMessage = view.findViewById(R.id.tv_calendar_empty);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        calendarView.setSelectedDate(Calendar.getInstance());
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String month;
                String day;
                //converted_date in yyyy-MM-dd format.
                String converted_date = String.format("%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay());

                try {
                    SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(getContext());
                    SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
                    Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE DAY LIKE '%" + converted_date + "%';", null);
                    if (cursor != null && cursor.moveToFirst()) {
                        recyclerView.setAdapter(new CalendarAdapter(cursor));
                        cursor.close();
                        emptyMessage.setVisibility(View.GONE);
                    } else {
                        recyclerView.setAdapter(null);
                        emptyMessage.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getContext(), "Error loading this date, please try again!", Toast.LENGTH_SHORT);
                    toast.show();

                }
            }
        });

        calendarView.addDecorators(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                String converted_date = String.format("%04d-%02d-%02d", day.getYear(), day.getMonth() + 1, day.getDay());

                try {
                    SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(getContext());
                    SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
                    Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE DAY LIKE '%" + converted_date + "%';", null);

                    if (cursor != null && cursor.moveToFirst()) {
                        return true;
                    }
                    cursor.close();
                    event_db.close();
                } catch (Exception e) {}
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(10, R.color.colorPrimary));
            }
        });
    }
}
