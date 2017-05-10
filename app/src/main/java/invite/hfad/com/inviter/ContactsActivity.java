package invite.hfad.com.inviter;


import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class ContactsActivity extends AppCompatActivity{

    private String yearData;
    private String monthData;
    private String dayData;
    private String titleData;
    private String descriptionData;
    private String hourData;
    private String minuteData;
    private boolean fromMakeEvent = false;

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            yearData = extras.getString("<yearData>");
            monthData = extras.getString("monthData");
            dayData = extras.getString("dayData");
            hourData = extras.getString("hourData");
            minuteData = extras.getString("minuteData");
            titleData= extras.getString("titleData");
            descriptionData = extras.getString("descriptionData");
            fromMakeEvent = extras.getBoolean("<fromEvent>");
        }



        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setSendVisibility(fromMakeEvent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.search_contacts){
            startActivity(new Intent(this, SearchContactsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        FriendsFragment p1 = new FriendsFragment();
        PhoneContactsFragment p2 = new PhoneContactsFragment();

        adapter.addFragment(p1, "FRIENDS");
        adapter.addFragment(p2, "CONTACTS");
        viewPager.setAdapter(adapter);
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

    public void setSendVisibility(boolean fromMakeEvent){
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.bSendContact);
        if(fromMakeEvent)
            button.setVisibility(View.VISIBLE);
        else
            button.setVisibility(View.GONE);
    }

}
