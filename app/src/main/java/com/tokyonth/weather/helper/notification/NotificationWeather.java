package com.tokyonth.weather.helper.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;
import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.MainActivity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.utils.DateUtil;
import com.tokyonth.weather.utils.FileUtil;
import com.tokyonth.weather.utils.SPUtils;

import static android.app.Notification.VISIBILITY_SECRET;

public class NotificationWeather {

    private Context context;
    private int notificationId = 0;
    private Weather weather;

    public NotificationWeather(Context context, boolean isOpen) {
        this.context = context;
        weather = new Gson().fromJson(FileUtil.getFile(Constant.SAVE_WEATHER_NAME), Weather.class);
        if (isOpen) {
            sendCustomNotification();
        } else {
            NotificationUtil.getNotificationManager(context).cancel(notificationId);
        }
    }

    @NonNull
    public NotificationCompat.Builder getNotificationBuilder() {
        String channel_id = context.getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channel_id, channel_id,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            //锁屏显示通知
            if ((boolean) SPUtils.getData(Constant.SP_LOCK_SCREEN_WEATHER_KEY, false)) {
                channel.setLockscreenVisibility(VISIBILITY_SECRET);
            }
            channel.setSound(null, null);
            NotificationUtil.getNotificationManager(context).createNotificationChannel(channel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channel_id);
        int statusBarStyle = (int) SPUtils.getData(Constant.SP_NOTIFICATION_WEATHER_STYLE_KEY, 0);
        if (statusBarStyle == 0) {
            int weatherIconPath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
            notification.setSmallIcon(weatherIconPath);
        } else if (statusBarStyle == 1){
            notification.setSmallIcon(R.mipmap.ic_launcher);
        }
        notification.setAutoCancel(true);
        return notification;
    }


    public void sendCustomNotification() {
        if (!NotificationUtil.isOpenPermission(context)) {
            return;
        }
        NotificationCompat.Builder builder = getNotificationBuilder();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notifition_weather);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, -1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_notification_weather, pendingIntent);

        builder.setOngoing(true);//设置无法取消
        builder.setAutoCancel(false);//点击后不取消
        builder.setCustomContentView(remoteViews);

        int weatherIconPath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        //Icon icon = Icon.createWithResource(BaseApplication.getContext(), weatherIconPath);
        //remoteViews.setImageViewIcon(R.id.notification_iv, icon);
        remoteViews.setImageViewResource(R.id.iv_notification_icon, weatherIconPath);
        remoteViews.setTextViewText(R.id.tv_notification_city, weather.getInfo().getCityName());
        remoteViews.setTextViewText(R.id.tv_notification_weather, weather.getInfo().getWeather() + "\t" +
                weather.getInfo().getTemp() + context.getString(R.string.celsius));
        remoteViews.setTextViewText(R.id.tv_notification_time, DateUtil.getSystemDate());
        NotificationUtil.getNotificationManager(context).notify(notificationId, builder.build());
    }

}

