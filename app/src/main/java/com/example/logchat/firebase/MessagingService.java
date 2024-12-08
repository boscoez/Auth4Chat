package com.example.logchat.firebase;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
/**
 * A service class that extends FirebaseMessagingService to handle token generation and incoming
 * remote messages from Firebase Cloud Messaging (FCM).
 */
public class MessagingService extends FirebaseMessagingService {
    /**
     * Called whenever a new token is generated for the device instance.
     * This is triggered when the FCM registration token changes, such as during initial generation
     * or if the token is invalidated (e.g., device restore, app reinstallation).
     * @param token The unique token used for sending messages to this application instance.
     *              This token is the same as the one retrieved by {@link com.google.firebase.messaging.FirebaseMessaging#getToken()}.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Log the new FCM token for debugging purposes.
        Log.d("FCM", "Token: " + token);
        // TODO: Send the token to your server or store it as needed for further messaging.
    }
    /**
     * Called when a remote message is received from FCM.
     * This can include notifications and data payloads.
     * @param message The remote message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        // Log the message body from the notification payload, if available.
        if (message.getNotification() != null) {
            Log.d("FCM", "Message: " + message.getNotification().getBody());
        }
        // TODO: Handle the message payload (e.g., display a notification, update the UI, or trigger actions).
    }
}