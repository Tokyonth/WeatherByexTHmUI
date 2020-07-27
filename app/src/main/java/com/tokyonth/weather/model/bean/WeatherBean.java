package com.tokyonth.weather.model.bean;

import com.tokyonth.weather.R;
import com.tokyonth.weather.base.BaseApplication;

public class WeatherBean {

    public static final String SUN = getString(R.string.weather_sun);
    public static final String OVERCAST = getString(R.string.weather_overcast);
    public static final String SNOW = getString(R.string.weather_snow);
    public static final String RAIN = getString(R.string.weather_rain);
    public static final String CLOUDY = getString(R.string.weather_cloudy);
    public static final String THUNDER = getString(R.string.weather_thunder);
    public static final String LIGHT_RAIN = getString(R.string.weather_light_rain);
    public static final String SHOWER = getString(R.string.weather_shower);

    public String weather;  //天气，取值为上面6种
    public String temperature; //温度值
    public String temperatureStr; //温度的描述值
    public String time; //时间值

    public WeatherBean(String weather, String temperature, String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.time = time;
        this.temperatureStr = temperature + getString(R.string.degree);
    }

    public WeatherBean(String weather, String temperature, String temperatureStr, String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.temperatureStr = temperatureStr;
        this.time = time;
    }

    public static String[] getAllWeathers() {
        return new String[]{SUN, RAIN, OVERCAST, CLOUDY, SNOW, THUNDER, SHOWER, LIGHT_RAIN};
    }

    private static String getString(int resID) {
        return BaseApplication.getContext().getString(resID);
    }

}
