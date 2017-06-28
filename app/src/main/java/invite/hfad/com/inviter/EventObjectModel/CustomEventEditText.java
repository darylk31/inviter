package invite.hfad.com.inviter.EventObjectModel;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.view.KeyEvent;
import android.widget.EditText;

import invite.hfad.com.inviter.R;

/**
 * Created by Jimmy on 6/27/2017.
 */

public class CustomEventEditText extends android.support.v7.widget.AppCompatEditText {
    public CustomEventEditText(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event)
    {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode())
        {
            AppBarLayout appBar = (AppBarLayout) findViewById(R.id.appbar_layout);
            appBar.setExpanded(false, true);

            //Want to call this method which will append text
            //init();
        }
        return false;
    }
}
