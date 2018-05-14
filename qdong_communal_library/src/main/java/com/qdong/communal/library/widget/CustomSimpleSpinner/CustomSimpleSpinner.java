package com.qdong.communal.library.widget.CustomSimpleSpinner;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.qdong.communal.library.R;
import com.qdong.communal.library.widget.CustomTagView.Tag;


import java.util.ArrayList;

/**
 *CustomSimpleSpinner
 * 下拉选择
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/3/9  17:47
 *  * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class CustomSimpleSpinner extends PopupWindow {

    private ArrayList<Tag> mDatas;
    private OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private SortAdapter mZoneAdapater;


    /**
     * 常量
     */
    private static final String TAG ="PopWindowChooseYear";
    private View mPopView;
    private ListView mListZone;


    public CustomSimpleSpinner(Context mContext, ArrayList<Tag> mDatas , OnItemClickListener mOnItemClickListener ) {
        super(mContext);
        this.mContext=mContext;
        this.mDatas=mDatas==null?new ArrayList<Tag>():mDatas;
        this.mOnItemClickListener=mOnItemClickListener;
        initView();
    }

    private void initView() {

       mPopView = LayoutInflater.from(mContext).inflate(
                R.layout.custom_simple_pop, null);
        mListZone = (ListView) mPopView.findViewById(R.id.lv_zone);

        mZoneAdapater=new SortAdapter(mContext,mDatas);
        mListZone.setAdapter(mZoneAdapater);

        resetListViewHeight();

        setListener();

        // 设置SelectPicPopupWindow的View
        this.setContentView(mPopView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果

        /** 避免对整体窗口采用动画,分别对两个按钮采取动画 **/
        this.setAnimationStyle(R.style.popwin_anim_style);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        //showAsDropDown
    }

    /**
     * @method name:resetListViewHeight
     * @des:重置listview高度,最多展示5条,多余5条则滑动
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/9/23
     * @author Chuck
     **/
    private void resetListViewHeight() {

        /**只让listview最高为5条item的高度***/
        RelativeLayout.LayoutParams params;
        boolean heightFlag=mDatas.size()<5;
        if (heightFlag
                && mListZone.getLayoutParams().height != RelativeLayout.LayoutParams.WRAP_CONTENT) {
            // 数据小于5条高度为包裹内容
            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mListZone.setLayoutParams(params);
            mListZone.requestLayout();
            mListZone.invalidate();
        } else if (!heightFlag
                && mListZone.getLayoutParams().height == RelativeLayout.LayoutParams.WRAP_CONTENT) {
            // 数据大于等于5条 高度为固定5条宽度
            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, mContext.getResources()
                    .getDimensionPixelSize(
                            R.dimen.pop_top_listview_item_height));
            mListZone.setLayoutParams(params);
            mListZone.requestLayout();
            mListZone.invalidate();
        }

        mZoneAdapater.notifyDataSetChanged();
    }

    /**
     * 按钮加点击监听
     */
    private void setListener() {

        mListZone.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });

        /**下面被点击时,弹窗消失*/
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mPopView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                int height = mPopView.findViewById(R.id.rl_devide).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y > height) {//触碰到了下面,则dismiss

                           dismiss();

                    }
                }
                return true;
            }
        });

    }




}
