package invite.hfad.com.inviter;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import invite.hfad.com.inviter.DialogBox.ChatDialogFragment;
import invite.hfad.com.inviter.EventObjectModel.PinFragment;

import static android.content.Context.MODE_PRIVATE;


public class ChatFragment extends Fragment {

    //Event id
    private String id;
    private String username;

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
    private FirebaseRecyclerAdapter<FriendlyMessage, ChatFragment.MessageViewHolder> mFirebaseAdapter;
    private EditText mMessageEditText;
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 60;
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private Button mSendButton;
    private ImageView mAddMessageImageView;
    private View rootView;
    private Toolbar toolbar;
    private TextView noChatText;
    private RecyclerView.AdapterDataObserver dataObserver;

    private SharedPreferences sharedPref;
    private User user;

    //Store username of previous message
    private String previousMessageName;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //id = getArguments().getString("event_id");
        id = "TEST";
        View view = inflater.inflate(R.layout.fragment_event_chat, container, false);
        toolbar = (Toolbar) view.findViewById(R.id.event_chat_toolbar);
        id = getArguments().getString("chat_id");
        this.username = getArguments().getString("username");
        toolbar.setTitle(username);
        noChatText = (TextView) view.findViewById(R.id.tvNoChat);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        rootView = getView();
        Gson gson = new Gson();
        sharedPref = getActivity().getSharedPreferences(Utils.APP_PACKAGE, MODE_PRIVATE);
        String json = sharedPref.getString("userObject", "");
        user = gson.fromJson(json, User.class);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        System.out.println("createOptionsMenu");
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public void onStart(){
        populateFragment();
        super.onStart();
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public ImageView messageImageView;
        public TextView messengerTextView;
        public CircleImageView messengerImageView;
        public TextView messageTimeStamp;
        private String id;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messageImageView = (ImageView) itemView.findViewById(R.id.messageImageView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
            messageTimeStamp = (TextView) itemView.findViewById(R.id.messageTimeStamp);
        }

    }

