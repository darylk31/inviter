package invite.hfad.com.inviter;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import invite.hfad.com.inviter.Contacts.ContactsActivity;
import invite.hfad.com.inviter.Contacts.FriendsFragment;
import invite.hfad.com.inviter.Contacts.PhoneContactsFragment;

public class EventViewPager extends AppCompatActivity {

    String id;
    ViewPager viewPager;
    private static int pageNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view_pager);
        getSupportActionBar().hide();
        this.id = getIntent().getStringExtra("event_id");
        updateEvent();
        viewPager = (ViewPager) findViewById(R.id.event_viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(pageNumber);
        viewPager.setOffscreenPageLimit(1);
    }

    private void setupViewPager(ViewPager viewPager) {
        EventViewPager.ViewPagerAdapter adapter = new EventViewPager.ViewPagerAdapter(getSupportFragmentManager());

        Bundle args = new Bundle();
        args.putString("event_id", id);
        EventInfoFragment p1 = new EventInfoFragment();
        p1.setArguments(args);
        EventChatFragment p2 = new EventChatFragment();
        p2.setArguments(args);

        adapter.addFragment(p1, "Event Info");
        adapter.addFragment(p2, "Event Chat");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pageNumber = position;
                System.out.println("position changed to:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void updateEvent(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events").child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()){
                        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        UserDatabaseHelper.delete_event(db, id);
                        db.close();
                        Toast.makeText(EventViewPager.this,"Sorry this event has been deleted",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EventViewPager.this, UserAreaActivity.class));
                        finish();

                    }
                    else {
                        Event event = dataSnapshot.getValue(Event.class);
                        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        UserDatabaseHelper.update_event(db, id, event);
                        db.close();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
