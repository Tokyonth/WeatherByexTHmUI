package com.tokyonth.weather.adapter;

import androidx.annotation.NonNull;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.tokyonth.weather.Constants;
import com.tokyonth.weather.R;
import com.tokyonth.weather.dynamic.DynamicWeatherView;
import com.tokyonth.weather.helper.WeatherTypeHelper;
import com.tokyonth.weather.model.bean.CityWeatherInfo;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.DateUtil;
import com.tokyonth.weather.utils.DisplayUtils;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.api.Api;
import com.tokyonth.weather.api.RetrofitFactory;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.widget.EnglishTextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CityManagementAdapter extends RecyclerView.Adapter<CityManagementAdapter.CityViewHolder> {

    private Context context;
    private List<SavedCity> savedCityList;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    private List<DynamicWeatherView> dynamicWeatherViewList = new ArrayList<>();

    public CityManagementAdapter(Context context, List<SavedCity> savedCityList) {
        this.context = context;
        this.savedCityList = savedCityList;
    }

    public void stopAll() {
        for (DynamicWeatherView dynamicWeatherView : dynamicWeatherViewList) {
            dynamicWeatherView.onPause();
        }
    }

    public void startAll() {
        for (DynamicWeatherView dynamicWeatherView : dynamicWeatherViewList) {
            dynamicWeatherView.onResume();
        }
    }

    public void notifyAdapterItemRemoved(int position) {
        dynamicWeatherViewList.get(position).onPause();
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public CityManagementAdapter.CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_management, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CityManagementAdapter.CityViewHolder holder, int position) {
        dynamicWeatherViewList.add(holder.dynamicWeatherView);
        if (position == 0) {
            DefaultCity defaultCity = LitePal.find(DefaultCity.class, 1);
            if (defaultCity != null) {
                int temp = (int) SPUtils.getData(Constants.DEFAULT_CITY_TEMP, 0);
                int img = (int) SPUtils.getData(Constants.DEFAULT_CITY_IMG, 0);
                int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(String.valueOf(img));
                String quality = (String) SPUtils.getData(Constants.DEFAULT_CITY_QUALITY, "");
                String lowTemp = (String) SPUtils.getData(Constants.DEFAULT_CITY_LOW_TEMP, "");
                String highTemp = (String) SPUtils.getData(Constants.DEFAULT_CITY_HIGH_TEMP, "");

                holder.ivLocal.setVisibility(View.VISIBLE);
                holder.cityName.setText(defaultCity.getCityName());
                holder.tempTv.setText(temp + context.getString(R.string.celsius));
                holder.weatherImageIv.setImageResource(weatherImagePath);
                holder.airTemp.setText("空气" + quality + "\t" + lowTemp + "/" + highTemp);

                Constants.WEATHER_TYPE weatherType = WeatherInfoHelper.getWeatherType(String.valueOf(img));
                holder.dynamicWeatherView.setDrawerType(WeatherTypeHelper.getType(true, weatherType));
                holder.dynamicWeatherView.onResume();
            }
        } else {
            SavedCity savedCity = savedCityList.get(position - 1);
            List<CityWeatherInfo> list = LitePal.findAll(CityWeatherInfo.class);
            for (CityWeatherInfo info : list) {
                if (savedCity.getCityName().equals(info.getName())) {
                    holder.tempTv.setText(info.getTemp() + context.getString(R.string.celsius));
                    int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(info.getImg());
                    holder.weatherImageIv.setImageResource(weatherImagePath);
                    Constants.WEATHER_TYPE weatherType = WeatherInfoHelper.getWeatherType(info.getImg());
                    holder.dynamicWeatherView.setDrawerType(WeatherTypeHelper.getType(info.isInTime(), weatherType));
                    holder.dynamicWeatherView.onResume();
                }
            }
            setWeatherInfo(savedCity, holder);
            holder.cityName.setText(savedCity.getCityName());
        }

        holder.itemView.setOnClickListener(v -> {
            stopAll();
            listener.onItemClick(v, position);
        });
        holder.itemView.setOnLongClickListener(v -> {
            holder.cardView.setStrokeColor(Color.RED);
            holder.cardView.setStrokeWidth(DisplayUtils.dip2px(context, 2));
            longClickListener.onItemLongClick(v, holder.getAdapterPosition());
            return true;
        });
    }

    public void setWeatherBackground(Weather weather) {
        List<Integer> list = WeatherInfoHelper.getSunriseSunset(weather);
        Constants.WEATHER_TYPE weatherType = WeatherInfoHelper.getWeatherType(weather.getInfo().getImg());
        boolean isInTime = DateUtil.isCurrentInTimeScope(list.get(0), list.get(1), list.get(2), list.get(3));
        if (weatherType != null) {
            new CityWeatherInfo(weather.getInfo().getCityName(), weather.getInfo().getTemp(), weather.getInfo().getImg(), isInTime).save();
        }
    }

    private void setWeatherInfo(SavedCity savedCity, final CityViewHolder holder) {
        new RetrofitFactory(Api.JISU_URL).getApiInterface()
                .getWeather(Api.getJisuAppKey(), savedCity.getCityCode())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        holder.tempTv.setText(weather.getInfo().getTemp() + context.getString(R.string.celsius));
                        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
                        holder.weatherImageIv.setImageResource(weatherImagePath);
                        holder.airTemp.setText("空气" + weather.getInfo().getAqi().getQuality() +
                                "\t" + weather.getInfo().getTempLow()
                                + "/" + weather.getInfo().getTempHigh());
                        setWeatherBackground(weather);
                    }

                    @Override
                    public void onError(Throwable e) {
                        holder.tempTv.setText("0");
                        holder.weatherImageIv.setImageResource(R.drawable.weather_nothing);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return savedCityList.size();
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {

        private TextView cityName;
        private ImageView weatherImageIv;
        private EnglishTextView tempTv;
        private TextView airTemp;
        private ImageView ivLocal;
        private MaterialCardView cardView;
        private DynamicWeatherView dynamicWeatherView;

        CityViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_management_city_name_tv);
            tempTv = itemView.findViewById(R.id.city_management_weather_temp_tv);
            weatherImageIv = itemView.findViewById(R.id.city_management_weather_image_iv);
            ivLocal = itemView.findViewById(R.id.iv_local);
            dynamicWeatherView = itemView.findViewById(R.id.item_dy);
            cardView = itemView.findViewById(R.id.city_management_cv);
            airTemp = itemView.findViewById(R.id.tv_air_and_temp);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        longClickListener = listener;
    }

}
