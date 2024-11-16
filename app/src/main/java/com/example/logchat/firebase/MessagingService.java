package com.example.logchat.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    /**
     * @param token The token used for sending messages to this application instance. This token is
     *              the same as the one retrieved by {@link FirebaseMessaging#getToken()}.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM","Token: "+token);
    }

    /**
     * @param message Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("FCM","Message: "+message.getNotification().getBody());
    }
}
