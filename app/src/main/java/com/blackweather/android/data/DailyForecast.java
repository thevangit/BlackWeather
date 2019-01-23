package com.blackweather.android.data;

import org.litepal.crud.LitePalSupport;

/**
 * d87:data.crate database
 * 这是一个典型的Java bean
 */
public class DailyForecast extends LitePalSupport {

    private int id;
    private String location;
    private String weatherId;
    private String date;
    private String localUpdateTime;
    private String codeDay;
    private String codeNight;
    private String textDay;
    private String textNight;
    private String humidity;
    private String sunRiseTime;
    private String sunSetTime;
    private String moonRiseTime;
    private String moonSetTime;
    private String precipitaion; //降水量
    private String precip; //降水概率
    private String pressure;
    private String tempMax;
    private String tempMin;
    private String uv; //紫外线强度
    private String visibility;
    private String windDirection;
    private String windDegree;
    private String windSpeed;
    private String windScale;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocalUpdateTime() {
        return localUpdateTime;
    }

    public void setLocalUpdateTime(String localUpdateTime) {
        this.localUpdateTime = localUpdateTime;
    }

    public String getCodeDay() {
        return codeDay;
    }

    public void setCodeDay(String codeDay) {
        this.codeDay = codeDay;
    }

    public String getCodeNight() {
        return codeNight;
    }

    public void setCodeNight(String codeNight) {
        this.codeNight = codeNight;
    }

    public String getTextDay() {
        return textDay;
    }

    public void setTextDay(String textDay) {
        this.textDay = textDay;
    }

    public String getTextNight() {
        return textNight;
    }

    public void setTextNight(String textNight) {
        this.textNight = textNight;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getSunRiseTime() {
        return sunRiseTime;
    }

    public void setSunRiseTime(String sunRiseTime) {
        this.sunRiseTime = sunRiseTime;
    }

    public String getSunSetTime() {
        return sunSetTime;
    }

    public void setSunSetTime(String sunSetTime) {
        this.sunSetTime = sunSetTime;
    }

    public String getMoonRiseTime() {
        return moonRiseTime;
    }

    public void setMoonRiseTime(String moonRiseTime) {
        this.moonRiseTime = moonRiseTime;
    }

    public String getMoonSetTime() {
        return moonSetTime;
    }

    public void setMoonSetTime(String moonSetTime) {
        this.moonSetTime = moonSetTime;
    }

    public String getPrecipitaion() {
        return precipitaion;
    }

    public void setPrecipitaion(String precipitaion) {
        this.precipitaion = precipitaion;
    }

    public String getPrecip() {
        return precip;
    }

    public void setPrecip(String precip) {
        this.precip = precip;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getWindText() {
        return windDirection;
    }

    public void setWindText(String windText) {
        this.windDirection = windText;
    }

    public String getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(String windDegree) {
        this.windDegree = windDegree;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindScale() {
        return windScale;
    }

    public void setWindScale(String windScale) {
        this.windScale = windScale;
    }
}
