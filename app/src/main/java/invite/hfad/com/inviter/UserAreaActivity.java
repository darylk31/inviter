package invite.hfad.com.inviter;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import invite.hfad.com.inviter.Contacts.ContactsActivity;
import invite.hfad.com.inviter.Contacts.FriendsFragment;
import invite.hfad.com.inviter.Inbox.InboxActivity;

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
    private TextView drawer_username;
    private FirebaseUser user;
    private int inboxCounter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        setViewPager();
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
                } else{

                // User is signed in
                String displayName = user.getDisplayName();
                Uri profileUri = user.getPhotoUrl();
                // If the above were null, iterate the provider data
                // and set with the first non null data
                for (UserInfo userInfo : user.getProviderData()) {
                    if (displayName == null && userInfo.getDisplayName() != null) {
                        displayName = userInfo.getDisplayName();
                    }
                    if (profileUri == null && userInfo.getPhotoUrl() != null) {
                        profileUri = userInfo.getPhotoUrl();
                    }
                }

                /*
                drawer_username = (TextView) findViewById(R.id.drawer_username);
                drawer_username.setText("AH");

                //Navigation Header
                setDisplayPicture();
                */
                }
            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.pref = getSharedPreferences("UserPref", 0);
        this.userID = pref.getString("userID", null);

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();
        userRef.keepSynced(true);

        navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);

        //CountInboxItems
        countInboxItems();
        addRequestListener();

        //Set drawer header
        setDrawer_username();
        setDisplayPicture();

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation View
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
                        startActivity(new Intent(UserAreaActivity.this,LoginActivity.class));
                        finish();
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

        //Update Scheduler
        scheduleUpdate();

    }

    private void scheduleUpdate() {
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        // call service
                        System.out.println("Contacts Scheduled Call");
                        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        UserDatabaseHelper.updateContacts(db, getApplicationContext());
                        System.out.println("Contacts updated");
                    }
                }, 0, 3, TimeUnit.DAYS);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        countInboxItems();
        auth.addAuthStateListener(authListener);
        if(auth == null){
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        countInboxItems();
        auth.addAuthStateListener(authListener);
        if(auth==null){
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        }
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

    public void onButtonClick(View v) {
        Intent intent = new Intent(this, MakeEventActivity.class);
        startActivity(intent);
    }

    private void setDisplayPicture(){
        final ImageView profilePictureView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(profilePictureView);
    }

    private void setDrawer_username(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        drawer_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_username);
        drawer_username.setText(user.getDisplayName());
    }

    /*
    public void uploadProfilePicture(View view){
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }
    */

    private void countInboxItems() {
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inboxCounter = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for(DataSnapshot dsChild : ds.getChildren()){
                        //inboxCounter = (int)dsChild.getChildrenCount();
                        inboxCounter++;
                        System.out.println("Inboxcounter was called with key :" + dsChild.getKey());
                    }
                }
                if(inboxCounter > 0){
                    SpannableString spanString = new SpannableString("Inbox (" + inboxCounter + ")");
                    spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
                    navigationView.getMenu().findItem(R.id.nav_inbox).setTitle(spanString);

                } else{
                    navigationView.getMenu().findItem(R.id.nav_inbox).setTitle("Inbox");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addRequestListener(){
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").child("Add_Request").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(!dataSnapshot.exists())
                    return;
                String contact = dataSnapshot.getKey();
                Contact ctest = new Contact(contact,true);
                if(ctest.getIsContact()){
                  return;
                }
                mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").child("Add_request").child(contact).setValue(contact);
                mDatabase.child("Users").child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            addRequestNotification(user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                System.out.println("what is datasnapshot " + dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addRequestNotification(User user){
        System.out.println("Notification entrance");
        if(user== null)
            return;
                NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.twitter_button)
                        .setContentTitle(this.getString(R.string.app_name))
                        .setContentText(user.getUsername() + "would like to add you!");
        // Sets an unique ID for the addRequestNotification
        int mNotificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the addRequestNotification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        System.out.println("addRequestNotification shown");
    }


}
