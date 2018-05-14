package com.qdong.communal.library.module.item_slide_delete;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.qdong.communal.library.R;


public class SlidingButtonView extends HorizontalScrollView {

    //删除按钮
    private TextView mTextView_Delete;

    //左侧控件
    private RadioButton rbtn;

    private TextView text;

    private int leftWidth;

    //记录滚动条滚动的距离
    private int mScrollWidth;

    public int getLeftWidth() {
        return leftWidth;
    }


    //自定义的接口，用于传达滑动事件
    private IonSlidingButtonListener mIonSlidingButtonListener;

    //记录按钮菜单是否打开，默认关闭false
    private Boolean isOpen = false;

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    //在onMeasure中只执行一次的判断
    private Boolean once = false;

    public SlidingButtonView(Context context) {
        this(context, null);
    }

    public SlidingButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public SlidingButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!once) {
            //只需要执行一次
            mTextView_Delete = (TextView) findViewById(R.id.tv_delete);
            rbtn = (RadioButton) findViewById(R.id.rbtn);
            text = (TextView) findViewById(R.id.text);
            once = true;

        }
    }

    //使Item在每次变更布局大小时回到初始位置，并且获取滚动条的可移动距离
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            //获取水平滚动条可以滑动的范围，即右侧按钮的宽度
            mScrollWidth = mTextView_Delete.getWidth();
            leftWidth = rbtn.getWidth();

            this.scrollTo(leftWidth, 0);
            // LogUtils.d("可以滑动的范围:" + mScrollWidth);

        }
    }

    private boolean canTouch = true;

    public boolean isCanTouch() {
        return canTouch;
    }

    public void setCanTouch(boolean canTouch) {
        this.canTouch = canTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canTouch) {
            return true;
        }
         int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mIonSlidingButtonListener.onDownOrMove(this);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                changeScrollx();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //滚动监听，为了让删除按钮显示在项的背后的效果
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //  mTextView_Delete.setTranslationX(l - mScrollWidth -100);
        // mTextView_Delete.setTranslationX(l - mScrollWidth  );
        //this.setX(l);
    }

    public void changeScrollx() {
//        LogUtils.d("getScrollX(): " + getScrollX());
//        LogUtils.d("mScrollWidth: " + mScrollWidth);
//        LogUtils.d("leftWidth: " + leftWidth);
        if (getScrollX()-leftWidth >= (mScrollWidth / 2)) {
            this.smoothScrollTo(mScrollWidth + leftWidth, 0);
            isOpen = true;
            mIonSlidingButtonListener.onMenuIsOpen(this);
        } else {
            this.smoothScrollTo(leftWidth, 0);
            isOpen = false;
        }
    }

    Handler scrollHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==2) {
                SlidingButtonView.this.smoothScrollTo(leftWidth,0);
            } else if (msg.what==1) {
                SlidingButtonView.this.smoothScrollTo(0,0);
            }
        }
    };

    public void openMenu() {
//        if (isOpen) {
//            return;
//        }
        Message msg = new Message();
        msg.what= 1;
        scrollHandler.sendMessage(msg);

        isOpen = true;
        mIonSlidingButtonListener.onMenuIsOpen(this);
    }

    public void closeMenu() {
        if (!isOpen) {
            return;
        }
        Message msg = new Message();
        msg.what= 2;
        scrollHandler.sendMessage(msg);

        isOpen = false;
    }

    public void setSlidingButtonListener(IonSlidingButtonListener listener) {
        mIonSlidingButtonListener = listener;
    }

    public interface IonSlidingButtonListener {
        void onMenuIsOpen(View view);

        void onDownOrMove(SlidingButtonView slidingButtonView);
    }

}
