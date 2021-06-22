package il.ac.hit.gamersarc;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "serviceTag";
    NotificationManager manager;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference reference = storage.getReference();
    DataBaseClass dataBaseClass = DataBaseClass.getInstance();
    final int NOTIF_ID = 11;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (remoteMessage.getData().get("messageType").equals("friendRequestAccepted")){
                //create notification
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                String userId = remoteMessage.getData().get("userId");
                createRequestAcceptedNotif(title, body, userId);
            }

            else if(remoteMessage.getData().get("messageType").equals("message")){
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                String userId = remoteMessage.getData().get("userId");
                String userMessageTime = remoteMessage.getData().get("time");
                Intent intent = new Intent("messagesReceiver");
                intent.putExtra("message",body);
                intent.putExtra("userId",userId);
                intent.putExtra("time",userMessageTime);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                createNotifMessages(title,body);


            } else if(remoteMessage.getData().get("messageType").equals("eventCancel")){
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                createEventCanceledNotif(title, body);

            } else if (remoteMessage.getData().get("messageType").equals("friendRequest")){
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                createEventCanceledNotif(title, body);
            }
            else if (remoteMessage.getData().get("messageType").equals("eventRequest")){
                String title = remoteMessage.getData().get("title");
                String body = remoteMessage.getData().get("body");
                createEventCanceledNotif(title, body);
            }



        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());


        }

    }

    private void createEventCanceledNotif(String title, String body) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= 26){
            String channelId  = "channelId";
            NotificationChannel channel = new NotificationChannel(channelId, "channelName", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId( channelId);
        }
        builder.setContentText(body).setContentTitle(title).setSmallIcon(android.R.drawable.star_on);
        builder.setContentIntent(pendingIntent);
        manager.notify(NOTIF_ID, builder.build());
    }

    private void createRequestAcceptedNotif(String title, String body, String userId) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= 26){
            String channelId  = "channelId";
            NotificationChannel channel = new NotificationChannel(channelId, "channelName", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId( channelId);
        }
        builder.setContentText(body).setContentTitle(title);
        builder.setSmallIcon(android.R.drawable.star_on);
        builder.setContentIntent(pendingIntent);
        manager.notify(NOTIF_ID, builder.build());
}


    private void createNotifMessages(String title, String body) {

        final int NOTIF_ID = 11;
        final Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= 26){
            String channelId  = "channelId";
            NotificationChannel channel = new NotificationChannel(channelId, "channelName", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId( channelId);
        }
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        builder.setContentText(body).setContentTitle(title).setSmallIcon(android.R.drawable.star_on);
        manager.notify(NOTIF_ID, builder.build());

    }

}

