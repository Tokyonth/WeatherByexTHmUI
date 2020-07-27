package com.tokyonth.weather.helper.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.WeatherModel;
import com.tokyonth.weather.model.WeatherModelImpl;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.OnWeatherListener;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.activity.MainActivity;

import org.litepal.LitePal;

public class WeatherSmallReceiver extends AppWidgetProvider implements OnWeatherListener {

    private ComponentName componentName;
    private AppWidgetManager manager;
    private RemoteViews remoteViews;
    private Context context;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        this.context = context;
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_small);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,R.id.widget_small_ll,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.widget_small_ll,pi);
        componentName = new ComponentName(context,WeatherSmallReceiver.class);
        manager = AppWidgetManager.getInstance(context);
        DefaultCity defaultCity = LitePal.find(DefaultCity.class,1);
        WeatherModel weatherModel = new WeatherModelImpl(context);
        weatherModel.loadLocationWeather(defaultCity,this);
    }

    @Override
    public void loadSuccess(Weather weather) {
        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        String temp = weather.getInfo().getTemp() + context.getString(R.string.celsius);
        remoteViews.setImageViewResource(R.id.widget_small_image_iv,weatherImagePath);
        remoteViews.setTextViewText(R.id.widget_small_text_tv,temp);
        manager.updateAppWidget(componentName,remoteViews);
    }

    @Override
    public void loadFailure(String msg) {
        remoteViews.setImageViewResource(R.id.widget_small_image_iv,R.drawable.weather_nothing);
        remoteViews.setTextViewText(R.id.widget_small_text_tv,"N/A");
        manager.updateAppWidget(componentName,remoteViews);
    }

}
