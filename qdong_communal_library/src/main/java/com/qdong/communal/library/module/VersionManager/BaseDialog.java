package com.qdong.communal.library.module.VersionManager;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdong.communal.library.R;


/**
 * Dialog基类
 *
 * @author LHD
 * @Date 2016-06-17 14:54
 *
 */
@SuppressLint("InflateParams")
public class BaseDialog {

	public Dialog dialog;
	public Activity activity;
	protected DialogCallback callback;

	private View btnLine, bottomLine, view;
	private LinearLayout llContentView;
	private TextView tvTitle, tvCancel, tvConfrim;

	private boolean isShowConfrim;

	public BaseDialog(Activity activity, boolean isShowConfrim) {
		this.activity = activity;
		this.isShowConfrim = isShowConfrim;
		init();
	}

	public BaseDialog(Activity activity, boolean isShowConfrim, DialogCallback callback) {
		this.callback=callback;
		this.activity = activity;
		this.isShowConfrim = isShowConfrim;
		init();
	}

	private void init() {
		dialog = new Dialog(activity, R.style.MenuDialogStyle);
		dialog.setCancelable(false);
		View view = LayoutInflater.from(activity).inflate(R.layout.dialog_base, null);
		initView(view);
		initListener();
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		window.setGravity(Gravity.CENTER);// 位于底部
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels;
		window.setLayout(w * 5 / 6, LayoutParams.WRAP_CONTENT);
		dialog.show();
	}

	private void initView(View view) {
		btnLine = view.findViewById(R.id.dialogBtnLine);
		bottomLine = view.findViewById(R.id.dialogBottomLine);
		llContentView = (LinearLayout) view.findViewById(R.id.dialogContentView);
		tvTitle = (TextView) view.findViewById(R.id.dialogTitle);
		tvCancel = (TextView) view.findViewById(R.id.dialogLeftBtn);
		tvConfrim = (TextView) view.findViewById(R.id.dialogRightBtn);
		if (!isShowConfrim) {
			btnLine.setVisibility(View.GONE);
			bottomLine.setVisibility(View.GONE);
			tvConfrim.setVisibility(View.GONE);
		}
	}

	public void setCancelable(boolean cancle) {
		dialog.setCancelable(cancle);
	}

	private void initListener() {
		tvCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogDismiss();
				if (callback != null)
					callback.onCancel();
			}
		});
		tvConfrim.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogDismiss();
				if (callback != null)
					callback.onConfirm();
			}
		});
	}

	/**
	 *
	 * 设置标题和提示内容
	 *
	 * @param title
	 *            标题
	 * @param confrim
	 *            确认按钮名称
	 * @param cancel
	 *            取消按钮名称
	 */
	public void setPromptText(String title, String confrim, String cancel) {
		tvTitle.setText(title);
		tvConfrim.setText(confrim);
		tvCancel.setText(cancel);
	}

	public TextView getConfirm() {
		return tvConfrim;
	}

	public TextView getCancel() {
		return tvCancel;
	}

	public void addContentView(View view) {
		this.view = view;
		llContentView.addView(view);
	}

	public View getView() {
		return view;
	}

	/**
	 * 设置响应事件
	 *
	 * @param callback
	 */
	public void setDialogCallback(DialogCallback callback) {
		this.callback = callback;
	}

	/**
	 * 销毁dialog对象
	 */
	public void dialogDismiss() {
		if (dialog != null) {
			if (dialog.isShowing())
				dialog.dismiss();
			dialog = null;
		}
	}

	/**
	 * 判断dialog是否显示
	 *
	 * @return
	 */
	public boolean isShowing() {
		if (dialog != null && dialog.isShowing())
			return true;
		return false;
	}
}
