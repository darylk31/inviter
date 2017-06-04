package invite.hfad.com.inviter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Jimmy on 6/3/2017.
 */

public class ProfileDialogBox extends Dialog implements android.view.View.OnClickListener{
    private String username;

    public ProfileDialogBox(Activity a,String username){
        super(a);
        this.username = username;
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
