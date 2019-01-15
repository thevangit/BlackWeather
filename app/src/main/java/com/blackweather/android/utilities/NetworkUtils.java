package com.blackweather.android.utilities;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Author: theVan
 * 1.这个类的工具方法的作用是连接Server
 */
public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - */
    /* 使用了和风天气的免费API接口，网址:www.heweather.com */
    /* - - - - - - - - - - - - - - - - - - - - - - - - - - */
    private static final String BASE_WEATHER_URL = "https://free-api.heweather.net/s6/";
    // 预留部分：实时天气预报URL的层级部分
    private static final String REAL_TIME_BASE_URL = BASE_WEATHER_URL + "weather/now.json";
    // 逐日天气预报URL的层级部分
    private static final String DAILY_BASE_URL = BASE_WEATHER_URL + "weather/forecast.json";
    // query部分
    private static final String KEY = "key";
    private static final String KEY_PARAM = "2c356d0ee50b4f15b1894cbf05ad8f66";
    private static final String LOCATION = "location";
    private static final String UNIT = "unit";
    private static final String UNIT_PARAM_IMPERIAL = "i";

    /**
     * TODO(1)build url:方法的作用（职责）是以指定的经度和纬度为基础构建连接服务器的url
     *
     * @param longtitude 经度
     * @param latiude 纬度
     * @return url
     * @throws MalformedURLException
     */
    private static URL buildUrlWithLonngtitdeLatitude(double longtitude, double latiude)
            throws MalformedURLException {
        // step1：构建uri,一般有两种构建方式，其中一种如下所示，另一种是利用Uri.Builder构造器
        Uri uri = Uri.parse(DAILY_BASE_URL).buildUpon()
                .appendQueryParameter(KEY, KEY_PARAM)
                .appendQueryParameter(LOCATION, longtitude + ":" + latiude)
                .build();
        // step2：以step1中构建的uri为基础，构建逐日天气预报的url,记住throws exception
        URL url = new URL(uri.toString());
        return url;
    }

    /**
     * 1.build url:方法的作用是以指定的城市名称为基础构建url
     *
     * @param cityName 指定的城市名称
     * @return url
     */
    private static URL buildUrlWithCityName(String cityName) throws MalformedURLException {
        // step1: build uri
        Uri uri = Uri.parse(DAILY_BASE_URL).buildUpon()
                .appendQueryParameter(KEY, KEY_PARAM)
                .appendQueryParameter(LOCATION, cityName)
                .build();
        // step2: build url with uri
        URL url = new URL(uri.toString());
        return url;
    }

    /**
     * TODO(2)get response：根据url从server上获取response，并将其转换为字符串,
     * 使用的框架为官方的HttpURLConnection
     *
     * @param url uniform resource location
     * @return 存储json格式文本的字符串
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        // step1：以url为基础构建HttpURLConnection的实例
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        StringBuilder response = null;
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

    public static URL getUrl() {return null;
    }
}
