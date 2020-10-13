package com.tokyonth.weather.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.tokyonth.weather.base.BaseActivity;
import com.tokyonth.weather.R;
import com.tokyonth.weather.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Override
    public void initActivity(Bundle savedInstanceState) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
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

    @Override
    public int setActivityView() {
        return R.layout.activity_settings;
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.menu_settings));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
