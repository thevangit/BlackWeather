package com.blackweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackweather.android.adapters.DetailRecyclerAdapter;
import com.blackweather.android.data.Info;
import com.blackweather.android.gson.Forecast;
import com.blackweather.android.gson.Weather;
import com.blackweather.android.task.BlackTask;
import com.blackweather.android.utilities.BlackUtils;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements SharedPreferences
        .OnSharedPreferenceChangeListener {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private TextView mTitleLocationView;
    private ImageView mWeatherIcoView;
    private TextView mFirstMaxView;
    private TextView mFirstMinView;
    private TextView mDateView;
    private TextView mFirstInfoView;
    private ImageView mMoreView;
    private Button mSettingsButton;
    private Button mBackButton;
    private ImageView mPicImage;

    private RecyclerView mDetailRecyclerView;
    private DetailRecyclerAdapter mDetailAdapter;

    private List<Info> mInfoList;

    private String mWeatherIdKey;
    private int mDayNum;

    private Forecast mForecast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_detail);

        // bind and setup views
        mTitleLocationView = findViewById(R.id.title_detail_location);
        mDateView = findViewById(R.id.date);
        mFirstInfoView = findViewById(R.id.weather_description);
        mFirstMaxView = findViewById(R.id.high_temperature);
        mFirstMinView = findViewById(R.id.low_temperature);
        mMoreView = findViewById(R.id.more);
        mDetailRecyclerView = findViewById(R.id.detail_recycler_view);
        mSettingsButton = findViewById(R.id.title_detail_settings);
        mBackButton = findViewById(R.id.title_detail_back);
        mWeatherIcoView = findViewById(R.id.weather_icon);
        mPicImage = findViewById(R.id.detail_pic_img);
        mMoreView.setVisibility(View.GONE);

        // 显示背景图片
        BlackTask.loadBgPic(this, mPicImage);
        Intent intent = getIntent();
        if (intent.hasExtra("weatherId_key") &&
                intent.hasExtra("day_num_key")) {
            mWeatherIdKey = intent.getStringExtra("weatherId_key");
            mDayNum = intent.getIntExtra("day_num_key", 0);
        }

        SharedPreferences sharedPreferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        String weatherJsonStr = sharedPreferences.getString(mWeatherIdKey, null);
        Weather weather = JsonUtils.handleWeatherResponse(weatherJsonStr);
        mForecast = weather.forecastList.get(mDayNum);

        Log.d(TAG, "debug_onCreate_dayNum: " + mDayNum);

        // 显示数据
        mTitleLocationView.setText(weather.basic.location);
        mDateView.setText(mForecast.date);
        mFirstInfoView.setText(mForecast.textDay);
        if (PreferenceUtils.isMetric(this)) {
            mFirstMaxView.setText(mForecast.tempMax + "\u00b0");
            mFirstMinView.setText(mForecast.tempMin + "\u00b0");
        } else {
            mFirstMaxView.setText(BlackUtils.celsiusToFahrenheit(mForecast.tempMax)
                    + "\u00b0");
            mFirstMinView.setText(BlackUtils.celsiusToFahrenheit(mForecast.tempMin)
                    + "\u00b0");
        }
        mWeatherIcoView.setImageResource(BlackUtils
                .getIcoResWithCode(Integer.parseInt(mForecast.codeDay)));
        mInfoList = resolveWeatherInfo(mForecast);
        Log.d(TAG, "debug_onCreate: " + mInfoList);
        mDetailAdapter = new DetailRecyclerAdapter(this, mInfoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetailRecyclerView.setLayoutManager(layoutManager);
        mDetailRecyclerView.setAdapter(mDetailAdapter);

        // 设置监听
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this,
                        SettingActivity.class);
                startActivity(intent);
            }
        });

    }

    private List<Info> resolveWeatherInfo(Forecast forecast) {
        if (forecast == null) {
            return null;
        }
        List<Info> infoList = new ArrayList<>();
        infoList.add(new Info(forecast.textDay, "白天："));
        infoList.add(new Info(forecast.textNight, "夜晚："));
        if (PreferenceUtils.isMetric(this)) {
            infoList.add(new Info(forecast.tempMax + "\u00b0", "最高温度："));
            infoList.add(new Info(forecast.tempMin + "\u00b0", "最低温度："));
        } else {
            infoList.add(new Info(BlackUtils.celsiusToFahrenheit(forecast.tempMax) +
                    "\u00b0", "最高温度："));
            infoList.add(new Info(BlackUtils.celsiusToFahrenheit(forecast.tempMin) +
                    "\u00b0", "最低温度："));
        }
        infoList.add(new Info(forecast.precip + "%", "降水概率："));
        infoList.add(new Info(forecast.precipitation + "mm", "降水量："));
        infoList.add(new Info(forecast.humidity + "%", "湿度："));
        infoList.add(new Info(forecast.visibility, "能见度："));
        infoList.add(new Info(forecast.windDirection, "风向："));
        infoList.add(new Info(forecast.windDegree + "\u00b0", "风向（角度）："));
        infoList.add(new Info(forecast.windSpeed + "km/h", "风速："));
        infoList.add(new Info(forecast.windScale, "风力等级："));
        infoList.add(new Info(forecast.uv, "紫外线强度："));
        infoList.add(new Info(forecast.pressure + "Pa", "大气压强："));
        return infoList;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_units_key))) {
            mInfoList.removeAll(mInfoList);
            mInfoList = resolveWeatherInfo(mForecast);
            mDetailAdapter.swapInfoList(mInfoList);
        }
    }
}
