package com.tokyonth.weather.model.bean;

import org.litepal.crud.LitePalSupport;

public class CityWeatherInfo extends LitePalSupport {

    private String name;
    private String temp;
    private String img;
    private boolean isInTime;

    public CityWeatherInfo(String name, String temp, String img, boolean isInTime) {
        this.name = name;
        this.temp = temp;
        this.img = img;
        this.isInTime = isInTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isInTime() {
        return isInTime;
    }

    public void setInTime(boolean inTime) {
        isInTime = inTime;
    }

}
