package com.blackweather.android;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import org.litepal.LitePal;

/**
 * Author: theVan
 * 自定义Application类，方便获取全局context并且初始化LitePal
 */
public class BlackApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(context);
    }

    public static Context getContext () {
        return context;
    }
}
