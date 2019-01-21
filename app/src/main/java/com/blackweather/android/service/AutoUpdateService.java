package com.blackweather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.blackweather.android.gson.Weather;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;
import com.blackweather.android.utilities.PreferenceUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 自动更新的后台service初步设定为8小时更新一次
 */
public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 8 * 60 * 60 * 1000; // 8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent startAUServieceIntent = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0,
                startAUServieceIntent, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather() {
        // 取出保存的本地信息
        List<String> savedWeatherIdsList = PreferenceUtils.fetchPagesWeatherId(this,
                PreferenceUtils.PAGES_WEATHER_ID_KEY);
        int count = savedWeatherIdsList.size();
        if (savedWeatherIdsList.size() > 0) {
            // 如果缓存的城市就更新天气信息
            for (final String savedWeatherId : savedWeatherIdsList) {
                if (savedWeatherId == null) {
                    return;
                }
                URL url;
                try {
                    url = NetworkUtils.buildUrlWithWeatherId(savedWeatherId);
                    NetworkUtils.sendOkHttpRequest(url, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String responseStr = response.body().string();
                            Weather weather = JsonUtils.handleWeatherResponse(responseStr);
                            if (weather != null && "ok".equals(weather.status)) {
                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(AutoUpdateService.this)
                                        .edit();
                                editor.putString(savedWeatherId, responseStr);
                                editor.apply();
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
