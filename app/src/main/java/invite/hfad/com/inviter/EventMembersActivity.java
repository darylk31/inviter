package invite.hfad.com.inviter;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import invite.hfad.com.inviter.Contacts.FriendsFragment;
import invite.hfad.com.inviter.Contacts.PhoneContactsFragment;
import invite.hfad.com.inviter.Contacts.SearchContactsActivity;

public class EventMembersActivity extends AppCompatActivity {

    private String id;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_members);
        this.id = getIntent().getStringExtra("event_id");
        getSupportActionBar().setTitle("Group Members");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.eventMembers_viewpager);
        setupViewPager();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle args = new Bundle();
        args.putString("event_id", id);
        EventMembersFragment membersFragment = new EventMembersFragment();
        membersFragment.setArguments(args);
        EventPendingFragment pendingFragment = new EventPendingFragment();
        pendingFragment.setArguments(args);

        adapter.addFragment(membersFragment, "Members");
        adapter.addFragment(pendingFragment, "Pending");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, EventViewPager.class)
                        .putExtra("event_id", id)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                );
                finish();
                break;
        }
        return true;
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
