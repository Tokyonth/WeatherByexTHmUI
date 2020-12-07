package com.tokyonth.weather.blur;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.tokyonth.weather.Constants;
import com.tokyonth.weather.R;
import com.tokyonth.weather.utils.DisplayUtils;
import com.tokyonth.weather.utils.SPUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class BlurSingle extends Blur {

    protected static Bitmap blurBkg;
    private boolean stopBlur = false;
    private int positionX, positionY;
    private int roundCorner = 32;
    private int radius = 8;
    private int scaleFactor = 26;

    private HashMap<View, ViewTreeObserver.OnPreDrawListener> viewOnPreDrawListenerHashMap;

    public void blurView(View... layoutView) {
        viewOnPreDrawListenerHashMap = new HashMap<>();
        positionX = positionY = 0;
        for (View view : layoutView) {
            ViewTreeObserver.OnPreDrawListener listener = blurViewTreeObserver(view);
            viewOnPreDrawListenerHashMap.put(view, listener);
        }
    }

    private ViewTreeObserver.OnPreDrawListener blurViewTreeObserver(View view) {
        ViewTreeObserver.OnPreDrawListener listener = () -> {
            int[] position = new int[2];
            view.getLocationInWindow(position);
            if (positionX != position[0] || positionY != position[1]) {
                cutBlurBitmap(view, radius, scaleFactor, roundCorner);
                positionX = position[0];
                positionY = position[1];
            }
            return true;
        };
        view.getViewTreeObserver().addOnPreDrawListener(listener);
        return listener;
    }

    public void stopBlur() {
        if (viewOnPreDrawListenerHashMap != null) {
            stopBlur = true;
            for (Map.Entry<View, ViewTreeObserver.OnPreDrawListener> entry : viewOnPreDrawListenerHashMap.entrySet()) {
                entry.getKey().getViewTreeObserver().removeOnPreDrawListener(entry.getValue());
            }
        }
    }

    /**
     * 根据layout的位置以及大小，从已经模糊处理过的图片中截取所需要的部分
     */
    private void cutBlurBitmap(View toView, int radius, float scaleFactor, float roundCorner) {
        if (stopBlur)
            return;
        if (radius < 1 || radius > 26) {
            scaleFactor = 8;
            radius = 2;
        }
        if (blurBkg == null) {
            initImage(toView.getContext(), radius, scaleFactor);
        }
        int top, left;
        int[] location = new int[2];
        toView.getLocationInWindow(location);
        left = location[0];
        top = location[1];
        if (toView.getWidth() > 0 && toView.getHeight() > 0) {
            Bitmap overlay = Bitmap.createBitmap((int) (toView.getWidth() / scaleFactor), (int) (toView.getHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(overlay);
            canvas.translate(-left / scaleFactor, -top / scaleFactor);
            canvas.scale(1 / scaleFactor, 1 / scaleFactor);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(blurBkg, 0, 0, paint);
            RoundedBitmapDrawable bdr = RoundedBitmapDrawableFactory.create(Resources.getSystem(), overlay);
            bdr.setCornerRadius(roundCorner);
            toView.setBackground(bdr);
        }
    }

    /**
     * 初始化，将原始图片处理成模糊后的图片
     */
    public void initImage(Context context, int radius, float scaleFactor) {
        Bitmap bitmap;
        String path = (String) SPUtils.getData(Constants.SP_PICTURE_PATH_KEY, "");
        if (!path.equals("")) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pic_bg);
        }
        int height = DisplayUtils.getScreenHeight(context);
        int width = DisplayUtils.getScreenWidth(context);

        //精确缩放到指定大小
        Bitmap bitmapOrigin = Bitmap.createScaledBitmap(bitmap, (int) (width / scaleFactor), (int) (height / scaleFactor), true);
        bitmapOrigin.setConfig(Bitmap.Config.ARGB_8888);
        Bitmap src = blurBitmap(bitmapOrigin, radius, true);
        if (src != null) {
            blurBkg = Bitmap.createScaledBitmap(src, width, height, true);
        }
    }

    public void setRoundCorner(int roundCorner) {
        this.roundCorner = roundCorner;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public void reSetPositions() {
        positionX = 0;
        positionY = 0;
    }

    public void changeImage() {
        if (blurBkg != null) {
            blurBkg = null;
        }
    }

}
