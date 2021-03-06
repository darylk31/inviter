package invite.hfad.com.inviter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import invite.hfad.com.inviter.Contacts.ContactsActivity;
import invite.hfad.com.inviter.Inbox.InboxActivity;

public class UserAreaActivity extends AppCompatActivity {

    private static int pageNumber;

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
    private ImageView profilePictureView;


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
                        SQLiteOpenHelper databaseHelper = new UserDatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        db.delete("EVENTS", null, null);
                        db.delete("FRIENDS", null, null);
                        db.close();
                        startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
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
                        UserDatabaseHelper.updateContacts(db);
                        System.out.println("Contacts updated");
                    }
                }, 0, 3, TimeUnit.DAYS);
    }


    @Override
    public void onStart() {
        super.onStart();
        countInboxItems();
        auth.addAuthStateListener(authListener);
        if (auth == null) {
            startActivity(new Intent(UserAreaActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        navigationView.setCheckedItem(R.id.nav_dashboard);
        viewPager.setAdapter(makeAdapter());
        viewPager.setCurrentItem(pageNumber);
        countInboxItems();
        auth.addAuthStateListener(authListener);
        if (auth == null) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
        }
        if (resultCode == RESULT_OK) {
            final ProgressDialog dialog = new ProgressDialog(UserAreaActivity.this);
            dialog.setMessage("Uploading image...");
            dialog.show();
            Uri selectedimg = data.getData();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            UploadTask uploadtask = storageRef.child("profile/" + user.getUid() + ".jpg").putFile(selectedimg);
            uploadtask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @SuppressWarnings("VisibleForTests")
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(taskSnapshot.getDownloadUrl())
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Glide.with(getApplicationContext())
                                                .load(taskSnapshot.getDownloadUrl().toString())
                                                .into(profilePictureView);
                                        //Update photoUrl of user database
                                        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getUid()).child(Utils.USER_PHOTO_URL).setValue(taskSnapshot.getDownloadUrl().toString());
                                        //Grab username from user database and update username table
                                        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getUid()).child(Utils.USER_USERNAME).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    mDatabase.child(Utils.USERNAMES).child(dataSnapshot.getValue().toString()).child(Utils.USER_PHOTO_URL).setValue(taskSnapshot.getDownloadUrl().toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        Toast.makeText(getApplicationContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                }
            });
        }
        ;
    }

    private void setViewPager() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_date_range_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_history_black_24dp));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(makeAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pageNumber = position;
                tabLayout.setScrollPosition(position, 0f, true);
                System.out.println("position changed to:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    private ViewPagerAdapter makeAdapter() {
        UserAreaActivity.ViewPagerAdapter adapter = new UserAreaActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CalendarFragment());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new HomeOldFragment());
        return adapter;
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
        Intent intent = new Intent(this, CreateEvent.class);
        startActivity(intent);
    }

    private void setDisplayPicture() {
        profilePictureView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        if (user.getPhotoUrl() == null) {
            Glide.with(this)
                    .load(R.drawable.profile_image)
                    .into(profilePictureView);
        } else {
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profilePictureView);
        }


        profilePictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                builder.setItems(R.array.Picture_Options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Choose Picture"), 1);
                                break;
                            case 1:
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(null)
                                        .build();
                                user.updateProfile(profileUpdate)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplicationContext(), "Profile photo removed.", Toast.LENGTH_SHORT).show();
                                                setDisplayPicture();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                        }
                    }
                });
                builder.show();

            }
        });
    }


    private void setDrawer_username() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        drawer_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.drawer_username);
        drawer_username.setText(user.getDisplayName());
    }


    private void countInboxItems() {
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child("Inbox").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                inboxCounter = 0;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot dsChild : ds.getChildren()) {
                        //inboxCounter = (int)dsChild.getChildrenCount();
                        inboxCounter++;
                        System.out.println("Inboxcounter was called with key :" + dsChild.getKey());
                    }
                }
                if (inboxCounter > 0) {
                    SpannableString spanString = new SpannableString("Inbox (" + inboxCounter + ")");
                    spanString.setSpan(new ForegroundColorSpan(Color.RED), 0, spanString.length(), 0);
                    navigationView.getMenu().findItem(R.id.nav_inbox).setTitle(spanString);

                } else {
                    navigationView.getMenu().findItem(R.id.nav_inbox).setTitle("Inbox");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addRequestListener() {
        //Friend Request Listener
        mDatabase.child("Users").child(auth.getCurrentUser().getUid()).child(Utils.INBOX).child(Utils.USER_ADD_REQUEST).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists())
                    return;
                String contact_display_name = dataSnapshot.getValue().toString();
                addRequestNotification(contact_display_name);
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
        mDatabase.child(Utils.USER).child(auth.getCurrentUser().getUid()).child(Utils.INBOX).child(Utils.USER_EVENT_REQUEST).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (!dataSnapshot.exists())
                    return;
                mDatabase.child(Utils.EVENT_DATABASE).child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = dataSnapshot.getValue(Event.class);
                            eventRequestNotification(e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
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

    private void eventRequestNotification(Event e) {
        if (user == null)
            return;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_twitter_bird_white_24dp)
                        .setContentTitle(this.getString(R.string.app_name))
                        .setContentText(e.getCreator() + " invites you to " + e.getEvent_name());
        // Sets an unique ID for the addRequestNotification
        int mNotificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the addRequestNotification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void addRequestNotification(String contact_display_name) {
        if (user == null)
            return;
        Intent myIntent = new Intent(UserAreaActivity.this, InboxActivity.class);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_twitter_bird_white_24dp)
                        .setContentTitle(this.getString(R.string.app_name))
                        .setContentText(contact_display_name + " would like to add you!");

        // Sets an unique ID for the addRequestNotification
        int mNotificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the addRequestNotification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


}
