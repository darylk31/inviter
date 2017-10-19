package invite.hfad.com.inviter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().hide();
        this.id = getIntent().getStringExtra("chat_id");
        String username = getIntent().getStringExtra("username");
        Bundle bundle = new Bundle();
        bundle.putString("chat_id", id);
        bundle.putString("username",username);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
        transaction.replace(R.id.chat_frame, chatFragment).commit();
    }
}
