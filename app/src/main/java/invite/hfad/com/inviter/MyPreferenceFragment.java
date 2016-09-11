package invite.hfad.com.inviter;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Jimmy on 9/6/2016.
 */
public class MyPreferenceFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
