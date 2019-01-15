package com.blackweather.android.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.blackweather.android.data.BlackWeatherContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blackweather.android.data.BlackWeatherContract.WeatherEntry;

/**
 * Author: theVan
 * 2.这个类的工具的作用是处理json的数据
 */
public final class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    /* - - - - - - - - - - - - - -*/
    /* 和风天气api返回的json的key */
    /* - - - - - - - - - - - - - -*/
    private static final String HEWEATHER = "HeWeather6";
    // "location"
    private static final String BASIC = "basic";
    private static final String BASIC_WEATHER_ID = "cid";
    private static final String BASIC_LOCATION_NAME = "location";
//    private static final String LOCATION_TIMEZONE_OFFSET = "timezone_offset";

    // "update"
    private static final String UPDATE = "daily";
    private static final String UPDATE_LOC_TIME = "loc";
    private static final String UPDATE_UTC_TIME = "utc";

    // "status"
    private static final String STATUS = "status";

    // "daily"
    private static final String DAILY = "daily_forecast";
    private static final String DAILY_CODE_DAY = "cond_code_d";
    private static final String DAILY_CODE_NIGHT = "cond_code_n";
    private static final String DAILY_TEXT_DAY = "cond_txt_d";
    private static final String DAILY_TEXT_NIGHT = "cond_txt_n";
    private static final String DAILY_DATE = "date";
    private static final String DAILY_HUMIDITY = "hum";
    private static final String DAILY_MOON_RISE_TIME = "mr";
    private static final String DAILY_MOON_SET_TIME = "ms";
    private static final String DAILY_SUN_RISE_TIME = "sr";
    private static final String DAILY_SUN_SET_TIME = "ss";
    private static final String DAILY_PRECIPITATION = "pcpn"; // 降水量
    private static final String DAILY_PRECIPITATION_PERCENT = "pop"; // 降水概率
    private static final String DAILY_PRESSURE = "pres"; // 大气压强
    private static final String DAILY_MAX_TEMP = "tmp_max";
    private static final String DAILY_MIN_TEMP = "tmp_min";
    private static final String DAILY_ULTRAVIOLET_RAYS = "uv_index"; // 紫外线强度
    private static final String DAILY_VISIBILITY = "vis"; // 能见度
    private static final String DAILY_WIND_DIRECTION_TEXT = "wind_dir";
    private static final String DAILY_WIND_DIRECTION_DEGREE = "wind_deg";
    private static final String DAILY_WIND_SPEED = "wind_spd";
    private static final String DAILY_WIND_SCALE = "wind_sc"; // 风力等级

    /**
     * TODO(3).handle json: 解析json数据，
     * 结构化的数据转换为ContentValues数组方便存入database,
     *
     * @param context 上下文，方便调用android提供的方法
     * @param jsonStr json格式的字符串
     * @return ContentValues数组
     * @throws JSONException
     */
    public static ContentValues[] getContentValuesFromForecastJson(Context context, String jsonStr)
            throws JSONException {
        // step1：构建整个json object
        JSONObject object = new JSONObject(jsonStr);
        JSONArray weatherArray = object.getJSONArray(HEWEATHER);
        JSONObject weatherObject = weatherArray.getJSONObject(0);
        // step2：查看是否返回错误
        String message = weatherObject.getString(STATUS);
        if (!message.equals("ok")) {
            Log.e(TAG, "status:" + message);
            return null;
        }
        // step3: 按需要解析json
        JSONObject basicObject = weatherObject.getJSONObject(BASIC);
        JSONObject updateObject = weatherObject.getJSONObject(UPDATE);
        JSONArray dailyArray = weatherObject.getJSONArray(DAILY);

        String weatherId = basicObject.getString(BASIC_WEATHER_ID);
        String cityName = basicObject.getString(BASIC_LOCATION_NAME);
        String updateTime = updateObject.getString(UPDATE_LOC_TIME);

        int count = dailyArray.length();
        ContentValues[] weatherValues = new ContentValues[count];
        for (int i = 0; i < count; i++) {
            JSONObject dayObject = dailyArray.getJSONObject(i);
            String date = dayObject.getString(DAILY_DATE);
            String codeInDay = dayObject.getString(DAILY_CODE_DAY);
            String codeInNight = dayObject.getString(DAILY_CODE_NIGHT);
            String textInDay = dayObject.getString(DAILY_TEXT_DAY);
            String textInNight = dayObject.getString(DAILY_TEXT_NIGHT);
            String sunRiseTime = dayObject.getString(DAILY_SUN_RISE_TIME);
            String sunSetTime = dayObject.getString(DAILY_SUN_SET_TIME);
            String moonRiseTime = dayObject.getString(DAILY_SUN_RISE_TIME);
            String moonSetTime = dayObject.getString(DAILY_SUN_SET_TIME);
            String highTemperature = dayObject.getString(DAILY_MAX_TEMP);
            String lowTemperature = dayObject.getString(DAILY_MIN_TEMP);
            String humidity = dayObject.getString(DAILY_HUMIDITY);
            String precipitation = dayObject.getString(DAILY_PRECIPITATION);
            String precip = dayObject.getString(DAILY_PRECIPITATION_PERCENT); // 降水概率
            String pressure = dayObject.getString(DAILY_PRESSURE);
            String uv = dayObject.getString(DAILY_ULTRAVIOLET_RAYS);
            String visibility = dayObject.getString(DAILY_VISIBILITY);
            String windDirectionInText = dayObject.getString(DAILY_WIND_DIRECTION_TEXT);
            String windDirectionInDegree = dayObject.getString(DAILY_WIND_DIRECTION_DEGREE);
            String windSpeed = dayObject.getString(DAILY_WIND_SPEED);
            String windScale = dayObject.getString(DAILY_WIND_SCALE);
            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherEntry.COLUMN_CITY_NAME, cityName);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            contentValues.put(WeatherEntry.COLUMN_DATE, date);
            contentValues.put(WeatherEntry.COLUMN_UPDATE_TIME, updateTime);
            contentValues.put(WeatherEntry.COLUMN_CODE_DAY, codeInDay);
            contentValues.put(WeatherEntry.COLUMN_CODE_NIGHT, codeInNight);
            contentValues.put(WeatherEntry.COLUMN_TEXT_DAY, textInDay);
            contentValues.put(WeatherEntry.COLUMN_TEXT_NIGHT, textInNight);
            contentValues.put(WeatherEntry.COLUMN_SUN_RISE_TIME, sunRiseTime);
            contentValues.put(WeatherEntry.COLUMN_SUN_SET_TIME, sunSetTime);
            contentValues.put(WeatherEntry.COLUMN_MOON_RISE_TIME, moonRiseTime);
            contentValues.put(WeatherEntry.COLUMN_MOON_SET_TIME, moonSetTime);
            contentValues.put(WeatherEntry.COLUMN_HIGH_TEMPERATURE, highTemperature);
            contentValues.put(WeatherEntry.COLUMN_LOW_TEMPERATURE, lowTemperature);
            contentValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
            contentValues.put(WeatherEntry.COLUMN_PRECIPITATION, precipitation);
            contentValues.put(WeatherEntry.COLUMN_PRECIPITATION_PERCENT, precip);
            contentValues.put(WeatherEntry.COLUMN_ULTRAVIOLET_RAYS, uv);
            contentValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
            contentValues.put(WeatherEntry.COLUMN_VISIBILITY, visibility);
            contentValues.put(WeatherEntry.COLUMN_WIND_DIRECTION_TEXT, windDirectionInText);
            contentValues.put(WeatherEntry.COLUMN_WIND_DIRECTION_DEGREE, windDirectionInDegree);
            contentValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            contentValues.put(WeatherEntry.COLUMN_WIND_SCALE, windScale);
            weatherValues[i] = contentValues;
        }
        return weatherValues;
    }
}
