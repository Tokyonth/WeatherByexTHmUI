package com.tokyonth.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tokyonth.weather.adapter.WeatherPagerAdapter;
import com.tokyonth.weather.base.BaseActivity;
import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.helper.WeatherTypeHelper;
import com.tokyonth.weather.dynamic.DynamicWeatherView;
import com.tokyonth.weather.fragment.WeatherPageSimple;
import com.tokyonth.weather.fragment.WeatherPageFull;
import com.tokyonth.weather.model.LocationMonitor;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.model.bean.Weather;
import com.tokyonth.weather.presenter.LocationPresenterImpl;
import com.tokyonth.weather.presenter.WeatherPresenter;
import com.tokyonth.weather.presenter.WeatherPresenterImpl;
import com.tokyonth.weather.utils.DisplayUtils;
import com.tokyonth.weather.utils.NetworkUtil;
import com.tokyonth.weather.utils.DateUtil;
import com.tokyonth.weather.helper.RefreshWeather;
import com.tokyonth.weather.helper.WeatherInfoHelper;
import com.tokyonth.weather.utils.FileUtil;
import com.tokyonth.weather.presenter.WeatherView;
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

    private TextView tvCityName;
    private ImageView ivDefaultCity;
    private ImageView ivBlurPicture;
    private ViewPager2 viewPager2;

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
        if ((boolean) SPUtils.getData(Constant.IMPORT_DATA, true)) {
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        } else {
            initView();
            initData();
            initPictureBackground();
            //initFragmentWeatherPage();
        }
    }

    private void initData() {
        weatherPresenter = new WeatherPresenterImpl(MainActivity.this);
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
        String jsonContent = FileUtil.getFile(Constant.SAVE_WEATHER_NAME);
        if (jsonContent != null && !jsonContent.equals("")) {
            weatherBean = new Gson().fromJson(FileUtil.getFile(Constant.SAVE_WEATHER_NAME), Weather.class);
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_title_more));
        setSupportActionBar(toolbar);
        setTitle(null);

        /*TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        int toolBarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);*/

        /*int resourceId= getResources().getIdentifier("navigation_bar_height","dimen","android");
        int height = getResources().getDimensionPixelSize(resourceId);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtils.getScreenWidth(MainActivity.this),
                DisplayUtils.getScreenHeight(MainActivity.this) + height);
        findViewById(R.id.frag_simple).setLayoutParams(params);*/

        ivDefaultCity = findViewById(R.id.iv_default_city);
        tvCityName = findViewById(R.id.tv_city_name);
        ivBlurPicture = findViewById(R.id.iv_main_pic);
        dynamicWeatherView = findViewById(R.id.dynamic_weather_view);

        viewPager2 = findViewById(R.id.vp2);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        viewPager2.setAdapter(new WeatherPagerAdapter(this));
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
               // boolean is = ;
               // if (position != 0)
                  //  swipeRefreshLayout.setEnabled(position == 0);
            }
        });
    }


    public void setWeatherBackground(Weather weather) {
        List<Integer> list = WeatherInfoHelper.getSunriseSunset(weather);
        Constant.WEATHER_TYPE weatherType = WeatherInfoHelper.getWeatherType(weather.getInfo().getImg());
        boolean isInTime = DateUtil.isCurrentInTimeScope(list.get(0), list.get(1), list.get(2), list.get(3));
        if (weatherType != null) {
            dynamicWeatherView.setDrawerType(WeatherTypeHelper.getType(isInTime, weatherType));
        } else {
            Snackbar.make(findViewById(R.id.cdl_weather_basic), getString(R.string.text_dynamic_background_error), Snackbar.LENGTH_LONG).show();
        }
    }


    private void initPictureBackground() {
        if ((boolean) SPUtils.getData(Constant.SP_USE_PICTURE_BACKGROUND_KEY, false)) {
            String path = (String) SPUtils.getData(Constant.SP_PICTURE_PATH_KEY, "");
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
    public void blurChange(boolean isChange) {
        //if (isChange)
            //initFragmentWeatherPage();
    }

    @Override
    public void showWeather(Weather weather, boolean isRefresh) {
        setWeatherBackground(weather);
        EventBus.getDefault().post(weather);
        String cityName = weather.getInfo().getCityName();
        tvCityName.setText(cityName);
       // new Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);

       // tvCityName.setText(LitePal.find(DefaultCity.class, 1).getCityName());
        /*List<SavedCity> savedCityList = LitePal.findAll(SavedCity.class);
        for (SavedCity savedCity : savedCityList) {
            if (savedCity.getCityName().equals(cityName)) {
                tvCityName.setText(cityName);
                break;
            }
        }*/
        if (isRefresh) {
          //  swipeRefreshLayout.setRefreshing(false);
            Snackbar.make(findViewById(R.id.cdl_weather_basic), getString(R.string.refresh_success), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void showError(String error) {
       // swipeRefreshLayout.setRefreshing(false);
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
                /*Intent intent = new Intent(MainActivity.this, CityActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                        Pair.create(tvCityName,"sharedChallenger")).toBundle());*/
               // Activity activity = (Activity) getContext();
               /* ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        new Pair(dynamicWeatherView, CityActivity.IMAGE_TRANSITION_NAME)
                       // new Pair(address1, DetailActivity.ADDRESS1_TRANSITION_NAME)
                );
                Intent intent = new Intent(this, CityActivity.class);
                //intent.putExtra(DetailActivity.EXTRA_IMAGE_URL, imageUrl);
                ActivityCompat.startActivity(this, intent, options.toBundle());*/

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            initPictureBackground();
            if ((boolean) SPUtils.getData(Constant.SP_USE_BLUR_KEY, false)) {
                EventBus.getDefault().postSticky("blurImageChange");
            }
            /*if (!(boolean) SPUtils.getData(Constant.SP_USE_BLUR_KEY, false) ||
                    !(boolean) SPUtils.getData(Constant.SP_USE_PICTURE_BACKGROUND_KEY, false)) {
                initFragmentWeatherPage();
                isFirst = true;
            }*/
        }
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
