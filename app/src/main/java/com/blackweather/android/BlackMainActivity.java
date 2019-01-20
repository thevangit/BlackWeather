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

    private String mHomeFragmentWeatherId; // 首先选择的weatherId。

    public static final int REQUEST_CODE_FOR_WEATHERID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List pagesWeatherId = SharedPreferenceUtils.fetchPagesWeatherId(this,
                SharedPreferenceUtils.PAGES_WEATHER_ID_KEY);
        if (pagesWeatherId != null && pagesWeatherId.size() > 0) {
            Intent intent = new Intent(this, BlackHomeActivity.class);
            startActivity(intent);
            finish();
        }
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String
//        if (prefs.getString("weather", null) != null) {
//            Intent intent = new Intent(this, BlackHomeActivity.class);
//            // 请求码的值随便设置，但必须>=0
////            startActivityForResult(intent, REQUEST_CODE_FOR_WEATHERID);
//            startActivity(intent);
//            finish();
//        }
    }
}
