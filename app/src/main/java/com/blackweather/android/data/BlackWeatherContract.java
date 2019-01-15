package com.blackweather.android.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 1.contract:定义表、表头的名字和uri
 */
public final class BlackWeatherContract {

    // step1：define uri
    public static final String CONTENT_AUTHORIRY = "com.blackWeather.android";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORIRY);

    public static final String PATH_WEATHER = "weather";

    // step2：define table
    public static final class WeatherEntry implements BaseColumns {

        // 定义uri，作用是让ContentProvider来通过该uri操作表中的内容
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();
        // 定义表名和表头的名字
        public static final String TABLE_NAME = "daily_forecast";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_UPDATE_TIME = "weather_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CODE_DAY = "code_day";
        public static final String COLUMN_CODE_NIGHT = "code_night";
        public static final String COLUMN_TEXT_DAY = "text_day";
        public static final String COLUMN_TEXT_NIGHT = "text_night";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_MOON_RISE_TIME = "moon_rise";
        public static final String COLUMN_MOON_SET_TIME = "moon_set";
        public static final String COLUMN_SUN_RISE_TIME = "sun_rise";
        public static final String COLUMN_SUN_SET_TIME = "sun_set";
        public static final String COLUMN_PRECIPITATION = "precipitation"; // 降水量
        public static final String COLUMN_PRECIPITATION_PERCENT = "precipitation_percent";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_HIGH_TEMPERATURE = "max_temp";
        public static final String COLUMN_LOW_TEMPERATURE = "min_temp";
        public static final String COLUMN_ULTRAVIOLET_RAYS = "ultraviolet_rays"; // 紫外线
        public static final String COLUMN_VISIBILITY = "visibility";
        public static final String COLUMN_WIND_DIRECTION_TEXT = "wind_text";
        public static final String COLUMN_WIND_DIRECTION_DEGREE = "wind_degree";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_WIND_SCALE = "wind_scale";

    }
}
