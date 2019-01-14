package com.blackweather.android.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author: theVan
 * 这个类的工具的作用是处理json的数据
 */
public final class JsonUtils {

    private static final String TAG = JsonUtils.class.getSimpleName();

    /* - - - - - - - - - - - - - -*/
    /* 心和天气api返回的json的key */
    /* - - - - - - - - - - - - - -*/
    private static final String RESULT = "result";
    // "location"
    private static final String LOCATION = "location";
    private static final String LOCATION_NAME = "name";
    private static final String LOCATION_TIMEZONE_OFFSET = "timezone_offset";

    // "daily"
    private static final String DAILY = "daily";
    private static final String DAILY_DATE = "date";
    private static final String DAILY_TEXT_DAY = "text_day";
    private static final String DAILY_CODE_DAY = "code_day";
    private static final String DAILY_TEXT_NIGHT = "text_night";
    private static final String DAILY_CODE_NIGHT = "code_night";
    private static final String DAILY_MAX_TEMP = "high";
    private static final String DAILY_MIN_TEMP = "low";
    private static final String DAILY_PRECIP = "precip"; // 降水概率
    private static final String DAILY_WIND_DIRECTION_TEXT = "wind_direction";
    private static final String DAILY_WIND_DIRECTION_DEGREE = "wind_direction_degree";
    private static final String DAILY_WIND_SPEED = "wind_speed";
    private static final String DAILY_WIND_SCALE = "wind_scale"; // 风力等级

    private static final String LAST_UPDATE_TIME = "last_update";

    private static final String STATUS = "status";
    private static final String STATUS_CODE = "status_code";

    /**
     * 3.handle json: 解析json数据，
     * 结构化的数据转换为ContentValues数组方便存入database,
     * 其它数据存入SharePreference中
     *
     * @param context 上下文，方便调用android提供的方法
     * @param jsonStr json格式的字符串
     * @return ContentValues数组
     * @throws JSONException
     */
    public static ContentValues[] getContentValuesFromJson(Context context, String jsonStr)
            throws JSONException {
        // step1：构建整个json object
        JSONObject weatherObject = new JSONObject(jsonStr);
        // step2：查看是否返回错误
        if (weatherObject.has(STATUS) && weatherObject.has(STATUS_CODE)) {
            String errorMessage = weatherObject.getString(STATUS);
            String errorCode = weatherObject.getString(STATUS_CODE);
            Log.e(TAG, "status:" + errorMessage + "status_code:" + errorCode);
            return null;
        }
        // step3: 按需要解析json
        JSONArray resultArray = weatherObject.getJSONArray(RESULT);
        JSONObject resultObject = resultArray.getJSONObject(0);
        JSONObject locationObjext = resultObject.getJSONObject(LOCATION);
        JSONArray dailyArray = resultObject.getJSONArray(DAILY);
        String lastUpdateTime = resultObject.getString(LAST_UPDATE_TIME);
        int count = dailyArray.length();
        ContentValues[] weatherValues = new ContentValues[count];
        for (int i = 0; i < count; i ++) {
            JSONObject dayObject = dailyArray.getJSONObject(i);
            String date = dayObject.getString(DAILY_DATE);
            String textInDay = dayObject.getString(DAILY_TEXT_DAY);
            String codeInDay = dayObject.getString(DAILY_CODE_DAY);
            String textInNight = dayObject.getString(DAILY_TEXT_NIGHT);
            String codeInNight = dayObject.getString(DAILY_CODE_NIGHT);
            String highTemperature = dayObject.getString(DAILY_MAX_TEMP);
            String lowTemperature = dayObject.getString(DAILY_MIN_TEMP);
            String precip = dayObject.getString(DAILY_PRECIP); // 降水概率
            String windDirectionInText = dayObject.getString(DAILY_WIND_DIRECTION_TEXT);
            String windDirectionInDegree = dayObject.getString(DAILY_WIND_DIRECTION_DEGREE);
            String windSpeed = dayObject.getString(DAILY_WIND_SPEED);
            String windScale = dayObject.getString(DAILY_WIND_SCALE);
            ContentValues contentValues = new ContentValues();
            contentValues.put( , date);
            contentValues.put( , textInDay);
            contentValues.put( , codeInDay);
            contentValues.put( , textInNight);
            contentValues.put( , codeInDay);
            contentValues.put( , codeInNight);
            contentValues.put( , highTemperature);
            contentValues.put( , lowTemperature);
            contentValues.put( , precip);
            contentValues.put( , windDirectionInText);
            contentValues.put( , windDirectionInDegree);
            contentValues.put( , windSpeed);
            contentValues.put( , windScale);
            weatherValues[i] = contentValues;
        }
        return weatherValues;
    }
}
