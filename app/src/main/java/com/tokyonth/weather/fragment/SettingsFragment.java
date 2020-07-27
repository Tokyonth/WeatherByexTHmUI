package com.tokyonth.weather.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.activity.AboutActivity;
import com.tokyonth.weather.helper.notification.NotificationWeather;
import com.tokyonth.weather.utils.ContentUriUtils;
import com.tokyonth.weather.utils.SPUtils;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    private Context context = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_settings, rootKey);
        context = getContext();
        initView();
    }

    private void initView() {
        String picPath = (String) SPUtils.getData(Constant.SP_PICTURE_PATH_KEY, "");
        if (!picPath.equals("")) {
            findPreference("pf_choice_picture").setSummary(picPath);
        }
        boolean isUsePicture = (boolean) SPUtils.getData(Constant.SP_USE_PICTURE_BACKGROUND_KEY, false);
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
    }

    private void blurSet(boolean bool) {
        findPreference("pf_use_blur").setEnabled(bool);
        findPreference("pf_set_blur_radius").setEnabled(bool);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "pf_notifications_weather_style":
                new MaterialAlertDialogBuilder(context)
                        .setTitle("请选择")
                        .setSingleChoiceItems(new String[]{"天气图标", "软件图标"}, (Integer) SPUtils.getData(Constant.SP_NOTIFICATION_WEATHER_STYLE_KEY,
                                0), (dialog, which) -> {
                            SPUtils.putData(Constant.SP_NOTIFICATION_WEATHER_STYLE_KEY, which);
                            if ((boolean) SPUtils.getData(Constant.SP_NOTIFICATION_WEATHER_KEY, false)) {
                                new NotificationWeather(context, true);
                            }
                            dialog.dismiss();
                        })
                        .create().show();
                break;
            case "pf_open_source_code":
                new MaterialAlertDialogBuilder(context)
                        .setTitle("开源")
                        .setPositiveButton("确定", null)
                        .create().show();
                break;
            case "pf_about":
                startActivity(new Intent(context, AboutActivity.class));
                break;
            case "pf_data_form":
                new MaterialAlertDialogBuilder(context)
                        .setTitle("请选择")
                        .setSingleChoiceItems(new String[]{"极速数据"}, 0, (dialog, which) -> dialog.dismiss())
                        .create().show();
                break;
            case "pf_choice_picture":
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra("crop", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, 0);
                break;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case "pf_notifications_weather":
                new NotificationWeather(context, (boolean) newValue);
                SPUtils.putData(Constant.SP_NOTIFICATION_WEATHER_KEY, newValue);
                break;
            case "pf_use_picture":
                SPUtils.putData(Constant.SP_USE_PICTURE_BACKGROUND_KEY, newValue);
                blurSet((boolean) newValue);
                break;
            case "pf_lock_screen_weather":
                SPUtils.putData(Constant.SP_LOCK_SCREEN_WEATHER_KEY, newValue);
                break;
            case "pf_use_blur":
                SPUtils.putData(Constant.SP_USE_BLUR_KEY, newValue);
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (data != null) {
                String path = ContentUriUtils.getPath(context, data.getData());
                if (path != null)
                    SPUtils.putData(Constant.SP_PICTURE_PATH_KEY, path);
                findPreference("pf_choice_picture").setSummary(path);
            }
        }
    }

}
