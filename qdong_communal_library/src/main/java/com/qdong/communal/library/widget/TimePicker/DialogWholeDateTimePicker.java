/**
 * HandNearDatePickerEncore
 * com.ztb.handnear.widget
 * DialogStartTimeChose.java
 * 责任人:
 * 创建/修改时间: 2015年7月14日-下午6:07:51
 * Copyright © 2013-2015 深圳掌通宝科技有限公司-版权所有
 */
package com.qdong.communal.library.widget.TimePicker;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qdong.communal.library.R;
import com.qdong.communal.library.util.ToastUtil;
import com.qdong.communal.library.widget.TimePicker.timePicker.adapter.ArrayWheelAdapter;
import com.qdong.communal.library.widget.TimePicker.timePicker.adapter.NumericWheelAdapter;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.OnWheelScrollListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.interfaces.TimeChoseFinishedListener;
import com.qdong.communal.library.widget.TimePicker.timePicker.view.WheelView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * DialogWholeDateTimePicker
 * 时间选择器（年 月 日 时 分） 自动以系统当前时间作为选择起点
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/1/30  16:30
 * * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class DialogWholeDateTimePicker extends AlertDialog implements OnClickListener {


    /**
     * 常量
     */
    private static final String TAG = "DialogWholeTime";

    private final int YEAR_MAX = 2088;//	最大值

    private final String YEAR_STRING = "年";
    //private final String YEAR_STRING="";
    private final String MONTH_STRING = "月";
    //private final String MONTH_STRING="";
    private final String DAY_STRING = "日";
    //private final String DAY_STRING="";
    private final String HOUR_STRING = "点";
    private final String MIN_STRING = "分";


    /**
     * 视图构造器
     */
    private LayoutInflater inflater;
    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 选取完成以后的回调接口
     **/
    private TimeChoseFinishedListener finishedListner;

    /***
     * UI控件
     */
    private Button cancelBtn;
    private Button commitBtn;


    boolean mIsToday = true;//是否是今天，默认就是今天。弹出来的时候展示当前时间


    private WheelView year;//年取滚轮控件
    private WheelView month;//月选取滚轮控件
    private WheelView day;//日选取滚轮控件
    private WheelView hour;//时选取滚轮控件
    private WheelView min;//分选取滚轮控件
    LinearLayout ll;//父容器
    View view = null;


    private SimpleDateFormat formatTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    private int mYear = 2016;
    private int mMonth = 1;
    private int mDay = 1;
    private int mHour = 1;
    private int mMinutes = 1;


    /**
     * 所有分钟数组
     */
    private String[] origanlMinuteStrings = new String[]{"00", "10", "20", "30", "40", "50"};
    /**
     * 小时可选集合
     */
    private ArrayList<String> hoursString = new ArrayList<String>();
    /**
     * 可选分钟集合
     */
    private ArrayList<String> minsString = new ArrayList<String>();

    private String timeString;//最后显示的日期
    private Date timeDate;//最后获取的date


    private DialogWholeDateTimePicker(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        this.context = context;
    }


    private DialogWholeDateTimePicker(Context context, int theme, TimeChoseFinishedListener finishedListner) {
        super(context, theme);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.finishedListner = finishedListner;

    }

    /**
     * 公开的构造
     *
     * @param context         上下文
     * @param finishedListner 回调监听器,用户点击确认后,将回调接口实例的实现
     */
    public DialogWholeDateTimePicker(Context context, TimeChoseFinishedListener finishedListner) {
        super(context, R.style.MyDialog);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.finishedListner = finishedListner;


    }

    /* (non-Javadoc)
     * @see android.app.AlertDialog#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.dialog_chose_year_nonth_date_year_hour_min);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        cancelBtn = (Button) this.findViewById(R.id.btn_cancel);
        commitBtn = (Button) this.findViewById(R.id.btn_commit);

        ll = (LinearLayout) this.findViewById(R.id.ll_time_picker_view);
        ll.addView(getDataPick());


        setListener();

    }


    /**
     * 按钮加点击监听
     */
    private void setListener() {
        cancelBtn.setOnClickListener(DialogWholeDateTimePicker.this);
        commitBtn.setOnClickListener(DialogWholeDateTimePicker.this);

    }


    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            if (finishedListner != null) {//非空验证
                finishedListner.handleCancle();
            }
            DialogWholeDateTimePicker.this.dismiss();
        } else if (id == R.id.btn_commit) {
            if (finishedListner != null) {//非空验证
                getTime();
                finishedListner.handleTimeStringAndDate(timeString, timeDate);
            }

            DialogWholeDateTimePicker.this.dismiss();
        }
    }

    /**
     * 获取分钟数组的起始值,此int值将决定分钟滚轮的数据源
     * 例如:
     * {"00","10","20","30","40","50"}
     * <p/>
     * 如果现在是09:05,则返回1
     * 即从"10"开始取数组
     *
     * @return
     */
    private int getIndexOfMintutes() {
        int result = -1;
        if (!mIsToday) {//如果不是今天,则全部从"00"开始取
            return 0;
        } else {//如果是今天,而且是当前小时
            Calendar c = Calendar.getInstance();//日历实例
            mHour = c.get(Calendar.HOUR_OF_DAY);//24进制的小时数
            mMinutes = c.get(Calendar.MINUTE);
            if (mMinutes >= 50) {
                return 0;
            } else {
                result = mMinutes / 10 + 1;
            }
            return result;
        }

    }

    /**
     * @param : @return
     * @return type:View
     *
     * @method name: getDataPick
     * @des:构造视图
     * @date 创建时间：2015年7月30日
     * @author Chuck
     */
    private View getDataPick() {

        Calendar c = Calendar.getInstance();//日历实例
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);//24进制的小时数
        mMinutes = c.get(Calendar.MINUTE);//分钟数

        Log.i(TAG, "mHour:" + mHour + "mMinutes:" + mMinutes);


        view = inflater.inflate(R.layout.wheel_year_month_day_hour_minutes_picker, null);//获取视图


        year = (WheelView) view.findViewById(R.id.year);
        month = (WheelView) view.findViewById(R.id.month);
        day = (WheelView) view.findViewById(R.id.day);
        hour = (WheelView) view.findViewById(R.id.hour);
        min = (WheelView) view.findViewById(R.id.min);


        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(context, mYear, YEAR_MAX);
        numericWheelAdapter1.setLabel(YEAR_STRING);
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);//是否可循环滑动
        year.addScrollingListener(scrollListener);


        NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(context, 1, 12, "%02d");
        numericWheelAdapter2.setLabel(MONTH_STRING);
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(true);
        month.addScrollingListener(scrollListener);

        //month.setVisibility(View.GONE);

        day.addScrollingListener(scrollListener);

        year.setVisibleItems(7);//设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        initDay(mYear, mMonth);
        day.setCyclic(true);


        if (!mIsToday) {//不是今天

            initWholeTimes();//按全部数据绘制滚轮

        } else {//是今天,能调用此处,说明今天还有可选

            if (mHour == 23 && mMinutes >= 50) {
                initWholeTimes();
            } else {


                /**小时只有一部分*/
                NumericWheelAdapter numericWheelAdapter3 = null;
                if (mMinutes < 50) {
                    numericWheelAdapter3 = new NumericWheelAdapter(context, mHour, 23, "%02d");
                    setHourStrings(mHour, 23);
                } else {
                    numericWheelAdapter3 = new NumericWheelAdapter(context, mHour + 1, 23, "%02d");
                    setHourStrings(mHour + 1, 23);
                }

                numericWheelAdapter3.setLabel(context.getString(R.string.hour));
                hour.setViewAdapter(numericWheelAdapter3);


                /**
                 * 分钟的选择滚轮
                 */
                String[] times = getMinutesStrings(getIndexOfMintutes());
                ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(context, times);
                min.setViewAdapter(arrayWheelAdapter);

            }

        }


        hour.addScrollingListener(scrollListener);

        min.addScrollingListener(scrollListener);
        min.setCyclic(false);

        /**设置可见item数目**/

        hour.setVisibleItems(7);
        min.setVisibleItems(7);


        /**设置当前item索引值*/

        year.setCurrentItem(0);
        month.setCurrentItem(mMonth - 1);
        day.setCurrentItem(mDay - 1);
        hour.setCurrentItem(0);
        min.setCurrentItem(0);

        return view;
    }

    /**
     */
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context, 1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel(DAY_STRING);
        day.setViewAdapter(numericWheelAdapter);
    }


    /**
     * @param :[year, month]
     * @return type:int
     *
     * @method name:getDay
     * @des:按年份，月份 计算日数
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

    /**
     * @param :
     * @return type:void
     *
     * @method name: initWholeTimes
     * @des:
     * @date 创建时间：2015年7月30日
     * @author Chuck
     */
    private void initWholeTimes() {
        /**只选取明天和后天,小时是全部的*/
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context, 0, 23, "%02d");
        setHourStrings(0, 23);
        numericWheelAdapter.setLabel(context.getString(R.string.hour));
        hour.setViewAdapter(numericWheelAdapter);

        /**
         * 分钟的选择滚轮
         */
        String[] times = getMinutesStrings(0);
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(context, times);
        min.setViewAdapter(arrayWheelAdapter);
    }


    /***
     * 修改小时数组的成员变量
     *
     * @param start
     * @param end
     */
    private void setHourStrings(int start, int end) {
        hoursString.clear();
        for (int i = start; i <= end; i++) {
            if (i < 10) {
                hoursString.add("0" + i);
            } else {
                hoursString.add(i + "");
            }
        }

        if (hoursString.size() < 7) {//如果可选的小时数量小于7,则设置为不可循环滑动

            hour.setCyclic(false);
        } else {
            hour.setCyclic(true);
        }

        /***
         * 如果切换日期导致小时选择数组的体积有大变小,则将小时滚轮的选中位置归0
         */
        try {
            int hourSelection = hour.getCurrentItem();
            if (hourSelection >= hoursString.size()) {
                hour.setCurrentItem(0);
                initMinute(true);
                min.setCurrentItem(0);
            }


        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    /***
     * 获取分钟滚轮的数据源
     *
     * @return
     */
    private String[] getMinutesStrings(int index) {


        if (index > 5) {//如果超过了5,则返回空值
            return null;
        } else {
            String[] newMinuteStrings = new String[6 - index];
            minsString.clear();
            int length = newMinuteStrings.length;
            for (int i = 0; i < length; i++) {
                newMinuteStrings[i] = origanlMinuteStrings[i + index] + context.getString(R.string.minute);
                minsString.add(newMinuteStrings[i]);
            }

            try {
                int minSelection = min.getCurrentItem();
                if (minSelection >= minsString.size()) {
                    min.setCurrentItem(0);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

            return newMinuteStrings;
        }


    }


    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        /**选完之后的回调*/
        @Override
        public void onScrollingFinished(WheelView wheel) {

            /***
             * 逻辑:
             *
             * 1,先判断是不是"今天"这项:
             *  A:不是"今天"这项:
             *      直接获取时间即可
             *
             *  B:是"今天"这项:
             *      1,先重新判断是否该包含今天,因为当前时间可能已经过了23点50,
             *         过了:toast提示,关闭dialog
             *
             *         未过:1,先判断当前获取的日期滚轮是否是"今天"
             *                 是:重新构建时间数据源和分钟数据源:
             *                       时钟数据源:分钟是否过了50?-->是:小时数从当前小时数加1开始
             *                                                                    否:小时数从的当前小时开始
             *                       分钟数据源:是否选中当前小时-->是:分钟数据源去除已过的时间
             *                                                                    否:分钟全部可选
             *                 否:重新构建小时数据源和分钟数据源:
             *                       时钟数据源:全部可选
             *                       分钟数据源:全部可选
             *
             *
             */

            Calendar c = Calendar.getInstance();//日历实例

            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH) + 1;
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);//24进制的小时数
            mMinutes = c.get(Calendar.MINUTE);//分钟数


            /**判断是不是今天**/
            if (year.getCurrentItem() == 0 && month.getCurrentItem() + 1 == mMonth && day.getCurrentItem() + 1 == mDay) {
                mIsToday = true;
            } else {
                mIsToday = false;
            }


            if (!mIsToday) {//如果不是今天,则随便选

                initHour(0);
                initMinute(false);
            } else {//如果弹出的那一刻是可选今天的,但用户过了一段时间才选,而此时已经不可选择今天了,则弹出提示,并关掉弹出框

                if (mHour == 23 && mMinutes >= 50) {//已经过了23:50,今天不可选了


                    showToastMessage(context, context.getString(R.string.chose_tomorow_time));

                    DialogWholeDateTimePicker.this.dismiss();


                } else {//今天仍旧可以被选

                    //如果此时分钟范围[0,49]则小时的最小值为当前小时数,分钟待选数组为{"50"}
                    //如果此时分钟[50,59]则小时的最小值为当时时间加1,分钟待选数组为{"00","10","20","30","40","50"}
                    if (mMinutes < 50) {//此时当前小时还可以选
                        initHour(mHour);
                        //initMinute(true);

                        int hourSelection = hour.getCurrentItem();
                        if (hourSelection == 0) {//时钟也是当前的小时
                            initMinute(true);
                            if (minsString.size() == 1) {//如果分钟数组只有一个,则选中
                                min.setCurrentItem(0);//分钟滚轮归0
                            }

                        } else {
                            initMinute(false);
                        }

                    } else {//当前小时已经不可选

                        initHour(mHour + 1);
                        // initMinute(false);
                        initMinute(false);
                    }


                }


            }


            try {

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


        String dateString = year.getCurrentItem() + mYear + "-" + month.getCurrentItem() + 1 + "-" + day.getCurrentItem() + 1;


        String currentMinString = minsString.get(min.getCurrentItem());
        currentMinString = currentMinString.substring(0, 2);


        dateString = new StringBuilder()
                .append((year.getCurrentItem() + mYear))
                .append("-")
                .append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1))
                .append("-")
                .append(((day.getCurrentItem() + 1) < 10) ? "0" + (day.getCurrentItem() + 1) : (day.getCurrentItem() + 1))
                .toString();


        timeString = new StringBuilder()
                .append(dateString)//日
                .append(" ").append(hoursString.get(hour.getCurrentItem()))//时
                .append(":").append(currentMinString)//分
                .toString();


        try {


            timeDate = formatTime.parse(timeString);

            if (timeDate != null) {

                Log.i(TAG, formatTime.format(timeDate));

                //Toast.makeText(MainActivity.this, result+",long:"+dateResult.getTime(), Toast.LENGTH_SHORT).show();

                //Toast.makeText(context, formatTime.format(timeDate), Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    /**
     * 重置小时选择的滚轮
     *
     * @param start 从几点钟开始,24小时制,从0开始
     */
    private void initHour(int start) {

        NumericWheelAdapter numericWheelAdapter3 = new NumericWheelAdapter(context, start, 23, "%02d");
        setHourStrings(start, 23);
        numericWheelAdapter3.setLabel(context.getString(R.string.hour));
        hour.setViewAdapter(numericWheelAdapter3);
    }

    /**
     * 获取分钟的滚轮
     *
     * @param isToday 是不是今天的
     */
    private void initMinute(boolean isToday) {

        ArrayWheelAdapter<String> arrayWheelAdapter = null;
        /**
         * 分钟的选择滚轮
         */
        if (isToday) {
            String[] times = getMinutesStrings(getIndexOfMintutes());

            arrayWheelAdapter = new ArrayWheelAdapter<String>(context, times);
        } else {
            String[] times = getMinutesStrings(0);//从"00"开始全部要

            arrayWheelAdapter = new ArrayWheelAdapter<String>(context, times);
        }


        min.setViewAdapter(arrayWheelAdapter);
    }


    private void showToastMessage(Context context, String message) {
        ToastUtil.showCustomMessage(context, message);
    }

}
