package com.qdong.communal.library.module.VersionManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.qdong.communal.library.R;


/**
 * 带按钮的提示dialog
 *
 * @author LHD
 *
 */
public class CustomPromptDialog extends BaseDialog {

	private EditText etInput;
	private TextView tvContent;

	public CustomPromptDialog(Activity activity) {
		super(activity, true);
		initView();
	}

	public CustomPromptDialog(Activity activity, boolean isShowConfrim) {
		super(activity, isShowConfrim);
		initView();
	}

	public CustomPromptDialog(Activity activity, boolean isShowConfrim, DialogCallback callback) {
		super(activity, isShowConfrim,callback);
		initView();
	}

	@SuppressLint("InflateParams")
	private void initView() {
		addContentView(LayoutInflater.from(activity).inflate(R.layout.custom_dialog, null));
		tvContent = (TextView) getView().findViewById(R.id.dialogText);
	}

	/**
	 * 加载输入view，设置字数限制
	 *
	 * @param inputLimit
	 */
	private void setInputLimit(int inputLimit) {
		etInput = (EditText) getView().findViewById(R.id.dialogInput);
		etInput.setVisibility(View.VISIBLE);
		etInput.setFilters(new InputFilter[] { new InputFilter.LengthFilter(inputLimit) });
	}

	/**
	 * 输入框提示信息
	 *
	 * @param hint
	 */
	public void setInputHint(String title, String hint, int inputLimit, String confirmBtn) {
		super.setPromptText(title, confirmBtn, activity.getString(R.string.cancel_));
		tvContent.setVisibility(View.GONE);
		setInputLimit(inputLimit);
		if (etInput != null) {
			etInput.setHint(hint);
			etInput.setMaxLines(2);
		}
	}

	public String getInputText() {
		return etInput.getText().toString().trim();
	}

	/**
	 *
	 * 设置标题和提示内容
	 *
	 * @param title
	 *            标题
	 * @param content
	 *            内容
	 * @param confirmBtn
	 *            确认按钮名称
	 */
	public void setPromptText(String title, String content, String confirmBtn) {
		tvContent.setText(content);
		super.setPromptText(title, confirmBtn,  activity.getString(R.string.cancel_));
	}
}