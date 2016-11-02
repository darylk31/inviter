package invite.hfad.com.inviter;


import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


public class ContactsActivity extends AppCompatActivity{

    private String yearData;
    private String monthData;
    private String dayData;
    private String titleData;
    private String descriptionData;
    private String hourData;
    private String minuteData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            yearData = extras.getString("<yearData>");
            monthData = extras.getString("monthData");
            dayData = extras.getString("dayData");
            hourData = extras.getString("hourData");
            minuteData = extras.getString("minuteData");
            titleData= extras.getString("titleData");
            descriptionData = extras.getString("descriptionData");
        }


        ContactsFragment contactsFragment = new ContactsFragment();

        //SEND DATA TO FRAGMENT
        //contactsFragment.setArguments(extras);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contacts_container,contactsFragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

}
