package com.qdong.communal.library.widget.TimePicker;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.qdong.communal.library.R;
import com.qdong.communal.library.util.ToastUtil;
import com.qdong.communal.library.widget.TimePicker.timePicker.adapter.NumericWheelAdapter;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.OnWheelScrollListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.TimeChoseFinishedListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.view.WheelView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * PopWindowChooseYear
 * 从底部弹出的年份选择器
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/3/9  17:47
 *  * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class PopWindowChooseYear extends PopupWindow implements View.OnClickListener{

    private View mMenuView;

    /**
     * 常量
     */
    private static final String TAG ="PopWindowChooseYear";

    private int mMinYear;
    private int mMaxYear;


    private final int YEAR_MAX=2088;//	默认最大值,如果构造乱传值,比如传个2000015,则使用此默认值

    private final String YEAR_STRING="年";


    /**视图构造器*/
    private LayoutInflater inflater;
    /**上下文对象*/
    private Context context;
    /**选取完成以后的回调接口**/
    private TimeChoseFinishedListener finishedListner;

    /***
     * UI控件
     */
    private Button cancelBtn;
    private Button commitBtn;

    private WheelView year;//年取滚轮控件
    LinearLayout ll;//父容器
    View view=null;
    private int mCalendarCurrentYear=2016;//当前日历年份


    public PopWindowChooseYear(Context context,int mMinYear,int mMaxYear, TimeChoseFinishedListener finishedListner) {
        super(context);
        this.finishedListner = finishedListner;
        this.context=context;
        this.mMinYear=mMinYear;
        this.mMaxYear=mMaxYear;
        initView();
    }

    private void initView() {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_choose_year,
                null);

        cancelBtn=(Button) mMenuView.findViewById(R.id.btn_cancel);
        commitBtn=(Button) mMenuView.findViewById(R.id.btn_submmit);

        ll=(LinearLayout)mMenuView. findViewById(R.id.ll_pickers);

        ll.addView(getDataPick());


        setListener();


        // 设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果

        /** 避免对整体窗口采用动画,分别对两个按钮采取动画 **/
        this.setAnimationStyle(R.style.AnimEventPosterBottom);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    /**
     * 按钮加点击监听
     */
    private void setListener() {
        cancelBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);

    }

    /* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
    @Override
    public void onClick(View v) {

        int id =v.getId();
        if(id==R.id.btn_cancel){
            if(finishedListner!=null){//非空验证
                finishedListner.handleCancle();
            }
            this.dismiss();
        }
        else if(id==R.id.btn_submmit){
            if(finishedListner!=null){//非空验证
                finishedListner.handleTimeStringAndDate(year.getCurrentItem()+mMinYear+"", null);
            }
            this.dismiss();
        }

    }

    /**
     *
     * @method name: getDataPick
     * @des:构造视图
     * @param : @return
     * @return type:View
     * @date 创建时间：2015年7月30日
     * @author Chuck
     */
    private View getDataPick() {

        Calendar c = Calendar.getInstance();//日历实例
        mCalendarCurrentYear=c.get(Calendar.YEAR);
        view = inflater.inflate(R.layout.wheel_year_picker, null);//获取视图
        year = (WheelView) view.findViewById(R.id.year);

        if(mMinYear<=0||mMinYear>3000||mMinYear>mMaxYear||(mMaxYear-mMinYear)>100){//界面乱传值时,使用默认值
            mMinYear=mCalendarCurrentYear;
            mMaxYear=YEAR_MAX;
        }
        NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(context,mMinYear, mMaxYear);
        numericWheelAdapter1.setLabel(YEAR_STRING);


        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);//是否可循环滑动
        year.addScrollingListener(scrollListener);
        year.setVisibleItems(7);//设置显示行数
        year.setCurrentItem(0);
        return view;
    }


    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        /**选完之后的回调*/
        @Override
        public void onScrollingFinished(WheelView wheel) {

            try {
                int chosenYear = mCalendarCurrentYear+year.getCurrentItem();//获取选中的年份
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

        }


    };



  /*  *//**
     * 土司显示
     *//*
    private void getTime() {


        String dateString = year.getCurrentItem()+mCalendarCurrentYear+"-"+month.getCurrentItem()+1+"-"+day.getCurrentItem()+1;




        dateString=new StringBuilder()
                .append((year.getCurrentItem() + mCalendarCurrentYear))
                .append(CONNECTOR)
                .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1))
                .append(CONNECTOR)
                .append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1) : (day.getCurrentItem() + 1))
                .toString();


        timeString=new StringBuilder().append(dateString).toString();



        try {


            timeDate =formatTime.parse( timeString);

            if(timeDate!=null){

                Log.i(TAG, formatTime.format(timeDate));

            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }*/

}
