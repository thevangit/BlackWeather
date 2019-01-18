package com.blackweather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {

    public String status;

    public Basic basic;

    public Update update;

    // 使用Gson映射数组的方法
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
