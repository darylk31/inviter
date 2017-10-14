package invite.hfad.com.inviter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import invite.hfad.com.inviter.EventObjectModel.EventViewPager;
import invite.hfad.com.inviter.Inbox.InboxActivity;

/**
 * Created by Daryl on 9/14/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public int CHAT_NOTIFICATION = 001;
    public int INBOX_NOTIFICATION = 002;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map map = remoteMessage.getData();
        String type = map.get("type").toString();
        String title = null;
        String body = null;
        String eventID = null;
        String tag = null;
        switch (type) {
            case "Chat":
                title = map.get("title").toString();
                body = map.get("body").toString();
                eventID = map.get("eventID").toString();
                break;
            case "EventRequest":
                SharedPreferences eventPref = getSharedPreferences(Utils.APP_PACKAGE, 0);
                int eventCount = eventPref.getInt("eventNotifications", 0);
                eventCount++;
                SharedPreferences.Editor eventEditor = eventPref.edit();
                eventEditor.putInt("eventNotifications", eventCount);
                eventEditor.commit();

                tag = "EventRequest";

                if (eventCount == 1){
                title = "Event Request";}
                else {
                    title = "Event Request (" + eventCount + ")";
                }
                body = map.get("body").toString();
                break;
            case "FriendRequest":
                SharedPreferences friendPref = getSharedPreferences(Utils.APP_PACKAGE, 0);
                int friendCount = friendPref.getInt("friendNotifications", 0);
                friendCount++;
                SharedPreferences.Editor friendEditor = friendPref.edit();
                friendEditor.putInt("friendNotifications", friendCount);
                friendEditor.commit();

                tag = "FriendRequest";

                if (friendCount == 0){
                    title = "Friend Request";}
                else {
                    title = "Friend Request (" + friendCount + ")";
                }
                body = map.get("body").toString();
                break;
        }
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_alarm_black_24dp)
                        .setContentTitle(title)
                        .setContentText(body);

        mBuilder.setAutoCancel(true);

        if (eventID != null) {
            Intent eventIntent = new Intent(getApplicationContext(), EventViewPager.class);
            eventIntent.putExtra("event_id", eventID);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    eventIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            mBuilder.setContentIntent(resultPendingIntent);

            mNotifyMgr.notify(eventID, CHAT_NOTIFICATION, mBuilder.build());
        }
        else {
            Intent inboxIntent = new Intent(getApplicationContext(), InboxActivity.class);

            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0,
                    inboxIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            mBuilder.setContentIntent(resultPendingIntent);


            mNotifyMgr.notify(tag, INBOX_NOTIFICATION, mBuilder.build());
        }
    }
}
