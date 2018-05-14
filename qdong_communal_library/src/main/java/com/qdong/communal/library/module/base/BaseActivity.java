package com.qdong.communal.library.module.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.qdong.communal.library.R;

import java.util.ArrayList;

/**
 * BaseActivity
 * activity父类,只做两个事:
 * 1,加入沉浸式
 * 2,让子类修改状态栏的颜色
 * 3,生命周期监控

 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/7  11:53
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class BaseActivity extends FragmentActivity {


    //控制是否需要 沉浸式状态栏
    private boolean isTranslucentStatus = true;

    //沉浸式状态栏底色布局,api>=19时才会初始化
    private View stateBar;

    //最外层的线性布局容器
    private LinearLayout ll_container;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ll_container = new LinearLayout(this);
        ll_container.setOrientation(LinearLayout.VERTICAL);

        for (LifeCycleListener listener : mListeners) {
            listener.onActivityCreated(this);
        }

    }

    /**
     * 设置沉浸式状态栏的背景
     */
    public void setStateBarBackgroundColor(int color) {
        if (stateBar != null) {
            stateBar.setBackgroundColor(color);
        }
    }

    /**
     * 设置沉浸式状态栏布局是否显示
     */
    public void setStateBarVisibility(int visibility) {
        if (stateBar != null) {
            stateBar.setVisibility(visibility);
        }
    }

    /**
     * 设置沉浸式状态栏的高度
     */
    public void setStateBarHeight(int height) {
        if (stateBar != null) {
            ViewGroup.LayoutParams layoutParams = stateBar.getLayoutParams();
            layoutParams.height = height;
            stateBar.setLayoutParams(layoutParams);
        }
    }


    /**
     * 获取沉浸式状态栏的启用状态
     */
    public boolean isTranslucentStatus() {
        return isTranslucentStatus;
    }



    /**
     * 设置是否启用沉浸式状态栏
     */
    public void setIsTranslucentStatus(boolean isTranslucentStatus) {
        this.isTranslucentStatus = isTranslucentStatus;
    }



    @Override
    public void setContentView(int layoutResID) {
        initSaataeBar();
        View view = View.inflate(this, layoutResID, null);
        ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        super.setContentView(ll_container);
    }

    @Override
    public void setContentView(View view) {
        initSaataeBar();
        ll_container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        super.setContentView(ll_container);
    }



    private void initSaataeBar() {
        if (isTranslucentStatus && Build.VERSION.SDK_INT >= 19) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //初始化stateBar
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
            stateBar = new View(this);
            stateBar.setBackgroundColor(getStatusBarColor());
            ll_container.addView(stateBar, params);
        }
    }

    /**状态栏高度**/
    public abstract int getStatusBarHeight();

    /**
     * @param :[]
     * @return type:int
     *
     * @method name:getStatusBarColor
     * @des:抽象出来,修改状态栏的颜色
     * @date 创建时间:2016/7/7
     * @author Chuck
     **/
    public  abstract int getStatusBarColor();

   /* {
        return Color.parseColor("#ff378DDA");
    }*/


    /////////////////////////////////////////



    private final ArrayList<LifeCycleListener> mListeners = new ArrayList<LifeCycleListener>();

    public static interface LifeCycleListener {
        public void onActivityCreated(BaseActivity activity);

        public void onActivityDestroyed(BaseActivity activity);

        public void onActivityStarted(BaseActivity activity);

        public void onActivityStopped(BaseActivity activity);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {
        public void onActivityCreated(BaseActivity activity) {
        }

        public void onActivityDestroyed(BaseActivity activity) {
        }

        public void onActivityStarted(BaseActivity activity) {
        }

        public void onActivityStopped(BaseActivity activity) {
        }
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (mListeners.contains(listener))
            return;
        mListeners.add(listener);
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        mListeners.remove(listener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityDestroyed(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStarted(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStopped(this);
        }
    }
}
