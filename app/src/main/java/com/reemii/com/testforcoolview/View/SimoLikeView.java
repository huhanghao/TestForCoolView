package com.reemii.com.testforcoolview.View;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.reemii.com.testforcoolview.R;
import com.reemii.com.testforcoolview.util.ColorUtils;

import java.util.ArrayList;

/**
 * 参考：http://blog.csdn.net/Simon_Crystin/article/details/78332452
 * Created by huhanghao on 2017/11/1.
 */

public class SimoLikeView extends View {

    Context mContext;
    private ColorStateList simoLikeColor;
    private String simoLikeText;
    private Paint simoLikePaint;
    private Paint simoTextPaint;
    private int centerX;
    private String simoDataText;
    private float textSize;
    private float textHeight;
    private float simoMaxHeight;
    private ValueAnimator growAnimator;
    private float growHeight;
    private ArrayList<Integer> gitList;
    private int pos = 0;
    private ValueAnimator gitAnimator;
    private int currentGit;
    private ValueAnimator closeAnimator;
    private ValueAnimator nodeAnimator;

    public enum SimoStatus {
        none,
        grow,
        faceTime,
        node,
        close
    }

    private SimoStatus currentStatu = SimoStatus.none;

    public SimoLikeView(Context context) {
        this(context, null);
    }

