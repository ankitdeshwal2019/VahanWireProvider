package com.electrom.vahanwireprovider.notification_service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.features.AmbulanceHomePage;
import com.electrom.vahanwireprovider.features.BookingHistoryMechanic;
import com.electrom.vahanwireprovider.features.BookingStatusMechanic;
import com.electrom.vahanwireprovider.features.MachanicHomePage;
import com.electrom.vahanwireprovider.new_app_driver.DriverHomePage;
import com.electrom.vahanwireprovider.new_app_tow.TowHomePage;
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

        Log.d(TAG, "From: " + remoteMessage.getData().get("sound"));

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: all data " + remoteMessage.getData().get("notification_details"));
            Log.e(TAG, "Message data payload: data  " + remoteMessage.getData());
            String notification_details = remoteMessage.getData().get("notification_details");
            String user_details = remoteMessage.getData().get("user_details");
            final String tag = remoteMessage.getData().get("tag");
            String type ="";

            JSONObject data = null;
            String booking_id = null;

            if(tag.equals("provider"))
            {
                Log.e(TAG, "onMessageReceived: version " +"success " );
                sendMyNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("tag"), remoteMessage.getData().get("tag_whosend"));
            }

            if(tag.contains("tow"))
            {
                try {
                    data = new JSONObject(notification_details);
                    type = data.isNull("request_type") ? "" : data.getString("request_type");
                    if(type.equals("emergency"))
                    {
                        Log.e(TAG, "onMessageReceived: " + "pre request");

                        booking_id = data.getString("booking_id");
                        JSONObject booking_status = data.getJSONObject("booking_status");
                        JSONObject user_status = booking_status.getJSONObject("user");
                        JSONObject tow = booking_status.getJSONObject("tow");
                        String status_u = user_status.getString("status");
                        String status = tow.getString("status");

                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_ISSUE, "Tow Request");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_u);

                        Log.e(TAG, "onMessageReceived:BOOKING_ID " + booking_id);
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS " + status);
                        Log.e(TAG, "onMessageReceived:NOTI_ISSUE " + "pre request");
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS_USER " + status_u);

                    }
                    else {

                        Log.e(TAG, "onMessageReceived: " + "emergency");
                        booking_id = data.getString("booking_id");
                        JSONObject booking_status = data.getJSONObject("booking_status");
                        String issue = data.getString("issue");
                        JSONObject mech = booking_status.getJSONObject("mechanic");
                        String status = mech.getString("status");
                        JSONObject user_status = booking_status.getJSONObject("user");
                        String status_u = user_status.getString("status");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_ISSUE, issue);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_u);

                        Log.e(TAG, "onMessageReceived:BOOKING_ID " + booking_id);
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS " + status);
                        Log.e(TAG, "onMessageReceived:NOTI_ISSUE " + issue);
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS_USER " + status_u);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(tag.contains("driver"))
            {
                try {
                    data = new JSONObject(notification_details);
                    type = data.isNull("request_type") ? "" : data.getString("request_type");
                    if(type.equals("prerequest"))
                    {
                        Log.e(TAG, "onMessageReceived: " + "pre request");
                        booking_id = data.getString("booking_id");
                        JSONObject booking_status = data.getJSONObject("booking_status");
                        JSONObject mech = booking_status.getJSONObject("servicecenter");
                        JSONObject user_status = booking_status.getJSONObject("user");
                        String status = mech.getString("status");
                        String status_u = user_status.getString("status");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_ISSUE, "Pre Request");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_u);

                        Log.e(TAG, "onMessageReceived:BOOKING_ID " + booking_id);
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS " + status);
                        Log.e(TAG, "onMessageReceived:NOTI_ISSUE " + "pre request");
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS_USER " + status_u);

                    }
                    else {

                        Log.e(TAG, "onMessageReceived: " + "pre request");

                        booking_id = data.getString("booking_id");
                        JSONObject booking_status = data.getJSONObject("booking_status");
                        String issue = data.getString("issue");
                        JSONObject mech = booking_status.getJSONObject("mechanic");
                        String status = mech.getString("status");
                        JSONObject user_status = booking_status.getJSONObject("user");
                        String status_u = user_status.getString("status");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_ISSUE, issue);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_u);

                        Log.e(TAG, "onMessageReceived:BOOKING_ID " + booking_id);
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS " + status);
                        Log.e(TAG, "onMessageReceived:NOTI_ISSUE " + issue);
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS_USER " + status_u);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (tag.contains("mechanic")) {
                try {
                    data = new JSONObject(notification_details);
                    type = data.isNull("request_type") ? "" : data.getString("request_type");
                    if(type.equals("prerequest"))
                    {
                        Log.e(TAG, "onMessageReceived: " + "pre request");
                        booking_id = data.getString("booking_id");
                        JSONObject booking_status = data.getJSONObject("booking_status");
                        JSONObject mech = booking_status.getJSONObject("servicecenter");
                        JSONObject user_status = booking_status.getJSONObject("user");
                        String status = mech.getString("status");
                        String status_u = user_status.getString("status");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_ISSUE, "Pre Request");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_u);

                        Log.e(TAG, "onMessageReceived:BOOKING_ID " + booking_id);
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS " + status);
                        Log.e(TAG, "onMessageReceived:NOTI_ISSUE " + "pre request");
                        Log.e(TAG, "onMessageReceived:BOOKING_STATUS_USER " + status_u);

                    }
                    else {

                        Log.e(TAG, "onMessageReceived: " + "pre request");

                        booking_id = data.getString("booking_id");
                        JSONObject booking_status = data.getJSONObject("booking_status");
                        String issue = data.getString("issue");
                        JSONObject mech = booking_status.getJSONObject("mechanic");
                        String status = mech.getString("status");
                        JSONObject user_status = booking_status.getJSONObject("user");
                        String status_u = user_status.getString("status");
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_ISSUE, issue);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                        SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_u);

                        Log.e(TAG, "onMessageReceived: BOOKING_ID " + booking_id);
                        Log.e(TAG, "onMessageReceived: BOOKING_STATUS " + status);
                        Log.e(TAG, "onMessageReceived: NOTI_ISSUE " + issue);
                        Log.e(TAG, "onMessageReceived: BOOKING_STATUS_USER " + status_u);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {

                if (tag.contains("ambulance")) {
                    data = new JSONObject(notification_details);
                    booking_id = data.getString("booking_id");
                    JSONObject booking_status = data.getJSONObject("booking_status");
                    JSONObject ambulance = booking_status.getJSONObject("driver");
                    String status = ambulance.getString("status");
                    Log.e(TAG, "onMessageReceived: name | status " + status + " | " + booking_id);
                    JSONObject user = booking_status.getJSONObject("user");
                    String status_user = user.getString("status");
                    SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_ID, booking_id);
                    SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS, status);
                    SessionManager.getInstance(getApplicationContext()).setString(SessionManager.BOOKING_STATUS_USER, status_user);
                }

                JSONObject use = new JSONObject(user_details);
                SessionManager.getInstance(getApplicationContext()).setString(SessionManager.NOTI_NAME, use.getString("fullname"));
                Log.e(TAG, "onMessageReceived: name " + use.getString("fullname"));

                sendMyNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("tag"), type);

                if (remoteMessage.getData() != null) {
                    if (tag.equalsIgnoreCase("ambulance")) {
                        startActivity(new Intent(getApplicationContext(), AmbulanceHomePage.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    else if (tag.equalsIgnoreCase("mechanic") && type.equalsIgnoreCase("prerequest")) {
                        startActivity(new Intent(getApplicationContext(), BookingHistoryMechanic.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    else if (tag.equalsIgnoreCase("mechanic")) {
                        startActivity(new Intent(getApplicationContext(), MachanicHomePage.class)
                        //startActivity(new Intent(getApplicationContext(), BookingStatusMechanic.class)
                         .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    else if (tag.equalsIgnoreCase("driver")) {
                        startActivity(new Intent(getApplicationContext(), DriverHomePage.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    else if (tag.equalsIgnoreCase("tow")) {
                        startActivity(new Intent(getApplicationContext(), TowHomePage.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }

                    Log.e(TAG, "onMessageReceived: " + "done");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
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

    private void sendMyNotification(String title, String body, String tag, String type) {

        PendingIntent pendingIntent = null;

        if (tag.equalsIgnoreCase("ambulance")) {
            Intent intent = new Intent(this, AmbulanceHomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }

        else if(tag.equalsIgnoreCase("mechanic") && type.equalsIgnoreCase("prerequest")) {

            Intent intent = new Intent(this, BookingHistoryMechanic.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        }

        else if (tag.equalsIgnoreCase("mechanic")) {

            Intent intent = new Intent(this, MachanicHomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        }

        else if (tag.equalsIgnoreCase("driver")) {

            Intent intent = new Intent(this, DriverHomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        }

        else if (tag.equalsIgnoreCase("tow")) {

            Intent intent = new Intent(this, TowHomePage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        }

        else if (tag.equalsIgnoreCase("provider")) {

            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
            .setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, goToMarket, PendingIntent.FLAG_ONE_SHOT);
        }

        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.apple_tone);
        NotificationCompat.Builder notificationBuilder;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            notificationBuilder = new NotificationCompat.Builder(this, "CH_ID")
                    .setSmallIcon(R.drawable.not_logo)
                    .setColor(getResources().getColor(R.color.notification_color))
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setContentIntent(pendingIntent);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this, "CH_ID")
                    .setSmallIcon(R.drawable.not_logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setContentIntent(pendingIntent);
        }

       /* NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "CH_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);*/

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