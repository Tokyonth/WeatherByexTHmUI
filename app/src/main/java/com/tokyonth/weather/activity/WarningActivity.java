package com.tokyonth.weather.activity;

import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.tokyonth.weather.R;
import com.tokyonth.weather.adapter.WarningAdapter;
import com.tokyonth.weather.base.BaseActivity;

public class WarningActivity extends BaseActivity {

    @Override
    public int setActivityView() {
        return R.layout.activity_warning;
    }

    @Override
    public void initActivity(Bundle savedInstanceState) {
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
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.act_title_warning));

        RecyclerView mWarningRv = findViewById(R.id.rv_warning);
        WarningAdapter mWarningAdapter = new WarningAdapter(this);
        mWarningRv.setLayoutManager(new LinearLayoutManager(this));
        mWarningRv.setAdapter(mWarningAdapter);
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
