package com.blackweather.android.db;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {

    private int id;

    private int mProvinceCode;

    private String mProvinceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceCode() {
        return mProvinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        mProvinceCode = provinceCode;
    }

    public String getProvinceName() {
        return mProvinceName;
    }

    public void setProvinceName(String provinceName) {
        mProvinceName = provinceName;
    }
}
