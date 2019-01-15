package com.blackweather.android.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.blackweather.android.data.BlackWeatherContract;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;

import java.net.URL;

public class BlackWeatherSyncTask {

    synchronized public static void syncWeather(Context context) {

        try {
            URL url = NetworkUtils.getUrl();
            String response = NetworkUtils.getResponseFromHttpUrl(url);
            ContentValues[] forecastValues = JsonUtils.getContentValuesFromForecastJson(context,
                    response);
            if (forecastValues != null && forecastValues.length != 0) {
                ContentResolver cr = context.getContentResolver();
                cr.delete(BlackWeatherContract.WeatherEntry.CONTENT_URI,
                        null, null);
                cr.bulkInsert(BlackWeatherContract.WeatherEntry.CONTENT_URI, forecastValues);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
