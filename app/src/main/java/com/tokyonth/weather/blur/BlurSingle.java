package com.tokyonth.weather.blur;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.tokyonth.weather.Constant;
import com.tokyonth.weather.R;
import com.tokyonth.weather.utils.DisplayUtils;
import com.tokyonth.weather.utils.SPUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class BlurSingle extends Blur {

    protected static Bitmap blurBkg;
    /**
     * 更具layout的位置以及大小，从已经模糊处理过的图片中截取所需要的部分
     */

    public static void cutBlurBitmap(View toView, int radius, float scaleFactor, float roundCorner) {
        // 获取View的截图
        if (radius < 1 || radius > 26) {
            scaleFactor = 8;
            radius = 2;
        }
        if (blurBkg == null) {
            initBkg(toView.getContext(), radius, scaleFactor);
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
    public static void initBkg(Context context, int radius, float scaleFactor) {
        Bitmap bitmap;
        String path = (String) SPUtils.getData(Constant.SP_PICTURE_PATH_KEY, "");
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

    public static void initBkgWithResize(Bitmap bitmap, int width, int height) {
        if (bitmap != null) {
            blurBkg = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
    }

    public static void destroy(View fromView) {
        if (blurBkg != null) {
            blurBkg = null;
        }
    }

    /**
     * 此类为控件模糊处理类
     */
    public static class BlurLayout {

        private int positionX, positionY;
        //毛玻璃效果参数,可以动态修改
        public static int RoundCorner = 32;
        public static int radius = 5;
        public static int scaleFactor = 26;

        public BlurLayout(final View layoutView) {
            positionX = positionY = 0;
            layoutView.getViewTreeObserver().addOnPreDrawListener(() -> {
                int[] position = new int[2];
                layoutView.getLocationInWindow(position);
                if (positionX != position[0] || positionY != position[1]) {
                    BlurSingle.cutBlurBitmap(layoutView, radius, scaleFactor, RoundCorner);
                    positionX = position[0];
                    positionY = position[1];
                }
                return true;
            });
        }

        public BlurLayout(final List<View> layoutView) {
            positionX = positionY = 0;

            for (View view : layoutView) {
                view.getViewTreeObserver().addOnPreDrawListener(() -> {
                    int[] position = new int[2];
                    view.getLocationInWindow(position);
                    if (positionX != position[0] || positionY != position[1]) {
                        BlurSingle.cutBlurBitmap(view, radius, scaleFactor, RoundCorner);
                        positionX = position[0];
                        positionY = position[1];
                    }
                    return true;
                });
            }

        }

        public void setRoundCorner(int roundCorner) {
            RoundCorner = roundCorner;
        }

        public void setRadius(int radius) {
            BlurLayout.radius = radius;
        }

        public void setScaleFactor(int scaleFactor) {
            BlurLayout.scaleFactor = scaleFactor;
        }

        public void reSetPositions() {
            positionX = 0;
            positionY = 0;
        }
    }

}
