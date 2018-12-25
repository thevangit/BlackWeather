package com.blackweather.android;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackweather.android.gson.Forecast;
import com.blackweather.android.gson.LifeStyle;
import com.blackweather.android.gson.Weather;
import com.blackweather.android.util.HttpUtil;
import com.blackweather.android.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView mWeatherLayout;

    private TextView mTitleCity;

    private TextView mTitleUpdateTime;

    private TextView mDegreeText;

    private TextView mWeatherInfoText;

    private LinearLayout mForecastLayout;

    private TextView mAqiText;

    private TextView mPm25Text;

    private TextView mComfortText;

    private TextView mCarWashText;

    private TextView mSportText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // 初始化控件
        mWeatherLayout = findViewById(R.id.weather_layout);
        mTitleCity = findViewById(R.id.title_city);
        mTitleUpdateTime =findViewById(R.id.title_update_time);
        mDegreeText = findViewById(R.id.degree_text);
        mWeatherInfoText = findViewById(R.id.weather_info_text);
        mForecastLayout = findViewById(R.id.forecast_layout);
        mAqiText = findViewById(R.id.aqi_text);
        mPm25Text = findViewById(R.id.pm25_text);
        mComfortText = findViewById(R.id.comfort_text);
        mCarWashText =findViewById(R.id.car_wash_text);
        mSportText = findViewById(R.id.sport_text);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            // 没有则从服务器上查询
            String weatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    /**
     * 根据天气id请求城市天气数据
     */
    public void requestWeather(final String weatherId) {
        Log.i("WeatherActivity", "requestWeather: 执行");
        String weatherUrl = "https://api.heweather.net/s6/weather?location=" +
                weatherId + "&key=5f1e588531514d65a6a08569774a17ec";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败2",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败1",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather类的数据
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.info;
        mTitleCity.setText(cityName);
        mTitleUpdateTime.setText(updateTime);
        mDegreeText.setText(degree);
        mWeatherInfoText.setText(weatherInfo);
        mForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, mForecastLayout,
                    false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            String dayInfo = forecast.infoAtDay;
            String dayNight = forecast.infoAtNight;
            String info = "日/夜：" + dayInfo + "/" + dayNight;
            infoText.setText(info);
            maxText.setText(forecast.max);
            minText.setText(forecast.min);
            mForecastLayout.addView(view);
        }
//        if (weather.aqi != null) {
//            mAqiText.setText(weather.aqi.city.aqi);
//            mPm25Text.setText(weather.aqi.city.pm25);
//        }
        String comfort = null;
        String carWash = null;
        String sport = null;
        for (LifeStyle life : weather.lifestyleList) {
            if ("comf".equals(life.type)) {
                comfort = "舒适度：" + life.subtitle + "\n" +life.text;
            } else if ("cw".equals(life.type)) {
                carWash = "洗车指数：" + life.subtitle + "\n" + life.text;
            } else if ("sport".equals(life.type)) {
                sport = "运动指数：" + life.subtitle + "\n" + life.text;
            }
        }
//        String comfort = "舒适度：" + weather.suggestion.Comfort.info;
//        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
//        String sport = "运动指数：" + weather.suggestion.sport.info;
        if (comfort != null) {
            mComfortText.setText(comfort);
        }
        if (carWash != null) {
            mCarWashText.setText(carWash);
        }
        if (sport != null) {
            mSportText.setText(sport);
        }
        mWeatherLayout.setVisibility(View.VISIBLE);
    }
}
