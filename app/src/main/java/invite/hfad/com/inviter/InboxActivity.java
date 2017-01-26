package invite.hfad.com.inviter;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class InboxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        TabLayout tabLayout = (TabLayout) (findViewById(R.id.tabs));

        TabLayout.Tab tab1 = tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp);
        TabLayout.Tab tab2 = tabLayout.newTab().setIcon(R.drawable.ic_launch_black_24dp);
        TabLayout.Tab tab3 = tabLayout.newTab().setIcon(R.drawable.ic_inbox_black_24dp);

        tabLayout.addTab(tab1);
        tabLayout.addTab(tab2);
        tabLayout.addTab(tab3);

        tab3.select();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tab_num = tab.getPosition();
                if (tab_num == 0) {
                    Intent intent = new Intent(InboxActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                if (tab_num == 1) {
                    Intent intent = new Intent(InboxActivity.this, MakeEventActivity.class);
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

}
