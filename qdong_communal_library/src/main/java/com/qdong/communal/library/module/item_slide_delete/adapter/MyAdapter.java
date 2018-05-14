package com.qdong.communal.library.module.item_slide_delete.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.qdong.communal.library.R;
import com.qdong.communal.library.module.item_slide_delete.SlidingButtonView;
import com.qdong.communal.library.util.DensityUtil;
import com.qdong.communal.library.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangzn on 17/1/1.
 */

public class MyAdapter extends RecyclerView.Adapter implements SlidingButtonView.IonSlidingButtonListener {

    Context context;
    private List<String> mDatas = new ArrayList<String>();

    private IonSlidingViewClickListener mIDeleteBtnClickListener;

    private SlidingButtonView mMenu = null;

    public MyAdapter(Context context, ArrayList<String> date) {
        this.context = context;
        this.mDatas = date;
        mIDeleteBtnClickListener = (IonSlidingViewClickListener) context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false);
        return new MyViewHolder(view);
    }

    boolean allopen = false;

    public void setAllopen(boolean allopen) {
        this.allopen = allopen;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.slidingButtonView.setSlidingButtonListener(MyAdapter.this);

        viewHolder.textView.setText(mDatas.get(position));

        //设置内容布局的宽为屏幕宽度
        viewHolder.layout_content.getLayoutParams().width = DensityUtil.getScreenWidth(context) + viewHolder.rl_left.getLayoutParams().width;
//        viewHolder.textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //判断是否有删除菜单打开
////                if (menuIsOpen()) {
////                    closeMenu();//关闭菜单
////                } else {
////                    int n = viewHolder.getLayoutPosition();
////                    mIDeleteBtnClickListener.onItemClick(v, n);
////                }
//
//            }
//        });

        viewHolder.btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = holder.getLayoutPosition();
                mIDeleteBtnClickListener.onDeleteBtnCilck(v, n);
            }
        });
        LogUtil.d("","项：" + position + "是否开:" + allopen);
        if (allopen) {
            LogUtil.d("","打开？");
            viewHolder.slidingButtonView.openMenu();
            viewHolder.slidingButtonView.setCanTouch(false);
        } else {
            viewHolder.slidingButtonView.closeMenu();
            viewHolder.slidingButtonView.setCanTouch(true);
        }

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 删除菜单打开信息接收
     */
    @Override
    public void onMenuIsOpen(View view) {
        mMenu = (SlidingButtonView) view;
    }

    /**
     * 滑动或者点击了Item监听
     *
     * @param slidingButtonView
     */
    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if (menuIsOpen()) {
            if (mMenu != slidingButtonView) {
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        mMenu.closeMenu();
        mMenu = null;

    }

    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if (mMenu != null) {
            return true;
        }
        return false;
    }

    public interface IonSlidingViewClickListener {
        void onItemClick(View view, int position);

        void onDeleteBtnCilck(View view, int position);
    }

    public void addData(int position) {
        mDatas.add(position, "添加项");
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);

    }

}
