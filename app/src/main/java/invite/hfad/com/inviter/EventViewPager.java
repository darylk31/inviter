package invite.hfad.com.inviter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

        viewPager = (ViewPager) findViewById(R.id.event_viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(pageNumber);
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
}
