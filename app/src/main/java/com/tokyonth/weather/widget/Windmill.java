package com.tokyonth.weather.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.tokyonth.weather.R;
import com.tokyonth.weather.utils.DisplayUtils;

import static java.lang.Math.PI;
import static java.lang.Math.atan;

/**
 * Created by wyg on 2017/8/1.
 */
public class Windmill extends View {

    private Paint mWindmillPaint; //支柱画笔
    private int width, height;
    private int mWindmillColor;//风车颜色
    private float mWindLengthPercent;//扇叶长度
    private Point mCenterPoint;//圆心
    private float x1, y1, x2, y2, x3, y3, x4, y4, x5, y5;//扇叶的点
    private double rad1, rad2, rad3, rad4;//弧度
    private double r1, r2, r3, r4;//斜边
    private ObjectAnimator mAnimator;//动画
    private float angle;//旋转弧度
    private double windSpeed = 1.0f;

    private int mDefaultSize;
    private Context mContext;

    public Windmill(Context context) {
        this(context, null);
    }

    public Windmill(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Windmill(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initAttrs(attrs);
        mWindmillPaint = new Paint();
        mCenterPoint = new Point();
        mWindmillPaint.setStyle(Paint.Style.FILL);
        mWindmillPaint.setStrokeWidth(2);//设置画笔粗细
        mWindmillPaint.setAntiAlias(true);
        mWindmillPaint.setColor(mWindmillColor);

        mAnimator = ObjectAnimator.ofFloat(this, "angle", 0, (float) (2 * PI));
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.setInterpolator(new LinearInterpolator());
    }

    private void initAttrs(AttributeSet attrs) {
        float DEFAULT_DIP = 120;
        mDefaultSize = DisplayUtils.dip2px(mContext, DEFAULT_DIP);
        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.Windmill);
            mWindLengthPercent = typedArray.getFloat(R.styleable.Windmill_windLengthParent, 0.33f);
            mWindmillColor = typedArray.getColor(R.styleable.Windmill_windmillColors, Color.WHITE);
            typedArray.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(myMeasure(widthMeasureSpec, mDefaultSize), myMeasure(heightMeasureSpec, mDefaultSize));
        //Log.d(TAG, "onMeasure: ");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {//w , h 即为onMeasure计算出来的值
        width = w;
        height = h;
        mCenterPoint.x = (int) (w * mWindLengthPercent);
        mCenterPoint.y = (int) (w * mWindLengthPercent);
        setBladeLocate();
        // Log.d(TAG, "onSizeChanged: ");
    }

    private void setBladeLocate() {
        x1 = mCenterPoint.x;
        y1 = mCenterPoint.y;

        //radian(弧度)
        rad1 = atan(width / 15f / (width / 30f)); //x1,y1与x2,y2形成的角,以圆点为坐标原点,返回角度为-pi/2 至 pi/2  artan（y/x）
        rad2 = atan(width / 6f / (width / 30f));//x1,y1 与 x3,y3
        rad3 = PI / 2;//tan90 不存在
        rad4 = atan(mCenterPoint.y / 2f / (-width / 30f)) + PI;//因为返回角度为 -pi/2 至pi,所以加PI

        //r 为斜边长度,与上面要对应
        r1 = Math.hypot(width / 30f, width / 15f);
        r2 = Math.hypot(width / 30f, width / 6f);
        r3 = Math.hypot(0, mCenterPoint.y);
        r4 = Math.hypot(width / 30f, mCenterPoint.y / 2f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        drawWind(canvas);
        drawPillar(canvas);
        canvas.restore();
    }

    private void drawPillar(Canvas canvas) {
        Path mPillarPath = new Path();
        mPillarPath.moveTo(mCenterPoint.x - width / 90f, mCenterPoint.y - width / 90f);
        mPillarPath.lineTo(mCenterPoint.x + width / 90f, mCenterPoint.y - width / 90f);//连线
        mPillarPath.lineTo(mCenterPoint.x + width / 35f, height - height / 35f);
        mPillarPath.quadTo(mCenterPoint.x, height, mCenterPoint.x - width / 35f, height - height / 35f);//贝塞尔曲线，控制点和终点
        mPillarPath.close();//闭合图形
        canvas.drawPath(mPillarPath, mWindmillPaint);
    }

    private void drawWind(Canvas canvas) {
        Path mWindPath = new Path();
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, width / 40f, mWindmillPaint);
        mWindPath.moveTo(x1, y1);
        x2 = mCenterPoint.x + (float) (r1 * Math.cos(rad1 + angle));
        y2 = mCenterPoint.y + (float) (r1 * Math.sin(rad1 + angle));
        x3 = mCenterPoint.x + (float) (r2 * Math.cos(rad2 + angle));
        y3 = mCenterPoint.y + (float) (r2 * Math.sin(rad2 + angle));
        x4 = mCenterPoint.x + (float) (r3 * Math.cos(rad3 + angle));
        y4 = mCenterPoint.y + (float) (r3 * Math.sin(rad3 + angle));
        x5 = mCenterPoint.x + (float) (r4 * Math.cos(rad4 + angle));
        y5 = mCenterPoint.y + (float) (r4 * Math.sin(rad4 + angle));

        mWindPath.cubicTo(x2, y2, x3, y3, x4, y4);
        mWindPath.quadTo(x5, y5, x1, y1);
        canvas.drawPath(mWindPath, mWindmillPaint);
        canvas.rotate(120, mCenterPoint.x, mCenterPoint.y);
        canvas.drawPath(mWindPath, mWindmillPaint);
        canvas.rotate(120, mCenterPoint.x, mCenterPoint.y);
        canvas.drawPath(mWindPath, mWindmillPaint);
        canvas.rotate(120, mCenterPoint.x, mCenterPoint.y);
    }

    private int myMeasure(int measureSpec, int defaultSize) {
        int mode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = defaultSize;
        if (mode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (mode == MeasureSpec.AT_MOST) {
            result = Math.min(specSize, defaultSize);
        }
        return result;
    }

    public void setWindSpeed(double windSpeed) {
        if (windSpeed == 0) {
            windSpeed = 1;
        }
        this.windSpeed = windSpeed;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        invalidate();
    }

    public void startAnimation() {
        mAnimator.setDuration((long) (10000 / (windSpeed * 0.80)));//乘以小于1的系数降低影响
        mAnimator.start();
    }

    public void stopAnimation() {
        clearAnimation();
    }

}
