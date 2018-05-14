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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * PopWindowChooseDate
 * 从底部弹出的日期选择器
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/3/9  17:47
 *  * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class PopWindowChooseDate extends PopupWindow implements View.OnClickListener{

    private  int fromYear;//起始年份
    private View mMenuView;



    /**
     * 常量
     */
    private static final String TAG ="DialogWholeTime";

    private final int YEAR_MAX=2088;//	最大值

    private final String YEAR_STRING="年";
    //private final String YEAR_STRING="";
    private final String MONTH_STRING="月";
    //private final String MONTH_STRING="";
    private final String DAY_STRING="日";
    //private final String DAY_STRING="";



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
    private WheelView month;//月选取滚轮控件
    private WheelView day;//日选取滚轮控件

    LinearLayout ll;//父容器
    View view=null;


    public static final String CONNECTOR="-";//日期字符串的连接符

    public static SimpleDateFormat formatTime = new SimpleDateFormat("yyyy"+CONNECTOR+"MM"+CONNECTOR+"dd");







    private int mYear=2016;
    private int mMonth=1;
    private int mDay=1;









    private String timeString;//最后显示的日期
    private Date timeDate;//最后获取的date


    private Date limitDate;//选时间的最早值,可以为空

    public PopWindowChooseDate(Context context, TimeChoseFinishedListener finishedListner) {
        super(context);
        this.finishedListner = finishedListner;
        this.context=context;

        initView();

    }
    public PopWindowChooseDate(Context context,int fromYear, TimeChoseFinishedListener finishedListner) {
        super(context);
        this.finishedListner = finishedListner;
        this.context=context;
        this.fromYear=fromYear;

        initView();

    }
    public PopWindowChooseDate(Context context, TimeChoseFinishedListener finishedListner, Date limitDate) {
        super(context);
        this.finishedListner = finishedListner;
        this.context=context;
        try {
            this.limitDate=formatTime.parse(formatTime.format(limitDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        initView();

    }
    public PopWindowChooseDate(Context context,int fromYear, TimeChoseFinishedListener finishedListner, Date limitDate) {
        super(context);
        this.finishedListner = finishedListner;
        this.context=context;
        this.fromYear=fromYear;
        this.limitDate=limitDate;

        initView();

    }

    private void initView() {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_choose_time,
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
        if(id== R.id.btn_cancel){
            if(finishedListner!=null){//非空验证
                finishedListner.handleCancle();
            }
            this.dismiss();
        }
        else if(id== R.id.btn_submmit){
            getTime();

            if(timeDate!=null&&limitDate!=null){
                try {
                    if(timeDate.getTime()<limitDate.getTime()){//可以等于
                        ToastUtil.showCustomMessage(context,"选取的时间不可早于"+formatTime .format(limitDate)+"!");
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(finishedListner!=null){//非空验证
                finishedListner.handleTimeStringAndDate(timeString, timeDate);
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
        if(fromYear>=1000){
            mYear=fromYear;
        }
        else {
            mYear=c.get(Calendar.YEAR);
        }

        mMonth=c.get(Calendar.MONTH)+1;
        mDay=c.get(Calendar.DAY_OF_MONTH);

        view = inflater.inflate(R.layout.wheel_year_month_day_picker, null);//获取视图


        year = (WheelView) view.findViewById(R.id.year);
        month = (WheelView) view.findViewById(R.id.month);
        day = (WheelView) view.findViewById(R.id.day);



        NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(context,mYear, YEAR_MAX);
        numericWheelAdapter1.setLabel(YEAR_STRING);
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);//是否可循环滑动
        year.addScrollingListener(scrollListener);


        NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(context,1, 12, "%02d");
        numericWheelAdapter2.setLabel(MONTH_STRING);
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(true);
        month.addScrollingListener(scrollListener);

        //month.setVisibility(View.GONE);



        year.setVisibleItems(7);//设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        initDay(mYear, mMonth);
        day.setCyclic(true);

        day.addScrollingListener(scrollListener);




        /**设置当前item索引值*/

        if(c.get(Calendar.YEAR)>=mYear){
            year.setCurrentItem(c.get(Calendar.YEAR)-mYear);
        }
        else {
            year.setCurrentItem(0);
        }

        month.setCurrentItem(mMonth-1);
        day.setCurrentItem(mDay-1);


        return view;
    }


    /**
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(context,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel(DAY_STRING);
        day.setViewAdapter(numericWheelAdapter);
    }


    /**
     * @method name:getDay
     * @des:按年份，月份 计算日数
     * @param :[year, month]
     * @return type:int
     * @date 创建时间:2016/1/30
     * @author Chuck
     **/
    private int getDay(int year, int month) {

        int day = 30;
        boolean flag = false;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }










    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        /**选完之后的回调*/
        @Override
        public void onScrollingFinished(WheelView wheel) {

            try {
                int chosenYear = mYear+year.getCurrentItem();//获取选中的年份
                int chosenMonth = month.getCurrentItem()+1;//获取选中的月份
                initDay(chosenYear, chosenMonth);//重新绘制日期 picker

                /**如果原来的选中的索引大于现在的day数组,则把选中值改为当前最后那个**/
                int getDay=getDay(chosenYear, chosenMonth);
                if(day.getCurrentItem()+1>getDay(chosenYear, chosenMonth)){
                    day.setCurrentItem(getDay-1);
                }
                getTime();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }


        }


    };



    /**
     * 土司显示
     */
    private void getTime() {


        String dateString = year.getCurrentItem()+mYear+"-"+month.getCurrentItem()+1+"-"+day.getCurrentItem()+1;




        dateString=new StringBuilder()
                .append((year.getCurrentItem() + mYear))
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

    }

}
