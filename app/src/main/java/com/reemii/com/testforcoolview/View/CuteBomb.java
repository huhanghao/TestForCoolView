package com.reemii.com.testforcoolview.View;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.reemii.com.testforcoolview.R;
import com.reemii.com.testforcoolview.util.ColorUtils;

import static android.animation.ValueAnimator.ofFloat;

/**
 * 参考：https://mp.weixin.qq.com/s/tXmLdKUXW_MclD1ZtEiEuw
 * Created by huhanghao on 2017/11/3.
 */

public class CuteBomb extends View {

    private Context mContext;
    private ColorStateList bombBodyColor;
    private ColorStateList bombLineColor;
    //    private int mRadius = 200;
    private int bombTime;
    private int bombLineWidth;
    private Paint mPaint;
    private Path mPath, mHeadLinePath;
    private DashPathEffect groundDashPathEffect;
    private int bodyRadius;
    private int bombCenterX;
    private int bombCenterY;
    private DashPathEffect circleDashPathEffect;
    private DashPathEffect smallLightPathEffect;
    //用于控制旋转
    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();
    private float eyeRadius, eyeMaxRadius, eyeMinRadius;
    private RectF mRectF;
    private float faceLROffset, faceMaxLROffset;
    private float faceTBOffset = 0, faceMaxTBOffset;
    private AnimatorSet set = new AnimatorSet();
    private float bombLRRotate = 15, bombMaxLRRotate = 15, bombTBRotate = 0, bombMaxTBRotate = 5;
    //用于计算mouth第二阶段变化
    private float mouthMaxWidthOffset, mouthMaxHeightOffset, mouthWidthOffset = 0, mouthHeightOffset = 0, mouthOffsetPercent = 0;
    //用于计算mouth第一阶段变化
    private float mouthFSMaxWidthOffset, mouthFSMaxHeightOffset, mouthFSWidthOffset = 0, mouthFSHeightOffset = 0;

    public enum BombStatus {
        none,
        burning,
        bomb,
    }

    private BombStatus currentStatus = BombStatus.none;

    public CuteBomb(Context context) {
        this(context, null);
    }

    public CuteBomb(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CuteBomb(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CuteBomb, defStyleAttr, 0);
        bombBodyColor = array.getColorStateList(R.styleable.CuteBomb_bomb_body_color);
        bombLineColor = array.getColorStateList(R.styleable.CuteBomb_bomb_line_color);
//        mRadius = array.getDimensionPixelSize(R.styleable.CuteBomb_bomb_radius, 0);
        bombTime = array.getInt(R.styleable.CuteBomb_bomb_time, 5);
        initAttr();

        initPaint();

    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPath = new Path();
        mRectF = new RectF();
        mHeadLinePath = new Path();
    }

