package com.tokyonth.weather.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tokyonth.weather.Constants;
import com.tokyonth.weather.R;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.base.BaseFragment;
import com.tokyonth.weather.model.bean.BlurEvent;
import com.tokyonth.weather.model.bean.ImageBackgroundEvent;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.widget.view.TempTextView;
import com.tokyonth.weather.widget.WeekWeatherView;

import org.greenrobot.eventbus.Subscribe;

public class WeatherPageSimple extends BaseFragment {

    // private TextView tvUpdateTime;
    private TempTextView tvTemp;
    private WeekWeatherView weekView;
    private TextView tvWeatherTips;

    private BlurSingle blurSingle;

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
    }

    @Override
    protected void initData() {
        blurSingle = new BlurSingle();
        if ((boolean) SPUtils.getData(Constants.SP_USE_PICTURE_BACKGROUND_KEY, false) &&
                (boolean) SPUtils.getData(Constants.SP_USE_BLUR_KEY, false)) {
            blurSingle.blurView(weekView);
        }
    }

    @Override
    protected void setBlurConfig(BlurEvent blurEvent) {
        if (blurEvent.isBlur()) {
            blurSingle.setRadius(blurEvent.getRadius());
            blurSingle.setRoundCorner(blurEvent.getRoundCorner());
            blurSingle.setScaleFactor(blurEvent.getScaleFactor());
            blurSingle.blurView(weekView);
        } else {
            blurSingle.stopBlur();
            weekView.setBackground(null);
        }
    }

    @Subscribe
    public void getImageBgEvent(ImageBackgroundEvent imageBackgroundEvent) {
        if (imageBackgroundEvent.isImageBg()) {
            blurSingle.stopBlur();
            weekView.setBackground(null);
        } else {
            blurSingle.changeImage();
        }
    }

    @Override
    protected void setWeather(Weather weather) {
        String tempLow = weather.getInfo().getTempLow();
        String tempHigh = weather.getInfo().getTempHigh();
        String tempInfo = weather.getInfo().getTemp() + getString(R.string.degree);
        tvWeatherTips.setVisibility(View.VISIBLE);
        tvWeatherTips.setText(getString(R.string.simple_weather_tips, tempHigh, tempLow, weather.getInfo().getWeather()));
        tvTemp.setText(tempInfo);
        weekView.setData(weather.getInfo().getDailyList());

        //String updateTime = WeatherInfoHelper.getUpdateTime(weather.getInfo().getUpdateTime());
        //tvUpdateTime.setText(getString(R.string.text_weather_update_time, updateTime));
        //int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        //iv_weather_text.setImageResource(weatherImagePath);
        //tv_weather_text.setText(weather.getInfo().getWeather());
    }

}
