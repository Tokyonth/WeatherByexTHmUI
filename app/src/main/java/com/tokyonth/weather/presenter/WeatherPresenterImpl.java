package com.tokyonth.weather.presenter;

import com.tokyonth.weather.base.BaseApplication;
import com.tokyonth.weather.model.WeatherModel;
import com.tokyonth.weather.model.WeatherModelImpl;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;

public class WeatherPresenterImpl implements WeatherPresenter, OnWeatherListener {

    private WeatherModel weatherModel;
    private WeatherView weatherView;

    public WeatherPresenterImpl(WeatherView weatherView){
        this.weatherView = weatherView;
        weatherModel = new WeatherModelImpl(BaseApplication.getContext());
    }

    @Override
    public void getWeather(SavedCity savedCity) {
        weatherModel.loadCityWeather(savedCity,this);
    }

    @Override
    public void getLocationWeather(DefaultCity defaultCity) {
        weatherModel.loadLocationWeather(defaultCity,this);
    }

    @Override
    public void loadSuccess(Weather weather) {
        weatherView.showWeather(weather, false);
    }

    @Override
    public void loadFailure(String msg) {
        weatherView.showError(msg);
    }

}
