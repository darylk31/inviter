package invite.hfad.com.inviter;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class UserAreaActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);


        TabLayout tabLayout = (TabLayout) (findViewById(R.id.tabs));

        TabLayout.Tab tab1 = tabLayout.newTab().setText("Home").setIcon(R.drawable.ic_home_black_24dp);
        TabLayout.Tab tab2 = tabLayout.newTab().setText("Make Event").setIcon(R.drawable.ic_launch_black_24dp);
        TabLayout.Tab tab3 = tabLayout.newTab().setText("Inbox").setIcon(R.drawable.ic_inbox_black_24dp);

        tabLayout.addTab(tab1);
        tabLayout.addTab(tab2);
        tabLayout.addTab(tab3);

        switch_frag(0);


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch_frag(position);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 1)
                    switch_frag(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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


    public void switch_frag(int position){
        if (position == 0){
            HomeFragment homefragment = new HomeFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, homefragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

        if (position == 1) {
            Intent intent = new Intent(this, MakeEventActivity.class);
            startActivity(intent);

        }

        if (position == 2) {
            InboxFragment inboxfragment = new InboxFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, inboxfragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

    }
}

