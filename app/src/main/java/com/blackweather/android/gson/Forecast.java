package com.blackweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    public String date;

    @SerializedName("cond_txt_d")
    public String infoAtDay;

    @SerializedName("cond_txt_n")
    public String infoAtNight;

    @SerializedName("tmp_max")
    public String max;

    @SerializedName("tmp_min")
    public String min;

}
