package com.tokyonth.weather.presenter;

import com.tokyonth.weather.model.bean.Weather;

public interface WeatherView {

    void showWeather(Weather weather, boolean isRefresh);
    void showError(String error);

}
