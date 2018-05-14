package com.qdong.communal.library.widget.TimePicker;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.qdong.communal.library.R;
import com.qdong.communal.library.util.DateFormatUtil;
import com.qdong.communal.library.widget.TimePicker.timePicker.adapter.NumericWheelAdapter;
import com.qdong.communal.library.widget.TimePicker.timePicker.view.WheelView;

import java.util.Date;

/**
 * @author LHD
 * @className: PopWindowChooseWeek
 * @package com.qdong.communal.library.widget.TimePicker
 * @description: 周选择器
 * @date 2016/9/22 14:36
 */
public abstract class PopWindowChooseWeek extends PopupWindow implements View.OnClickListener {

    private View mMenuView;
    private final int WEEK_MAX = DateFormatUtil.getWeekOfYear(new Date()) - 1;//	最大周为当前周
    private final String YEAR_STRING = "周";
    private LayoutInflater inflater;
    private Context context;
    private Button cancelBtn;
    private Button commitBtn;

    private WheelView year;//年取滚轮控件
    LinearLayout ll;//父容器
    View view = null;

    public PopWindowChooseWeek(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popwindow_choose_year,
                null);
        cancelBtn = (Button) mMenuView.findViewById(R.id.btn_cancel);
        commitBtn = (Button) mMenuView.findViewById(R.id.btn_submmit);
        ll = (LinearLayout) mMenuView.findViewById(R.id.ll_pickers);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            this.dismiss();
        } else if (id == R.id.btn_submmit) {
            selectWeek(year.getCurrentItem() + 1);
            this.dismiss();
        }
    }

    private View getDataPick() {
        view = inflater.inflate(R.layout.wheel_year_picker, null);//获取视图
        year = (WheelView) view.findViewById(R.id.year);
        NumericWheelAdapter numericWheelAdapter1 = new NumericWheelAdapter(context, 1, WEEK_MAX);
        numericWheelAdapter1.setLabel(YEAR_STRING);
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(true);//是否可循环滑动
        year.setVisibleItems(7);//设置显示行数
        year.setCurrentItem(0);
        return view;
    }

    public abstract void selectWeek(int week);
}
