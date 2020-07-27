package com.tokyonth.weather.helper;

import com.tokyonth.weather.Constant;
import com.tokyonth.weather.dynamic.BaseDrawer;

public class WeatherTypeHelper {

    public static BaseDrawer.Type getType(boolean isDay, Constant.WEATHER_TYPE weatherType) {
        BaseDrawer.Type DrawerType;
        switch (weatherType) {
            case WEATHER_TYPE_SUNNY:
                DrawerType = isDay ? BaseDrawer.Type.CLEAR_D : BaseDrawer.Type.CLEAR_N;
                break;
            case WEATHER_TYPE_CLOUDY:
                DrawerType = isDay ? BaseDrawer.Type.CLOUDY_D : BaseDrawer.Type.CLOUDY_N;
                break;
            case WEATHER_TYPE_RAINY:
                DrawerType = isDay ? BaseDrawer.Type.RAIN_D : BaseDrawer.Type.RAIN_N;
                break;
            case WEATHER_TYPE_SNOWY:
                DrawerType = isDay ? BaseDrawer.Type.SNOW_D : BaseDrawer.Type.SNOW_N;
                break;
            case WEATHER_TYPE_FOGGY:
                DrawerType = isDay ? BaseDrawer.Type.FOG_D : BaseDrawer.Type.FOG_N;
                break;
            case WEATHER_TYPE_SANDY:
                DrawerType = isDay ? BaseDrawer.Type.SAND_D : BaseDrawer.Type.SAND_N;
                break;
            case WEATHER_TYPE_HAZY:
                DrawerType = isDay ? BaseDrawer.Type.HAZE_D : BaseDrawer.Type.HAZE_N;
                break;
            default:
                DrawerType = BaseDrawer.Type.DEFAULT;
                break;
        }
        return DrawerType;
    }
}
