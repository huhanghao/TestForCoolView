package com.reemii.com.testforcoolview.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.reemii.com.testforcoolview.R;
import com.reemii.com.testforcoolview.util.ColorUtils;

/**
 * Created by huhanghao on 2017/10/31.
 * 参考：https://github.com/ChengangFeng/TickView
 */

public class ProgressButtonNew extends View {
    private Context mContext;
    private ColorStateList circleColor;
    private ColorStateList tickColor;
    private int mRadius;
    private int ringRadius;
    private Boolean ischecked = false;
    private Paint ringPaint;
    private Paint tickPaint;
    private Paint initPaint;
    private int centerX;
    private int centerY;
    private Path mPath = new Path();
    Path ringPath = new Path();
    private ValueAnimator ringAnimator;
    private float ringAngle;
    private Paint circleChangePaint;
    private ValueAnimator circleAnimator;
    private float changeRadius;
    private Paint circleBgPaint;
    private ValueAnimator flatAnimator;
    private float flatRadius;

    public enum ButtonStatus {
        none,
        ringing,
        cirling,
        flatting,
        finish
    }

    private ButtonStatus currentStatus = ButtonStatus.none;

    //记录打钩路径的三个点坐标
    private float[] mPoints = new float[6];


    public ProgressButtonNew(Context context) {
        this(context, null);
    }

    public ProgressButtonNew(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressButtonNew(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImitateKeepButton, defStyleAttr, 0);
        mRadius = array.getDimensionPixelSize(R.styleable.ImitateKeepButton_radius, 0);
        circleColor = array.getColorStateList(R.styleable.ImitateKeepButton_ring_bg_color);
        tickColor = array.getColorStateList(R.styleable.ImitateKeepButton_tick_color);


        array.recycle();

        initAttributes();

        initPaintOrPath();

        reset();
    }

    //当xml中没有定义时，设置默认的属性
    private void initAttributes() {
        circleColor = (circleColor != null) ? circleColor : ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
        tickColor = (tickColor != null) ? tickColor : ColorStateList.valueOf(Color.WHITE);
        ringRadius = mRadius;

    }

    //初始化画笔及路径
    private void initPaintOrPath() {

        initPaint = new Paint();
        initPaint.setAntiAlias(true);
        initPaint.setColor(ColorUtils.getColor(mContext, R.color.gray_10));
        initPaint.setStyle(Paint.Style.STROKE);
        initPaint.setStrokeWidth(10);

        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);
        ringPaint.setColor(circleColor.getColorForState(getDrawableState(), 0));
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(10);

        circleBgPaint = new Paint();
        circleBgPaint.setAntiAlias(true);
        circleBgPaint.setColor(circleColor.getColorForState(getDrawableState(), 0));
        circleBgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circleBgPaint.setStrokeWidth(10);

        circleChangePaint = new Paint();
        circleChangePaint.setAntiAlias(true);
        circleChangePaint.setColor(ColorUtils.getColor(mContext, R.color.white));
        circleChangePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circleChangePaint.setStrokeWidth(10);

//        ringPaint = new Paint();
//        ringPaint.setAntiAlias(true);
//        ringPaint.setColor(ColorUtils.getColor(mContext, R.color.aquamarine));
//        ringPaint.setStyle(Paint.Style.STROKE);
//        ringPaint.setStrokeWidth(10);

        tickPaint = new Paint();
        tickPaint.setColor(tickColor.getColorForState(getDrawableState(), 0));
        tickPaint.setAntiAlias(true);
        tickPaint.setStyle(Paint.Style.STROKE);
        tickPaint.setStrokeWidth(10);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int resultWidth = widthSize;
        int resultHeight = heightSize;

        if (widthMode == MeasureSpec.AT_MOST) {
            int contentWidth = (mRadius) * 2 + getPaddingLeft() + getPaddingRight();
            resultWidth = (contentWidth < widthSize) ? contentWidth : resultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = (mRadius) * 2 + getPaddingTop() + getPaddingBottom();
            resultHeight = (contentHeight < heightSize) ? contentHeight : resultHeight;
        }

        centerX = resultWidth / 2;
        centerY = resultHeight / 2;

        //设置打钩的几个点坐标
        mPath.moveTo(-mRadius / 4 - mRadius / 10, 0);
        mPath.lineTo(-mRadius / 10, mRadius / 4);
        mPath.lineTo(mRadius / 2 - mRadius / 10, -mRadius / 2 + mRadius / 4);
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                currentStatus = ButtonStatus.ringing;

                // 圆环动画
                ringAnimator = ValueAnimator.ofFloat(0, 360f);
                ringAnimator.setDuration(600);
                ringAnimator.setInterpolator(new LinearInterpolator());
                ringAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ringAngle = (float) valueAnimator.getAnimatedValue();
                        if (ringAngle == 360) {
                            ringAnimator.removeAllUpdateListeners();
//                            onViewClick.onFinish(this);
                            currentStatus = ButtonStatus.cirling;
                            circleAnimator.start();

                        }
                        postInvalidate();
                    }
                });
                ringAnimator.start();

                // 圆盘收缩动画
                circleAnimator = ValueAnimator.ofFloat((float) mRadius, 0);
                circleAnimator.setDuration(600);
                circleAnimator.setInterpolator(new LinearInterpolator());
                circleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        changeRadius = (float) valueAnimator.getAnimatedValue();
                        if (changeRadius == 0) {
                            circleAnimator.removeAllUpdateListeners();
//                            onViewClick.onFinish(this);
                            currentStatus = ButtonStatus.flatting;
                            flatAnimator.start();

                        }
                        postInvalidate();
                    }
                });

                // 圆盘膨胀动画
                flatAnimator = ValueAnimator.ofFloat((float) mRadius, mRadius + mRadius / 10, mRadius);
                flatAnimator.setDuration(300);
                flatAnimator.setInterpolator(new LinearInterpolator());
                flatAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        flatRadius = (float) valueAnimator.getAnimatedValue();
                        if (flatRadius == mRadius + mRadius / 10){
                            Toast.makeText(mContext,"完成",Toast.LENGTH_SHORT).show();
                        }

                        postInvalidate();
                    }
                });
            }

            return true;
            case MotionEvent.ACTION_UP: {
                ringAnimator.removeAllUpdateListeners();
                currentStatus = ButtonStatus.none;
                postInvalidate();
            }
            default:{

            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(centerX, centerY);

        switch (currentStatus) {
            case none: {
                canvas.drawCircle(0, 0, ringRadius, initPaint);
                canvas.drawPath(mPath, initPaint);
                break;
            }
            case ringing: {
                drawRing(canvas);
                break;
            }

            case cirling: {
                drawCircle(canvas);
                break;
            }
            case flatting: {
                drawFlatting(canvas);
                break;
            }
            case finish: {

                break;
            }
            default:{

            }
        }

    }

    private void drawFlatting(Canvas canvas) {
        canvas.drawCircle(0, 0, flatRadius, circleBgPaint);
        canvas.drawPath(mPath, tickPaint);
    }

    private void drawCircle(Canvas canvas) {

        canvas.drawCircle(0, 0, ringRadius, circleBgPaint);
        canvas.drawCircle(0, 0, changeRadius, circleChangePaint);
    }

    private void drawRing(Canvas canvas) {
        ringPath.reset();
        RectF rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        ringPath.addArc(rectF, -90, ringAngle);
        canvas.drawPath(ringPath, ringPaint);
    }


    private void reset() {

    }

}
