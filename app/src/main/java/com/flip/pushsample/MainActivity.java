package com.flip.pushsample;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Random;

/**
 * Created by pthibau1 on 2017-08-12.
 */

public class MainActivity extends AppCompatActivity {

    Button basicPushBtn;
    PrefsHelper prefsHelper;

    NotificationCompat.Builder summaryHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsHelper = new PrefsHelper(this);

        prefsHelper.saveInt(Constants.NOTIFICATION_COUNT, 0);

        basicPushBtn = findViewById(R.id.push_basic);

        basicPushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBasicPush();
            }
        });
    }

    /**
     * The basic push notification needs the following :
     * 1) small icon
     * 2) content title
     * 3) content text
     *
     * Recommended : Always set a group and a group header notification in order to bundle them
     *
     * @return
     */
    private NotificationCompat.Builder getBasicPush() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Content Title")
                .setContentText("Content text")
                .setGroup(Constants.DEFAULT_NOTIFICATION_GROUP)
                .setDeleteIntent(getDeleteIntent())
                .setAutoCancel(true);

        builder.getExtras().putInt(Constants.NOTIFICATION_ID, new Random().nextInt(1000));

        return builder;
    }

    private NotificationCompat.Builder getGroupHeader() {
        if(summaryHeader == null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Constants.DEFAULT_NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Summary Content Title")
                    .setContentText("Summary Content text")
                    .setGroup(Constants.DEFAULT_NOTIFICATION_GROUP)
                    .setGroupSummary(true)
                    .setAutoCancel(true);

            builder.getExtras().putInt(Constants.NOTIFICATION_ID, 1);

            summaryHeader = builder;
        }

        return summaryHeader;
    }

    private void launchBasicPush() {
        send(getBasicPush());
    }

    private void send(NotificationCompat.Builder builder) {

        int currentNotifCount = prefsHelper.getInt(Constants.NOTIFICATION_COUNT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder summaryHeader = getGroupHeader();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //if we have Nougat or higher, group notifs
            if(notificationManager.getActiveNotifications().length > 0) {
                notificationManager.notify(builder.getExtras().getInt(Constants.NOTIFICATION_ID), builder.build());
            } else {
                notificationManager.notify(builder.getExtras().getInt(Constants.NOTIFICATION_ID), builder.build());
                notificationManager.notify(summaryHeader.getExtras().getInt(Constants.NOTIFICATION_ID), summaryHeader.build());
            }
        } else {
            //On 4.4 until 7.0 the summary notif is the only one that shows, so we send it once and then just update it
            NotificationCompat.InboxStyle style = (NotificationCompat.InboxStyle) summaryHeader.mStyle ;
            if(style == null) {
                style = new NotificationCompat.InboxStyle();
            }
            style.addLine("Line : " + currentNotifCount);
            style.setBigContentTitle("Total Push Count : " + currentNotifCount);
            style.setSummaryText("Style Summary Text");
            summaryHeader.setStyle(style);

            notificationManager.notify(summaryHeader.getExtras().getInt(Constants.NOTIFICATION_ID), summaryHeader.build());
            notificationManager.notify(builder.getExtras().getInt(Constants.NOTIFICATION_ID), builder.build());
        }

        prefsHelper.saveInt(Constants.NOTIFICATION_COUNT, ++currentNotifCount);
    }

    private PendingIntent getDeleteIntent() {
        Intent deleteNotificationIntent = new Intent(this, NotificationDeletionIntentService.class);
        return PendingIntent.getService(this, 0, deleteNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
