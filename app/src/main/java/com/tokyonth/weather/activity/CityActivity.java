package com.tokyonth.weather.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import com.tokyonth.weather.base.BaseActivity;
import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.CityManagementAdapter;
import com.tokyonth.weather.model.bean.City;
import com.tokyonth.weather.model.bean.DefaultCity;
import com.tokyonth.weather.model.bean.SavedCity;
import com.tokyonth.weather.view.search.SearchFragment;

import com.google.android.material.snackbar.Snackbar;
import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.util.List;

public class CityActivity extends BaseActivity {

    private CityManagementAdapter city_adapter;
    private List<SavedCity> saved_city_list;

    @Override
    public int setActivityView() {
        return R.layout.activity_city;
    }

    @Override
    public void initActivity(Bundle savedInstanceState) {
        saved_city_list = LitePal.findAll(SavedCity.class);
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

        findViewById(R.id.fab).setOnClickListener(v -> {
            SearchFragment searchFragment = SearchFragment.newInstance();
            searchFragment.showFragment(getSupportFragmentManager(), SearchFragment.TAG);
            searchFragment.setCitySelect(city -> {
                if (city.getCityCode() != null) {
                    saveCity(city);
                    searchFragment.dismiss();
                } else {
                    Snackbar.make(findViewById(R.id.city_management_con), getString(R.string.no_city_code), Snackbar.LENGTH_LONG).show();
                }
            });
        });

        RecyclerView cityRv = findViewById(R.id.city_management_rv);
        city_adapter = new CityManagementAdapter(this, saved_city_list);
        city_adapter.setOnItemClickListener((view, position) -> {
            if (position == 0) {
                DefaultCity defaultCity = LitePal.find(DefaultCity.class, 1);
                EventBus.getDefault().post(defaultCity);
            } else {
                SavedCity savedCity = saved_city_list.get(position - 1);
                EventBus.getDefault().post(savedCity);
            }
            finish();
        });
        city_adapter.setOnItemLongClickListener((view, position) -> {
            if (position == 0) {
                Snackbar.make(view, getString(R.string.text_cannot_del_default_city), Snackbar.LENGTH_SHORT).show();
            } else {
                showPopupMenu(view, position);
            }
        });
        cityRv.setLayoutManager(new GridLayoutManager(this, 2));
        cityRv.setAdapter(city_adapter);
    }

    private void saveCity(City city) {
        if (city != null) {
            SavedCity savedCity = new SavedCity(city.getCityId(), city.getParentId(), city.getCityCode(), city.getCityName());
            if (!compareTwoCities(savedCity)) {
                savedCity.save();
                Snackbar.make(findViewById(R.id.city_management_con), getString(R.string.add_city_success), Snackbar.LENGTH_LONG).show();
                saved_city_list.add(savedCity);
                city_adapter.notifyDataSetChanged();
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
                saved_city_list.get(position - 1).delete();
                saved_city_list.remove(position - 1);
                city_adapter.notifyItemRemoved(position);
                Snackbar.make(findViewById(R.id.city_management_con), getString(R.string.city_del_city), Snackbar.LENGTH_SHORT).show();
            }
            return true;
        });
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