    private void initAttr() {
        bombBodyColor = (bombBodyColor != null) ? bombBodyColor : ColorStateList.valueOf(getResources().getColor(R.color.coral));
        bombLineColor = (bombLineColor != null) ? bombLineColor : ColorStateList.valueOf(getResources().getColor(R.color.colorAccent));
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
            int contentWidth = getMeasuredWidth() + getPaddingLeft() + getPaddingRight();
            resultWidth = (contentWidth < widthSize) ? contentWidth : resultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = getMeasuredHeight() + getPaddingTop() + getPaddingBottom();
            resultHeight = (contentHeight < heightSize) ? contentHeight : resultHeight;
        }

        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
//        initData();
    }

    private void initData() {
        faceTBOffset = 0;
//        bombTBRotate=0;
//        bombLRRotate=15;
        faceLROffset = -faceMaxLROffset;
//        currentBlastCircleRadius =0;
//        blastCircleRadiusPercent=0;
        eyeRadius = eyeMaxRadius;
//        headLinePercent=1;
        mouthWidthOffset = 0;
        mouthHeightOffset = 0;
        mouthOffsetPercent = 0;
        mouthFSWidthOffset = 0;
        mouthFSHeightOffset = 0;
//        headLineLightRate=0;
    }

    public void startAnim() {
        stopAnim();

        ValueAnimator faceTBAnim = getFaceTopBottomAnim();
        ValueAnimator faceLRAnim = getFaceLeftRightAnim();
        AnimatorSet faceChangeAnim = getFaceChangeAnim();
//        set.play(getBlastAnim()).after(faceChangeAnim);
        set.play(faceTBAnim).after(faceLRAnim);
        set.play(faceChangeAnim).after(faceLRAnim);
//        set.play(faceLRAnim).with(getHeadLineAnim());
        set.start();

//        faceLRAnim.start();
    }

    public void stopAnim() {
        set.cancel();
        initData();
        invalidate();
    }

    private void init() {
        bombLineWidth = getMeasuredWidth() / 35;
        mPaint.setStrokeWidth(bombLineWidth);
        float[] groundEffectFloat = new float[]{bombLineWidth / 4, bombLineWidth / 2 + bombLineWidth, bombLineWidth * 2, bombLineWidth / 3 * 2 + bombLineWidth, getMeasuredWidth(), 0};
        groundDashPathEffect = new DashPathEffect(groundEffectFloat, 0);
        bodyRadius = (int) (getMeasuredHeight() / 3.4f);

        bombCenterX = getWidth() / 2;
        bombCenterY = getHeight() - bombLineWidth / 2 * 3 - bodyRadius;

        eyeRadius = eyeMaxRadius = bombLineWidth / 2;
        eyeMinRadius = eyeMaxRadius / 6;
        faceMaxLROffset = bodyRadius / 3;
        faceMaxTBOffset = bodyRadius / 3;

        groundEffectFloat = new float[]{
                getRadianLength(60, bodyRadius)
                , getRadianLength(10, bodyRadius)
                , getRadianLength(10, bodyRadius)
                , getRadianLength(10, bodyRadius)
                , getRadianLength(230, bodyRadius)
                , getRadianLength(20, bodyRadius)
                , getRadianLength(30, bodyRadius)};//设置画与不画所占长度
        circleDashPathEffect = new DashPathEffect(groundEffectFloat, 0);

        groundEffectFloat = new float[]{
                0,
                getRadianLength(200, bodyRadius - bombLineWidth * 3)
                , getRadianLength(20, bodyRadius - bombLineWidth * 3)
                , getRadianLength(10, bodyRadius - bombLineWidth * 3)
                , getRadianLength(10, bodyRadius - bombLineWidth * 3)
                , getRadianLength(10, bodyRadius - bombLineWidth * 3)
                , getRadianLength(1, bodyRadius - bombLineWidth * 3)
                , getRadianLength(200, bodyRadius - bombLineWidth * 3)};//设置画与不画所占长度
        smallLightPathEffect = new DashPathEffect(groundEffectFloat, 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        startAnim();

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (currentStatus) {
            case none: {
                drawFloor(canvas);

                drawBody(canvas);

                drawCircle(canvas);

                drawFace(canvas);

                drawHead(canvas);

                break;
            }
            case burning: {
                break;
            }
            case bomb: {
                break;
            }
            default: {

            }
        }

    }

    private void drawHead(Canvas canvas) {

        canvas.save();
        mCamera.save();
        mCamera.rotate(bombTBRotate, 0, -bombLRRotate);
        mMatrix.reset();
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-bombCenterX, -(bombCenterY));
        mMatrix.postTranslate(bombCenterX, (bombCenterY));
        canvas.concat(mMatrix);


    }

    private void drawFace(Canvas canvas) {

        canvas.save();
        mCamera.save();
//        mCamera.rotate(bombTBRotate,0,-bombLRRotate/3);
//        Log.d("hhh","bombTBRotate = " + bombTBRotate);
        mMatrix.reset();
        mCamera.getMatrix(mMatrix);
        mCamera.restore();
        mMatrix.preTranslate(-bombCenterX, -(bombCenterY));
        mMatrix.postTranslate(bombCenterX, bombCenterY);
        mMatrix.postTranslate(faceLROffset, faceTBOffset);
        canvas.concat(mMatrix);

        // 眼睛 椭圆控制
        mPaint.setColor(bombLineColor.getColorForState(getDrawableState(), 0));
        mPaint.setStyle(Paint.Style.FILL);
        float eyeY = bombCenterY + bodyRadius / 5;
        float eyeWidth = Math.max(eyeMaxRadius, eyeRadius);
        mRectF.set(bombCenterX - bodyRadius / 3.5f - eyeWidth, eyeY - eyeRadius
                , bombCenterX - bodyRadius / 3.5f + eyeWidth, eyeY + eyeRadius);
        canvas.drawOval(mRectF, mPaint);
        mRectF.set(bombCenterX + bodyRadius / 3.5f - eyeWidth, eyeY - eyeRadius
                , bombCenterX + bodyRadius / 3.5f + eyeWidth, eyeY + eyeRadius);
        canvas.drawOval(mRectF, mPaint);
        //画嘴巴 路径
        float mouthY = eyeY + bombLineWidth - mouthHeightOffset;//嘴巴起始高度
        float mouthMaxY = mouthY + bodyRadius / 7 + mouthHeightOffset - mouthFSHeightOffset;//嘴巴最底部
        float mouthHalfDistance = bodyRadius / 5 - mouthWidthOffset * 0.5f + mouthFSWidthOffset;//嘴巴顶部的拐角的一半宽度
        float mouthTopHalfDistance = (mouthHalfDistance - bodyRadius / 5 / 10) - mouthWidthOffset; //嘴巴顶部的一半宽度
        float mouthHorDistanceHalf = (mouthMaxY - mouthY) / (6 - 4 * mouthOffsetPercent);//嘴角控制点的距离嘴角点的竖直距离
        if (mouthTopHalfDistance < bodyRadius / 5 / 10) {//让过渡更加自然
            mouthTopHalfDistance = 0;
        }
        mPath.reset();
        mPath.moveTo(bombCenterX - mouthTopHalfDistance, mouthY);
        mPath.lineTo(bombCenterX + mouthTopHalfDistance, mouthY);

        mPath.quadTo(bombCenterX + mouthHalfDistance, mouthY,
                bombCenterX + mouthHalfDistance, mouthY + mouthHorDistanceHalf);
        mPath.cubicTo(bombCenterX + mouthHalfDistance, mouthY + mouthHorDistanceHalf * 2,
                bombCenterX + (mouthHalfDistance - bodyRadius / 5 / 4) * (1 - mouthOffsetPercent), mouthMaxY,
                bombCenterX, mouthMaxY);

        mPath.cubicTo(bombCenterX - (mouthHalfDistance - bodyRadius / 5 / 4) * (1 - mouthOffsetPercent), mouthMaxY,
                bombCenterX - mouthHalfDistance, mouthY + mouthHorDistanceHalf * 2,
                bombCenterX - mouthHalfDistance, mouthY + mouthHorDistanceHalf);
        mPath.quadTo(bombCenterX - mouthHalfDistance, mouthY, bombCenterX - mouthTopHalfDistance, mouthY);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        //画舌头 圆和嘴巴的缩放相交
        int save = canvas.saveLayer(0, 0, getMeasuredWidth(), getMeasuredHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawPath(mPath, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        mPaint.setColor(ColorUtils.getColor(mContext, R.color.tomato));
        canvas.drawCircle(bombCenterX, mouthY + (mouthMaxY - mouthY) / 8 + bodyRadius / (5 - 1.4f * mouthOffsetPercent), bodyRadius / 5, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.scale(0.8f, 0.8f, bombCenterX, (mouthMaxY + mouthY) / 2);
        canvas.drawPath(mPath, mPaint);
        canvas.restoreToCount(save);
        mPaint.setXfermode(null);

        //酒窝
        mPaint.setColor(Color.parseColor("#5689cc"));
        canvas.drawCircle(bombCenterX - bodyRadius / 3.5f - bombLineWidth, (mouthMaxY + mouthY) / 2, bombLineWidth / 3, mPaint);
        canvas.drawCircle(bombCenterX + bodyRadius / 3.5f + bombLineWidth, (mouthMaxY + mouthY) / 2, bombLineWidth / 3, mPaint);
        mPaint.setPathEffect(null);
        canvas.restore();

    }


    private void drawBody(Canvas canvas) {
        // 画圆的身体
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bombBodyColor.getColorForState(getDrawableState(), 0));
        canvas.drawCircle(bombCenterX, bombCenterY, bodyRadius - bombLineWidth / 2, mPaint);

        // 画大光斑
        mPaint.setColor(ColorUtils.getColor(mContext, R.color.white));
        mPaint.setPathEffect(null);//设置虚线效果
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(45);
        RectF lightRecf = new RectF(bombCenterX - bodyRadius - bombLineWidth / 4, bombCenterY - bodyRadius - bombLineWidth / 4, bombCenterX + bodyRadius + bombLineWidth / 4, bombCenterY + bodyRadius + bombLineWidth / 4);
        canvas.drawArc(lightRecf, 150, 100, false, mPaint);

        // 画小光斑
        mPaint.setPathEffect(smallLightPathEffect);//设置虚线效果
        mPaint.setStrokeWidth(15);
        mPath.reset();
        mPath.addCircle(bombCenterX, bombCenterY, bodyRadius - bombLineWidth * 3, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setPathEffect(circleDashPathEffect);//设置虚线效果
        mPaint.setColor(bombLineColor.getColorForState(getDrawableState(), 0));
        mPaint.setStrokeWidth(bombLineWidth);
        mPath.reset();
        mPath.addCircle(bombCenterX, bombCenterY, bodyRadius, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);

    }

    private void drawFloor(Canvas canvas) {
        mPaint.setPathEffect(groundDashPathEffect);//设置虚线效果
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(bombLineColor.getColorForState(getDrawableState(), 0));
        mPath.reset();
        mPath.moveTo(bombLineWidth, getHeight() - bombLineWidth);
        mPath.lineTo(getWidth() - bombLineWidth / 2, getHeight() - bombLineWidth);
        canvas.drawPath(mPath, mPaint);
    }

    private float getRadianLength(float angle, float radius) {
        return (float) (angle * Math.PI * radius / 180f);
    }

    private ValueAnimator getFaceLeftRightAnim() {
        ValueAnimator valueAnimator = ofFloat(-1, 0, 1, 0)
                .setDuration(1400);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                faceLROffset = faceMaxLROffset * value;
                bombLRRotate = -bombMaxLRRotate * value;
                if (Math.abs(value) < 0.3 && animation.getAnimatedFraction() < 0.6) {
                    eyeRadius = Math.max(eyeMaxRadius * Math.abs(value), eyeMinRadius);
                } else {
                    eyeRadius = eyeMaxRadius;
                }
                invalidate();
            }
        });
        return valueAnimator;
    }

    private ValueAnimator getFaceTopBottomAnim() {
        ValueAnimator objectAnimator = ofFloat(0, 1)
                .setDuration(300);
        objectAnimator.setStartDelay(200);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                faceTBOffset = -faceMaxTBOffset * value;
//                bombTBRotate= bombMaxTBRotate*value;
                invalidate();
            }
        });
        return objectAnimator;
    }

    private AnimatorSet getFaceChangeAnim() {
        AnimatorSet animatorSet = new AnimatorSet();
        //眼睛
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0, 1.4f)
                .setDuration(450);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                eyeRadius = eyeMaxRadius * (float) animation.getAnimatedValue();
                if (eyeRadius < eyeMinRadius) {
                    eyeRadius = eyeMinRadius;
                }
            }
        });

        ValueAnimator mouthAnimator = ValueAnimator.ofFloat(0, 1, 0, 1)
                .setDuration(450);
        mouthAnimator.setInterpolator(new DecelerateInterpolator());
        mouthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (animation.getAnimatedFraction() > 0.75) {//第一阶段还是第二阶段
                    mouthWidthOffset = mouthMaxWidthOffset * value;
                    mouthHeightOffset = mouthMaxHeightOffset * value;
                    mouthOffsetPercent = animation.getAnimatedFraction();
                } else {
                    mouthFSWidthOffset = mouthFSMaxWidthOffset * value;
                    mouthFSHeightOffset = mouthFSMaxHeightOffset * value;
                }
                invalidate();
            }
        });
        animatorSet.play(valueAnimator).with(mouthAnimator);
        return animatorSet;
    }

}
