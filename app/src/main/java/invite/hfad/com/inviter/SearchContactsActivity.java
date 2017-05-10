package invite.hfad.com.inviter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;

public class SearchContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        SearchView search = (SearchView) findViewById(R.id.searchview_contacts);
        search.onActionViewExpanded();
    }

}
