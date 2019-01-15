package com.blackweather.android.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blackweather.android.R;

/**
 * 4.set preferences:偏好设置
 */
public final class BlackWeatherPreference {

    /**
     * 1)判断当前偏好单位是否为公制
     */
    public static boolean isMetric(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultUnits = context.getString(R.string.pref_units_metric);
        String prefUnits = sp.getString(keyForUnits, defaultUnits);
        String metric = context.getString(R.string.pref_units_metric);
        boolean isMetric = false;
        if (prefUnits.equals(metric)) {
            isMetric = true;
        }
        return isMetric;
    }
}
