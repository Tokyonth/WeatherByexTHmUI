package com.tokyonth.weather;

import com.tokyonth.weather.base.BaseApplication;
import com.tokyonth.weather.router.Router;

import java.io.File;

public class Constants {

    public static String SP_FILE_NAME = "config";
    public static final String IMPORT_DATA = "import_data";
    public static final String WEATHER_COLOR = "weather_color";
    public static final String DEFAULT_CITY_TEMP = "temp";
    public static final String DEFAULT_CITY_IMG = "img";
    public static final String DEFAULT_CITY_QUALITY = "quality";
    public static final String DEFAULT_CITY_LOW_TEMP = "low_temp";
    public static final String DEFAULT_CITY_HIGH_TEMP = "low_high";
    public static final String SAVE_WEATHER_NAME = "weather.json";
    public static final String PRECISE_LOCATION_NAME = "precise_location";
    public static final File DEFAULT_CACHE_PATH = BaseApplication.getContext().getCacheDir();

    public static final String SP_NOTIFICATION_WEATHER_KEY = "sp_notification_weather";
    public static final String SP_NOTIFICATION_WEATHER_STYLE_KEY = "sp_notification_weather_style";
    public static final String SP_USE_PICTURE_BACKGROUND_KEY = "sp_use_picture_background";
    public static final String SP_LOCK_SCREEN_WEATHER_KEY = "sp_lock_screen_weather";
    public static final String SP_PICTURE_PATH_KEY = "sp_picture_path";
    public static final String SP_USE_BLUR_KEY = "sp_use_blur";

    public enum WEATHER_TYPE {
        WEATHER_TYPE_SUNNY,
        WEATHER_TYPE_CLOUDY,
        WEATHER_TYPE_RAINY,
        WEATHER_TYPE_SNOWY,
        WEATHER_TYPE_FOGGY,
        WEATHER_TYPE_SANDY,
        WEATHER_TYPE_HAZY
    }

    public static int ROUTER_ANIM_ENTER = Router.RES_NONE;
    public static int ROUTER_ANIM_EXIT = Router.RES_NONE;

}
