package com.blackweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackweather.android.gson.Forecast;
import com.blackweather.android.gson.LifeStyle;
import com.blackweather.android.gson.Weather;
import com.blackweather.android.service.AutoUpdateService;
import com.blackweather.android.util.HttpUtil;
import com.blackweather.android.util.Utility;
import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    private Button mNavButton;

    private SwipeRefreshLayout mSwipeRefresh;

    private String mWeatherId;

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

    private ImageView mBcPicImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 将背景图和status bar融合在一起
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);

        // 初始化控件
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavButton = findViewById(R.id.nav_button);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.colorPrimary); // 设置刷新时动画的颜色
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
        mBcPicImg = findViewById(R.id.bc_pic_img);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
            mWeatherId = weather.basic.weatherId;
        } else {
            // 没有则从服务器上查询
            mWeatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        String bcPic = prefs.getString("bc_pic", null);
        if (bcPic != null) {
            Glide.with(this).load(bcPic).into(mBcPicImg);
        } else {
            loadBingPic();
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
                        mSwipeRefresh.setRefreshing(false);
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
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                            mSwipeRefresh.setRefreshing(false);
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
        // 激活AutoUpdateService
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }

    /**
     * 加载背景图片
     */
    private void loadBingPic() {
        Glide.with(this).load(R.drawable.bg_1).into(mBcPicImg);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public SwipeRefreshLayout getSwipeRefresh() {
        return mSwipeRefresh;
    }

}
