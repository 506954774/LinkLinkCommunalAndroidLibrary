package com.qdong.communal.library.module.VersionManager;

/**
 * 自定义弹窗响应时间接口
 *
 * @author LHD
 * @Date 2016-05-25 下午14:15
 *
 */
public interface DialogCallback {

	/**
	 * 取消按钮回调
	 */
	void onCancel();

	/**
	 * 确认按钮回调
	 */
	void onConfirm();
}
