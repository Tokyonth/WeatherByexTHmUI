package com.tokyonth.weather.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tokyonth.weather.adapter.WeatherPagerAdapter;
import com.tokyonth.weather.base.BaseActivity;
import com.tokyonth.weather.Constants;
import com.tokyonth.weather.R;
import com.tokyonth.weather.helper.RefreshWeather;
import com.tokyonth.weather.helper.WeatherTypeHelper;
import com.tokyonth.weather.dynamic.DynamicWeatherView;
import com.tokyonth.weather.model.LocationMonitor;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.ImageBackgroundEvent;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.LocationPresenterImpl;
import com.tokyonth.weather.presenter.WeatherPresenter;
import com.tokyonth.weather.presenter.WeatherPresenterImpl;
import com.tokyonth.weather.utils.DateUtil;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.utils.FileUtil;
import com.tokyonth.weather.presenter.WeatherView;
import com.tokyonth.weather.utils.NetworkUtil;
import com.tokyonth.weather.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.litepal.LitePal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class MainActivity extends BaseActivity implements WeatherView {

    private Weather weatherBean = null;
    private WeatherPresenter weatherPresenter;
    private DynamicWeatherView dynamicWeatherView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView tvCityName;
    private ImageView ivDefaultCity;
    private ImageView ivBlurPicture;

    private boolean isFirst = true;
    private boolean isDefaultCity = true;
    private String cityName = null;

    @Override
    public int setActivityView() {
        return R.layout.activity_main;
    }

    @Override
    public boolean isDarkStatusBar() {
        return false;
    }

    @Override
    public boolean isTranslucentNav() {
        return true;
    }

    @Override
    public void initActivity(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        startSplashActivity();
    }

    private void startSplashActivity() {
        if ((boolean) SPUtils.getData(Constants.IMPORT_DATA, true)) {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        } else {
            initView();
            initData();
        }
    }

    private void initData() {
        weatherPresenter = new WeatherPresenterImpl(this);
        new LocationPresenterImpl().loadLocation(this, new LocationMonitor() {
            @Override
            public void Failure() {
                if (LitePal.find(DefaultCity.class, 1) == null) {
                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setTitle(getString(R.string.text_tips))
                            .setMessage(getString(R.string.text_failed_to_locate))
                            .setNegativeButton(getString(R.string.text_manual_selection),
                                    (dialog, which) -> startActivity(new Intent(MainActivity.this, CityActivity.class)))
                            .setPositiveButton(getString(R.string.text_cancel), null)
                            .setCancelable(false)
                            .create().show();
                } else {
                    DefaultCity defaultCity = LitePal.find(DefaultCity.class, 1);
                    tvCityName.setText(defaultCity.getCityName());
                    cityName = defaultCity.getCityName();
                }
            }

            @Override
            public void Success() {

            }
        });
        String jsonContent = FileUtil.getFile(Constants.SAVE_WEATHER_NAME);
        if (jsonContent != null && !jsonContent.equals("")) {
            weatherBean = new Gson().fromJson(FileUtil.getFile(Constants.SAVE_WEATHER_NAME), Weather.class);
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_title_more));
        setSupportActionBar(toolbar);
        setTitle(null);

        ivDefaultCity = findViewById(R.id.iv_default_city);
        tvCityName = findViewById(R.id.tv_city_name);
        ivBlurPicture = findViewById(R.id.iv_main_pic);
        dynamicWeatherView = findViewById(R.id.dynamic_weather_view);
        swipeRefreshLayout = findViewById(R.id.sr_refresh_city);
        swipeRefreshLayout.setOnRefreshListener(this::refreshWeather);
        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> false);
        //swipeRefreshLayout.setRefreshing(true);

        WeatherPagerAdapter weatherPagerAdapter = new WeatherPagerAdapter(this);
        ViewPager2 viewPager2 = findViewById(R.id.vp_weather_page);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(weatherPagerAdapter);
        viewPager2.setOffscreenPageLimit(weatherPagerAdapter.getItemCount());
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                swipeRefreshLayout.setEnabled(position == 0);
            }
        });
    }

    public void setWeatherBackground(Weather weather) {
        List<Integer> list = WeatherInfoHelper.getSunriseSunset(weather);
        Constants.WEATHER_TYPE weatherType = WeatherInfoHelper.getWeatherType(weather.getInfo().getImg());
        boolean isInTime = DateUtil.isCurrentInTimeScope(list.get(0), list.get(1), list.get(2), list.get(3));
        if (weatherType != null) {
            dynamicWeatherView.setDrawerType(WeatherTypeHelper.getType(isInTime, weatherType));
        } else {
            Snackbar.make(findViewById(R.id.cdl_weather_basic), getString(R.string.text_dynamic_background_error), Snackbar.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void getLocation(DefaultCity defaultCity) {
        weatherPresenter.getLocationWeather(defaultCity);
        ivDefaultCity.setVisibility(View.VISIBLE);
        cityName = defaultCity.getCityName();
        isDefaultCity = true;
    }

    @Subscribe
    public void getCity(SavedCity savedCity) {
        weatherPresenter.getWeather(savedCity);
        ivDefaultCity.setVisibility(View.GONE);
        cityName = savedCity.getCityCode();
        isDefaultCity = false;
    }

    @Subscribe
    public void getImageBgEvent(ImageBackgroundEvent imageBackgroundEvent) {
        if (imageBackgroundEvent.isImageBg()) {
            String path = imageBackgroundEvent.getImagePath();
            if (!path.equals("")) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                    ivBlurPicture.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            ivBlurPicture.setVisibility(View.VISIBLE);
            // dynamicWeatherView.onResume();
        } else {
            ivBlurPicture.setVisibility(View.GONE);
            // dynamicWeatherView.onPause();
        }
    }

    @Override
    public void showWeather(Weather weather, boolean isRefresh) {
        setWeatherBackground(weather);
        EventBus.getDefault().post(weather);
        tvCityName.setText(weather.getInfo().getCityName());
        if (isRefresh) {
            swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(findViewById(R.id.cdl_weather_basic), getString(R.string.refresh_success), Snackbar.LENGTH_LONG).show();
        } else {
            new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 800);
        }
    }

    private void refreshWeather() {
        if (NetworkUtil.isNetworkConnected()) {
             new RefreshWeather(isDefaultCity, cityName).setWeatherView(this);
        } else {
             Snackbar.make(findViewById(R.id.cdl_weather_basic), getString(R.string.no_network_connection), Snackbar.LENGTH_LONG).show();
             swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showError(String error) {
        swipeRefreshLayout.setRefreshing(false);
        Snackbar.make(findViewById(R.id.cdl_weather_basic), error + getString(R.string.load_last_time_msg), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_city:
                startActivity(new Intent(MainActivity.this, CityActivity.class));
                break;
            case R.id.action_settings:
                startActivityForResult(new Intent(MainActivity.this, SettingsActivity.class), 0);
                break;
            case R.id.action_warning:
                startActivity(new Intent(MainActivity.this, WarningActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (weatherBean != null && isFirst) {
            setWeatherBackground(weatherBean);
            EventBus.getDefault().post(weatherBean);
            isFirst = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dynamicWeatherView != null)
            dynamicWeatherView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dynamicWeatherView != null)
            dynamicWeatherView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dynamicWeatherView != null)
            dynamicWeatherView.onDestroy();
    }

}
