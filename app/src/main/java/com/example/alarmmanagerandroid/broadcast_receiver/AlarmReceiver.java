package com.example.alarmmanagerandroid.broadcast_receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.alarmmanagerandroid.DestinationActivity;
import com.example.alarmmanagerandroid.R;

/// this BroadcastReceiver has to be specified in the androidManifest.xml file
public class AlarmReceiver extends BroadcastReceiver {

    /// this code will run when the BroadcastReceiver triggers our call
    @Override
    public void onReceive(Context context, Intent intent) {

        /// create the pending intent to be passed to notification
        Intent destinationIntent = new Intent(context, DestinationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, destinationIntent, PendingIntent.FLAG_IMMUTABLE);

        /// create the notification & show it
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificationChannelId")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Alarm Manager demo app")
                .setContentText("This is the content text")
                .setAutoCancel(true) /// remove from notification tray, when notification is pressed
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH) /// set the priority according to the notification priority
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123, builder.build());

    }
}
