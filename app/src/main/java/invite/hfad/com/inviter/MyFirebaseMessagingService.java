package invite.hfad.com.inviter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import invite.hfad.com.inviter.EventObjectModel.EventViewPager;
import invite.hfad.com.inviter.Inbox.InboxActivity;

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

            builder.setAutoCancel(true);

            Intent inboxIntent = new Intent(getApplicationContext(), InboxActivity.class);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    inboxIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            builder.setContentIntent(resultPendingIntent);

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
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                            .setContentTitle(title)
                            .setContentText(body);

            mBuilder.setAutoCancel(true);

            Intent eventIntent = new Intent(getApplicationContext(), EventViewPager.class);
            eventIntent.putExtra("event_id", eventID);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    eventIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            mBuilder.setContentIntent(resultPendingIntent);

            int mNotificationId = 001;

            mNotifyMgr.notify(eventID, mNotificationId, mBuilder.build());
        }
    }
}
