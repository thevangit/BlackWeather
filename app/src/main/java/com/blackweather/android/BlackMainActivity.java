package com.blackweather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.blackweather.android.utilities.SharedPreferenceUtils;

import java.util.List;

/**
 * d90 Author:theVan
 */
public class BlackMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List pagesWeatherId = SharedPreferenceUtils.fetchPagesWeatherId(this,
                SharedPreferenceUtils.PAGES_WEATHER_ID_KEY);
        // 如果有缓存则直接进入页面
        if (pagesWeatherId != null && pagesWeatherId.size() > 0) {
            Intent intent = new Intent(this, BlackHomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
