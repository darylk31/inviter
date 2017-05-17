package invite.hfad.com.inviter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

import invite.hfad.com.inviter.Contacts.ContactsActivity;

public class UserAreaActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static final int GET_FROM_GALLERY = 3;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference mDatabase;
    private SharedPreferences pref;
    private String userID;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FirebaseUser user;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        setViewPager();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };


        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Navigation View
        navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);

                //Closing Drawer on item Click
                drawerLayout.closeDrawers();

                //DO when menu item is clicked
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.nav_inbox:
                        intent = new Intent(UserAreaActivity.this, InboxActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_contacts:
                        intent = new Intent(UserAreaActivity.this, ContactsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_settings:
                        intent = new Intent(UserAreaActivity.this, SettingActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_signout:
                        auth.signOut();
                        pref.edit().clear().commit();
                        return true;
                    default:
                        return true;
                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        this.pref = getSharedPreferences("UserPref", 0);
        this.userID = pref.getString("userID", null);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.keepSynced(true);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //Display name changes.
                TextView drawer_username = (TextView) findViewById(R.id.drawer_username);
                drawer_username.setText(user.getDisplayname());
                //TODO: Display picture changes.
                setDisplayPicture();
                //TODO: Contacts changes, Events changes,



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        //Navigation Header
        setDisplayPicture();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
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

    private void setViewPager() {

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_date_range_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_history_black_24dp));

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        UserAreaActivity.ViewPagerAdapter adapter = new UserAreaActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CalendarFragment());
        adapter.addFragment(new HomeOldFragment());
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

    private void setDisplayPicture(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        final ImageView profilePictureView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
      //  if(user.getPhotoUrl() != null){
        //    Picasso.with(this).load(user.getPhotoUrl().toString()).into(profilePictureView);
        //}else {
        //    Picasso.with(this).load(defaultPhotoUrl).into(profilePictureView);

        /*
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(profilePictureView);
        */
    }

    /*
    public void uploadProfilePicture(View view){
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

}
