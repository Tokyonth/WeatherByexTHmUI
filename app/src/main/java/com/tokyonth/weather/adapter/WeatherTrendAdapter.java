package com.tokyonth.weather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.WeatherBean;

import java.util.List;

public class WeatherTrendAdapter extends RecyclerView.Adapter<WeatherTrendAdapter.WeatherTrendHolder> {

    private List<WeatherBean> weatherBean;

    public WeatherTrendAdapter(List<WeatherBean> weatherBean) {
        this.weatherBean = weatherBean;
    }

    private int getIconResId(String weather) {
        int resId = 0;
        if (weather.equals(WeatherBean.SUN)) {
            resId = R.drawable.weather_sunny;
        } else if (weather.equals(WeatherBean.OVERCAST)) {
            resId = R.drawable.weather_overcast;
        } else if (weather.equals(WeatherBean.RAIN)) {
            resId = R.drawable.weather_heavy_rain;
        } else if (weather.equals(WeatherBean.SNOW)) {
            resId = R.drawable.weather_light_snow;
        } else if (weather.equals(WeatherBean.CLOUDY)) {
            resId = R.drawable.weather_cloudy;
        } else if (weather.equals(WeatherBean.THUNDER)) {
            resId = R.drawable.weather_thunder;
        } else if (weather.equals(WeatherBean.LIGHT_RAIN)) {
            resId = R.drawable.weather_light_rain;
        } else if (weather.equals(WeatherBean.SHOWER)) {
            resId = R.drawable.weather_rain_shower;
        }
        return resId;
    }

    @NonNull
    @Override
    public WeatherTrendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_trend,parent,false);
        return new WeatherTrendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherTrendHolder holder, int position) {
        WeatherBean bean = weatherBean.get(position);
        holder.weather.setText(bean.weather);
        holder.time.setText(bean.time);
        holder.temp.setText(bean.temperatureStr);
        holder.imageView.setImageResource(getIconResId(bean.weather));
    }

    @Override
    public int getItemCount() {
        return weatherBean.size();
    }

    static class WeatherTrendHolder extends RecyclerView.ViewHolder {

        private TextView temp, time, weather;
        private ImageView imageView;

        WeatherTrendHolder(@NonNull View itemView) {
            super(itemView);
            temp = itemView.findViewById(R.id.weather_trend_item_tv_temp);
            time = itemView.findViewById(R.id.weather_trend_item_tv_time);
            imageView = itemView.findViewById(R.id.weather_trend_item_iv_info);
            weather = itemView.findViewById(R.id.weather_trend_item_tv_weather);
        }
    }

}
