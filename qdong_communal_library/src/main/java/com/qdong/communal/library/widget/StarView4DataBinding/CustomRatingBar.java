package com.qdong.communal.library.widget.StarView4DataBinding;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;

/**
 * CustomRatingBar
 * 这个类,完全是因为之前的StarEvaluteView里使用系统的RatingBar,并使用的setTag的方法,而DataBind这个框架
 * 在解析xml时会递归每一个子控件,也用到了getTag.于是冲突报错,所以,此处加一个int属性,不再setTag,直接赋值给成员变量
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/24  20:53
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class CustomRatingBar extends RatingBar {

    int mTag;

    public int getmTag() {
        return mTag;
    }

    public void setmTag(int mTag) {
        this.mTag = mTag;
    }

    public CustomRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRatingBar(Context context) {
        super(context);
    }
}
