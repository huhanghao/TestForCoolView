package com.reemii.com.testforcoolview.View;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by huhanghao on 2017/10/11.
 */

public class WriteView extends View {

    Context mContext;
    private Paint mPaint;
    private Path mPath = new Path();
    private float mPreX,mPreY;
    private Canvas saveCanvas;
    private int resultWidth;
    private int resultHeight;

    public WriteView(Context context) {
        this(context, null);
    }

    public WriteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WriteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        resultWidth = widthSize;
        resultHeight = heightSize;

        if (widthMode == MeasureSpec.AT_MOST) {
            int contentWidth = resultWidth + getPaddingLeft() + getPaddingRight();
            resultWidth = (contentWidth < widthSize) ? contentWidth : resultWidth;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            int contentHeight = resultHeight + getPaddingTop() + getPaddingBottom();
            resultHeight = (contentHeight < heightSize) ? contentHeight : resultHeight;
        }

        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //将Path的初始位置设置到手指的触点处
                mPath.moveTo(event.getX(), event.getY());
                mPreX=event.getX();
                mPreY=event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float endX=(mPreX+event.getX())/2;
                float endY=(mPreY+event.getY())/2;
                //quadTo前两个参数是控制点，后两个是终点
                mPath.quadTo(mPreX, mPreY, endX, endY);
                mPreX=event.getX();
                mPreY=event.getY();
                invalidate();
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 路径和画笔联合绘制成图形
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 保存图片
     */
    public void savePic(){
        Bitmap bm = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
        saveCanvas = new Canvas(bm);
        saveCanvas.drawColor(Color.WHITE);
        saveCanvas.drawPath(mPath, mPaint);
        saveCanvas.save();

        File f = new File("/sdcard/0.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 50, fos);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String absolutePath = f.getAbsolutePath();

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(mContext.getContentResolver(),
                    f.getAbsolutePath(), "123", null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // 最后通知图库更新
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + f.getAbsolutePath())));
    }

    public void clear(){
        mPath.reset();
        postInvalidate();
    }

    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
