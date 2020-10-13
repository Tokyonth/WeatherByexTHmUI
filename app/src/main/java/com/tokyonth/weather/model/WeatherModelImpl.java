package com.tokyonth.weather.model;

import android.content.Context;

import com.google.gson.Gson;
import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.OnWeatherListener;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.utils.api.Api;
import com.tokyonth.weather.utils.NetworkUtil;
import com.tokyonth.weather.utils.api.RetrofitFactory;
import com.tokyonth.weather.utils.FileUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class WeatherModelImpl implements WeatherModel {

    private Context context;

    public WeatherModelImpl(Context context) {
        this.context = context;
    }

    @Override
    public void loadCityWeather(SavedCity savedCity, final OnWeatherListener listener) {
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
            new RetrofitFactory(Api.JISU_URL).getApiInterface()
                    .getWeather(Api.getJisuAppKey(), savedCity.getCityCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Weather>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Weather weather) {
                            listener.loadSuccess(weather);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            listener.loadFailure(context.getString(R.string.no_network_connection));
        }
    }

    @Override
    public void loadLocationWeather(DefaultCity defaultCity, final OnWeatherListener listener) {
        String city = defaultCity.getCityName();
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
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
                            String jsonStr = new Gson().toJson(weather);
                            FileUtil.saveFile(jsonStr, Constant.SAVE_WEATHER_NAME);

                            int temp = Integer.parseInt(weather.getInfo().getTemp());
                            int img = Integer.parseInt(weather.getInfo().getImg());
                            int color = WeatherInfoHelper.getWeatherColor(weather.getInfo().getImg());
                            SPUtils.putData(Constant.DEFAULT_CITY_TEMP, temp);
                            SPUtils.putData(Constant.DEFAULT_CITY_IMG, img);
                            SPUtils.putData(Constant.WEATHER_COLOR, color);
                            SPUtils.putData(Constant.DEFAULT_CITY_QUALITY, weather.getInfo().getAqi().getQuality());
                            SPUtils.putData(Constant.DEFAULT_CITY_LOW_TEMP, weather.getInfo().getTempLow());
                            SPUtils.putData(Constant.DEFAULT_CITY_HIGH_TEMP, weather.getInfo().getTempHigh());
                            listener.loadSuccess(weather);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            listener.loadFailure(context.getString(R.string.no_network_connection));
        }

    }

}
