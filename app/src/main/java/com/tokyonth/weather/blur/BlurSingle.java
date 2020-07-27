package com.tokyonth.weather.blur;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.tokyonth.weather.utils.DisplayUtils;

import java.util.List;

public class BlurSingle extends Blur {

    protected static Bitmap blurBkg;
    public static double brightness=-0.1;

    public BlurSingle(){

    }

    /**
     * 更具layout的位置以及大小，从已经模糊处理过的图片中截取所需要的部分
     * @param fromView
     * @param toView
     * @param radius
     * @param scaleFactor
     * @param roundCorner
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void cutBluredBitmap(View fromView, View toView, int radius, float scaleFactor, float roundCorner) {
        // 获取View的截图

        if (radius < 1 || radius > 26) {
            scaleFactor = 8;
            radius = 2;
        }

        if(blurBkg==null){
            initBkg(fromView,radius,scaleFactor);
        }


        int top,left;
        int[] location=new int[2];
        toView.getLocationInWindow(location);
        left=location[0];
        top=location[1];
        if(toView.getWidth()>0&&toView.getHeight()>0){
            Bitmap overlay = Bitmap.createBitmap((int) (toView.getWidth() / scaleFactor), (int) (toView.getHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(overlay);
            canvas.translate(-left / scaleFactor, -top / scaleFactor);
            canvas.scale(1 / scaleFactor, 1 / scaleFactor);
            Paint paint = new Paint();
            paint.setFlags(Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(blurBkg, 0, 0, paint);
            RoundedBitmapDrawable bdr= RoundedBitmapDrawableFactory.create(Resources.getSystem(),overlay);
            bdr.setCornerRadius(roundCorner);
            toView.setBackground(bdr);
        }

    }


    private static Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 初始化，将原始图片处理成模糊后的图片
     * @param fromView
     * @param radius
     * @param scaleFactor
     */
    public static void initBkg(View fromView, int radius, float scaleFactor){
        //BitmapDrawable bd = (BitmapDrawable) fromView.getBackground();
        //Bitmap bkg1 = bd.getBitmap();
        fromView.setDrawingCacheEnabled(false);
        Bitmap bkg1 = ((BitmapDrawable) ((ImageView) fromView).getDrawable()).getBitmap();


        //int height = (fromView.getDrawingCache().getHeight());  //屏幕高度
        //int width = (fromView.getDrawingCache().getWidth());    //屏幕宽度
        int height = DisplayUtils.getScreenHeight(fromView.getContext());
        int width = DisplayUtils.getScreenWidth(fromView.getContext());

        //精确缩放到指定大小
        Bitmap bkg_origin= Bitmap.createScaledBitmap(bkg1,(int)(width/scaleFactor),(int)(height/scaleFactor), true);
        bkg_origin.setConfig(Bitmap.Config.ARGB_8888);
        Bitmap bkg=Bitmap.createScaledBitmap(blurBitmap(bkg_origin,radius,true),width,height,true);
        blurBkg=bkg;

    }

    public static void initBkgWithResieze(View fromView,Bitmap bitmap, int width, int height){
        if(bitmap!=null){
            Bitmap bkg=Bitmap.createScaledBitmap(bitmap,width,height,true);
            blurBkg=bkg;
        }
    }
    public static void destroy(View fromView){
        if(blurBkg!=null){
            blurBkg=null;
        }

    }

    /**
     * 此类为控件模糊处理类
     *
     */
    public static class BlurLayout {

        private int positionX,positionY;
        private View layoutView,layoutBkg;

        //毛玻璃效果参数,可以动态修改

        public static int RoundCorner=32;
        public static int radius=5;
        public static int scaleFactor=26;

      /*  private int RoundCorner = ConstValue.RoundCorner;
        private int radius = ConstValue.radius;
        private int scaleFactor = ConstValue.scaleFactor;
        */

        public BlurLayout(final View layoutView, final View layoutBkg){
            positionX=positionY=0;
            layoutView.getViewTreeObserver().addOnPreDrawListener(() -> {
                int position[]=new int[2];
                layoutView.getLocationInWindow(position);
                if(positionX!=position[0]||positionY!=position[1]){
                    BlurSingle.cutBluredBitmap(layoutBkg,layoutView,radius,scaleFactor,RoundCorner);
                    positionX=position[0];
                    positionY=position[1];
                }
                return true;
            });
        }

        public BlurLayout(final List<View> layoutView, final View layoutBkg){
            positionX=positionY=0;

            for (View view : layoutView) {
                view.getViewTreeObserver().addOnPreDrawListener(() -> {
                    int position[]=new int[2];
                    view.getLocationInWindow(position);
                    if(positionX!=position[0]||positionY!=position[1]){
                        BlurSingle.cutBluredBitmap(layoutBkg,view,radius,scaleFactor,RoundCorner);
                        positionX=position[0];
                        positionY=position[1];
                    }
                    return true;
                });
            }

        }

        public void setLayoutView(View layoutView) {
            this.layoutView = layoutView;
        }

        public void setLayoutBkg(View layoutBkg) {
            this.layoutBkg = layoutBkg;
        }

        public void setRoundCorner(int roundCorner) {
            RoundCorner = roundCorner;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public void setScaleFactor(int scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        public void reSetPositions(){
            positionX=0;
            positionY=0;
        }
    }



}
