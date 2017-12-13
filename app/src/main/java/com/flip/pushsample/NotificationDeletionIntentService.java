package com.flip.pushsample;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;

/**
 * Created by pthibau1 on 2017-08-12.
 */

public class NotificationDeletionIntentService extends JobIntentService {

    private PrefsHelper prefsHelper;

    public NotificationDeletionIntentService() {
        super();
    }

    @Override
    protected void onHandleWork(@Nullable Intent intent) {
        prefsHelper = new PrefsHelper(this);

//        if(intent.getExtras().containsKey(Constants.NOTIFICATION_ID)) {
//            int id = intent.getIntExtra(Constants.NOTIFICATION_ID, 0);

            int currentPushCount = prefsHelper.getInt(Constants.NOTIFICATION_COUNT);

            prefsHelper.saveInt(Constants.NOTIFICATION_COUNT, Math.max(--currentPushCount, 0));
//        }
    }

}
