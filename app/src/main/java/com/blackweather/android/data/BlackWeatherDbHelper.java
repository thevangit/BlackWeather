package com.blackweather.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.blackweather.android.data.BlackWeatherContract.WeatherEntry;
import com.blackweather.android.gson.Weather;

/**
 * 2.DbHelper:SQLiteOpenHelper类的作用是创建数据库和管理数据库的版本更新
 */
public class BlackWeatherDbHelper extends SQLiteOpenHelper {

    // 定义数据库的名字和版本
    public static final String DATABASE_NAME = "weather.db";

    private static final int DATABASE_VERSION = 1;

    // 定义构造函数
    public BlackWeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * 回调函数，当第一次创建数据库时调用该函数
     *
     * @param db 数据库
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // step1:定义sql的创建书库的语句
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                WeatherEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WEATHER_ID + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_CODE_DAY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_CODE_NIGHT + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_TEXT_DAY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_TEXT_NIGHT + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_SUN_RISE_TIME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_SUN_SET_TIME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_MOON_RISE_TIME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_MOON_SET_TIME + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_HIGH_TEMPERATURE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_LOW_TEMPERATURE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_HUMIDITY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PRECIPITATION + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PRECIPITATION_PERCENT + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_ULTRAVIOLET_RAYS + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_PRESSURE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_VISIBILITY + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WIND_DIRECTION_TEXT + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WIND_DIRECTION_DEGREE + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SPEED + " TEXT NOT NULL, " +
                WeatherEntry.COLUMN_WIND_SCALE + " TEXT NOT NULL); ";

        // step2:执行sql语句创建database
        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /**
     * 当数据库的schema更改时，更改version以调用该方法从未更新数据库
     *
     * @param db database
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
