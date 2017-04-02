package com.jbworks.bmwibus.ibus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by joe-work on 5/11/15.
 */
public class BootupStartReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Log.d("Service", "Service Started from Boot");
        // Start Service On Boot Start Up
        Intent service = new Intent(context, IBusMessageService.class);
        context.startService(service);

        Intent notificationIntent = new Intent(context, IBusMessageService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 01, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("BMW IBus");
        builder.setContentText("The Service is Running");
        builder.setSubText("Click to Restart");
        builder.setNumber(101);
        builder.setContentIntent(pendingIntent);
        builder.setTicker("Fancy Notification");
        builder.setSmallIcon(android.R.color.transparent);
        builder.setAutoCancel(false);
        builder.setPriority(Notification.PRIORITY_DEFAULT);
        builder.setOngoing(true);
        Notification notification = builder.build();
        NotificationManager notificationManger =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManger.notify(01, notification);

        //Start App On Boot Start Up
//            Intent App = new Intent(context, MainActivity.class);
//            App.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(App);
    }
}
