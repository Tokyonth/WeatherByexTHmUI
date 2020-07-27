package com.tokyonth.weather.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.base.BaseFragment;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.view.TempTextView;
import com.tokyonth.weather.view.custom.WeekWeatherView;

public class WeatherPageSimple extends BaseFragment {

    private TextView tvUpdateTime;
    private TempTextView tvTemp;
    private WeekWeatherView weekView;
    private TextView tvWeatherTips;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weather_simple;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        tvUpdateTime = view.findViewById(R.id.tv_weather_update_time);
        tvTemp = view.findViewById(R.id.tv_weather_temp);
        weekView = view.findViewById(R.id.week_view);
        tvWeatherTips = view.findViewById(R.id.tv_weather_tips);
    }

    @Override
    protected void initData() {
        if ((boolean) SPUtils.getData(Constant.SP_USE_BLUR_KEY, false)) {
            //setBlur();
        }
    }

    private void setBlur(){
        View view = View.inflate(getActivity(), R.layout.activity_main, null);
        ImageView imageView = view.findViewById(R.id.iv_main_pic);
        //ImageView imageView = ((MainActivity)getActivity()).ivMainPic;
        BlurSingle.BlurLayout blur_single = new BlurSingle.BlurLayout(weekView, imageView);
        blur_single.setRadius(5);
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
        tvUpdateTime.setText(getString(R.string.text_weather_update_time, updateTime));
        tvTemp.setText(tempInfo);
        //int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        //iv_weather_text.setImageResource(weatherImagePath);
        //tv_weather_text.setText(weather.getInfo().getWeather());
    }

}
