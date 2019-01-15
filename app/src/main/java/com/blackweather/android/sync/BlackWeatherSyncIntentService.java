package com.blackweather.android.sync;

import android.app.IntentService;
import android.content.Intent;

public class BlackWeatherSyncIntentService extends IntentService {

    public BlackWeatherSyncIntentService() {
        super("BlackWeatherSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        BlackWeatherSyncTask.syncWeather(this);
    }
}
