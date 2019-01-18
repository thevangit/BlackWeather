package com.blackweather.android.gson;

import android.support.v4.widget.SwipeRefreshLayout;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    @SerializedName("cond_code_d")
    public String codeDay;

    @SerializedName("cond_code_n")
    public String codeNight;

    @SerializedName("cond_txt_d")
    public String textDay;

    @SerializedName("cond_txt_n")
    public String textNight;

    public String date;

    @SerializedName("hum")
    public String humidity;

    @SerializedName("pcpn")
    public String precipitation;

    @SerializedName("pop")
    public String precip;

    @SerializedName("pres")
    public String pressure;

    @SerializedName("tmp_max")
    public String tempMax;

    @SerializedName("tmp_min")
    public String tempMin;

    @SerializedName("uv_index")
    public String uv;

    @SerializedName("vis")
    public String visibility;

    @SerializedName("wind_deg")
    public String windDegree;

    @SerializedName("wind_dir")
    public String windDirection;

    @SerializedName("wind_spd")
    public String windSpeed;

    @SerializedName("wind_sc")
    public String windScale;

}
