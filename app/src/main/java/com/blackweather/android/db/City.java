package com.blackweather.android.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {

    private int id;

    private int mCityCode;

    private String mCityName;

    /**
     * 所属省份的id
     */
    private int mProvinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityCode() {
        return mCityCode;
    }

    public void setCityCode(int cityCode) {
        mCityCode = cityCode;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public int getProvinceId() {
        return mProvinceId;
    }

    public void setProvinceId(int provinceId) {
        mProvinceId = provinceId;
    }
}
