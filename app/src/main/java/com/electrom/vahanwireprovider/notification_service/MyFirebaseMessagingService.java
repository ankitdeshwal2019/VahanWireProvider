package com.electrom.vahanwireprovider.notification_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.electrom.vahanwireprovider.MainActivity;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.features.AmbulanceProvider;
import com.electrom.vahanwireprovider.features.MachanicHomePage;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getData().get("sound"));

        // payload. {tag=test tag, body=test body, sound=apple_tone.mp3, title=test title}

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData().get("notification_details"));
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
            String Obj =  remoteMessage.getData().get("notification_details");
            String userD =  remoteMessage.getData().get("user_details");
            final String tag = remoteMessage.getData().get("tag");
            try {


                JSONObject data = new JSONObject(Obj);
                String booking_id = data.getString("booking_id");
                JSONObject booking_status = data.getJSONObject("booking_status");
                SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                Log.e(TAG, "onMessageReceived: " + booking_id );
                if(tag.contains("mechanic"))
                {
                    String issue = data.getString("issue");
                    JSONObject mech = booking_status.getJSONObject("mechanic");
                    String b_s = mech.getString("cancel_reason");
                    String status = mech.getString("status");
                    Log.e(TAG, "onMessageReceived: name | essue " + issue);
                    SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_ISSUE,  issue);
                    SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                }

                if(tag.contains("ambulance"))
                {
                    JSONObject ambulance = booking_status.getJSONObject("driver");
                    String b_s = ambulance.getString("cancel_reason");
                    String status = ambulance.getString("status");
                    SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);

                    Log.e(TAG, "onMessageReceived: name | status" + status + " | " + booking_id);
                }

                JSONObject user = booking_status.getJSONObject("user");
                String b_s_user = user.getString("cancel_reason");
                String status_user = user.getString("status");

                SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_user);

                JSONObject use = new JSONObject(userD);

                SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_NAME,  use.getString("fullname"));


                sendMyNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("tag"));

                if(remoteMessage.getData()!=null)
                {

                            if (tag.equalsIgnoreCase("ambulance")) {
                                startActivity(new Intent(getApplicationContext(), AmbulanceProvider.class));
                            }

                            if (tag.equalsIgnoreCase("mechanic")) {
                                startActivity(new Intent(getApplicationContext(), MachanicHomePage.class));
                            }

                            Log.e(TAG, "onMessageReceived: " + "done" );

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            catch (NullPointerException e){
                e.printStackTrace();
            }

            //sendMyNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("tag"));

        }

        // Check if message contains a notification
        if (remoteMessage.getNotification() != null) {
            //sendNotification(remoteMessage.getNotification().getBody());

        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]
    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    /*private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    *//**
     * Handle time allotted to BroadcastReceivers.
     *//*
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }*/

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */

    private void sendMyNotification(String title, String body, String tag) {

        PendingIntent pendingIntent = null;

        if (tag.equalsIgnoreCase("ambulance")) {
            Intent intent = new Intent(this, AmbulanceProvider.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        if (tag.equalsIgnoreCase("mechanic")) {

            Intent intent = new Intent(this, MachanicHomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        }

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.apple_tone);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "CH_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if (soundUri != null) {
                // Changing Default mode of notification
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
                // Creating an Audio Attribute
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                // Creating Channel
                NotificationChannel notificationChannel = new NotificationChannel("CH_ID", "Testing_Audio", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setSound(soundUri, audioAttributes);
                mNotificationManager.createNotificationChannel(notificationChannel);
            }
        }
        mNotificationManager.notify(0, notificationBuilder.build());

        /*try {
            // The line below will set it as a default ring tone replace
            // RingtoneManager.TYPE_RINGTONE with RingtoneManager.TYPE_NOTIFICATION
            // to set it as a notification tone
            RingtoneManager.setActualDefaultRingtoneUri(
                    getApplicationContext(), RingtoneManager.TYPE_RINGTONE,
                    soundUri);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
            r.play();
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}