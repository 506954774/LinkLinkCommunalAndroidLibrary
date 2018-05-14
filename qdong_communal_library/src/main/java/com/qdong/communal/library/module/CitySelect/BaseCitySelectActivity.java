package com.qdong.communal.library.module.CitySelect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qdong.communal.library.R;
import com.qdong.communal.library.module.base.BaseActivity;
import com.qdong.communal.library.util.CharacterParser;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.PinyinComparator;
import com.qdong.communal.library.util.ToastUtil;
import com.qdong.communal.library.widget.ClearEditText;
import com.qdong.communal.library.widget.CustomMaskLayerView.CustomMaskLayerView;
import com.qdong.communal.library.widget.SideBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * BaseCitySelectActivity
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/10  14:59
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class BaseCitySelectActivity extends BaseActivity implements
        OnCitySelectedListener {

    private LinearLayout mLlTitleContainer;//titile的父容器
    private LinearLayout mLlTopContainer;//top的父容器
    private LinearLayout mLlBottomContainer;//bottom的父容器
    private ClearEditText mClearEditText;
    private TextView mTvCurrentCity;
    private TextView mTvFlash;
    private SideBar mSideBar;
    private ListView mListView;
    private CustomMaskLayerView mCustomMaskLayerView;
    private CityAdapter mAdapter;
    private Subscription mSubscription;

    private ArrayList<CityModel> mDatas = new ArrayList<CityModel>();


    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;
    private CityModel mCityModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_city_select);

        // 实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        initWedget();
        setListener();
        initData();

    }

    private void setListener() {

        // 搜索框输入值过滤监听
        mClearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 设置右侧触摸监听
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                if(getString(R.string.recenty_).equals(s)){//"近" 则滑到第一个
                    mListView.setSelection(0);
                }
                else if(getString(R.string.hot_).equals(s)) {//"热",滑动到第二个
                    mListView.setSelection(1);
                }
                else{//字母
                    // 该字母首次出现的位置
                    int position = mAdapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        int index=position+2;//把"近" 和"热"算上
                        if(index>=0&&index<mAdapter.getCount()){
                            mListView.setSelection(index);
                        }
                    }
                }

            }
        });

        /*findViewById(R.id.tv_current_city).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCitySelected(mCityModel);
            }
        });*/

    }

    private void setUpView() {
        mCustomMaskLayerView.dismiss();
        mListView.setAdapter(mAdapter);
    }



    private void initData() {

        /****
         * 子类如果自己提供了数据源,就用子类的,否则用父类的
         */
        mCustomMaskLayerView.showLoading(getString(R.string.is_loading));
        ArrayList<CityModel> citys = getCitys();
        if (citys == null || citys.size() == 0) {
            /**子线程中,从数据库获取城市数据***/
            getDataFromDB();

        } else {
            mDatas.addAll(citys);
            setUpView();
        }
    }

    /**
     * @method name:getDataFromDB
     * @des:子线程中处理:1,从数据库获取城市列表,2,给每个城市加上拼音索引,3,排序 A-Z
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/9/12
     * @author Chuck
     **/
    private void getDataFromDB() {

        mSubscription= Observable.create(new Observable.OnSubscribe<ArrayList<CityModel>>() {
                    @Override
                    public void call(Subscriber<? super ArrayList<CityModel>> subscriber) {
                        ArrayList<CityModel> list = getCitysFromDB();
                        subscriber.onNext(list);
                    }
                })
                .subscribeOn(Schedulers.io()) //指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread()) //指定 Subscriber 的回调发生在主线程
                .subscribe(new Subscriber<ArrayList<CityModel>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ArrayList<CityModel> data) {
                        //ToastUtil.showCustomMessage(BaseCitySelectActivity.this,"RXJAVA");
                        mDatas.addAll(data);
                        setUpView();

                        try {
                            if(mSubscription!=null){
                                mSubscription.unsubscribe();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initWedget() {
        mLlTitleContainer= (LinearLayout) findViewById(R.id.rl_photo_top);
        if(getTitleView()!=null){
            mLlTitleContainer.addView(getTitleView());
        }
        mLlTopContainer= (LinearLayout) findViewById(R.id.ll_top);
        if(getTopView()!=null){
            mLlTopContainer.addView(getTopView());
        }
        mLlBottomContainer= (LinearLayout) findViewById(R.id.ll_bottom);
        if(getBottomView()!=null){
            mLlBottomContainer.addView(getBottomView());
        }
        mClearEditText= (ClearEditText) findViewById(R.id.filter_edit);
        mTvCurrentCity= (TextView) findViewById(R.id.tv_current_city);
        mTvFlash= (TextView) findViewById(R.id.dialog);
        mSideBar= (SideBar) findViewById(R.id.sidrbar);
        mSideBar.setTextView(mTvFlash);
        mListView= (ListView) findViewById(R.id.listview);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mListView.setDividerHeight(0);
        mCustomMaskLayerView= (CustomMaskLayerView) findViewById(R.id.loading_view);

        mCityModel=getCurrentCity();
        if(mCityModel==null){
            mCityModel=new CityModel();
            mCityModel.setCity("深圳");
            mCityModel.setCode(CityUtil.getCityCode(this,mCityModel.getCity()));
        }
        mTvCurrentCity.setText(mCityModel.getCity());
        mTvCurrentCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   onCitySelected(mCityModel);
            }
        });

        mAdapter=new CityAdapter(this,mDatas,getCurrentCity(),this);


    }


    /**
     * @param :[]
     * @return type:java.util.ArrayList<com.qdong.communal.library.module.CitySelect.CityModel>
     * @method name:getCitysFromDB
     * @des:从数据库获取城市信息
     * @date 创建时间:2016/9/10
     * @author Chuck
     **/
    private ArrayList<CityModel> getCitysFromDB() {
        ArrayList<CityModel> citys = CityUtil.getCityList(this);
        for (CityModel currentBean : citys) {
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(currentBean.getCity());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 设置拼音首字母（正则表达式，判断首字母是否是英文字母）
            if (sortString.matches("[A-Z]")) {
                currentBean.setSortLetters(sortString.toUpperCase());
            } else {
                currentBean.setSortLetters("#");
            }
        }

        LogUtil.e("citys", citys.get(0));

        // 根据a-z进行排序源数据
        Collections.sort(citys, pinyinComparator);
        //ToastUtil.showCustomMessage(this, citys.get(0).toString());
        return citys;
    }


    /**
     * 输入关键字，查找本地好友并更新列表试图
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        ArrayList<CityModel> list = new ArrayList<CityModel>();
        if (TextUtils.isEmpty(filterStr)) {
            list = mDatas;
        } else {
            list.clear();// 清空原列表
            for (CityModel sortModel : mDatas) {
                String name = sortModel.getCity();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    list.add(sortModel);
                }
            }
        }
        // 根据a-z进行排序
        Collections.sort(list, pinyinComparator);
        mAdapter.refreshListView(list,list==mDatas);//第二个参数决定了 最近使用的城市和热门城市是否展示出来,true为展示
        mListView.setSelection(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(mSubscription!=null){
                if(!mSubscription.isUnsubscribed()){
                    mSubscription.unsubscribe();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        /**隐藏软键盘**/
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.showSoftInput(mClearEditText,InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(mClearEditText.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * @param :[]
     * @return type:java.util.ArrayList<com.qdong.communal.library.module.CitySelect.CityModel>
     * @method name:getCitys
     * @des:如果子类不重写这个方法,或者返回空,界面就会使用父类的城市数据源
     * @date 创建时间:2016/9/10
     * @author Chuck
     **/
    protected ArrayList<CityModel> getCitys() {
        return null;
    }


    /***
     * 子类提供topview
     **/
    protected  View getTopView(){return  null;};
    /***
     * 子类提供bottomview
     **/
    protected  View getBottomView(){return  null;};

    /***
     * 子类提供titleview,里面可以加回退和标题,取消
     **/
    public abstract View getTitleView();

    /***
     * 子类提供当前的已选的city
     **/
    public abstract CityModel getCurrentCity();



}
