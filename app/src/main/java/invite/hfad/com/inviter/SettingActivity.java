package invite.hfad.com.inviter;

import android.app.Activity;
import android.os.Bundle;

public class SettingActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
    }

}
