package com.qdong.communal.library.widget.TimePicker;

import java.text.SimpleDateFormat;

/**
 * DateFormatType
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/11/12  14:39
 * Copyright : 趣动智能科技有限公司-版权所有
 */
public enum DateFormatType {

    YEAR(new SimpleDateFormat("yyyy")),
    YEAR_MONTH(new SimpleDateFormat("yyyy-MM")),
    YEAR_MONTH_DAY(new SimpleDateFormat("yyyy-MM-dd")),
    YEAR_MONTH_DAY_HOUR_MIN(new SimpleDateFormat("yyyy-MM-dd-HH-mm"));


    public SimpleDateFormat getmSimpleDateFormat() {
        return mSimpleDateFormat;
    }

    private final SimpleDateFormat mSimpleDateFormat;

    private DateFormatType(SimpleDateFormat value){
        mSimpleDateFormat = value;
    }

}
