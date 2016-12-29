package invite.hfad.com.inviter;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        TabLayout tabLayout = (TabLayout) (findViewById(R.id.tabs));

        TabLayout.Tab tab1 = tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp);
        TabLayout.Tab tab2 = tabLayout.newTab().setIcon(R.drawable.ic_launch_black_24dp);
        TabLayout.Tab tab3 = tabLayout.newTab().setIcon(R.drawable.ic_inbox_black_24dp);

        tabLayout.addTab(tab1);
        tabLayout.addTab(tab2);
        tabLayout.addTab(tab3);

        tab1.select();


        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        ViewPager myViewPager = (ViewPager) findViewById(R.id.pager);
        myViewPager.setAdapter(myPagerAdapter);



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tab_num = tab.getPosition();
                if (tab_num == 1) {
                    Intent intent = new Intent(HomeActivity.this, MakeEventActivity.class);
                    startActivity(intent);
                }
                if (tab_num == 2) {
                    Intent intent = new Intent(HomeActivity.this, InboxActivity.class);
                    startActivity(intent);
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }


    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm){
            super (fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            }
            else {
                HomeOldFragment oldFragment = new HomeOldFragment();
                return oldFragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent_settings = new Intent(this, SettingActivity.class);
                startActivity(intent_settings);
                return true;
            case R.id.contacts:
                Intent intent_contacts = new Intent(this, ContactsActivity.class);
                startActivity(intent_contacts);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
