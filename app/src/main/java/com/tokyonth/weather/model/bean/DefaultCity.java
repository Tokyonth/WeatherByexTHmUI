package com.tokyonth.weather.model.bean;

import org.litepal.crud.LitePalSupport;

public class DefaultCity extends LitePalSupport {

    private String cityName;
    private String parentCityName;
    private String longitude;
    private String latitude;

    public DefaultCity(String cityName, String parentCityName, String longitude, String latitude) {
        this.cityName = cityName;
        this.parentCityName = parentCityName;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getParentCityName() {
        return parentCityName;
    }

    public void setParentCityName(String parentCityName) {
        this.parentCityName = parentCityName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

}
