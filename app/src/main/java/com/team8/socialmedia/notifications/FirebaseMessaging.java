package com.team8.socialmedia.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.team8.socialmedia.R;
import com.team8.socialmedia.activities.PostDetailActivity;
import com.team8.socialmedia.activities.ChatActivity;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        //get current user from shared preferences
        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_USERID", "None");
        System.out.println(message.getData()+"");
        String notificationType = message.getData().get("notificationType");
        if (notificationType.equals("PostNotification")) {
            //post notification
            String sender= message.getData().get("sender");
            String pId= message.getData().get("pId");
            String pTitle= message.getData().get("pTitle");
            String pDescription= message.getData().get("pDescription");
            //if user is same that has posted don't show notification
            if(!sender.equals(savedCurrentUser)){
                showPostNotification(""+pId,""+pTitle,""+pDescription);
            }
        } else if (notificationType.equals("ChatNotification")) {
            //chat notification
            String sent = message.getData().get("sent");
            String user = message.getData().get("user");
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fUser != null && sent.equals(fUser.getUid())) {
                if (!savedCurrentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOAndAboveNotification(message);
                    } else {
                        sendNormalNotification(message);
                    }
                }
            }
        }


    }

    private void showPostNotification(String pId, String pTitle, String pDescription) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID= new Random().nextInt(3000);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupPostNotificationChannel(notificationManager);
        }

        //show post detail activity using post id when notification clicked
        Intent intent= new Intent(this, PostDetailActivity.class);
        intent.putExtra("postId",pId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        //LargeIcon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        //sound for notification
        Uri notificationSounUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder= new NotificationCompat.Builder(this, ""+ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(pTitle)
                .setContentText(pDescription)
                .setSound(notificationSounUri)
                .setContentIntent(pendingIntent);

        //show notification
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    private void setupPostNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName="New Notification";
        String channelDescription = "Device to device post notification";

        NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(channelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if(notificationManager!=null){
            notificationManager.createNotificationChannel(adminChannel);
        }


    }

    private void sendNormalNotification(RemoteMessage message) {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defSoundUri)
                .setContentIntent(pIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int j = 0;
        if (j > 0) {
            j = i;
        }
        notificationManager.notify(j, builder.build());
    }

    private void sendOAndAboveNotification(RemoteMessage message) {
        String user = message.getData().get("user");
        String icon = message.getData().get("icon");
        String title = message.getData().get("title");
        String body = message.getData().get("body");

        RemoteMessage.Notification notification = message.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("hisUid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_MUTABLE);
        Uri defSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getONotifications(title, body, pIntent, defSoundUri, icon);

        int j = 0;
        if (j > 0) {
            j = i;
        }
        notification1.getManger().notify(j, builder.build());
    }

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);
        //update user token
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            //signed in, update token
            updateToken(token);
        }
    }

    private void updateToken(String tokenRefesh) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefesh);
        ref.child(user.getUid()).setValue(token);
    }

}
