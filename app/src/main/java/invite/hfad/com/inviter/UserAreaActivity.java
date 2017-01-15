package invite.hfad.com.inviter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAreaActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    //private FirebaseAuth auth;
    //private FirebaseAuth.AuthStateListener authListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        setCustomActionBar();
        setViewPager();
        setNavigationDisplayPicture();

 }


        /*
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("UserAreaActivity", "onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.getPhotoUrl() != null) {
                        Log.d("UserAreaActivity", "photoURL: " + user.getPhotoUrl());
                        //Picasso.with(MainActivity.this).load(user.getPhotoUrl()).into(imageView);
                    }
                } else {
                    startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
                }
            }

            ;

        };

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
    */


    private void setViewPager() {

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_date_range_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_history_black_24dp));

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        UserAreaActivity.ViewPagerAdapter adapter = new UserAreaActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CalendarFragment());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new HomeOldFragment());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }

    public void onMakeEvent(View v) {
        Intent intent = new Intent(this, MakeEventActivity.class);
        startActivity(intent);
    }

    private void setCustomActionBar() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
    }

    private void drawerToggle() {
        //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    private void setNavigationDisplayPicture(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        ImageView profilePictureView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        Picasso.with(this).load("https://scontent-sea1-1.xx.fbcdn.net/v/t1.0-9/14457341_10153958094445963_3322825613904558278_n.jpg?oh=49fc47777f5e5941849f092bdb12f66b&oe=58E1296D").into(profilePictureView);

    }
}
