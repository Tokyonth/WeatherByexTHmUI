package com.tokyonth.weather.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tokyonth.weather.R;
import com.tokyonth.weather.base.BaseActivity;
import com.tokyonth.weather.utils.AppUtils;

import java.io.File;

public class AboutActivity extends BaseActivity {

    @Override
    public int setActivityView() {
        return R.layout.activity_about;
    }

    @Override
    public void initActivity(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
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
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(getString(R.string.act_title_about));
        TextView tvAppVersion = findViewById(R.id.tv_ver);
        tvAppVersion.append(AppUtils.getAppVersionName(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_about_shard:
                File apkFile = new File(getPackageResourcePath());
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("file/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apkFile));
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
