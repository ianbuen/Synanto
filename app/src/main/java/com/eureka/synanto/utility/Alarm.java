package com.eureka.synanto.utility;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.eureka.synanto.R;
import com.eureka.synanto.firebase.NotifyTask;
import com.eureka.synanto.fragments.CreateFragment;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        String eventID, userID;

//        if (intent.getExtras() != null || true) {
//            eventID = intent.getExtras().getString("eventID");
//            userID = intent.getExtras().getString("userID");

//            new NotifyTask(userID, eventID, NotifyTask.EVENT_REMINDER);
//        }

//        new NotifyTask(CreateFragment.userID, CreateFragment.eventID, NotifyTask.EVENT_REMINDER, CreateFragment.userID);

        String event, date, time, venue;

        if (intent.getExtras() != null) {
            event = intent.getExtras().getString("eventName");
            date = intent.getExtras().getString("eventDate");
            time = intent.getExtras().getString("eventTime");
            venue = intent.getExtras().getString("eventVenue");

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.icon_synanto)
                            .setContentTitle("Event Ongoing")
                            .setContentText("The " + event + " event is now ongoing held at the " + venue);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }

        MediaPlayer mp = MediaPlayer.create(context, R.raw.rain);
        mp.start();

        PowerManager pm = (PowerManager)     context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
        wl.acquire();

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] s = { 0, 100, 10, 500, 10, 100, 0, 500, 10, 100, 10, 500 };

        vibrator.vibrate(s, -1);
    }


}