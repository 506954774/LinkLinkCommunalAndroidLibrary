package com.qdong.communal.library.widget.CustomTagView;

import android.graphics.drawable.Drawable;

/**
 * TextViewBackgroundUpdater
 * 在不同状况下,提供textview不同的资源
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/2/2  11:43
 */
public interface TextViewResoursProvider {
    Drawable getBackground(Object key, boolean isChecked);
    int getTextColor(Object key, boolean isChecked);
}
