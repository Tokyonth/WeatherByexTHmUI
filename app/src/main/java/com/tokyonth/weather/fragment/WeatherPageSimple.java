package com.tokyonth.weather.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.base.BaseFragment;
import com.tokyonth.weather.helper.RefreshWeather;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.NetworkUtil;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.view.TempTextView;
import com.tokyonth.weather.view.custom.WeekWeatherView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class WeatherPageSimple extends BaseFragment {

   // private TextView tvUpdateTime;
    private TempTextView tvTemp;
    private WeekWeatherView weekView;
    private TextView tvWeatherTips;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weather_simple;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
       // tvUpdateTime = view.findViewById(R.id.tv_weather_update_time);
        tvTemp = view.findViewById(R.id.tv_weather_temp);
        weekView = view.findViewById(R.id.week_view);
        tvWeatherTips = view.findViewById(R.id.tv_weather_tips);

        swipeRefreshLayout = view.findViewById(R.id.sr_refresh_city);
        swipeRefreshLayout.setOnRefreshListener(this::refreshWeather);
        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> false);
        swipeRefreshLayout.setRefreshing(true);

        new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
    }

    @Override
    protected void initData() {
        if ((boolean) SPUtils.getData(Constant.SP_USE_PICTURE_BACKGROUND_KEY, false) &&
                (boolean) SPUtils.getData(Constant.SP_USE_BLUR_KEY, false)) {
            setBlur();
        }

    }


    private void refreshWeather() {
        if (NetworkUtil.isWifiConnected() || NetworkUtil.isMobileConnected()) {
           // new RefreshWeather(isDefaultCity, cityName).setWeatherView(this);
        } else {
           // Snackbar.make(findViewById(R.id.cdl_weather_basic), getString(R.string.no_network_connection), Snackbar.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void receiveMsg(String insType) {
        if ("blurImageChange".equals(insType)) {
            setBlur();
        }
    }

    private void setBlur() {
        new BlurSingle.BlurLayout(weekView).setRadius(8);
        BlurSingle.initBkg(getContext(), 8, 32);
    }

    @Override
    protected void setWeather(Weather weather) {
        String tempLow = weather.getInfo().getTempLow();
        String tempHigh = weather.getInfo().getTempHigh();
        tvWeatherTips.setVisibility(View.VISIBLE);
        tvWeatherTips.setText(getString(R.string.simple_weather_tips, tempHigh, tempLow, weather.getInfo().getWeather()));
        weekView.setData(weather.getInfo().getDailyList());

        String updateTime = WeatherInfoHelper.getUpdateTime(weather.getInfo().getUpdateTime());
        String tempInfo = weather.getInfo().getTemp() + getString(R.string.degree);
       // tvUpdateTime.setText(getString(R.string.text_weather_update_time, updateTime));
        tvTemp.setText(tempInfo);
        //int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        //iv_weather_text.setImageResource(weatherImagePath);
        //tv_weather_text.setText(weather.getInfo().getWeather());
    }

}
