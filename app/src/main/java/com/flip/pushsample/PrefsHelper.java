package com.flip.pushsample;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by pthibau1 on 2017-08-12.
 */

public class PrefsHelper {

    private SharedPreferences preferences;

    public PrefsHelper(Context context) {
        preferences = context.getSharedPreferences(Constants.DEFAULT_SHARED_PREFS, Context.MODE_PRIVATE);
    }

    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return preferences.getInt(key, -1);
    }
}
