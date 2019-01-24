package com.blackweather.android.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.RouteInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.blackweather.android.HomeActivity;
import com.blackweather.android.R;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author: theVan
 * 1.这个类的工具方法的作用是连接Server
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /* - - - - - - - - - - - - - - - - - - - - - - - */
    /* 使用了和风天气的免费API接口,www.heweather.com */
    /* - - - - - - - - - - - - - - - - - - - - - - - */
    private static final String BASE_WEATHER_URL = "https://api.heweather.net/s6/";
    // 预留部分,实时天气预报URL的层级部分
    private static final String REAL_TIME_BASE_URL = BASE_WEATHER_URL + "weather/now";
    // 逐日天气预报URL的层级部分
    private static final String DAILY_BASE_URL = BASE_WEATHER_URL + "weather/forecast";
    // query部分
    private static final String KEY = "key";
    private static final String KEY_PARAM = "5f1e588531514d65a6a08569774a17ec";
    private static final String LOCATION = "location";
    private static final String UNIT = "unit";
    private static final String UNIT_PARAM_IMPERIAL = "i";


    /**
     * 1.build url
     * 方法的作用是以指定的经度和纬度为基础构建连接服务器的url
     *
     * @param longitude 经度
     * @param latitude 纬度
     * @return url
     * @throws MalformedURLException
     */
    public static URL buildUrlWithLonngtitdeLatitude(double longitude, double latitude)
            throws MalformedURLException {
        // step1：构建uri,一般有两种构建方式，其中一种如下所示，另一种是利用Uri.Builder构造器
        Uri uri = Uri.parse(DAILY_BASE_URL).buildUpon()
                .appendQueryParameter(KEY, KEY_PARAM)
                .appendQueryParameter(LOCATION, longitude + "," + latitude)
                .build();
        // step2：以step1中构建的uri为基础，构建逐日天气预报的url,记住throws exception
        URL url = new URL(uri.toString());
        return url;
    }

    /**
     * 1.build url
     * 方法的作用是以指定的城市名称为基础构建url
     *
     * @param weatherId 指定城市的天气代号
     * @return url
     */
    public static URL buildUrlWithWeatherId(String weatherId) throws MalformedURLException {
        // step1: build uri
        Uri uri = Uri.parse(DAILY_BASE_URL).buildUpon()
                .appendQueryParameter(KEY, KEY_PARAM)
                .appendQueryParameter(LOCATION, weatherId)
                .build();
        // step2: build url with uri
        URL url = new URL(uri.toString());
        return url;
    }

    /**
     * 2.send request(and get response With HttpURLConnection)
     * 根据url从server上获取response，并将其转换为字符串,
     *
     * @return 存储json格式文本的字符串
     * @throws IOException
     */
    public static String sendRequestWithHttpConnection(URL url) throws IOException {
        // step1：以url为基础构建HttpURLConnection的实例
        Log.d(TAG, "debug3 url:" + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuilder response = new StringBuilder();
        BufferedReader reader = null;
        try {
            // step2: 设置http方法或其它参数
            connection.setRequestMethod("GET");
            // step3: 获取输入字节流inputStream
            InputStream in = connection.getInputStream();
            // step4: 读取inputStream，将其转换为字符串
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ( (line = reader.readLine()) != null ) response.append(line);
        } finally {
            // step5：关闭实例，释放内存
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                reader.close();
            }
        }
        return response.toString();
    }

    /**
     * 2. send request(and get response with OkHttp)
     *
     * @param callback 回调接口
     */
    public static void sendOkHttpRequest(URL url, okhttp3.Callback callback) {
        // 1）获取OkHttpClient实例
        OkHttpClient client = new OkHttpClient();
        // 2）使用Builder构建Request实例
        Request request = new Request.Builder().url(url).build();
        // 3）发送request并执行callback
        client.newCall(request).enqueue(callback);
    }

    /**
     * 重载
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }


//    /**
//     * 根据weatherId获取天气数据
//     */
//    synchronized public static void requestWeatherWith
//            (String weatherId, okhttp3.Callback callback) throws MalformedURLException {
//        URL url = buildUrlWithWeatherId(weatherId);
//        sendOkHttpRequest(url, callback);
//    }
}