    private void populateFragment(){
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Set default username as anonymous
        mUsername = ANONYMOUS;
        //Initalize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //TODO:: Check if user is logged in?
        mUsername = mFirebaseUser.getDisplayName();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }
        //Initalize ProgressBar and RecyclerView
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) rootView.findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        //New child entries
        mFirebaseDatabaseReference = Utils.getDatabase().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, ChatFragment.MessageViewHolder>(
                FriendlyMessage.class,
                R.layout.item_message,
                ChatFragment.MessageViewHolder.class,
                mFirebaseDatabaseReference.child(Utils.CHAT_DATABASE).child(id).child(Utils.CHAT).limitToLast(50)) {

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder, final FriendlyMessage friendlyMessage, int position) {
                checkForMessageText();
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (friendlyMessage.getText() != null) {
                    viewHolder.messageTextView.setText(friendlyMessage.getText());
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
                viewHolder.messengerTextView.setText(friendlyMessage.getDisplayname());
                if (friendlyMessage.getPhotoUrl() == null) {
                    viewHolder.messageImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(),
                            R.drawable.ic_account_circle_black_36dp));
                } else {
                    Glide.with(ChatFragment.this)
                            .load(friendlyMessage.getPhotoUrl())
                            .into(viewHolder.messengerImageView);
                }
                //TimeStamp
                viewHolder.messageTimeStamp.setVisibility(TextView.GONE);
                if(friendlyMessage.getTimeStamp() != null){
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    try {
                        Date date = format.parse(friendlyMessage.getTimeStamp());
                        int hour = date.getHours() % 12;
                        if (hour == 0)
                            hour = 12;
                        String timeText = String.format("%02d:%02d %s", hour ,date.getMinutes(), date.getHours() < 12 ? "AM" : "PM");
                        viewHolder.messageTimeStamp.setText(timeText);
                        System.out.println( "this is the sent time" + date.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                //If it's my message
                Drawable otherBubble = ContextCompat.getDrawable(getContext(), R.drawable.chat_bubble_ex1);
                Drawable myBubble = ContextCompat.getDrawable(getContext(), R.drawable.chat_bubble_ex2);
                Drawable systemBubble = ContextCompat.getDrawable(getContext(), R.drawable.chat_bubble_ex3);
                viewHolder.messageTextView.setTextColor(Color.WHITE);
                //Check for admin Messages
                if(friendlyMessage.getName().equals(Utils.APP)){
                    viewHolder.messengerTextView.setVisibility(View.GONE);
                    viewHolder.messageTextView.setBackground(systemBubble);
                }
                //Check for my messages
                else if (friendlyMessage.getName().equals(user.getUsername())) {
                    viewHolder.messageTextView.setBackground(myBubble);
                }
                //Everyone elses messages
                else {
                    viewHolder.messageTextView.setBackground(otherBubble);
                }
                //Check to see if previous message was said by the same person
                if(position - 1 >= 0) {
                    if (mFirebaseAdapter.getItem(position - 1).getName().equals(friendlyMessage.getName())) {
                        viewHolder.messengerTextView.setVisibility(View.GONE);
                        viewHolder.messengerImageView.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder.messengerTextView.setVisibility(View.VISIBLE);
                        viewHolder.messengerImageView.setVisibility(View.VISIBLE);
                    }
                }
                previousMessageName = friendlyMessage.getName();
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(friendlyMessage.getId() != null) {
                            DialogFragment chatDialogFragment = new ChatDialogFragment();
                            Bundle args = new Bundle();
                            args.putString("message",friendlyMessage.getText());
                            args.putString("message_id",friendlyMessage.getId());
                            args.putString("id",id);
                            chatDialogFragment.setArguments(args);
                            android.app.FragmentManager fm = getActivity().getFragmentManager();
                            chatDialogFragment.show(fm,"dialog");
                        }
                        return true;
                    }
                });
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(viewHolder.messageTimeStamp.getVisibility() == View.GONE){
                            viewHolder.messageTimeStamp.animate().alpha(1.0f);
                            viewHolder.messageTimeStamp.setVisibility(View.VISIBLE);
                        } else{
                            viewHolder.messageTimeStamp.setVisibility(View.GONE);
                            viewHolder.messageTimeStamp.animate().alpha(0.0f);
                        }
                    }
                });
            }
        };

        dataObserver = new RecyclerView.AdapterDataObserver() {
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
                ValueEventListener read_listener = new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            long total_messages = dataSnapshot.getChildrenCount();
                            mFirebaseDatabaseReference.child(Utils.USER).child(mFirebaseAuth.getCurrentUser().getDisplayName())
                                    .child(Utils.USER_EVENTS).child(id).child(Utils.EVENT_READ_MESSAGES).setValue(total_messages);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                mFirebaseDatabaseReference.child(Utils.CHAT_DATABASE).child(id).child(Utils.CHAT).addListenerForSingleValueEvent(read_listener);
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(dataObserver);

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);
        mMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
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
        mSendButton = (Button) rootView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String friendlyMessageId = mFirebaseDatabaseReference.child(Utils.CHAT_DATABASE).child(id).child(Utils.CHAT).push().getKey();
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                FriendlyMessage friendlyMessage = new FriendlyMessage(friendlyMessageId,mMessageEditText.getText().toString(),
                        mUsername, mPhotoUrl,timeStamp, null,user.getDisplayname());
                mFirebaseDatabaseReference.child(Utils.CHAT_DATABASE).child(id).child(Utils.CHAT).child(friendlyMessageId).setValue(friendlyMessage);
                mMessageEditText.setText("");
                //Update event's last update message
                mFirebaseDatabaseReference.child(Utils.CHAT_DATABASE).child(id).child(Utils.EVENT_LAST_MODIFIED).setValue(Utils.getCurrentDate());
            }
        });
        //Send Images
        mAddMessageImageView = (ImageView)
                rootView.findViewById(R.id.addMessageImageView);
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
        checkForMessageText();
    }

    private void checkForMessageText(){
        mFirebaseDatabaseReference.child(Utils.CHAT_DATABASE).child(id).child(Utils.CHAT).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    noChatText.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    noChatText.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStop(){
        super.onStop();
        mFirebaseAdapter.unregisterAdapterDataObserver(dataObserver);
    }
}
