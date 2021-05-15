package com.yy.mediaplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class VisualizerView extends View {
    private Paint paint = new Paint();
    private Rect rect = new Rect();
    //保存波形抽样点的值
    private byte[] bytes;
    private byte type=0;
    private float[] points;

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        bytes = null;
        paint.setStrokeWidth(1f);
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
    }


    public void updataVisualizer(byte[] ftt){
        bytes = ftt;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_DOWN){
            return false;
        }
        type++;
        if(type>=3){
            type=0;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bytes==null){
            return;
        }
        canvas.drawColor(Color.WHITE);
        rect.set(0,0,getWidth(),getHeight());
        switch (type){
            case 0:
                for (int i=0;i<bytes.length-1;i++){
                    float left = getWidth()*i/(bytes.length-1);
                    float top = rect.height()-(byte)(bytes[i+1]+128)*rect.height()/128;
                    float right = left+1;
                    float bottom = rect.height();
                    canvas.drawRect(left,top,right,bottom,paint);
                }
                break;
            case 1:
                for (int i=0;i<bytes.length-1;i+=18){
                    float left = getWidth()*i/(bytes.length-1);
                    float top = rect.height()-(byte)(bytes[i+1]+128)*rect.height()/128;
                    float right = left+6;
                    float bottom = rect.height();
                    canvas.drawRect(left,top,right,bottom,paint);
                }
                break;
            case 2:
                if(points == null || points.length<bytes.length*4){
                    points = new float[bytes.length*4];
                }
                for(int i=0;i<bytes.length-1;i++){
                    points[i*4] = rect.width()*i/(bytes.length-1);
                    points[i*4+1] = (rect.height()/2)+((byte)(bytes[i]+128)*128)/(rect.height()/2);
                    points[i*4+2] = rect.width()*(i+1)/(bytes.length-1);
                    points[i*4+3] = (rect.height()/2)+((byte)(bytes[i+1]+128)*128)/(rect.height()/2);
                }
                canvas.drawLines(points,paint);
                break;
        }
    }
}
