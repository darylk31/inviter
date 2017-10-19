package invite.hfad.com.inviter;

import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Daryl on 9/14/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken != null) {
            Utils.getDatabase().getReference().child(Utils.USER).child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())
                    .child(Utils.USER_TOKEN).child(refreshedToken).setValue(refreshedToken);
        }
    }
}
