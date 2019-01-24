package com.blackweather.android;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.blackweather.android.gson.Weather;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class LocationLoader extends AsyncTaskLoader<String[]> {

    private static final String TAG = LocationLoader.class.getSimpleName();

    private URL mURL;
//    private LocationClient mLocationClient;

    public LocationLoader(@NonNull Context context, URL url) {
        super(context);
        mURL = url;
        //        mLocationClient = locationClient;
    }

    @Override
    public String[] loadInBackground() {
        Log.d(TAG, "debug4 loadInBackground: " + "得到执行");
        Log.d(TAG, "debug4 url: " + mURL);

        final String[] strings = new String[2];
        String weatherStr;
        try {
            weatherStr = NetworkUtils.sendRequestWithHttpConnection(mURL);
            Weather weather = JsonUtils.handleWeatherResponse(weatherStr);
            if (weather != null && "ok".equals(weather.status)) {
                strings[0] = weather.basic.location;
                strings[1] = weather.basic.weatherId;
                return strings;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
