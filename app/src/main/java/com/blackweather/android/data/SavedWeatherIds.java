package com.blackweather.android.data;

import org.litepal.crud.LitePalSupport;

public class SavedWeatherIds extends LitePalSupport {

    private int id;

    private String weatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }
}
