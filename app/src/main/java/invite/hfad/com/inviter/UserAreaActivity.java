package invite.hfad.com.inviter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

public class UserAreaActivity extends Activity {

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

            }
        });
    }



    public void switch_frag(int position){
        if (position == 0){
            HomeFragment homefragment = new HomeFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, homefragment);
            ft.addToBackStack(null);
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
            ft.addToBackStack(null);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
        }

    }
}

