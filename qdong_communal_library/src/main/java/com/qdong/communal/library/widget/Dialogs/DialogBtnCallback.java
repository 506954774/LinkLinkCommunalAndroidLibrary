package com.qdong.communal.library.widget.Dialogs;

/**
 * DialogBtnCallback
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/2/2  14:08
 */
public interface DialogBtnCallback {
    /**点击了"取消"**/
    void handleCancel();
    /**点击了确认*/
    void handleSubmit(Object object);
}
