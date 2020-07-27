package com.tokyonth.weather.adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.utils.SPUtils;
import com.tokyonth.weather.utils.api.Api;
import com.tokyonth.weather.view.BurnRoundView;
import com.tokyonth.weather.view.EnglishTextView;
import com.tokyonth.weather.utils.api.RetrofitFactory;
import com.tokyonth.weather.helper.WeatherInfoHelper;

import org.litepal.LitePal;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("SetTextI18n")
public class CityManagementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<SavedCity> savedCityList;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public CityManagementAdapter(Context context, List<SavedCity> savedCityList) {
        this.context = context;
        this.savedCityList = savedCityList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_management, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            DefaultCity defaultCity = LitePal.find(DefaultCity.class, 1);
            int temp = (int) SPUtils.getData(Constant.DEFAULT_CITY_TEMP, 0);
            int img = (int) SPUtils.getData(Constant.DEFAULT_CITY_IMG, 0);
            int weatherColor = WeatherInfoHelper.getWeatherColor(String.valueOf(img));
            int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(String.valueOf(img));

            ((CityViewHolder) holder).cityName.setText(defaultCity.getCityName());
            ((CityViewHolder) holder).tempTv.setText(temp + context.getString(R.string.celsius));
            ((CityViewHolder) holder).linearLayout.setBackgroundColor(weatherColor);
            ((CityViewHolder) holder).weatherImageIv.setImageResource(weatherImagePath);
        } else {
            SavedCity savedCity = savedCityList.get(position - 1);
            setWeatherInfo(savedCity, ((CityViewHolder) holder));
            ((CityViewHolder) holder).cityName.setText(savedCity.getCityName());
        }
        ((CityViewHolder) holder).itemView.setOnClickListener(v -> listener.onItemClick(v, position));
        ((CityViewHolder) holder).itemView.setOnLongClickListener(v -> {
            longClickListener.onItemLongClick(v, holder.getAdapterPosition());
            return true;
        });
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
                        int weatherColor = WeatherInfoHelper.getWeatherColor(weather.getInfo().getImg());
                        holder.linearLayout.setBackgroundColor(weatherColor);
                        holder.tempTv.setText(weather.getInfo().getTemp() + context.getString(R.string.celsius));
                        int weatherImagePath = WeatherInfoHelper.getWeatherImagePath(weather.getInfo().getImg());
                        holder.weatherImageIv.setImageResource(weatherImagePath);

                        holder.burnRoundView.setBurnSrc(weatherImagePath, weatherColor);
                    }

                    @Override
                    public void onError(Throwable e) {
                        holder.tempTv.setText("00");
                        holder.weatherImageIv.setImageResource(R.drawable.weather_nothing);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return savedCityList.size() + 1;
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {

        private TextView cityName;
        private ImageView weatherImageIv;
        private EnglishTextView tempTv;
        private LinearLayout linearLayout;
        private BurnRoundView burnRoundView;

        CityViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.city_management_city_name_tv);
            tempTv = itemView.findViewById(R.id.city_management_weather_temp_tv);
            weatherImageIv = itemView.findViewById(R.id.city_management_weather_image_iv);
            linearLayout = itemView.findViewById(R.id.city_management_back_ll);
            burnRoundView = itemView.findViewById(R.id.burn_icon);
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
