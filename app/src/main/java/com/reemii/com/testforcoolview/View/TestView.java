package com.reemii.com.testforcoolview.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.reemii.com.testforcoolview.R;
import com.reemii.com.testforcoolview.util.ColorUtils;

/**
 * Created by huhanghao on 2017/9/7.
 */

public class TestView extends View {
    private Context mContext;
    private int centerX;
    private int centerY;
    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();
    private float rotateValue;

    public TestView(Context context) {
        this(context, null);

    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int resultWidth = widthSize;
        int resultHeight = heightSize;

        if (widthMode == MeasureSpec.AT_MOST) {
            int contentWidth = widthSize + getPaddingLeft() + getPaddingRight();
            resultWidth = (contentWidth < widthSize) ? contentWidth : resultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = heightSize + getPaddingTop() + getPaddingBottom();
            resultHeight = (contentHeight < heightSize) ? contentHeight : resultHeight;
        }

        centerX = resultWidth / 2;
        centerY = resultHeight / 2;

        setMeasuredDimension(resultWidth, resultHeight);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 180);

                valueAnimator.setDuration(500);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        rotateValue = (float) valueAnimator.getAnimatedValue();

                        if (rotateValue == 360) {
                            valueAnimator.removeAllUpdateListeners();
                        }
                        postInvalidate();
                    }
                });
                valueAnimator.start();

                break;

            }
            case MotionEvent.ACTION_UP: {

                break;
            }
            default: {

            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置背景色
        canvas.drawARGB(255, 139, 197, 186);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        paint.setColor(ColorUtils.getColor(mContext, R.color.colorAccent));

        Paint standardPaint = new Paint();
        standardPaint.setColor(ColorUtils.getColor(mContext, R.color.blue));
        canvas.drawLine(0, centerY - 200, getWidth(), centerY - 200, standardPaint);
        canvas.drawLine(0, centerY + 200, getWidth(), centerY + 200, standardPaint);
        canvas.drawLine(centerX - 300, 0, centerX - 300, getHeight(), standardPaint);
        canvas.drawLine(centerX + 300, 0, centerX + 300, getHeight(), standardPaint);

        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
        int r = canvasWidth / 3;
        //正常绘制黄色的圆形ååå
        paint.setColor(0xFFFFCC44);
        canvas.drawCircle(r, r, r, paint);
        //使用CLEAR作为PorterDuffXfermode绘制蓝色的矩形
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        paint.setColor(0xFF66AAFF);
        canvas.drawRect(r-rotateValue, r-rotateValue, r * 2.7f, r * 2.7f, paint);
        //最后将画笔去除Xfermode
        paint.setXfermode(null);
        canvas.restoreToCount(layerId);

    }
}
