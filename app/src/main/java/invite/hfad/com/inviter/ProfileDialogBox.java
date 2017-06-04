package invite.hfad.com.inviter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Created by Jimmy on 6/3/2017.
 */

public class ProfileDialogBox extends Dialog implements android.view.View.OnClickListener{

    public ProfileDialogBox(Activity a){
        super(a);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_profile_dialog);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            default:
                break;
        }
        dismiss();
    }
}
