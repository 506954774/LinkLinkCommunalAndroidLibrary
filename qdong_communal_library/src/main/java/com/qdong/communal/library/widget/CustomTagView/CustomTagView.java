package com.qdong.communal.library.widget.CustomTagView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdong.communal.library.R;
import com.qdong.communal.library.util.DensityUtil;
import com.qdong.communal.library.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * CustomTagListView
 * 自动换行的文本标签展示,选取 控件
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/2/1  11:40
 **/
public class CustomTagView extends LinearLayout {

    private Context mContext;
    private ArrayList<Tag> mTags;//字符串集合
    private HashMap<Integer, LinearLayout> mChildViews = new HashMap<Integer, LinearLayout>();//子布局map，key是行数，以0开始
    private int mRowIndex;//行索引
    private int mDriverWidth;//item间隔的宽度
    private int mWidth;//this的宽度像素,由外面来设定,"layout_width"
    private TextViewResoursProvider mTvResourseProvider;//item背景提供者


    /**
     * 以下属性可以从布局xml里配置,以下长度单位均为dp,但是不带"dp" 比如: mItemPadingTop="10"
     **/
    private int mLiearLayoutPaddingLeft;//每行的左边padding
    private int mLiearLayoutPaddingRight;//每行的右边padding
    private int mLiearLayoutPaddingTop;//每行的距离上面的间隔
    private int mLiearLayoutPaddingBottom;//每行的距离上面的间隔
    private int mItemPadingLeft;//每个条目的左padding
    private int mItemPadingTop;//每个条目的上padding
    private int mItemPadingRight;//每个条目的右padding
    private int mItemPadingBottom;//每个条目的下padding
    private int mItemHeight;//每个条目的高度
    private int mTextSize = 14;//字号
    private int mTextColorNormal = getResources().getColor(R.color.item_txt_color_normal);//未选中字色
    private int mTextColorChecked = getResources().getColor(R.color.item_txt_color_selected);//选中字色
    private int mItemBackGroundSelectorId=R.drawable.tag_background_selector;
    private Drawable mDrivers;//水平线性布局的间隔
    private boolean mIsClickable;//item可否点击,默认是不能点击的

    private boolean mIsOnlyOne;//是否只能选取1个
    private OnCheckChangedListener mOnCheckChangedListener;//listener


    private JudgeIsClickableProvide mJudgeIsClickableProvide;//按钮是否可以点击

    public JudgeIsClickableProvide getmJudgeIsClickableProvide() {
        return mJudgeIsClickableProvide;
    }

