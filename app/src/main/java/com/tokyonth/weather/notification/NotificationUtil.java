package com.tokyonth.weather.notification;

import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tokyonth.weather.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotificationUtil {

    private static NotificationManager mNotificationManager;

    public static boolean isOpenPermission(Context context) {
        if (!NotificationUtil.isNotificationEnabled(context)) {
            new MaterialAlertDialogBuilder(context)
                    .setTitle(context.getString(R.string.text_tips))
                    .setMessage(context.getString(R.string.text_no_notification_permission))
                    .setPositiveButton(context.getString(R.string.text_definite), (dialog, which) -> {
                        Intent mIntent = new Intent()
                                .setAction("android.settings.APPLICATION_DETAILS_SETTINGS")
                                .setData(Uri.fromParts("package", context.getPackageName(), null));
                        context.startActivity(mIntent);
                    })
                    .create().show();
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= 24) {
            if (getNotificationManager(context) != null) {
                return getNotificationManager(context).areNotificationsEnabled();
            }
        }
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo info = context.getApplicationInfo();
        String pag = context.getApplicationContext().getPackageName();
        int uid = info.uid;
        try {
            Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method method = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String.class);
            Field field = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
            int value = (int) field.get(Integer.class);
            return ((int) method.invoke(mAppOps, value, uid, pag) == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static NotificationManager getNotificationManager(Context context) {
        if (mNotificationManager == null) {
            synchronized (NotificationUtil.class) {
                if (mNotificationManager == null) {
                    mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                }
            }
        }
        return mNotificationManager;
    }

}
