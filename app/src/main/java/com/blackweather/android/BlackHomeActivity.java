package com.blackweather.android;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blackweather.android.gson.Weather;
import com.blackweather.android.utilities.JsonUtils;
import com.blackweather.android.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class BlackHomeActivity extends AppCompatActivity {

    //    private DrawerLayout mDrawerLayout;
    private RecyclerView mHomeRecyclerView;
    private BlackAdapter mAdapter;
    private String mWeatherId;
    private TextView mLocationTextView;

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
        setContentView(R.layout.home_page);

        // 初始化
//        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLocationTextView = findViewById(R.id.title_location);
        mHomeRecyclerView = findViewById(R.id.home_recycler_view);
        mAdapter = new BlackAdapter();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherStr = prefs.getString("weather", null);
        mHomeRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mHomeRecyclerView.setLayoutManager(layoutManager);
        if (weatherStr != null) {
            Weather weather = JsonUtils.handleWeatherResponse(weatherStr);
            mAdapter.setData(weather);
            mLocationTextView.setText(weather.basic.location);
        }else {
            try {
                mWeatherId = getIntent().getStringExtra("weather_id");
//            mWeatherLayout.setVisibility(View.INVISIBLE);
                requestWeather(mWeatherId);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

//        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                requestWeather(mWeatherId);
//            }
//        });
//        mNavButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mDrawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//        String bcPic = prefs.getString("bc_pic", null);
//        if (bcPic != null) {
//            Glide.with(this).load(bcPic).into(mBcPicImg);
//        } else {
//            loadBingPic();
//        }
    }

    /**
     * 根据天气id请求城市天气数据
     */
    public void requestWeather(final String weatherId) throws MalformedURLException {
        URL url = NetworkUtils.buildUrlWithWeatherId(weatherId);
        NetworkUtils.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BlackHomeActivity.this, "获取天气信息失败2",
                                Toast.LENGTH_SHORT).show();
//                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = JsonUtils.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(BlackHomeActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mAdapter.setData(weather);
                        } else {
                            Toast.makeText(BlackHomeActivity.this, "获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

//    /**
//     * 加载背景图片
//     */
//    private void loadBingPic() {
//        Glide.with(this).load(R.drawable.bg_1).into(mBcPicImg);
//    }
//
//    public DrawerLayout getDrawerLayout() {
//        return mDrawerLayout;
//    }
//
//    public SwipeRefreshLayout getSwipeRefresh() {
//        return mSwipeRefresh;
//    }

}
