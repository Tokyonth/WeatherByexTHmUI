package com.tokyonth.weather.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tokyonth.weather.Constants;
import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.IndexAdapter;
import com.tokyonth.weather.adapter.WeatherTrendAdapter;
import com.tokyonth.weather.base.BaseFragment;
import com.tokyonth.weather.blur.BlurSingle;
import com.tokyonth.weather.model.bean.BlurEvent;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.model.bean.WeatherBean;
import com.tokyonth.weather.helper.WeatherInfoHelper;

import com.tokyonth.weather.model.bean.entity.Hourly;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.widget.EnglishTextView;
import com.tokyonth.weather.widget.SemicircleProgressView;
import com.tokyonth.weather.widget.Windmill;
import com.tokyonth.weather.widget.sunrisesunset.SunriseSunsetView;
import com.tokyonth.weather.widget.sunrisesunset.formatter.SunriseSunsetLabelFormatter;
import com.tokyonth.weather.widget.sunrisesunset.model.Time;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherPageFull extends BaseFragment {

    private TextView tv_level, tv_primary_pollute, tv_affect, tv_pressure;
    private TextView tv_pm25, tv_pm10, tv_so2, tv_no2, tv_co, tv_o3;
    private TextView tv_air_quality, tv_wind, tv_wind_speed, tv_forecast_day, tv_forecast_hourly;
    private RecyclerView rv_index, rv_weather_trend;
    private ImageView iv_air_quality;

    private EnglishTextView tv_humidity;
    private SunriseSunsetView sunset_view;
    private Windmill windmill_big, windmill_small;
    private SemicircleProgressView semicircle_progress_view;

    private BlurSingle blurSingle;
    private List<View> blurViewList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_weather_full;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        tv_level = view.findViewById(R.id.aqi_quality_level_tv);
        tv_primary_pollute = view.findViewById(R.id.aqi_primary_pollute_tv);
        tv_affect = view.findViewById(R.id.aqi_affect_tv);
        tv_pm25 = view.findViewById(R.id.aqi_pm25_tv);
        tv_pm10 = view.findViewById(R.id.aqi_pm10_tv);
        tv_so2 = view.findViewById(R.id.aqi_so2_tv);
        tv_no2 = view.findViewById(R.id.aqi_no2_tv);
        tv_co = view.findViewById(R.id.aqi_co_tv);
        tv_o3 = view.findViewById(R.id.aqi_o3_tv);
        tv_air_quality = view.findViewById(R.id.weather_airquality_tv);
        tv_wind = view.findViewById(R.id.weather_wind_tv);
        tv_wind_speed = view.findViewById(R.id.weather_wind_speed_tv);
        tv_humidity = view.findViewById(R.id.weather_humidity_tv);
        tv_forecast_hourly = view.findViewById(R.id.weather_forecast_hourly_tips_tv);
        tv_forecast_day = view.findViewById(R.id.weather_forecast_day_tips_tv);
        tv_pressure = view.findViewById(R.id.weather_pressure_tv);
        iv_air_quality = view.findViewById(R.id.weather_airquality_image_iv);
        rv_weather_trend = view.findViewById(R.id.weather_trend_rv);
        rv_index = view.findViewById(R.id.weather_index_rv);
        windmill_big = view.findViewById(R.id.windmill_big);
        windmill_small = view.findViewById(R.id.windmill_small);
        semicircle_progress_view = view.findViewById(R.id.semicircle_progress_view);
        sunset_view = view.findViewById(R.id.ssv);

        sunset_view.setLabelFormatter(new SunriseSunsetLabelFormatter() {
            @Override
            public String formatSunriseLabel(@NonNull Time sunrise) {
                return formatLabel(sunrise);
            }

            @Override
            public String formatSunsetLabel(@NonNull Time sunset) {
                return formatLabel(sunset);
            }

            private String formatLabel(Time time) {
                return String.format(Locale.getDefault(), "%02d:%02d", time.hour, time.minute);
            }
        });
        blurViewList = new ArrayList<>();
        blurViewList.add(rv_weather_trend);
        blurViewList.add(view.findViewById(R.id.content_weather_msg));
        blurViewList.add(view.findViewById(R.id.ll_ssv));
        blurViewList.add(view.findViewById(R.id.pager_aqi_weather));
        blurViewList.add(view.findViewById(R.id.pager_index_weather));
    }

    @Override
    protected void initData() {
        blurSingle = new BlurSingle();
        if ((boolean) SPUtils.getData(Constants.SP_USE_PICTURE_BACKGROUND_KEY, false) &&
                (boolean) SPUtils.getData(Constants.SP_USE_BLUR_KEY, false)) {
            blurSingle.blurView(blurViewList.get(0), blurViewList.get(1), blurViewList.get(2), blurViewList.get(3), blurViewList.get(4));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setWeather(Weather weather) {
        // 空气污染内容
        tv_pm25.setText("PM2.5 : " + weather.getInfo().getAqi().getPm2_5() + " μg/m³");
        tv_pm10.setText("PM10 : " + weather.getInfo().getAqi().getPm10() + " μg/m³");
        tv_so2.setText("SO₂ : " + weather.getInfo().getAqi().getSo2() + " μg/m³");
        tv_no2.setText("NO₂ : " + weather.getInfo().getAqi().getNo2() + " μg/m³");
        tv_o3.setText("O₃ : " + weather.getInfo().getAqi().getO3() + " μg/m³");
        tv_co.setText("CO : " + weather.getInfo().getAqi().getCo() + " μg/m³");

        // 24小时天气
        List<WeatherBean> data = new ArrayList<>();
        for (int i = 0; i <= 23; i++) {
            Hourly hourly = weather.getInfo().getHourlyList().get(i);
            WeatherBean bean = new WeatherBean(hourly.getWeather(), hourly.getTemp(), hourly.getTime());
            data.add(bean);
        }
        WeatherTrendAdapter adapter = new WeatherTrendAdapter(data);
        LinearLayoutManager ms = new LinearLayoutManager(getContext());
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_weather_trend.setLayoutManager(ms);
        rv_weather_trend.setAdapter(adapter);

        // 湿度、大气压等
        String humidityInfo = "空气湿度 : " + weather.getInfo().getHumidity() + "%";
        String windInfo = weather.getInfo().getWindDirect() + "\n" +  weather.getInfo().getWindPower();
        tv_wind.setText(windInfo);
        tv_humidity.setText(humidityInfo);
        tv_pressure.setText("气体压强 : " + weather.getInfo().getPressure() + "hPa");
        tv_forecast_day.setText(WeatherInfoHelper.getDayWeatherTipsInfo(weather.getInfo().getDailyList()));
        tv_forecast_hourly.setText(WeatherInfoHelper.getHourlyWeatherTipsInfo(weather.getInfo().getHourlyList()));

        // 空气质量
        String air_quality = weather.getInfo().getAqi().getQuality();
        int air_quality_color = WeatherInfoHelper.getAirQualityColor(air_quality);
        String levelInfo = "空气质量" + weather.getInfo().getAqi().getAqiInfo().getLevel();
        String primaryPolluteInfo = "首要污染物 : " + weather.getInfo().getAqi().getPrimarypollutant();
        int color = WeatherInfoHelper.getAirQualityColor(weather.getInfo().getAqi().getQuality());

        tv_level.setTextColor(WeatherInfoHelper.getAirQualityColor(weather.getInfo().getAqi().getQuality()));
        tv_affect.setText(weather.getInfo().getAqi().getAqiInfo().getAffect());
        tv_primary_pollute.setText(primaryPolluteInfo);
        tv_level.setText(levelInfo);
        tv_air_quality.setText(air_quality);
        tv_air_quality.setTextColor(air_quality_color);
        iv_air_quality.setColorFilter(air_quality_color);

        int aqi_index = Integer.parseInt(weather.getInfo().getAqi().getAqi());
        int default_max = 100;
        while (aqi_index > default_max) {
            default_max += 100;
        }
        semicircle_progress_view.setSesameValues(aqi_index, default_max);
        semicircle_progress_view.setSemicircleTitleColor(color);
        semicircle_progress_view.setFrontLineColor(color);

        // 生活指数
        IndexAdapter index_adapter = new IndexAdapter(getContext(), weather.getInfo().getIndexList());
        rv_index.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_index.setAdapter(index_adapter);

        // 日出日落
        List<Integer> list = WeatherInfoHelper.getSunriseSunset(weather);
        sunset_view.setSunriseTime(new Time(list.get(0), list.get(1)));
        sunset_view.setSunsetTime(new Time(list.get(2), list.get(3)));
        sunset_view.startAnimate();

        //refreshSSV(Sunrise_h, Sunrise_m, Sunset_h, Sunset_m);

        // 风车、风速
        String str = weather.getInfo().getWindSpeed();
        double wind_speed = Double.parseDouble(str);
        tv_wind_speed.setText("风速 : " + str + "m/s");
        windmill_big.startAnimation();
        windmill_small.startAnimation();

        windmill_big.setWindSpeed(wind_speed);
        windmill_small.setWindSpeed(wind_speed);
    }

    @Override
    protected void setBlurConfig(BlurEvent blurEvent) {
        if (blurEvent.isBlur()) {
            blurSingle.setRadius(blurEvent.getRadius());
            blurSingle.setRoundCorner(blurEvent.getRoundCorner());
            blurSingle.setScaleFactor(blurEvent.getScaleFactor());
            blurSingle.blurView(blurViewList.get(0), blurViewList.get(1), blurViewList.get(2), blurViewList.get(3), blurViewList.get(4));
        } else {
            blurSingle.stopBlur();
            int drawableRes = R.drawable.frame_reveal_weather_msg;
            for (View view : blurViewList) {
                view.setBackgroundResource(drawableRes);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windmill_big.clearAnimation();
        windmill_small.clearAnimation();
    }

}
