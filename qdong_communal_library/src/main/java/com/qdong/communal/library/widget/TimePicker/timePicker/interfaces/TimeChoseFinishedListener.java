package com.qdong.communal.library.widget.TimePicker.timePicker.interfaces;

import java.util.Date;

/**
 * TimeChoseFinishedListener
 * 对外提供的接口,处理回调
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/1/30  16:33
 * Copyright : 2014-2015 深圳掌通宝科技有限公司-版权所有
 */
public interface TimeChoseFinishedListener {
    public void handleTimeStringAndDate(String timeString, Date timeDate);
    public void handleCancle();
}
