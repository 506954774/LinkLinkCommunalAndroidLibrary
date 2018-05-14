package com.qdong.communal.library.widget.CircularLoadingView;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

import com.qdong.communal.library.R;
import com.qdong.communal.library.widget.CircularLoadingView.painter.InternalCirclePainter;
import com.qdong.communal.library.widget.CircularLoadingView.painter.InternalCirclePainterImp2;
import com.qdong.communal.library.widget.CircularLoadingView.painter.ProgressPainter;
import com.qdong.communal.library.widget.CircularLoadingView.painter.ProgressPainterImp2;


/**
 * @author Adrián García Lomas
 * 修改人:Chuck
 */
public class CircularLoadingView extends RelativeLayout {

    private InternalCirclePainter internalCirclePainter;//内层的圆环
    private ProgressPainter progressPainter;//表示进度的圆环
    private Interpolator interpolator = new AccelerateDecelerateInterpolator();
    private int internalBaseColor = Color.YELLOW;
    private int progressColor = Color.WHITE;
    private float min = 0;
    private float last = min;
    private float max = 100;
    private ValueAnimator valueAnimator;
    private OnValueChangeListener valueChangeListener;
    private float value;
    private int duration = 1000;//如果使用动画展示,动画时长
    private int progressStrokeWidth = 18;//圆弧宽度
    private int argQuantity=50;//圆弧总个数
    float ratio=0.1f;//每段圆弧与圆弧加间隔之和的比例




    public CircularLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * All the children must have a max height and width, never bigger than the internal circle
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        //Log.e("CircularLoadingView","onLayout执行,每秒60帧执行");

        super.onLayout(changed, left, top, right, bottom);
        final int count = getChildCount();
        int maxWidth = getWidth() / 2;
        int maxHeight = getHeight() / 2;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            int mesaureWidth = child.getMeasuredWidth();
            int measureHeight = child.getMeasuredWidth();

            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            //child.setTranslationY(padingTop);

            LayoutParams relativeLayoutParams =
                    (LayoutParams) child.getLayoutParams();
            relativeLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            child.setLayoutParams(relativeLayoutParams);

            if (mesaureWidth > maxWidth) {
                layoutParams.width = maxWidth;
            }

            if (measureHeight > maxHeight) {
                layoutParams.height = maxHeight;
            }
        }
    }

    private void init(Context context, AttributeSet attributeSet) {
        setWillNotDraw(false);
        TypedArray attributes = context.obtainStyledAttributes(attributeSet,
                R.styleable.CircularLoadingView);
        initAttributes(attributes);
        initPainters();
        initValueAnimator();
    }

    private void initAttributes(TypedArray attributes) {
        internalBaseColor = attributes.getColor(R.styleable.CircularLoadingView_base_color,
                internalBaseColor);
        progressColor = attributes.getColor(R.styleable.CircularLoadingView_progress_color,
                progressColor);
        max = attributes.getFloat(R.styleable.CircularLoadingView_max, max);
        ratio = attributes.getFloat(R.styleable.CircularLoadingView_ratio, ratio);
        duration = attributes.getInt(R.styleable.CircularLoadingView_duration, duration);
        argQuantity = attributes.getInt(R.styleable.CircularLoadingView_argQuantity, argQuantity);
        progressStrokeWidth = attributes.getInt(R.styleable.CircularLoadingView_progress_stroke_width,
            progressStrokeWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.e("CircularLoadingView","onSizeChanged执行");

        super.onSizeChanged(w, h, oldw, oldh);
        progressPainter.onSizeChanged(h, w);
        internalCirclePainter.onSizeChanged(h, w);
        animateValue();
    }

    private void initPainters() {
        internalCirclePainter = new InternalCirclePainterImp2(internalBaseColor, progressStrokeWidth,argQuantity,ratio);
        progressPainter = new ProgressPainterImp2(progressColor, min, max, progressStrokeWidth,argQuantity,ratio);
    }

    private void initValueAnimator() {
        valueAnimator = new ValueAnimator();
        valueAnimator.setInterpolator(interpolator);
        valueAnimator.addUpdateListener(new ValueAnimatorListenerImp());
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //Log.e("onDraw","线程id:"+Thread.currentThread().getId());

        //每个部分是按先后顺序来的,先draw的在底下
        internalCirclePainter.draw(canvas);//里面的圆环
        progressPainter.draw(canvas);//进度圆环
        invalidate();
    }

    /**
     * @method name:setValue
     * @des:不加动画,直接设置进度
     * @param :[value]
     * @return type:void
     * @date 创建时间:2017/7/21
     * @author Chuck
     **/
    public void setValue(float value) {
        this.value = value;

        if (value <= max && value >=min) {
            progressPainter.setValue(value);
            if (valueChangeListener != null) {
                valueChangeListener.onValueChange(value);
            }
            last = value;
        }

    }

    /**
     * @method name:setValueWithAnimation
     * @des:带动画效果的进度展示
     * @param :[value, duration]
     * @return type:void
     * @date 创建时间:2017/7/21
     * @author Chuck
     **/
    public void setValueWithAnimation(float value, int duration) {

        this.value = value;
        this.duration=duration>0?duration:3000;
        if(value==min){
            progressPainter.setValue(value);
            if (valueChangeListener != null) {
                valueChangeListener.onValueChange(value);
            }
            last = value;
        }
        else{
            if (value <= max && value >min) {
                animateValue();
            }
        }

    }

    private void animateValue() {
        if (valueAnimator != null) {
            valueAnimator.setFloatValues(last, value);
            valueAnimator.setDuration(duration);
            valueAnimator.start();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec );
    }

    public void setOnValueChangeListener(OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;

        if (valueAnimator != null) {
            valueAnimator.setInterpolator(interpolator);
        }
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
        progressPainter.setMin(min);
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
        progressPainter.setMax(max);
    }

    private class ValueAnimatorListenerImp implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            Float value = (Float) valueAnimator.getAnimatedValue();
            progressPainter.setValue(value);

            if (valueChangeListener != null) {
                valueChangeListener.onValueChange(value);
            }

            last = value;
        }
    }

    public interface OnValueChangeListener {
        void onValueChange(float value);
    }



    public void reset() {
        last = min;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        progressPainter.setColor(progressColor);
    }

    public int getInternalBaseColor() {
        return internalBaseColor;
    }

    public void setInternalBaseColor(int internalBaseColor) {
        this.internalBaseColor = internalBaseColor;
        internalCirclePainter.setColor(progressColor);
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }


}
