package com.tokyonth.weather.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tokyonth.weather.Constants;
import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.AboutActivity;
import com.tokyonth.weather.activity.BlurSetActivity;
import com.tokyonth.weather.model.bean.BlurEvent;
import com.tokyonth.weather.model.bean.ImageBackgroundEvent;
import com.tokyonth.weather.notification.NotificationWeather;
import com.tokyonth.weather.router.Router;
import com.tokyonth.weather.utils.ContentUriUtils;
import com.tokyonth.weather.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    private Activity activity;
    private BlurEvent blurEvent;
    private ImageBackgroundEvent imageBackgroundEvent;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey);
        activity = getActivity();
        initView();
    }

    private void initView() {
        String picPath = (String) SPUtils.getData(Constants.SP_PICTURE_PATH_KEY, "");
        if (!picPath.equals("")) {
            findPreference("pf_choice_picture").setSummary(picPath);
        }
        boolean isUsePicture = (boolean) SPUtils.getData(Constants.SP_USE_PICTURE_BACKGROUND_KEY, false);
        blurSet(isUsePicture);
        findPreference("pf_data_form").setSummary("极速数据");

        findPreference("pf_notifications_weather_style").setOnPreferenceClickListener(this);
        findPreference("pf_notifications_weather").setOnPreferenceChangeListener(this);
        findPreference("pf_lock_screen_weather").setOnPreferenceChangeListener(this);
        findPreference("pf_use_blur").setOnPreferenceChangeListener(this);

        findPreference("pf_use_picture").setOnPreferenceChangeListener(this);
        findPreference("pf_choice_picture").setOnPreferenceClickListener(this);

        findPreference("pf_data_form").setOnPreferenceClickListener(this);
        findPreference("pf_open_source_code").setOnPreferenceClickListener(this);
        findPreference("pf_about").setOnPreferenceClickListener(this);
        findPreference("pf_blur").setOnPreferenceClickListener(this);
    }

    private void blurSet(boolean bool) {
        findPreference("pf_set_blur_radius").setEnabled(bool);

        SwitchPreferenceCompat switchPreference = findPreference("pf_use_blur");
        switchPreference.setChecked(bool);
        switchPreference.setEnabled(bool);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "pf_notifications_weather_style":
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("请选择")
                        .setSingleChoiceItems(new String[]{"天气图标", "软件图标"}, (Integer) SPUtils.getData(Constants.SP_NOTIFICATION_WEATHER_STYLE_KEY,
                                0), (dialog, which) -> {
                            SPUtils.putData(Constants.SP_NOTIFICATION_WEATHER_STYLE_KEY, which);
                            if ((boolean) SPUtils.getData(Constants.SP_NOTIFICATION_WEATHER_KEY, false)) {
                                new NotificationWeather(activity, true);
                            }
                            dialog.dismiss();
                        })
                        .create().show();
                break;
            case "pf_open_source_code":
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("开源")
                        .setPositiveButton("确定", null)
                        .create().show();
                break;
            case "pf_about":
                startActivity(new Intent(activity, AboutActivity.class));
                break;
            case "pf_data_form":
                new MaterialAlertDialogBuilder(activity)
                        .setTitle("请选择")
                        .setSingleChoiceItems(new String[]{"极速数据"}, 0, (dialog, which) -> dialog.dismiss())
                        .setNegativeButton("确定", null)
                        .create().show();
                break;
            case "pf_choice_picture":
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 0);
                break;
            case "pf_blur":
                Router.newIntent(activity)
                        .to(BlurSetActivity.class)
                        .launch();
                break;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "pf_notifications_weather":
                new NotificationWeather(activity, (boolean) newValue);
                SPUtils.putData(Constants.SP_NOTIFICATION_WEATHER_KEY, newValue);
                break;
            case "pf_use_picture":
                SPUtils.putData(Constants.SP_USE_PICTURE_BACKGROUND_KEY, newValue);
                imageBackgroundEvent = new ImageBackgroundEvent((boolean) newValue, (String) SPUtils.getData(Constants.SP_PICTURE_PATH_KEY, ""));
                EventBus.getDefault().post(imageBackgroundEvent);
                blurSet((boolean) newValue);
                break;
            case "pf_lock_screen_weather":
                SPUtils.putData(Constants.SP_LOCK_SCREEN_WEATHER_KEY, newValue);
                break;
            case "pf_use_blur":
                SPUtils.putData(Constants.SP_USE_BLUR_KEY, newValue);
                blurEvent = new BlurEvent((boolean) newValue, 8, 32, 26);
                EventBus.getDefault().post(blurEvent);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                String path = ContentUriUtils.getPath(activity, data.getData());
                if (path != null)
                    SPUtils.putData(Constants.SP_PICTURE_PATH_KEY, path);
                findPreference("pf_choice_picture").setSummary(path);
            }
        }
    }

}
