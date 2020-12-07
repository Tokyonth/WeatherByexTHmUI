package com.tokyonth.weather.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tokyonth.weather.Constants;
import com.tokyonth.weather.R;
import com.tokyonth.weather.base.BaseActivity;
import com.tokyonth.weather.presenter.CityPresenterImpl;
import com.tokyonth.weather.presenter.LoadCitySituationListener;
import com.tokyonth.weather.utils.NetworkUtil;
import com.tokyonth.weather.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity {

    private static final int PERMISSION = 1;
    private RelativeLayout containerRl;

    @Override
    public int setActivityView() {
        return R.layout.activity_splash;
    }

    @Override
    public void initActivity(Bundle savedInstanceState) {
        if (NetworkUtil.isNetworkConnected()) {
            initView();
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.text_tips))
                    .setMessage(getString(R.string.no_network_connection))
                    .setPositiveButton(getString(R.string.text_exit), (dialogInterface, i) -> finish())
                    .setCancelable(false)
                    .create().show();
        }
    }

    @Override
    public boolean isDarkStatusBar() {
        return false;
    }

    @Override
    public boolean isTranslucentNav() {
        return false;
    }

    private void initView() {
        containerRl = findViewById(R.id.splash_container_rl);
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_anim);
        findViewById(R.id.splash_loading_iv).startAnimation(rotateAnimation);
        showTips();
        importCityData();
    }

    private void importCityData() {
        new CityPresenterImpl().saveCityList(new LoadCitySituationListener() {
            @Override
            public void Success() {
                Snackbar.make(containerRl, getString(R.string.import_success), Snackbar.LENGTH_LONG).show();
                startHomeActivity();
            }

            @Override
            public void Fail() {
                Snackbar.make(containerRl, getString(R.string.import_failed), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void showTips() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.permission_explain_title))
                .setMessage(getString(R.string.permission_explain_msg))
                .setNegativeButton(getString(R.string.text_definite), (dialog, which) -> requestPermission())
                .setCancelable(false)
                .create().show();
    }

    private void requestPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(this, permissions, PERMISSION);
        }
    }

    private void startHomeActivity() {
        SPUtils.putData(Constants.IMPORT_DATA, false);
        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(homeIntent);
            finish();
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION) {
            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, getString(R.string.must_allow_permission), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
            } else {
                Toast.makeText(this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