    public SimoLikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SimoLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SimoLikeView, defStyleAttr, 0);
        simoLikeColor = array.getColorStateList(R.styleable.SimoLikeView_simo_color_like);
        simoLikeText = array.getString(R.styleable.SimoLikeView_simo_like_text);
        simoDataText = array.getString(R.styleable.SimoLikeView_simo_data_text);

        initAnimationList();

        initAttr();

        initPaint();


    }

    private void initAnimation() {
        growAnimator = ValueAnimator.ofFloat(centerX / 2, simoMaxHeight);
        growAnimator.setDuration(500);
        growAnimator.setInterpolator(new LinearInterpolator());
        growAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                growHeight = (float) valueAnimator.getAnimatedValue();
                if (growHeight == simoMaxHeight) {
                    growAnimator.removeAllUpdateListeners();

                    currentStatu = SimoStatus.faceTime;
                    gitAnimator.start();

                }
                postInvalidate();
            }
        });


        gitAnimator = ValueAnimator.ofObject(new TypeEvaluator() {
            @Override
            public Object evaluate(float v, Object o, Object t1) {
                pos++;
                int curInt = (int) (0 + v * (gitList.size() - 1));
                return curInt;
            }
        }, gitList);
        gitAnimator.setDuration(2000);
        gitAnimator.setInterpolator(new BounceInterpolator());
        gitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int mPos = (int) valueAnimator.getAnimatedValue();
                currentGit = gitList.get(mPos);
                if (mPos == (gitList.size() - 1)) {
                    currentStatu = SimoStatus.node;
                    gitAnimator.removeAllUpdateListeners();
                    nodeAnimator.start();
                    pos = 0;
                }
                postInvalidate();
            }
        });

        nodeAnimator = ValueAnimator.ofFloat(simoMaxHeight, simoMaxHeight - centerX / 10);
        nodeAnimator.setDuration(300);
        nodeAnimator.setInterpolator(new LinearInterpolator());
        nodeAnimator.setRepeatCount(3);
        nodeAnimator.setRepeatMode(ValueAnimator.REVERSE);
        nodeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            private int count = 0;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                growHeight = (float) valueAnimator.getAnimatedValue();
                if (growHeight == simoMaxHeight) {
                    count++;
                }
                if (count == 4) {
                    nodeAnimator.removeAllUpdateListeners();
                    closeAnimator.start();
                    currentStatu = SimoStatus.close;
                }
                postInvalidate();
            }
        });


        closeAnimator = ValueAnimator.ofFloat(simoMaxHeight, centerX / 2);
        closeAnimator.setDuration(500);
        closeAnimator.setInterpolator(new LinearInterpolator());
        closeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                growHeight = (float) valueAnimator.getAnimatedValue();
                if (growHeight == centerX / 2) {
                    closeAnimator.removeAllUpdateListeners();
                    currentStatu = SimoStatus.none;
                }
                postInvalidate();
            }
        });
    }

    private void initAnimationList() {
        gitList = new ArrayList<>();
        gitList.add(R.drawable.like_1);
        gitList.add(R.drawable.like_2);
        gitList.add(R.drawable.like_2_1);
        gitList.add(R.drawable.like_2_2);
        gitList.add(R.drawable.like_2_3);
        gitList.add(R.drawable.like_3);
        gitList.add(R.drawable.like_3_1);
        gitList.add(R.drawable.like_4);
        gitList.add(R.drawable.like_4_1);
        gitList.add(R.drawable.like_4_2);
        gitList.add(R.drawable.like_4_3);
        gitList.add(R.drawable.like_5);
        gitList.add(R.drawable.like_6);
        gitList.add(R.drawable.like_6_1);
        gitList.add(R.drawable.like_6_2);
        gitList.add(R.drawable.like_6_3);
        gitList.add(R.drawable.like_7);
        gitList.add(R.drawable.like_7_1);
        gitList.add(R.drawable.like_7_2);
        gitList.add(R.drawable.like_7_3);
        gitList.add(R.drawable.like_8);
        gitList.add(R.drawable.like_8_1);
        gitList.add(R.drawable.like_8_2);
        gitList.add(R.drawable.like_8_3);
        gitList.add(R.drawable.like_9);
        gitList.add(R.drawable.like_9_1);
        gitList.add(R.drawable.like_9_2);
        gitList.add(R.drawable.like_9_3);
        gitList.add(R.drawable.like_10);
    }


    private void initAttr() {
        simoLikeColor = (simoLikeColor != null) ? simoLikeColor : ColorStateList.valueOf(getResources().getColor(R.color.ghostwhite));
        simoLikeText = (simoLikeText != null) ? simoLikeText : "喜欢";
        simoDataText = (simoDataText != null) ? simoDataText : "70%";
    }

    private void initPaint() {
        simoLikePaint = new Paint();
        simoLikePaint.setAntiAlias(true);
        simoLikePaint.setColor(simoLikeColor.getColorForState(getDrawableState(), 0));
        simoLikePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        simoLikePaint.setStrokeWidth(3);

        simoTextPaint = new Paint();
        simoTextPaint.setAntiAlias(true);
        simoTextPaint.setColor(ColorUtils.getColor(mContext, R.color.black));
        simoTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        simoTextPaint.setStrokeWidth(1);
        simoTextPaint.setTextSize(40);
        textSize = simoTextPaint.getTextSize();

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

        textHeight = textSize * 3;
        simoMaxHeight = getHeight() - textHeight - 30;

        setMeasuredDimension(resultWidth, resultHeight);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        initAnimation();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (currentStatu == SimoStatus.none) {

                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    Region region = new Region(0, (int) (getHeight() - centerX), (int) getWidth(), (int) getHeight());
                    if (region.contains(x, y)) {
                        currentStatu = SimoStatus.grow;
                        growAnimator.start();
                    }
                }

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

        canvas.translate(centerX, getHeight());

        switch (currentStatu) {

            case none: {

                drawSmile(canvas, 0, R.drawable.like_1);

                break;
            }

            case grow: {
                canvas.drawCircle(0, -centerX / 2, centerX / 2, simoLikePaint);

                RectF rectF = new RectF(-centerX / 2, -growHeight, centerX / 2, -centerX / 2);
                canvas.drawRect(rectF, simoLikePaint);

                canvas.drawCircle(0, -growHeight, centerX / 2, simoLikePaint);

                drawSmile(canvas, growHeight - centerX / 2, R.drawable.like_1);

                drawLikeText(canvas, simoLikeText, growHeight);
                drawDataText(canvas, simoDataText, growHeight);



                break;
            }
            case faceTime: {

                canvas.drawCircle(0, -centerX / 2, centerX / 2, simoLikePaint);

                RectF rectF = new RectF(-centerX / 2, -simoMaxHeight, centerX / 2, -centerX / 2);
                canvas.drawRect(rectF, simoLikePaint);

                canvas.drawCircle(0, -simoMaxHeight, centerX / 2, simoLikePaint);

                drawSmile(canvas, simoMaxHeight - centerX / 2, currentGit);

                drawLikeText(canvas, simoLikeText, simoMaxHeight);
                drawDataText(canvas, simoDataText, simoMaxHeight);
                break;
            }
            case node: {
                canvas.drawCircle(0, -centerX / 2, centerX / 2, simoLikePaint);

                RectF rectF = new RectF(-centerX / 2, -simoMaxHeight, centerX / 2, -centerX / 2);
                canvas.drawRect(rectF, simoLikePaint);

                canvas.drawCircle(0, -simoMaxHeight, centerX / 2, simoLikePaint);

                drawSmile(canvas, growHeight - centerX / 2, R.drawable.like_10);

                drawLikeText(canvas, simoLikeText, simoMaxHeight);
                drawDataText(canvas, simoDataText, simoMaxHeight);
                break;
            }
            case close: {

                canvas.drawCircle(0, -centerX / 2, centerX / 2, simoLikePaint);

                RectF rectF = new RectF(-centerX / 2, -growHeight, centerX / 2, -centerX / 2);
                canvas.drawRect(rectF, simoLikePaint);

                canvas.drawCircle(0, -growHeight, centerX / 2, simoLikePaint);

                drawSmile(canvas, growHeight - centerX / 2, R.drawable.like_10);

                drawLikeText(canvas, simoLikeText, growHeight);

                drawDataText(canvas, simoDataText, growHeight);


                break;
            }
            default: {

            }
        }

    }

    private void drawSmile(Canvas canvas, float height, int resID) {
        canvas.drawCircle(0, -centerX / 2, centerX / 2, simoLikePaint);

        Resources r = mContext.getResources();
        Drawable drawale = r.getDrawable(resID);

        //创建内存中的一张图片
        Bitmap bitmap = Bitmap.createBitmap(centerX, centerX, Bitmap.Config.ARGB_8888);
        //图片画片
        Canvas cas = new Canvas(bitmap);
        drawale.setBounds(0, 0, centerX, centerX);
        //图片加载到bitmap上
        drawale.draw(cas);
        //画到View上
        canvas.drawBitmap(bitmap, -centerX / 2, -centerX - height, simoLikePaint);
    }

    private void drawLikeText(Canvas canvas, String text, float height) {
        float textWidth = text.length() * textSize;
        canvas.drawText(text, -textWidth / 2, -height - centerX / 5 *3, simoTextPaint);
    }

    private void drawDataText(Canvas canvas, String text, float height) {
        float textWidth = text.length() * textSize;
        canvas.drawText(text, -textWidth / 3, -height - centerX, simoTextPaint);
    }

}
