package invite.hfad.com.inviter;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by Daryl on 9/14/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().isEmpty()) {
            System.out.println("Firebase: Received Notification");
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody());

            int mNotificationId = 001;

            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotifyMgr.notify(mNotificationId, builder.build());

        }

        else {
            System.out.println("Firebase: Received Data");
            Map map = remoteMessage.getData();
            String title = map.get("title").toString();
            String body = map.get("body").toString();
            String eventID = map.get("eventID").toString();

            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getBaseContext())
                            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                            .setContentTitle(title)
                            .setContentText(body);

            int mNotificationId = 1;
            mNotifyMgr.notify(eventID, mNotificationId, mBuilder.build());
        }
    }
}
