package com.blackweather.android.data;

public class Info{

    private String mLabel;
    private String mValue;

    public Info(String value, String label){
        mValue = value;
        mLabel = label;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getValue() {
        return mValue;
    }

    @Override
    public String toString() {
        return "label:" + mLabel + ",value: " + mValue;
    }
}
