package com.team8.socialmedia.notifications;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM Token", "Refreshed token: " + token);

        // If you need to send the token to your server, you can do it here
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here if needed
        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM Data Payload", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("FCM Notification", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendRegistrationToServer(String tokenRef) {
        // Implement this method to send the FCM token to your server if needed
        // For example, you might want to associate the token with the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
            Token token = new Token(tokenRef);
            ref.child(user.getUid()).setValue(token);
        }
    }
}
