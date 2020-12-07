package com.tokyonth.weather.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.tokyonth.weather.Constants;
import com.tokyonth.weather.notification.NotificationWeather;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.utils.FileUtil;

import org.litepal.LitePal;

public class BaseApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
        context = getApplicationContext();

        SPUtils.getInstance(context, Constants.SP_FILE_NAME);
        if (!FileUtil.isFile(Constants.SAVE_WEATHER_NAME)) {
            FileUtil.saveFile("", Constants.SAVE_WEATHER_NAME);
        }
        boolean isOpen = (boolean) SPUtils.getData(Constants.SP_NOTIFICATION_WEATHER_KEY, false);
        new NotificationWeather(context, isOpen);
    }

    public static Context getContext() {
        return context;
    }

}
