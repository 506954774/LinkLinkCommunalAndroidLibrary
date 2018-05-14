package com.qdong.communal.library.widget.CircularLoadingView.painter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;


/**
 * @author Chuck
 */
public class InternalCirclePainterImp2 implements InternalCirclePainter {

    private RectF internalCircle;//画出圆弧时,圆弧的外切矩形
    private Paint internalCirclePaint;
    private int color;
    private float startAngle = 270f;
    int arcQuantity=100;//等分(圆弧加间隔),比如arcQuantity=100时,表示将有100个圆弧,和100个空白间隔
    float ratio=0.5f;//每段圆弧与圆弧加间隔之和的比例,ratio=0.5表示每个圆弧与相邻的间隔弧度比是1:1
    private int width;
    private int height;
    private int internalStrokeWidth = 48;//圆环宽度





    public InternalCirclePainterImp2(int color, int progressStrokeWidth,  int arcQuantity,float ratio) {
        this.color = color;
        this.internalStrokeWidth = progressStrokeWidth;
        this.arcQuantity = arcQuantity;
        if(ratio>0&&ratio<1){
            this.ratio = ratio;
        }

        init();
    }


    private void init() {
        initExternalCirclePainter();
    }

    private void initExternalCirclePainter() {
        internalCirclePaint = new Paint();
        internalCirclePaint.setAntiAlias(true);
        internalCirclePaint.setStrokeWidth(internalStrokeWidth);
        internalCirclePaint.setColor(color);
        internalCirclePaint.setStyle(Paint.Style.STROKE);


    }

    //圆弧外切矩形
    private void initExternalCircle() {
        internalCircle = new RectF();
        float padding = internalStrokeWidth * 0.5f;
        internalCircle.set(padding, padding , width - padding, height - padding);


        initExternalCirclePainter();
    }


    @Override
    public void draw(Canvas canvas) {



        float eachAngle=360f/arcQuantity;

        float eachArcAngle=eachAngle*ratio;

        for(int i=0;i<arcQuantity*2;i++){
            if(i%2==0){//遇到偶数就画圆弧,基数则跳过
                canvas.drawArc(internalCircle, startAngle+eachAngle*i/2, eachArcAngle, false, internalCirclePaint);
            }
            else{
               continue;
            }
        }

    }

    public void setColor(int color) {
        this.color = color;
        internalCirclePaint.setColor(color);
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void onSizeChanged(int height, int width) {
        this.width = width;
        this.height = height;
        initExternalCircle();
    }
}
