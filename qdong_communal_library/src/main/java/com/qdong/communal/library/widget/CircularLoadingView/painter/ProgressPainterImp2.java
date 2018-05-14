package com.qdong.communal.library.widget.CircularLoadingView.painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;


/**
 * @author Chuck
 */
public class ProgressPainterImp2 implements ProgressPainter {

    private RectF progressCircle;
    private Paint progressPaint;
    private int color = Color.RED;
    private float startAngle = 270f;
    private int internalStrokeWidth = 48;
    private float min;
    private float max;
    private int width;
    private int height;

    private int currentPecent;//当前的百分比

    int arcQuantity=100;//等分(圆弧加间隔),比如arcQuantity=100时,表示将有100个圆弧,和100个空白间隔
    float ratio=0.5f;//每段圆弧与圆弧加间隔之和的比例,ratio=0.5表示每个圆弧与相邻的间隔弧度比是1:1

    public ProgressPainterImp2(int color, float min, float max, int progressStrokeWidth, int arcQuantity,float ratio) {
        this.color = color;
        this.min = min;
        this.max = max;
        this.internalStrokeWidth = progressStrokeWidth;
        this.arcQuantity = arcQuantity;
        this.ratio = ratio;
        init();
        Log.e("ProgressPainterImp","构造函数执行");
    }

    private void init() {
        initInternalCirclePainter();

    }

    private void initInternalCirclePainter() {
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStrokeWidth(internalStrokeWidth);
        progressPaint.setColor(color);
        progressPaint.setStyle(Paint.Style.STROKE);

    }

    //初始化外切的那个矩形
    private void initInternalCircle() {
        progressCircle = new RectF();
        float padding = internalStrokeWidth * 0.5f;
        progressCircle.set(padding, padding , width - padding, height - padding);

        initInternalCirclePainter();
    }


    @Override
    public void draw(Canvas canvas) {


        float eachAngle=360f/arcQuantity;

        float eachArcAngle=eachAngle*ratio;

        int quantity=2*arcQuantity*currentPecent/100;
        for(int i=0;i<quantity;i++){
            if(i%2==0){//遇到偶数就画圆弧,基数则跳过
                canvas.drawArc(progressCircle, startAngle+eachAngle*i/2, eachArcAngle, false, progressPaint);
            }
            else{
                continue;
            }
        }
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setValue(float value) {
        this.currentPecent = (int) (( 100f * value) / max);
    }

    @Override
    public void onSizeChanged(int height, int width) {
        Log.e("ProgressPainterImp","onSizeChanged执行");

        this.width = width;
        this.height = height;
        initInternalCircle();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        progressPaint.setColor(color);
    }
}
