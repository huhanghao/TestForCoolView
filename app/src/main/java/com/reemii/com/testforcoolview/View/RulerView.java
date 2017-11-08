package com.reemii.com.testforcoolview.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.reemii.com.testforcoolview.R;
import com.reemii.com.testforcoolview.util.ColorUtils;

/**
 * Created by huhanghao on 2017/11/8.
 */

public class RulerView extends View {
    private Context mContext;
    private int centerX;
    private int centerY;
    private int mWidth;
    private int mHeight;
    private int longLine;
    private int shortLine;
    private int lineWidth = 6;
    private Paint mPaint;
    private int unitWidth;
    private int scaleMax;
    private float moveDistance;
    private float downX;
    private int midCount = 60;
    private int lastMidCount = 60;
    private int moveCount = 0;
    private float trueMove;
    private boolean isActionUP = false;
    private Paint mTextPaint;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
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
            int contentWidth = getWidth() + getPaddingLeft() + getPaddingRight();
            resultWidth = (contentWidth < widthSize) ? contentWidth : resultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = getHeight() + getPaddingTop() + getPaddingBottom();
            resultHeight = (contentHeight < heightSize) ? contentHeight : resultHeight;
        }

        centerX = resultWidth / 2;
        centerY = resultHeight / 2;
        mWidth = resultWidth;
        mHeight = resultHeight;

        setMeasuredDimension(resultWidth, resultHeight);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        longLine = mHeight / 4;
        shortLine = mHeight / 8;
        unitWidth = lineWidth * 10;
        scaleMax = mWidth / 2 / unitWidth + 5;

        mPaint = new Paint();
        mTextPaint = new Paint();

        mTextPaint.setColor(ColorUtils.getColor(mContext, R.color.colorAccent));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画背景
        drawBg(canvas);

        // 画中线
        drawMidLine(canvas);

        // 画刻度
        drawScale(canvas);

        // 画刻度显示
        drawText(canvas);

    }

    private void drawText(Canvas canvas) {
        mTextPaint.setTextSize(80);
        canvas.drawText(midCount + "", centerX - mTextPaint.getTextSize() / 2, centerY / 2, mTextPaint);
    }

    private void drawScale(Canvas canvas) {
        int scaleLength;

        midCount = lastMidCount;
        mPaint.reset();
        mPaint.setColor(ColorUtils.getColor(mContext, R.color.gray_10));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(lineWidth);

        mTextPaint.setTextSize(50);

        // 根据移动计算尺子标尺位置
        moveCount = (int) moveDistance / unitWidth;
        trueMove = moveDistance % unitWidth;
        midCount += moveCount;

        if (isActionUP) {
            lastMidCount = midCount;
        }

        Log.d("hhh", "midCount = " + midCount);

        for (int i = 0; i < scaleMax; i++) {

            if (((midCount - i) % 5) == 0) {
                scaleLength = longLine;
            } else {
                scaleLength = shortLine;
            }
            // 画左边
            canvas.drawLine(centerX - i * unitWidth - trueMove, centerY, centerX - i * unitWidth - trueMove, centerY + scaleLength, mPaint);
            if (scaleLength == longLine) {
                canvas.drawText(midCount - i + "", centerX - i * unitWidth - trueMove - mTextPaint.getTextSize() / 2, centerY + scaleLength + 60, mTextPaint);
            }

            if (((midCount + i) % 5) == 0) {
                scaleLength = longLine;
            } else {
                scaleLength = shortLine;
            }
            // 画右边
            canvas.drawLine(centerX + i * unitWidth - trueMove, centerY, centerX + i * unitWidth - trueMove, centerY + scaleLength, mPaint);
            if (scaleLength == longLine) {
                canvas.drawText(midCount + i + "", centerX + i * unitWidth - trueMove - mTextPaint.getTextSize() / 2, centerY + scaleLength + 60, mTextPaint);
            }
        }
    }

    private void drawBg(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(ColorUtils.getColor(mContext, R.color.gray_10));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(lineWidth);
        canvas.drawLine(0, centerY, getWidth(), centerY, mPaint);
    }

    private void drawMidLine(Canvas canvas) {
        mPaint.reset();
        mPaint.setColor(ColorUtils.getColor(mContext, R.color.lightskyblue));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(lineWidth * 2);
        canvas.drawLine(centerX, centerY, centerX, centerY + longLine, mPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                isActionUP = false;
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                float moveX = event.getX();
                moveDistance = downX - moveX;
                Log.d("hhh", "moveDistance = " + moveDistance);
                postInvalidate();
                return true;
            }
            case MotionEvent.ACTION_UP: {

                downX = event.getX();
                float absTrueMove = Math.abs(trueMove);

                Log.d("hhh", "trueMove = " + trueMove);

                // 判断是左滑还是右滑
                if (trueMove < 0) {   // 左滑
                    absTrueMove = unitWidth - absTrueMove;

                    if ((absTrueMove > 0) && (absTrueMove > unitWidth / 2)) {
                        moveDistance = moveDistance - moveDistance % unitWidth;
                    } else if ((absTrueMove > 0) && (absTrueMove <= unitWidth / 2)) {
                        moveDistance = moveDistance - moveDistance % unitWidth - unitWidth;
                    }
                } else {
                    if ((absTrueMove > 0) && (absTrueMove > unitWidth / 2)) {
                        moveDistance = moveDistance - moveDistance % unitWidth + unitWidth;
                    } else if ((absTrueMove > 0) && (absTrueMove <= unitWidth / 2)) {
                        moveDistance = moveDistance - moveDistance % unitWidth;
                    }
                }

                Log.d("hhh", "absTrueMove = " + absTrueMove);


                isActionUP = true;
                if (absTrueMove > 0) {
                    postInvalidate();
                }
                return true;

            }
            default: {
                return super.onTouchEvent(event);
            }
        }
    }
}
