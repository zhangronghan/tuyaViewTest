package com.example.administrator.tuyaviewtest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2017/4/23.
 */

public class MyView extends View {
    private final int[] paintColor;
    private int screenWidth;
    private int screenHeight;
    private Paint mPaint;
    private Paint mBitmapPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private List<DrawPath> savePath;
    private List<DrawPath> deletePath;
    private Context context;
    private int currentStyle=1;
    private int currentSize=5;
    private int currentColor=Color.RED;
    private DrawPath dp;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private onSaveFinishListener mOnSaveFinishListener;

    public MyView(Context context,onSaveFinishListener mOnSaveFinishListener ,int width, int height) {
        super(context);
        this.context=context;
        this.mOnSaveFinishListener=mOnSaveFinishListener;
        screenWidth=width;
        screenHeight=height;
        paintColor = new int[]{
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.BLACK
        };
        setLayerType(LAYER_TYPE_SOFTWARE,null);
        initCanvas();
        savePath=new ArrayList<DrawPath>();
        deletePath=new ArrayList<DrawPath>();
    }

    private void initCanvas() {
        initPaint();
        mBitmapPaint=new Paint(Paint.DITHER_FLAG);
        mBitmap=Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.argb(0, 0, 0, 0));
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);

    }

    private void initPaint() {
        mPaint=new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        if (currentStyle == 1) {
            mPaint.setStrokeWidth(currentSize);
            mPaint.setColor(currentColor);
        } else {
            mPaint.setAlpha(0);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setStrokeWidth(50);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(mBitmap,0,0,mBitmapPaint);
        if(mPath !=null){
            canvas.drawPath(mPath,mPaint);
        }
    }

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(mY - y);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }
    private void touch_up() {
        mPath.lineTo(mX, mY);
        mCanvas.drawPath(mPath, mPaint);
        savePath.add(dp);
        mPath = null;
    }

    public void saveToSDCard(){
        Log.e("MyView","saveToSDCard");
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate=new Date(System.currentTimeMillis());
        String str=formatter.format(curDate)+"paint.png";
        File file=new File("sdcard/"+str);
        FileOutputStream output=null;
        try {
            output=new FileOutputStream(file);
        } catch(Exception e){
            e.printStackTrace();
        }

        mBitmap.compress(Bitmap.CompressFormat.PNG,100,output);
        Uri uri=Uri.fromFile(Environment.getExternalStorageDirectory());
        Intent intent=new Intent(Intent.ACTION_MEDIA_MOUNTED);
        intent.setData(uri);
        String absolute=file.getAbsolutePath();
        mOnSaveFinishListener.onSaveFinish(absolute);
        Log.e("TAG","图片已保存");
    }

    //撤销
    public void undo(){
        if(savePath !=null && savePath.size()>0){
            DrawPath drawPath=savePath.get(savePath.size() - 1);
            deletePath.add(drawPath);
            savePath.remove(savePath.size() -1);
            redrawOnBitmap();
        }

    }

    //重做
    public void redo(){
        if(savePath !=null && savePath.size()>0){
            savePath.clear();
            redrawOnBitmap();
        }
    }

    //恢复
    public void recover(){
        if(deletePath.size() > 0){
            DrawPath drawPath=deletePath.get(deletePath.size() -1);
            savePath.add(drawPath);
            mCanvas.drawPath(drawPath.path,drawPath.paint);
            deletePath.remove(deletePath.size() -1);
            invalidate();
        }

    }


    private void redrawOnBitmap() {
        initCanvas();
        Iterator<DrawPath> iter = savePath.iterator();
        while (iter.hasNext()) {
            DrawPath drawPath = iter.next();
            mCanvas.drawPath(drawPath.path, drawPath.paint);
        }
        invalidate();
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPath=new Path();
                dp=new DrawPath();
                dp.paint=mPaint;
                dp.path=mPath;
                touch_start(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                touch_move(x,y);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;

    }

    public void selectPaintStyle(int which) {
        if (which == 0) {
            currentStyle = 1;
            initPaint();
        }

        if (which == 1) {
            currentStyle = 2;
            initPaint();
        }
    }

    public void selectPaintSize(int which) {
        currentSize = which;
        initPaint();
    }

    public void selectPaintColor(int which) {
        currentColor = paintColor[which];
        initPaint();
    }

    public class DrawPath{
        public Path path;
        public Paint paint;
    }

}
