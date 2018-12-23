package com.blackweather.android.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {

    private int id;

    private int mCountyCode;

    private String mCountyName;

    /**
     * 所属城市的id
     */
    private int mCityId;

    private String mWeatherId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountyCode() {
        return mCountyCode;
    }

    public void setCountyCode(int countyCode) {
        mCountyCode = countyCode;
    }

    public String getCountyName() {
        return mCountyName;
    }

    public void setCountyName(String countyName) {
        mCountyName = countyName;
    }

    public int getCityId() {
        return mCityId;
    }

    public void setCityId(int cityId) {
        mCityId = cityId;
    }

    public String getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(String weatherId) {
        mWeatherId = weatherId;
    }
}
