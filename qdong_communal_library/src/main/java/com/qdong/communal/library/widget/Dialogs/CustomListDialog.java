package com.qdong.communal.library.widget.Dialogs;


import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.qdong.communal.library.R;
import com.qdong.communal.library.module.PhotoChoose.MyAdapter;

/**
 * 公共的带列表选项Dialog
 *
 * @param <T> 泛型
 * @author LHD
 * @Date 2016-06-14
 */
@SuppressLint("InflateParams")
public abstract class CustomListDialog<T> {

    private View view;
    private Dialog dialog;
    private Activity activity;
    private ListView lv;
    private MyAdapter<T> myAdapter;
    private View.OnClickListener listener;
    private TextView tvTitle, tvCancel, tvComfirm;

    public CustomListDialog(Activity activity) {
        this.activity = activity;
        initView();
        init();
    }

    /**
     * 加载界面
     */
    private void initView() {
        dialog = new Dialog(activity, R.style.MenuDialogStyle);
        view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_dev, null);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);// 位于底部
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        window.setLayout(w * 4 / 5, LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void init() {
        tvTitle = (TextView) view.findViewById(R.id.dialogTitle);
        tvCancel = (TextView) view.findViewById(R.id.dialogLeftBtn);
        tvComfirm = (TextView) view.findViewById(R.id.dialogRightBtn);
        lv = (ListView) view.findViewById(R.id.dailog_list);
        lv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        initListener();
    }

    private void initListener() {
        tvCancel.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                dialogDismiss();
            }
        });
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position, myAdapter.getItem(position));
                dialogDismiss();
            }
        });
        if (listener != null)
            tvComfirm.setOnClickListener(listener);
    }

    /**
     * 设置标题和提示内容
     *
     * @param title   标题
     * @param adapter 适配器
     */
    public void setPromptText(String title, MyAdapter<T> adapter) {
        tvTitle.setText(title);
        myAdapter = adapter;
        lv.setAdapter(myAdapter);
    }

    /**
     * 销毁dialog对象
     */
    public void dialogDismiss() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    /**
     * 判断dialog是否显示
     *
     * @return
     */
    public boolean isShowing() {
        if (dialog != null && dialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * 选择选项
     *
     * @param position 位置
     * @param t        对象
     */
    public abstract void selectItem(int position, T t);

    /**
     * 对确定按钮设置点击事件
     *
     * @param listener
     */
    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }
}
