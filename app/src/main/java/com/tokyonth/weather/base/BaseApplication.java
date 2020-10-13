package com.tokyonth.weather.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.tokyonth.weather.Constant;
import com.tokyonth.weather.helper.notification.NotificationWeather;
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

        SPUtils.getInstance(context, Constant.SP_FILE_NAME);
        if (!FileUtil.isFile(Constant.SAVE_WEATHER_NAME)) {
            FileUtil.saveFile("", Constant.SAVE_WEATHER_NAME);
        }
        boolean isOpen = (boolean) SPUtils.getData(Constant.SP_NOTIFICATION_WEATHER_KEY, false);
        new NotificationWeather(context, isOpen);
    }

    public static Context getContext() {
        return context;
    }

}
