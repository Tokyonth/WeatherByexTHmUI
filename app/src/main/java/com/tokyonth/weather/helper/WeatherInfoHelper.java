package com.tokyonth.weather.helper;

import android.content.res.Resources;
import android.graphics.Color;

import com.tokyonth.weather.Constant;
import com.tokyonth.weather.base.BaseApplication;
import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.model.bean.entity.Daily;
import com.tokyonth.weather.model.bean.entity.Hourly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeatherInfoHelper {

    public static String getHourlyWeatherTipsInfo(List<Hourly> hourlyList) {
        String info = BaseApplication.getContext().getString(R.string.default_hourly_weather_tips);
        for (Hourly hourly : hourlyList) {
            if (!hourly.getImg().isEmpty()) {
                int imgCode = Integer.parseInt(hourly.getImg());
                if (imgCode >= 3) {
                    info = hourly.getTime() + "\t" + hourly.getWeather();
                    return info;
                }
            }
        }
        return info;
    }

    public static String getDayWeatherTipsInfo(List<Daily> dailyList) {
        String weatherInfo = BaseApplication.getContext().getString(R.string.default_day_weather_tips);
        for (Daily daily : dailyList) {
            if (!daily.getDay().getImg().isEmpty()) {
                int dayImgCode = Integer.parseInt(daily.getDay().getImg());
                int nightImgCode = Integer.parseInt(daily.getNight().getImg());
                if (dayImgCode >= 3) {
                    if (getDay(daily.getDate()).equals(BaseApplication.getContext().getString(R.string.text_today))) {
                        weatherInfo = getDay(daily.getDate()) + BaseApplication.getContext().getString(R.string.text_day) + "\t" + daily.getDay().getWeather();
                    } else {
                        weatherInfo = getDay(daily.getDate()) + BaseApplication.getContext().getString(R.string.text_day_date) + "\t" + daily.getDay().getWeather();
                    }
                    return weatherInfo;
                }
                if (nightImgCode >= 3) {
                    if (getDay(daily.getDate()).equals(BaseApplication.getContext().getString(R.string.text_today))) {
                        weatherInfo = getDay(daily.getDate()) + BaseApplication.getContext().getString(R.string.text_night) + "\t" + daily.getDay().getWeather();
                    } else {
                        weatherInfo = getDay(daily.getDate()) + BaseApplication.getContext().getString(R.string.text_night_date) + "\t" + daily.getDay().getWeather();
                    }
                    return weatherInfo;
                }
            }
        }
        return weatherInfo;
    }

    public static String getDay(String date) {
        String[] dates = date.split("-");
        String today = String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        if (dates[2].equals(today)) {
            return BaseApplication.getContext().getString(R.string.text_today);
        }
        return dates[2];
    }

    public static String getUpdateTime(String time) {
        String[] times = time.split(" ");
        return times[1];
    }

    public static int getWeatherImagePath(String img) {
        Resources resources = BaseApplication.getContext().getResources();
        String packageName = BaseApplication.getContext().getPackageName();
        String category = "drawable";
        int weatherImagePath = 0;
        if (!img.isEmpty()) {
            int imgCode = Integer.parseInt(img);
            if (imgCode == 0) {
                weatherImagePath = resources.getIdentifier("weather_sunny", category, packageName);
            } else if (imgCode == 1) {
                weatherImagePath = resources.getIdentifier("weather_cloudy", category, packageName);
            } else if (imgCode == 2) {
                weatherImagePath = resources.getIdentifier("weather_overcast", category, packageName);
            } else if (imgCode == 3) {
                weatherImagePath = resources.getIdentifier("weather_rain_shower", category, packageName);
            } else if (imgCode == 4) {
                weatherImagePath = resources.getIdentifier("weather_shower", category, packageName);
            } else if (imgCode == 5) {
                weatherImagePath = resources.getIdentifier("weather_hail", category, packageName);
            } else if (imgCode == 6 || imgCode == 7) {
                weatherImagePath = resources.getIdentifier("weather_light_rain", category, packageName);
            } else if (imgCode == 8 || imgCode == 21 || imgCode == 22) {
                weatherImagePath = resources.getIdentifier("weather_moderate_rain", category, packageName);
            } else if (imgCode == 9 || imgCode == 301 || imgCode == 23) {
                weatherImagePath = resources.getIdentifier("weather_heavy_rain", category, packageName);
            } else if (imgCode == 10 || imgCode == 11 || imgCode == 12 || imgCode == 24 || imgCode == 25) {
                weatherImagePath = resources.getIdentifier("weather_rainstorm", category, packageName);
            } else if (imgCode == 14 || imgCode == 15 || imgCode == 26) {
                weatherImagePath = resources.getIdentifier("weather_light_snow", category, packageName);
            } else if (imgCode == 13 || imgCode == 16 || imgCode == 17 || imgCode == 27 || imgCode == 28 || imgCode == 302) {
                weatherImagePath = resources.getIdentifier("weather_moderate_snow", category, packageName);
            } else if (imgCode == 18 || imgCode == 32 || imgCode == 49 || imgCode == 57 || imgCode == 58) {
                weatherImagePath = resources.getIdentifier("weather_fog", category, packageName);
            } else if (imgCode == 19) {
                weatherImagePath = resources.getIdentifier("weather_icerain", category, packageName);
            } else if (imgCode == 20 || imgCode == 29 || imgCode == 30 || imgCode == 31) {
                weatherImagePath = resources.getIdentifier("weather_sand", category, packageName);
            } else if (imgCode >= 53 && imgCode <= 56) {
                weatherImagePath = resources.getIdentifier("weather_haze", category, packageName);
            }
            return weatherImagePath;
        }
        return weatherImagePath;

    }

    public static int getWeatherColor(String img) {
        Resources resources = BaseApplication.getContext().getResources();
        int weatherColor = Color.BLACK;
        if (!img.isEmpty()) {
            int imgCode = Integer.parseInt(img);
            if (imgCode == 0) {
                weatherColor = resources.getColor(R.color.sunny);
            } else if (imgCode == 1 || imgCode == 2) {
                weatherColor = resources.getColor(R.color.cloudy);
            } else if (imgCode >= 3 && imgCode <= 12 || imgCode == 19 || imgCode >= 21 && imgCode <= 25 || imgCode == 301) {
                weatherColor = resources.getColor(R.color.rain);
            } else if (imgCode >= 13 && imgCode <= 17 || imgCode >= 26 && imgCode <= 28 || imgCode == 302) {
                weatherColor = resources.getColor(R.color.snow);
            } else if (imgCode == 18 || imgCode == 32 || imgCode == 49 || imgCode == 57 || imgCode == 58) {
                weatherColor = resources.getColor(R.color.fog);
            } else if (imgCode == 20 || imgCode == 29 || imgCode == 30 || imgCode == 31) {
                weatherColor = resources.getColor(R.color.sand);
            } else if (imgCode >= 53 && imgCode <= 56) {
                weatherColor = resources.getColor(R.color.haze);
            }
        }
        return weatherColor;
    }

    public static int getAirQualityColor(String airQuality) {
        int airQualityColor = 0;
        if (!airQuality.isEmpty()) {
            if (airQuality.equals(getString(R.string.air_quality_good))) {
                airQualityColor = getColor(R.color.airQualityGood);
            } else if (airQuality.equals(getString(R.string.air_quality_medium))) {
                airQualityColor = getColor(R.color.airQualityMedium);
            } else if (airQuality.equals(getString(R.string.air_quality_lightly_pollute))) {
                airQualityColor = getColor(R.color.airQualityLightlyPollute);
            } else if (airQuality.equals(getString(R.string.air_quality_medium_pollute))) {
                airQualityColor = getColor(R.color.airQualityMediumPollute);
            } else if (airQuality.equals(getString(R.string.air_quality_heavy_pollute))) {
                airQualityColor = getColor(R.color.airQualityHeavyPollute);
            } else if (airQuality.equals(getString(R.string.air_quality_deep_pollute))) {
                airQualityColor = getColor(R.color.airQualityDeepPollute);
            }
            return airQualityColor;
        }
        return airQualityColor;
    }

    private static String getString(int resID) {
        return BaseApplication.getContext().getString(resID);
    }
    private static int getColor(int resID) {
        return BaseApplication.getContext().getResources().getColor(resID);
    }


    public static Constant.WEATHER_TYPE getWeatherType(String img) {
        Constant.WEATHER_TYPE weatherType = null;
        if (!img.isEmpty()) {
            int imgCode = Integer.parseInt(img);
            if (imgCode == 0) {
                weatherType = Constant.WEATHER_TYPE.WEATHER_TYPE_SUNNY;
            } else if (imgCode == 1 || imgCode == 2) {
                weatherType = Constant.WEATHER_TYPE.WEATHER_TYPE_CLOUDY;
            } else if (imgCode >= 3 && imgCode <= 12 || imgCode == 19 || imgCode >= 21 && imgCode <= 25 || imgCode == 301) {
                weatherType = Constant.WEATHER_TYPE.WEATHER_TYPE_RAINY;
            } else if (imgCode >= 13 && imgCode <= 17 || imgCode >= 26 && imgCode <= 28 || imgCode == 302) {
                weatherType = Constant.WEATHER_TYPE.WEATHER_TYPE_SNOWY;
            } else if (imgCode == 18 || imgCode == 32 || imgCode == 49 || imgCode == 57 || imgCode == 58) {
                weatherType = Constant.WEATHER_TYPE.WEATHER_TYPE_FOGGY;
            } else if (imgCode == 20 || imgCode == 29 || imgCode == 30 || imgCode == 31) {
                weatherType = Constant.WEATHER_TYPE.WEATHER_TYPE_SANDY;
            } else if (imgCode >= 53 && imgCode <= 56) {
                weatherType = Constant.WEATHER_TYPE.WEATHER_TYPE_HAZY;
            }
            return weatherType;
        }
        return null;
    }

    public static List<Integer> getSunriseSunset(Weather weather) {
        String Sunrise = weather.getInfo().getDailyList().get(0).getSunrise();
        String Sunset = weather.getInfo().getDailyList().get(0).getSunset();

        int index = Sunrise.indexOf(":");
        String SunriseBefore = Sunrise.substring(0, index);
        String SunriseAfter = Sunrise.substring(index + 1);
        String SunsetBefore = Sunset.substring(0, index);
        String SunsetAfter = Sunset.substring(index + 1);

        int Sunrise_h = Integer.parseInt(SunriseBefore);
        int Sunrise_m = Integer.parseInt(SunriseAfter);
        int Sunset_h = Integer.parseInt(SunsetBefore);
        int Sunset_m = Integer.parseInt(SunsetAfter);

        // list 0 - 3 分别是 日出的小时和分钟 和 日落的小时和分钟
        List<Integer> list = new ArrayList<>();
        list.add(Sunrise_h);
        list.add(Sunrise_m);
        list.add(Sunset_h);
        list.add(Sunset_m);

        return list;
    }

}
