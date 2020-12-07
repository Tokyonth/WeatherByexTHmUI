package com.tokyonth.weather.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tokyonth.weather.base.BaseActivity;
import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.CityManagementAdapter;
import com.tokyonth.weather.model.bean.City;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.widget.search.SearchFragment;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.util.List;

public class CityActivity extends BaseActivity {

    private CityManagementAdapter cityAdapter;
    private List<SavedCity> savedCityList;

    private FloatingActionButton fabAddCity;

    @Override
    public int setActivityView() {
        return R.layout.activity_city;
    }

    @Override
    public void initActivity(Bundle savedInstanceState) {
        savedCityList = LitePal.findAll(SavedCity.class);
        initView();
    }

    @Override
    public boolean isDarkStatusBar() {
        return true;
    }

    @Override
    public boolean isTranslucentNav() {
        return false;
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.act_title_select_city));

        fabAddCity = findViewById(R.id.fab_add_city);
        fabAddCity.setOnClickListener(v -> {
           /* ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    new TransitionAnimationPair(fabAddCity, "SearchActivity"));
            Router.newIntent(this)
                    .options(options)
                    .to(SearchCityActivity.class)
                    .launch();*/
            SearchFragment searchFragment = SearchFragment.newInstance();
            searchFragment.showFragment(getSupportFragmentManager(), SearchFragment.TAG);
            searchFragment.setCitySelect(city -> {
                if (city.getCityCode() != null) {
                    if (LitePal.find(DefaultCity.class, 1) == null) {
                        DefaultCity defaultCity = new DefaultCity(city.getCityName(),
                                city.getParentId(), null, null);
                        defaultCity.save();
                        savedCityList.add(new SavedCity(city.getCityId(), city.getParentId(), city.getCityCode(), city.getCityName()));
                        cityAdapter.notifyItemChanged(0);
                    } else {
                        saveCity(city);
                    }
                    searchFragment.dismiss();
                } else {
                    Snackbar.make(findViewById(R.id.city_management_con), getString(R.string.no_city_code), Snackbar.LENGTH_LONG).show();
                }
            });
        });

        RecyclerView rvCity = findViewById(R.id.city_management_rv);
        cityAdapter = new CityManagementAdapter(this, savedCityList);
        cityAdapter.setOnItemClickListener((view, position) -> {
            DefaultCity defaultCity = LitePal.find(DefaultCity.class, 1);
            if (position == 0 && defaultCity != null) {
                EventBus.getDefault().post(defaultCity);
            } else {
                SavedCity savedCity = savedCityList.get(position - 1);
                EventBus.getDefault().post(savedCity);
            }
            finish();
        });
        cityAdapter.setOnItemLongClickListener((view, position) -> {
            if (position == 0) {
                Snackbar.make(view, getString(R.string.text_cannot_del_default_city), Snackbar.LENGTH_SHORT).show();
            } else {
                showPopupMenu(view, position);
            }
        });

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rvCity.setLayoutManager(gridLayoutManager);
        rvCity.setAdapter(cityAdapter);
    }

    private void saveCity(City city) {
        if (city != null) {
            SavedCity savedCity = new SavedCity(city.getCityId(), city.getParentId(), city.getCityCode(), city.getCityName());
            if (!compareTwoCities(savedCity)) {
                savedCity.save();
                Snackbar.make(findViewById(R.id.city_management_con), getString(R.string.add_city_success), Snackbar.LENGTH_LONG).show();
                savedCityList.add(savedCity);
                cityAdapter.notifyItemChanged(savedCityList.size());
            } else {
                Snackbar.make(findViewById(R.id.city_management_con), getString(R.string.city_already_exist), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private boolean compareTwoCities(SavedCity city) {
        List<SavedCity> savedCityList = LitePal.findAll(SavedCity.class);
        for (SavedCity savedCity : savedCityList) {
            if (savedCity.getCityId().equals(city.getCityId())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenu(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.city_management_popup_menu, popupMenu.getMenu());
        try {
            Field field = popupMenu.getClass().getDeclaredField("mPopup");
            field.setAccessible(true);
            MenuPopupHelper helper = (MenuPopupHelper) field.get(popupMenu);
            if (helper != null) {
                helper.setForceShowIcon(true);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.city_management_popup_menu_delete) {
                savedCityList.get(position - 1).delete();
                savedCityList.remove(position - 1);
                cityAdapter.notifyAdapterItemRemoved(position);
                Snackbar.make(findViewById(R.id.city_management_con), getString(R.string.city_del_city), Snackbar.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
      //  cityAdapter.stopAll();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  cityAdapter.startAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // cityAdapter.stopAll();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