    public void setmJudgeIsClickableProvide(JudgeIsClickableProvide mJudgeIsClickableProvide) {
        this.mJudgeIsClickableProvide = mJudgeIsClickableProvide;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public CustomTagView(Context mContext, ArrayList<Tag> mTags, int width) {
        super(mContext);
        this.mContext = mContext;
        this.mTags = mTags;
        mWidth = width;
        resetView();
    }

    public OnCheckChangedListener getmOnCheckChangedListener() {
        return mOnCheckChangedListener;
    }

    public void setmOnCheckChangedListener(OnCheckChangedListener mOnCheckChangedListener) {
        this.mOnCheckChangedListener = mOnCheckChangedListener;
    }

    public ArrayList<Tag> getmTags() {
        return mTags;
    }


    public TextViewResoursProvider getmTvResourseProvider() {
        return mTvResourseProvider;
    }

    public void setmTvResourseProvider(TextViewResoursProvider mTvResourseProvider) {
        this.mTvResourseProvider = mTvResourseProvider;
    }

    public void setTagList(ArrayList<Tag> mTags) {
        this.mTags = mTags;
        resetView();
    }

    public void setStringList(ArrayList<String> strings) {
        this.mTags = initTagsFromStrings(strings);
        resetView();
    }

    /**
     * @param :[strings]
     * @return type:java.util.ArrayList<com.ztb.handneartech.bean.Tag>
     * @method name:initTagsFromStrings
     * @des:设置文本集合
     * @date 创建时间:2016/4/6
     * @author Chuck
     **/
    private ArrayList<Tag> initTagsFromStrings(ArrayList<String> strings) {

        ArrayList<Tag> tags = new ArrayList<Tag>();
        if (strings == null || strings.size() == 0) {
            return tags;
        }
        for (int i = 0; i < strings.size(); i++) {
            Tag bean = new Tag();
            bean.setName(strings.get(i));
            tags.add(bean);
        }
        return tags;
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:resetView
     * @des:刷新控件
     * @date 创建时间:2016/4/6
     * @author Chuck
     **/
    private void resetView() {
        mRowIndex = 0;
        mChildViews.clear();
        this.removeAllViews();
        initView();
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:refresh
     * @des:刷新
     * @date 创建时间:2016/4/6
     * @author Chuck
     **/
    public void refresh() {

        resetView();
    }

    public CustomTagView(AttributeSet attrs, Context mContext, ArrayList<Tag> mTags, int width) {
        super(mContext, attrs);
        this.mContext = mContext;
        this.mTags = mTags;
        mWidth = width;
        initView();
    }

    public CustomTagView(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        this.mContext = mContext;

        TypedArray types = mContext.obtainStyledAttributes(attrs, R.styleable.TagsListView);

        mLiearLayoutPaddingLeft = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mLiearLayoutPaddingLeft, 0));
        mLiearLayoutPaddingRight = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mLiearLayoutPaddingRight, 0));
        mLiearLayoutPaddingTop = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mLiearLayoutPaddingTop, 0));
        mLiearLayoutPaddingBottom = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mLiearLayoutPaddingBottom, 10));

        mItemPadingLeft = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mItemPadingLeft, 10));
        mItemPadingTop = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mItemPadingTop, 2));
        mItemPadingRight = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mItemPadingRight, 10));
        mItemPadingBottom = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mItemPadingBottom, 2));
        mItemHeight = DensityUtil.dp2px(mContext, types.getInteger(R.styleable.TagsListView_mItemHeight, 30));

        mTextSize = types.getInteger(R.styleable.TagsListView_mTextSize, 14);

        mMaxChooseCount = types.getInteger(R.styleable.TagsListView_mMaxChooseCount, 1);


        mTextColorNormal = types.getColor(R.styleable.TagsListView_mTextColorNormal, getResources().getColor(R.color.item_txt_color_normal));
        mTextColorChecked = types.getColor(R.styleable.TagsListView_mTextColorChecked, getResources().getColor(R.color.item_txt_color_selected));
        mItemBackGroundSelectorId = types.getResourceId(R.styleable.TagsListView_mItemBackGroundSelector,R.drawable.tag_background_selector);
        mDrivers = types.getDrawable(R.styleable.TagsListView_mDrivers);
        mIsClickable = types.getBoolean(R.styleable.TagsListView_mIsClickable, false);
        mIsOnlyOne = types.getBoolean(R.styleable.TagsListView_mIsOnlyOne, false);

        /**不要共用一个选择器,否则部分手机背景显示会有异常 by:CHUCK**/
        //mDeafaultTagBackgroundSelector=getResources().getDrawable(R.drawable.tag_background_selector);

        initWidth(mContext, attrs);

    }

    /**
     * @param :[mContext, attrs]
     * @return type:void
     * @method name:initWidth
     * @des:获取控件的宽度
     * @date 创建时间:2016/2/18
     * @author Chuck
     **/
    private void initWidth(Context mContext, AttributeSet attrs) {


        String w = "";//获取xml里面配置的宽度
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            if ("layout_width".equals(attrs.getAttributeName(i))) {
                w = attrs.getAttributeValue(i);
            }
        }

        if (!TextUtils.isEmpty(w)) {
            if (w.endsWith("p")) {//以dp配置的话,转换为像素
                try {
                    String dp = w.substring(0, w.indexOf("."));
                    int d = Integer.valueOf(dp);
                    mWidth = DensityUtil.dp2px(mContext, d);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (w.endsWith("x")) {//以px配置的话,直接赋值为mWidth
                try {
                    String dp = w.substring(0, w.indexOf("."));
                    mWidth = Integer.valueOf(dp);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (w.startsWith("@")) {//dimens 配置的
                mWidth = getResources().getDimensionPixelSize(Integer.parseInt(w.substring(1)));
            } else {//wrapcotent 或者 matchparent

                //实际使用中不可以使用wrapcontent.可以使用matchParent
                //用matchparent则什么也不做.让视图监听器自动监听绘制完毕获取宽度

            }
        }
    }

    public CustomTagView(AttributeSet attrs, int defStyle, Context mContext, ArrayList<Tag> mTags, int width) {
        super(mContext, attrs, defStyle);
        this.mContext = mContext;
        this.mTags = mTags;
        mWidth = width;
        initView();
    }

    /**
     * @param :[mWidth]
     * @return type:void
     * @method name:setmWidth
     * @des:设置换行的最大长度
     * @date 创建时间:2016/2/2
     * @author Chuck
     **/
    public void setmWidth(int mWidth) {
        if (mWidth > 0) {
            LayoutParams lp = (LayoutParams) this.getLayoutParams();
            lp.width = mWidth;
            this.mWidth = mWidth;

            postInvalidate();
            resetView();
        }

    }

    private int mMaxChooseCount = 1;//最大值

    public int getmMaxChooseCount() {
        return mMaxChooseCount;
    }

    public void setmMaxChooseCount(int mMaxChooseCount) {
        this.mMaxChooseCount = mMaxChooseCount;
        resetView();
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!mIsClickable) {
                return;
            }

            //这个接口,起提示作用,不同实现有不同提示
            if(mJudgeIsClickableProvide!=null){
                if(!mJudgeIsClickableProvide.isClickable()){
                    return;
                }
            }

            Integer index = (Integer) v.getTag();
            if (mIsOnlyOne) {
                clearCheck();
                boolean isChecked = mTags.get(index).isChecked();
                mTags.get(index).setIsChecked(!isChecked);
                resetView();
                if (mOnCheckChangedListener != null) {
                    mOnCheckChangedListener.onCheckedChanged(getTagResult());
                }
                return;
            }
            if (index >= 0 && index < mTags.size()) {
                boolean isChecked = mTags.get(index).isChecked();

                if (getResult() != null && getResult().size() == mMaxChooseCount && !isChecked) {//选择超过限制了
                    if (mMaxChooseCount != 1) {//单选不提示
                        ToastUtil.showCustomMessage(mContext, "最多选取" + mMaxChooseCount + "个!");
                    }
                    return;
                }

                mTags.get(index).setIsChecked(!isChecked);
                resetView();
                /**外界调用者提供接口处理回调**/
                if (mOnCheckChangedListener != null) {
                    mOnCheckChangedListener.onCheckedChanged(getTagResult());
                }
            }


        }
    };

    /**
     * 清除选中状态
     */
    public void clearCheck() {
        for (int i = 0; i < mTags.size(); i++) {
            mTags.get(i).setIsChecked(false);
            resetView();
        }
    }


    /**
     * @param :[]
     * @return type:void
     * @method name:initView
     * @des:初始化控件
     * @date 创建时间:2016/2/1
     * @author Chuck
     **/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {

        setOrientation(VERTICAL);

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                int height = getMeasuredHeight();
                int width = getMeasuredWidth();
                //ToastUtil.showCustomMessage(mContext, width + "");
                mWidth = width;//赋值

                mRowIndex = 0;
                mChildViews.clear();
                CustomTagView.this.removeAllViews();
                drawItems();
                return true;
            }
        });

        drawItems();


    }

    /**
     * @param :[]
     * @return type:void
     * @method name:drawItems
     * @des:绘制item
     * @date 创建时间:2016/2/18
     * @author Chuck
     **/
    private void drawItems() {

        if (mTags == null || mTags.size() == 0) {
            mTags = new ArrayList<Tag>();
        }

        if (mDrivers == null) {
            mDrivers = mContext.getResources().getDrawable(R.drawable.shape_liearlayout_driver);
            //mDriverWidth=mDrivers.getMinimumWidth();
        }
        if (mDriverWidth == 0) {
            if (mDrivers != null) {
                mDriverWidth = mDrivers.getMinimumWidth();
            }
        }

        for (int i = 0; i < mTags.size(); i++) {

            if (mChildViews.get(mRowIndex) == null) {//如果这行没有子布局

                addTextviewInNewLine(i);

            } else {//如果之前就有了

                //先计算当前的子LiearLayout的宽度+当前这个textview的宽度 是否 超出this的宽度，超了则再add一个子线性布局
                LinearLayout childView = (LinearLayout) this.getChildAt(mRowIndex);
                TextView tv = getTextView();
                if (childViewWidthIsFull(childView, this, tv, mTags.get(i).getName())) {//放得下，则直接添加
                    setUpTextView(i, tv);
                    childView.addView(tv);
                } else {//需要换行
                    mRowIndex++;
                    addTextviewInNewLine(i);
                }

            }
        }

        invalidate();
    }

    /**
     * @param :[i, tv]
     * @return type:void
     * @method name:setBackground
     * @des:加背景色
     * @date 创建时间:2016/2/1
     * @author Chuck
     **/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackground(int i, TextView tv) {
        if (i < 0 || i >= mTags.size()) {
            return;
        }

        tv.setTag(i);
        boolean isChecked = mTags.get(i).isChecked();

        tv.setSelected(isChecked);
        tv.setTextColor(isChecked ? mTextColorChecked : mTextColorNormal);



       /* if(mTvResourseProvider==null){//外界没有提供textview资源设置接口,则按xml里面配置的来給值

            *//**安卓16 以上的设备**//*
            boolean HEIGH_VERSION = Integer.valueOf(Build.VERSION.SDK).intValue() > Build.VERSION_CODES.JELLY_BEAN;
            if(HEIGH_VERSION){
                tv.setBackground(isChecked?mItemBackGroundDrawableChecked:mItemBackGroundDrawableNormal);
                tv.setTextColor(isChecked ? mTextColorChecked : mTextColorNormal);
            }
            else {
                tv.setBackgroundResource(isChecked?R.drawable.shape_round_btn_pressed:R.drawable.shape_round_btn_normal);
                tv.setTextColor(isChecked?mTextColorChecked:mTextColorNormal);
            }

        }
        else {//外界有提供textveiw资源接口,则按接口来給值
            *//**安卓16 以上的设备**//*
            boolean HEIGH_VERSION = Integer.valueOf(Build.VERSION.SDK).intValue() > Build.VERSION_CODES.JELLY_BEAN;
            if(HEIGH_VERSION){
                tv.setBackground(mTvResourseProvider.getBackground(mTags.get(i).getName(), isChecked));
                tv.setTextColor(mTvResourseProvider.getTextColor(mTags.get(i).getName(), isChecked));
            }
            else {
                tv.setBackgroundResource(isChecked?R.drawable.shape_round_btn_pressed:R.drawable.shape_round_btn_normal);
                tv.setTextColor(isChecked?mTextColorChecked:mTextColorNormal);
            }

        }*/

    }

    /**
     * @param :[i]
     * @return type:void
     * @method name:addTextviewInNewLine
     * @des:换行添加textview
     * @date 创建时间:2016/2/1
     * @author Chuck
     **/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addTextviewInNewLine(int i) {

        LinearLayout childLiearLayout = new LinearLayout(mContext);
        childLiearLayout.setShowDividers(SHOW_DIVIDER_MIDDLE);
        childLiearLayout.setPadding(mLiearLayoutPaddingLeft, mLiearLayoutPaddingTop, mLiearLayoutPaddingRight, mLiearLayoutPaddingBottom);
        childLiearLayout.setDividerDrawable(mDrivers);
        childLiearLayout.setOrientation(HORIZONTAL);//它是横向的
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        childLiearLayout.setLayoutParams(lp);
        mChildViews.put(mRowIndex, childLiearLayout);

        //那么此时是这行的第一个Textview
        TextView tv = getTextView();
        setUpTextView(i, tv);

        childLiearLayout.addView(tv);
        this.addView(childLiearLayout);
    }

    /**
     * @param :[i, tv]
     * @return type:void
     * @method name:setUpTextView
     * @des:设置tag的文本 背景
     * @date 创建时间:2016/4/6
     * @author Chuck
     **/
    private void setUpTextView(int i, TextView tv) {

        tv.setText(mTags.get(i).getName());
        setBackground(i, tv);
        tv.setPadding(mItemPadingLeft, mItemPadingTop, mItemPadingRight, mItemPadingBottom);
        tv.setSingleLine();
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setOnClickListener(mClickListener);

    }

    /**
     * @param :[childView]  子view
     * @param :[parentView] 父view
     * @param :[tv]         文本控件
     * @param :[name]       当前要加的文本串
     * @return type:boolean 返回true，表示不用换行  false表示需要换行
     * @method name:childViewWidthIsFull
     * @des:计算当前子线性布局加当前文本的总宽度是否超过this的宽度
     * @date 创建时间:2016/2/1
     * @author Chuck
     **/
    private boolean childViewWidthIsFull(LinearLayout childView, LinearLayout parentView, TextView tv, String name) {
        if (childView == null || parentView == null || TextUtils.isEmpty(name)) {
            return false;
        } else {
            int childWidth = calculateChildViewLength(childView);
            int textLength = (int) tv.getPaint().measureText(name) + mItemPadingLeft + mItemPadingRight;
            int parentWidth = mWidth;

            if (childWidth + textLength >= parentWidth) {//超过了父布局的宽度，则返回false
                return false;
            } else {//返回true
                return true;
            }
        }

    }

    /**
     * @param :[childView]
     * @return type:int
     * @method name:calculateChildViewLength
     * @des:计算子view的宽度 , 通过textview的paint的meatrueText来计算
     * @date 创建时间:2016/2/1
     * @author Chuck
     **/
    private int calculateChildViewLength(LinearLayout childView) {
        int result = 0;
        int count = childView.getChildCount();
        if (childView == null || count == 0) {
            return result;
        } else {
            for (int i = 0; i < count; i++) {
                View view = childView.getChildAt(i);
                if (view instanceof TextView) {
                    TextView tv = (TextView) view;
                    result += tv.getPaint().measureText(tv.getText().toString())//文本长度
                            + mDriverWidth//item间隔
                            + mItemPadingLeft//textview的左padding
                            + mItemPadingRight//textview的右padding
                    ;

                }
            }
        }

        result += mLiearLayoutPaddingLeft + mLiearLayoutPaddingRight;

        return result;
    }

    /**
     * @param :[]
     * @return type:android.widget.TextView
     * @method name:getTextView
     * @des:构造tag
     * @date 创建时间:2016/4/6
     * @author Chuck
     **/
    private TextView getTextView() {

        TextView tv = new TextView(mContext);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, mItemHeight));

        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
        tv.setGravity(Gravity.CENTER);
        //tv.setBackground(getResources().getDrawable(R.drawable.tag_background_selector));
        //tv.setBackground(mItemBackGroundSelector);
        tv.setBackground(getResources().getDrawable(mItemBackGroundSelectorId));
        tv.setPadding(mItemPadingLeft, mItemPadingTop, mItemPadingRight, mItemPadingBottom);
        tv.setSingleLine();
        tv.setEllipsize(TextUtils.TruncateAt.END);

        return tv;
    }


    /**
     * @param :[]
     * @return type:java.util.ArrayList<java.lang.String>
     * @method name:getResult
     * @des:供外界获取选取结果
     * @date 创建时间:2016/2/2
     * @author Chuck
     **/
    public ArrayList<String> getResult() {

        ArrayList<String> result = new ArrayList<String>();
        if (mTags == null || mTags.size() == 0) {
            return result;
        }
        for (int i = 0; i < mTags.size(); i++) {
            Tag bean = mTags.get(i);
            if (bean != null && bean.isChecked()) {
                result.add(bean.getName());
            }
        }
        return result;
    }

    /**
     * @param :[]
     * @return type:java.util.ArrayList<Tag>
     * @method name:getTagResult
     * @des:供外界获取选取结果
     * @date 创建时间:2016/2/2
     * @author Chuck
     **/
    public ArrayList<Tag> getTagResult() {

        ArrayList<Tag> result = new ArrayList<Tag>();
        if (mTags == null || mTags.size() == 0) {
            return result;
        }
        for (int i = 0; i < mTags.size(); i++) {
            Tag bean = mTags.get(i);
            if (bean != null && bean.isChecked()) {
                result.add(bean);
            }
        }
        return result;
    }

    /**
     * JudgementProvide
     * 按钮是否可以点击
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2017/1/29  12:31
     * Copyright : 2014-2015 深圳趣动只能科技有限公司-版权所有
     **/
    public interface JudgeIsClickableProvide{
        boolean isClickable();
    }

}

