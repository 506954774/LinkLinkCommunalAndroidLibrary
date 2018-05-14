package com.qdong.communal.library.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * CustomMoneyEditText
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/7/17  11:43
 * Copyright : 2014-2016 深圳趣动智能科技有限公司-版权所有
 **/
public class CustomMoneyEditText extends EditText implements TextWatcher {

    public CustomMoneyEditText(Context context) {
        super(context);
        init();
    }

    public CustomMoneyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomMoneyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private InputFilter inputFilter = new InputFilter() {


        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            // 删除等特殊字符，直接返回
            if (TextUtils.isEmpty(source)) {
                return null;
            }
            String dValue = dest.toString();
            String[] splitArray = dValue.split("\\.");
            if (splitArray.length > 1) {
                String dotValue = splitArray[1];
                // 2 表示输入框的小数位数
                int diff = dotValue.length() + 1 - 2;
                if (diff > 0) {
                    return source.subSequence(start, end - diff);
                }
            }
            return null;
        }
    };

    private void init() {
        //设置输入框允许输入的类型（正则）
        //对应的布局属性是--->android:digits="0123456789."
        setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        //设置输入字符
        setFilters(new InputFilter[]{inputFilter});
        addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //如果输入框为空则不处理
        if (TextUtils.isEmpty(s)) {
            return;
        }
        //第一个字符不为小数点
        if (s.length() == 1 && s.toString().equals(".")) {
            setText("");
            return;
        }
        int counter = counter(s.toString(), '.');
        if (counter > 1) {
            //小数点第一次出现的位置
            int index = s.toString().indexOf('.');
            setText(s.subSequence(0, index + 1));
        }
        setSelection(getText().toString().length());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 统计一个字符在字符串中出现的次数
     *
     * @param s 字符串
     * @param c 字符
     * @return 數量
     */
    public int counter(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
