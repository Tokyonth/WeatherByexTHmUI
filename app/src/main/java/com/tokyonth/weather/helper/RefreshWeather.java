package com.tokyonth.weather.helper;

import android.util.Log;

import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.WeatherView;
import com.tokyonth.weather.utils.api.RetrofitFactory;
import com.tokyonth.weather.utils.api.Api;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RefreshWeather {

    public WeatherView weatherView;

    public void setWeatherView(WeatherView weatherView) {
        this.weatherView = weatherView;
    }

    public RefreshWeather(boolean isDefaultCity, String city) {
        if (!isDefaultCity) {
            new RetrofitFactory(Api.JISU_URL).getApiInterface()
                    .getWeather(Api.getJisuAppKey(), city)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Weather>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Weather weather) {
                            weatherView.showWeather(weather, true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            weatherView.showError(e.toString());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            new RetrofitFactory(Api.JISU_URL).getApiInterface()
                    .getLocationWeather(Api.getJisuAppKey(), city)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Weather>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Weather weather) {
                            weatherView.showWeather(weather, true);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("错误", "-->" + e + city);
                            weatherView.showError(e.toString());
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

}
