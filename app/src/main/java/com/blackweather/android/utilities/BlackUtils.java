package com.blackweather.android.utilities;

import android.content.Context;

import com.blackweather.android.R;

public final class BlackUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getIconResInDay(int code) {
        if (code == 100 || code == 900) return R.drawable.art_clear;
        else if (code == 103) return R.drawable.art_light_clouds;
        else if (code >= 101 && code <= 213) return R.drawable.art_clouds;
        else if (code >= 300 && code <= 304) return R.drawable.art_storm;
        else if (code >= 305 && code <= 309) return R.drawable.art_light_rain;
        else if (code >= 310 && code <= 399) return R.drawable.art_rain;
        else if (code >= 400 && code <= 499 || code == 901) return R.drawable.art_snow;
        else if (code >= 500 && code <= 515) return R.drawable.art_fog;
        else return R.mipmap.bw_3;
    }

    /**
     * 把摄氏度转换为华氏度
     */
    public static float celsiusToFahrenheit(float temperatureInCelsius) {
        float temperatureInFahrenheit = (float) ((temperatureInCelsius * 1.8) + 32);
        return temperatureInFahrenheit;
    }

}
