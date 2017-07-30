package invite.hfad.com.inviter;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.Contacts.ContactsActivity;


public class EventPage extends Activity {
    private static boolean TOOLBAR_COLLAPSED = true;
    String id;
    String event_string;
    Toolbar toolbar;
    AppBarLayout appBarLayout;

    //Chat
    private static final String ANONYMOUS = "Anonymous";
    private String mUsername;
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String mPhotoUrl;
    private ProgressBar mProgressBar;
    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder> mFirebaseAdapter;
    private EditText mMessageEditText;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 60;
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private Button mSendButton;
    private ImageView mAddMessageImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        this.id = getIntent().getStringExtra("event_id");
        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(this.getApplicationContext());
            SQLiteDatabase event_db = eventDatabaseHelper.getReadableDatabase();
            Cursor cursor = event_db.rawQuery("SELECT * FROM EVENTS WHERE EID LIKE '" + id + "';", null);
            cursor.moveToLast();
            TextView event_name = (TextView) findViewById(R.id.tv_eventpagename);
            this.event_string = cursor.getString(4);
            event_name.setText(event_string);
            TextView event_date = (TextView) findViewById(R.id.tv_eventpagedate);
            String event_day = cursor.getString(2);
            //TODO: Select just date.
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(event_day);
                String output_day = new SimpleDateFormat("dd", Locale.ENGLISH).format(date);
                String output_month = new SimpleDateFormat("MMM", Locale.ENGLISH).format(date);
                String output_year = new SimpleDateFormat("yyyy", Locale.ENGLISH).format(date);
                event_date.setText(output_month + " " + output_day + ", " + output_year);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //TODO: Select just time.
            TextView event_time = (TextView) findViewById(R.id.tv_eventpagetime);
            event_time.setText(cursor.getString(2));
            cursor.close();
            event_db.close();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Error: Event unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
        this.toolbar = (Toolbar) findViewById(R.id.event_toolbar);
        toolbar.inflateMenu(R.menu.menu_eventpage);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.invite_eventpage:
                        Intent invite_intent = new Intent(EventPage.this, ContactsActivity.class);
                        startActivity(invite_intent);
                        break;
                    case R.id.edit_eventpage:
                        Intent edit_intent = new Intent(EventPage.this, MakeEventActivity.class);
                        edit_intent.putExtra("Edit Id", id);
                        startActivity(edit_intent);
                        break;
                    case R.id.delete_eventpage:
                        //Alert dialog to confirm
                        deleteEvent();
                        Intent delete_intent = new Intent(EventPage.this, UserAreaActivity.class);
                        startActivity(delete_intent);
                        break;
                }
                return true;
            }
        });
        appBarListener();
        populateChat();
    }


    public void deleteEvent() {
        try {
            SQLiteOpenHelper eventDatabaseHelper = new UserDatabaseHelper(this.getApplicationContext());
            SQLiteDatabase event_db = eventDatabaseHelper.getWritableDatabase();
            UserDatabaseHelper.delete_event(event_db, id);
            event_db.close();
            startActivity(new Intent(this, UserAreaActivity.class));
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(this.getApplicationContext(), "Error: Unable to delete", Toast.LENGTH_SHORT);
            toast.show();
        }
        Toast toast = Toast.makeText(this.getApplicationContext(), "Event Deleted", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onSendMessage(View view) {
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public TextView messageTimeStamp;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            messageTimeStamp = (TextView) itemView.findViewById(R.id.messageTimeStamp);
        }
    }


    private void populateChat() {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mUsername = ANONYMOUS;
        // Initalize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        // If User is not logged
        if (mFirebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        // Initalize ProgressBar and RecyclerView
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        // New child entires
        mFirebaseDatabaseReference = Utils.getDatabase().getReference();
        System.out.println(id);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                MessageViewHolder.class,
                mFirebaseDatabaseReference.child(Utils.EVENT).child(id).child(Utils.CHAT)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, FriendlyMessage friendlyMessage, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
                    System.out.println(friendlyMessage.getText());
                    viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
                    viewHolder.messageImageView.setVisibility(ImageView.GONE);
                } else {
                    String imageUrl = friendlyMessage.getImageUrl();
                    //TODO:: DOWNLOAD IMAGE?
                    if (imageUrl.startsWith("gs://")) {
                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReferenceFromUrl(imageUrl);
                        storageReference.getDownloadUrl().addOnCompleteListener(
                                new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUrl = task.getResult().toString();
                                            Glide.with(viewHolder.messageImageView.getContext())
                                                    .load(downloadUrl)
                                                    .into(viewHolder.messageImageView);
                                        } else {
                                        }
                                    }
                                });
                    } else {
                        Glide.with(viewHolder.messageImageView.getContext())
                                .load(friendlyMessage.getImageUrl())
                                .into(viewHolder.messageImageView);
                    }
                    viewHolder.messageImageView.setVisibility(ImageView.VISIBLE);
                    viewHolder.messageTextView.setVisibility(TextView.GONE);
                }
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messageImageView.setImageDrawable(ContextCompat.getDrawable(EventPage.this,
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(EventPage.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
                //TimeStamp
                if(friendlyMessage.getTimeStamp() != null){
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    try {
                        Date date = format.parse(friendlyMessage.getTimeStamp());
                        viewHolder.messageTimeStamp.setText(date.toString());
                        System.out.println( "this is the sent time" + date.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(mSharedPreferences
                        .getInt(Utils.FRIENDLY_MSG_LENGTH, DEFAULT_MSG_LENGTH_LIMIT))
        });
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //Send Chat text
        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(),
                        mUsername, mPhotoUrl,timeStamp, null);
                mFirebaseDatabaseReference.child(Utils.EVENT).child(id).child(Utils.CHAT).push().setValue(friendlyMessage);
                mMessageEditText.setText("");
            }
        });
        //Send Images
        mAddMessageImageView = (ImageView)
                findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Select image for image message on click.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

        private void appBarListener(){
            appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBarLayout.setExpanded(false);
            final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            //Get Title Showing
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    int collapsed_num = appBarLayout.getTotalScrollRange();

                    if(verticalOffset == -collapsed_num){
                        collapsingToolbarLayout.setTitleEnabled(true);
                        collapsingToolbarLayout.setTitle(event_string);
                    } else{
                        collapsingToolbarLayout.setTitleEnabled(false);
                    }
                    if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                        // Collapsed
                        TOOLBAR_COLLAPSED = true;
                    }
                    else if (verticalOffset == 0) {
                        TOOLBAR_COLLAPSED = false;
                    } else{
                        TOOLBAR_COLLAPSED = false;
                    }
                }}
            );
        }

        @Override
        public void onBackPressed() {
            if(TOOLBAR_COLLAPSED){
                super.onBackPressed();
            } else {
                AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar_layout);
                appBar.setExpanded(false, true);
            }
        }



    /*
    public void Test(){
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        appBarLayout.setExpanded(false);
    }
    */

}
