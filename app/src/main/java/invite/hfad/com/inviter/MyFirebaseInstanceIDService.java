package invite.hfad.com.inviter;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Daryl on 9/14/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        setToken(refreshedToken);


    }

    public void setToken(String token){
        //TODO: upload to User table
    }

}
