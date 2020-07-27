package com.tokyonth.weather.dynamic;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;

import com.tokyonth.weather.R;
import com.tokyonth.weather.helper.SvgResources;

/**
 * 晴天
 *
 * @author Mixiaoxiao
 */
public class SunnyDrawer extends BaseDrawer {

    private GradientDrawable drawable;
    private ArrayList<SunnyHolder> holders = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SunnyDrawer(Context context) {
        super(context, false);
        this.context = context;
        drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{0x20ffffff, 0x10ffffff});
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        paint.setColor(0x33ffffff);
    }

    @Override
    public boolean drawWeather(Canvas canvas, float alpha) {
        final float size = width * centerOfWidth;
        for (SunnyHolder holder : holders) {
            holder.updateRandom(drawable, alpha);
            drawable.draw(canvas);
        }
        //paint.setColor(Color.argb((int) (alpha * 0.18f * 255f), 245, 124, 0));
        paint.setColor(Color.parseColor("#FFE2AF44"));
        //canvas.drawCircle(width / 2, size, width * 0.12f, paint);

        Bitmap bitmap = SvgResources.getBitmapFromDrawable(R.drawable.ic_sun_view);
        bitmap = Bitmap.createScaledBitmap(bitmap, 180, 180, true);

        canvas.drawBitmap(bitmap, width / 2, -height, null);

        return true;
    }

    private static final int SUNNY_COUNT = 3;
    private final float centerOfWidth = 0.02f;

    @Override
    protected void setSize(int width, int height) {
        super.setSize(width, height);
        if (this.holders.size() == 0) {
            final float minSize = width * 0.16f;//dp2px(SUNNY_MIN_SIZE);
            final float maxSize = width * 1.5f;//dp2px(SUNNY_MAX_SIZE);
            final float center = width * centerOfWidth;
            float deltaSize = (maxSize - minSize) / SUNNY_COUNT;
            for (int i = 0; i < SUNNY_COUNT; i++) {
                final float curSize = maxSize - i * deltaSize * getRandom(0.9f, 1.1f);
                SunnyHolder holder = new SunnyHolder(width >> 1, center - 30, curSize / 2, curSize / 2);
                holders.add(holder);
            }
        }

    }

    public static class SunnyHolder {

        public float x;
        public float y;
        public float w;
        public float h;
        public final float maxAlpha = 2.0f;
        public float curAlpha;// [0,1]
        public boolean alphaIsGrowing = true;
        private final float minAlpha = 1.3f;

        public SunnyHolder(float x, float y, float w, float h) {
            super();
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.curAlpha = getRandom(minAlpha, maxAlpha);
        }

        public void updateRandom(GradientDrawable drawable, float alpha) {

            final float delta = getRandom(0.003f * maxAlpha, 0.008f * maxAlpha);
            if (alphaIsGrowing) {
                curAlpha += delta;
                if (curAlpha > maxAlpha) {
                    curAlpha = maxAlpha;
                    alphaIsGrowing = false;
                }
            } else {
                curAlpha -= delta;
                if (curAlpha < minAlpha) {
                    curAlpha = minAlpha;
                    alphaIsGrowing = true;
                }
            }

            final int left = Math.round(x - w / 2f);
            final int right = Math.round(x + w / 2f);
            final int top = Math.round(y - h / 2f);
            final int bottom = Math.round(y + h / 2f);
            drawable.setBounds(left, top, right, bottom);
            drawable.setGradientRadius(w / 2.2f);
            drawable.setColor(Color.parseColor("#FFE2AF44"));
            drawable.setAlpha((int) (255 * curAlpha * alpha));
        }
    }
}
