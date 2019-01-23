package com.blackweather.android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.blackweather.android.utilities.PreferenceUtils;

import java.util.List;

/**
 * Author:theVan
 */
public class BlackMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);
        List pagesWeatherId = PreferenceUtils.fetchPagesWeatherId(this,
                PreferenceUtils.PAGES_WEATHER_ID_KEY);
        // 如果有缓存则直接进入页面
        if (pagesWeatherId != null && pagesWeatherId.size() > 0) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
