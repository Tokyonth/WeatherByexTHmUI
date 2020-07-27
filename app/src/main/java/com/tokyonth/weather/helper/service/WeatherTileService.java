package com.tokyonth.weather.helper.service;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import androidx.annotation.RequiresApi;
import android.widget.Toast;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.WeatherModel;
import com.tokyonth.weather.model.WeatherModelImpl;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.OnWeatherListener;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.activity.MainActivity;

import org.litepal.LitePal;

@RequiresApi(api = Build.VERSION_CODES.N)
public class WeatherTileService extends TileService implements OnWeatherListener {

    @Override
    public void onStartListening() {
        DefaultCity defaultCity = LitePal.find(DefaultCity.class,1);
        if(defaultCity != null){
            WeatherModel weatherModel = new WeatherModelImpl(getApplicationContext());
            weatherModel.loadLocationWeather(defaultCity,this);
        } else {
            Toast.makeText(this, getApplicationContext().getString(R.string.text_default_city_not_exist), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityAndCollapse(intent);
    }

    @Override
    public void loadSuccess(Weather weather) {
        int weatherIconPath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
        Icon icon = Icon.createWithResource(this,weatherIconPath);
        String temp = weather.getInfo().getTemp() + getApplicationContext().getString(R.string.celsius);
        String local = weather.getInfo().getCityName();
        getQsTile().setState(Tile.STATE_INACTIVE);
        getQsTile().setIcon(icon);
        getQsTile().setLabel(local + temp);
        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

    @Override
    public void loadFailure(String msg) {
        Icon icon = Icon.createWithResource(this, R.drawable.weather_nothing);
        getQsTile().setState(Tile.STATE_INACTIVE);
        getQsTile().setIcon(icon);
        getQsTile().setLabel("N/A");
        getQsTile().setState(Tile.STATE_ACTIVE);
        getQsTile().updateTile();
    }

}
