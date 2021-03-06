package com.qdong.communal.library.module.BaseRefreshableListFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.google.gson.JsonElement;
import com.qdong.communal.library.R;
import com.qdong.communal.library.module.network.QDongApi;
import com.qdong.communal.library.module.network.QDongNetInfo;
import com.qdong.communal.library.module.network.RetrofitAPIManager;
import com.qdong.communal.library.module.network.RxHelper;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.NetworkUtil;
import com.qdong.communal.library.widget.CustomMaskLayerView.CustomMaskLayerView;
import com.qdong.communal.library.widget.CustomMaskLayerView.ReloadCallback;
import com.qdong.communal.library.widget.RefreshRecyclerView.RefreshRecyclerView;
import com.qdong.communal.library.widget.RefreshRecyclerView.itemDecoration.DividerItemDecoration;
import com.qdong.communal.library.widget.RefreshRecyclerView.listener.OnBothRefreshListener;
import com.qdong.communal.library.widget.RefreshRecyclerView.listener.OnLoadMoreListener;
import com.qdong.communal.library.widget.RefreshRecyclerView.listener.OnPullDownListener;
import com.qdong.communal.library.widget.RefreshRecyclerView.manager.RecyclerMode;
import com.qdong.communal.library.widget.RefreshRecyclerView.manager.RecyclerViewManager;
import com.qdong.communal.library.widget.RefreshRecyclerView.manager.RefreshRecyclerAdapterManager;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * BaseRefreshableListFragment
 * 类的描述:
 * 可刷新列表类的Fragment的基类,网络请求事件使用retrofit和RxJava实现.泛型是列表数据模型.适用场景:以get请求获取数据的可下拉刷新,上拉加载的界面.数据载体控件为RecyclerView
 * <p/>
 * 4个抽象方法,分别是:
 * {@link #getBaseUrl()}:   子类提供请求的url,注意,是baseurl
 * {@link #callApi(QDongApi, int, int)}: 子类通过retrofit框架里面的方法调用接口,返回一个Observable接口实例
 * {@link #refreshData()}:  子类解析数据,
 * {@link #initAdapter()} :子类提供适配器,
 * <p/>
 * 子类还可以重写
 * {@link #getRecyclerViewItemAnimator()} :设置item插入,删除动画,默认是系统提供的
 * {@link #getRecyclerViewItemDecoration()}:设置item分割线,默认为横向的黑线
 * {@link #getRecyclerViewHeadView()}:设置recyclerView的headview,因为系统的recyclerView是不提供headview的,
 * {@link #getRecyclerViewFootView()} :设置footview
 * {@link #getRecyclerViewPullMode()} :设置刷新开关:NONE,BOTH,TOP,BOTTOM. 父类默认是BOTH
 * {@link #getRecyclerViewLayoutManager()}:设置layoutManager, 父类默认是垂直的list
 * {@link #resetAutoCancelLoadMore()}:当界面数据源的size小于或等于pageSize时,此方法的返回值决定了此时会不会触发"上拉加载更多"的事件
 * {@link #getFragmentTitleView()}:子类在listview的上面布置view
 * {@link #onInitDataResult(boolean)}:首次加载的结果回调,
 * {@link #onRefreshDataResult(boolean)}:刷新的结果回调,
 * {@link #onLoadMoreDataResult(boolean)}:加载更多的结果回调,
 * {@link #isShowLoadingFirstTime()} :首次加载时,展不展示loading画面
 * {@link #onInitNoNetWork()} :首次加载就没有网络
 * <p/>
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/20  18:24
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class BaseRefreshableListFragment<T> extends Fragment {

    /***************************************
     * Constant
     ****************************************/
    protected static final String TAG = "BaseRefreshableListFragment";
    protected static final int REQUEST_TYPR_FIRST_TIME_LOAD = 0; // 初始化数据的加载
    protected static final int REQUEST_TYPR_REFRESH = 0X258; // 是下拉刷新
    protected static final int REQUEST_TYPR_LOAD_MORE = 0X369; // 是上拉加载
    protected static final int DEFAULT_FIRST_PAGE_NUM = 0;
    protected static final int DEFAULT_MAX_PAGE_SIZE = 20;


    /******************************************
     * Widget
     *******************************************/
    protected RefreshRecyclerView mRefreshRecyclerView;
    protected CustomMaskLayerView loadingView;//loadingView
    protected View mContentView;
    protected LinearLayout mLlTitle;
    protected LinearLayout mLlBottom;
    protected LinearLayout mLlFloatContainer;//提供悬浮覆盖的可能


    private MyHandler<T> mHandler = new MyHandler(this);
    protected List<T> mListData = new ArrayList<T>();
    protected int mCurrentPage = DEFAULT_FIRST_PAGE_NUM; // 默认当前第1页
    protected int mPageSize = DEFAULT_MAX_PAGE_SIZE;
    protected MyBaseRecyclerAdapter mAdapter;//子类提供
    private boolean mAutoCancelLoadMore;//此布尔值决定了:当数据size小于PageSize时要不要自动屏蔽上拉事件.默认不屏蔽,重写方法来设置.
    private int mCurrentRequestType;//当前的请求类型(首次加载,刷新,加载更多)
    protected Bundle savedInstanceState;

    /************************************************
     * RetroFit RxJava
     **********************************************/
    private CustomObserver mObserver;//观察者
    protected QDongApi mApi;//服务器接口,使用注解
    private Subscription subscription;//订阅,在界面销毁时要注意调用unSubscrib
    protected boolean mIsLoading = false;//正在发网络请求

    public QDongApi getmApi() {
        return mApi;
    }

    /**
     * @param :[pageSize]
     * @return type:void
     * @method name:setPageSize
     * @des:设置请求页面最大值
     * @date 创建时间:2016/5/10
     * @author Chuck
     **/
    public void setPageSize(int pageSize) {
        this.mPageSize = pageSize;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;


        if (mContentView == null) {
            //mContentView = View.inflate(getActivity(), R.layout.fragment_base_list, null);

            mContentView = inflater.inflate(R.layout.fragment_base_list, container, false);

            init(mContentView);
            firstTimeLoad();
        } else {
            // 不为null时，需要把自身从父布局中移除，因为ViewPager会再次添加
            ViewParent parent = mContentView.getParent();
            if (parent != null && parent instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) parent;
                group.removeView(mContentView);
            }
        }
        return mContentView;
    }

    /**
     * @param :[view]
     * @return type:void
     * @method name:initView
     * @des:初始化
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    private void init(View view) {

        initRetroFit_RxJava();
        initView(view);


    }

    /**
     * @param :[view]
     * @return type:void
     * @method name:initView
     * @des:初始化控件
     * @date 创建时间:2016/6/25
     * @author Chuck
     **/
    private void initView(View view) {

        mAutoCancelLoadMore = resetAutoCancelLoadMore();

        mRefreshRecyclerView = (RefreshRecyclerView) view.findViewById(R.id.recyclerView);
        mLlFloatContainer = (LinearLayout) view.findViewById(R.id.ll_head_container);
        mLlTitle = (LinearLayout) view.findViewById(R.id.ll_title);
        mLlBottom = (LinearLayout) view.findViewById(R.id.ll_bottom);
        View title=getFragmentTitleView();
        if (title != null) {
            mLlTitle.addView(title);
        }
        loadingView = (CustomMaskLayerView) view.findViewById(R.id.loading_view);
        loadingView.setTransparentMode(CustomMaskLayerView.STYLE_TRANSPARENT_ON);


        if (getRecyclerViewItemDecoration() != null) {
            mRefreshRecyclerView.getmRecyclerView().addItemDecoration(getRecyclerViewItemDecoration());//设置item的分割线
        }
        if (getRecyclerViewItemAnimator() != null) {
            mRefreshRecyclerView.getmRecyclerView().setItemAnimator(getRecyclerViewItemAnimator());//设置item的动画
        }

        mAdapter = initAdapter();//初始化adapter


        RefreshRecyclerAdapterManager manager = RecyclerViewManager.with(mAdapter, getRecyclerViewLayoutManager());


        manager.setMode(getRecyclerViewPullMode())//刷新模式
                .addHeaderView(getRecyclerViewHeadView())//加头
                .addFooterView(getRecyclerViewFootView());//加尾巴

        if (getRecyclerViewPullMode() == RecyclerMode.BOTH) {
            manager.setOnBothRefreshListener(new OnBothRefreshListener() {
                @Override
                public void onPullDown() {
                    pullDownAction();
                }

                @Override
                public void onLoadMore() {
                    pullUpAction();
                }
            });
        } else if (getRecyclerViewPullMode() == RecyclerMode.TOP) {
            manager.setOnPullDownListener(new OnPullDownListener() {
                @Override
                public void onPullDown() {
                    pullDownAction();
                }
            });
        } else if (getRecyclerViewPullMode() == RecyclerMode.BOTTOM) {
            manager.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    pullUpAction();
                }
            });
        }


        manager.into(mRefreshRecyclerView, getActivity());


        loadingView.setmReloadCallback(new ReloadCallback() {
            @Override
            public void reload() {
                loadingView.showLoading();
                if (!NetworkUtil.hasNetWork(getActivity())) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingView.showError();
                            onComplete();
                            return;
                        }
                    }, 1000);
                } else {
                    if (!mIsLoading) {
                        mCurrentPage = DEFAULT_FIRST_PAGE_NUM;//页码回归
                        getDataFromServer(true, mCurrentPage, REQUEST_TYPR_REFRESH);
                        mIsLoading = true;
                    }
                }

            }
        });
    }

    /**
     * CustommObserver
     * 自定义的观察者,处理网络请求,结果封装在netInfo里,请求类型取他的actionType 有三个:首次加载,刷新,加载更多
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2016/6/25  22:11
     * Copyright : 趣动智能科技有限公司-版权所有
     **/
    class CustomObserver implements Observer<QDongNetInfo> {
        @Override
        public void onCompleted() {
            mIsLoading = false;//当前没有在发请求
            LogUtil.e("RxJava", "onCompleted(),线程id:" + Thread.currentThread().getId());
            //Toast.makeText(getActivity(),"onCompleted(),线程id:"+Thread.currentThread().getId(), Toast.LENGTH_SHORT).show();
            onComplete();
        }

        @Override
        public void onError(Throwable e) {
            mIsLoading = false;//当前没有在发请求
            LogUtil.e("RxJava", "onError(),线程id:" + Thread.currentThread().getId() + ",异常:" + e.toString());
            //Toast.makeText(getActivity(), "onError(),线程id:"+Thread.currentThread().getId()+",异常:"+ e.toString(), Toast.LENGTH_SHORT).show();

            loadingView.showError();
            mRefreshRecyclerView.onRefreshCompleted();

            switch (mCurrentRequestType) {
                case REQUEST_TYPR_FIRST_TIME_LOAD://首次加载数据
                    onInitDataResult(false);
                    break;
                case REQUEST_TYPR_REFRESH://刷新
                    onRefreshDataResult(false);
                    break;
                case REQUEST_TYPR_LOAD_MORE://加载更多
                    mCurrentPage--;
                    onLoadMoreDataResult(false);
                    break;
            }
        }

        @Override
        public void onNext(QDongNetInfo info) {
            mIsLoading = false;//当前没有在发请求
            LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info:" + info.toString());
            //Toast.makeText(getActivity(), "onNext(),线程id:"+Thread.currentThread().getId()+",info:"+ info.toString(), Toast.LENGTH_SHORT).show();

            if (info == null) {
                loadingView.dismiss();
                mRefreshRecyclerView.onRefreshCompleted();
            } else {

                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getActionType():" + info.getActionType());
                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.isSuccess():" + info.isSuccess());
                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getResult()!=null:" + (info.getResult() != null));
                LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getResult():" + info.getResult());

                switch (info.getActionType()) {
                    case REQUEST_TYPR_FIRST_TIME_LOAD://首次加载数据

                        loadingView.dismiss();
                        if (info.isSuccess()) {
                            if (info.getResult() != null) {
                                try {

                                    List<T> list = resolveData(info.getResult());

                                    LogUtil.e("RxJava", "onNext(),线程id:" + Thread.currentThread().getId() + ",info.getResult().toString:" + info.getResult().toString());

                                    if (list == null || list.size() == 0) {
                                        loadingView.showNoContent();
                                        onInitDataResult(true);
                                    } else {
                                        mListData.clear();
                                        mListData = list;
                                        mAdapter.setData(mListData);
                                        onInitDataResult(true);
                                    }

                                    mRefreshRecyclerView.onRefreshCompleted();
                                } catch (Exception e) {
                                    loadingView.showError();
                                    mRefreshRecyclerView.onRefreshCompleted();
                                    onInitDataResult(false);
                                    e.printStackTrace();
                                }
                            } else {
                                loadingView.showNoContent();
                                onInitDataResult(false);

                            }
                        } else {
                            loadingView.showError();
                            onInitDataResult(false);
                        }

                        break;
                    case REQUEST_TYPR_REFRESH://刷新

                        loadingView.dismiss();
                        if (info.isSuccess()) {
                            if (info.getResult() != null) {
                                try {
                                    List<T> list = resolveData(info.getResult());
                                    mListData.clear();
                                    mListData = list;
                                    if (mListData.size() == 0) {//如果刷新成功后没有数据了,则显示没有内容视图
                                        loadingView.showNoContent();
                                    }
                                    mAdapter.setData(mListData);
                                    mRefreshRecyclerView.onRefreshCompleted();
                                } catch (Exception e) {
                                    loadingView.showError();
                                    mRefreshRecyclerView.onRefreshCompleted();
                                    e.printStackTrace();
                                }
                            } else {
                                mRefreshRecyclerView.onRefreshCompleted();
                                loadingView.showNoContent();
                            }


                            onRefreshDataResult(true);
                        } else {
                            mRefreshRecyclerView.onRefreshCompleted();
                            loadingView.showError();
                            onRefreshDataResult(false);
                        }

                        break;
                    case REQUEST_TYPR_LOAD_MORE://加载更多

                        loadingView.dismiss();
                        if (info.isSuccess()) {
                            if (info.getResult() != null) {
                                try {
                                    List<T> list = resolveData(info.getResult());
                                    if (list == null || list.size() == 0) {
                                        mRefreshRecyclerView.onNoMoreData();
                                        mCurrentPage--;
                                    } else {
                                        mListData.addAll(list);
                                        mAdapter.setData(mListData);
                                        mRefreshRecyclerView.onRefreshCompleted();
                                    }

                                } catch (Exception e) {
                                    mRefreshRecyclerView.onNoMoreData();
                                    mCurrentPage--;
                                    e.printStackTrace();
                                }
                            } else {
                                mRefreshRecyclerView.onNoMoreData();
                                mCurrentPage--;
                            }

                            onLoadMoreDataResult(true);
                        } else {
                            mRefreshRecyclerView.onRefreshCompleted();
                            onLoadMoreDataResult(false);
                            loadingView.showError();
                            mCurrentPage--;
                        }

                        break;

                    default:
                        break;
                }
            }

        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:initRetroFit_RxJava
     * @des:请求框架
     * @date 创建时间:2016/6/25
     * @author Chuck
     **/
    private void initRetroFit_RxJava() {

      /*  mObserver=new mObserver<NetInfo>(){
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNext(NetInfo info) {
                Toast.makeText(getActivity(),info.toString(),Toast.LENGTH_LONG).show();

            }
        };*/

        mObserver = new CustomObserver();


        if (TextUtils.isEmpty(getBaseUrl())) {
            mApi = RetrofitAPIManager.provideClientApi(getActivity());//定义出来,因为在使用自动登录获取最新token时要用到
        } else {
            mApi = RetrofitAPIManager.provideClientApi(getActivity(), getBaseUrl());//定义出来,因为在使用自动登录获取最新token时要用到
        }

    }

    /**
     * @param :[]
     * @return type:void
     * @method name:pullUpAction
     * @des:上拉
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    private void pullUpAction() {

        if (NetworkUtil.checkNetWorkWithToast(getActivity())) {//有网
            loadingMoreData(); // 上拉加载更多
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshRecyclerView.onRefreshCompleted();
                }
            }, 1000);
        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:pullDownAction
     * @des:下拉
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    private void pullDownAction() {

        if (NetworkUtil.checkNetWorkWithToast(getActivity())) {//有网
            refreshData(); // 下拉刷新
        } else {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRefreshRecyclerView.onRefreshCompleted();
                }
            }, 1000);

        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:firstTimeLoad
     * @des:首次加载数据,这个无需参数,或者参数是默认值.
     * @date 创建时间:2016/4/20
     * @author Chuck
     **/
    public void firstTimeLoad() {
        // 判断是否有网络,没有网络的话显示网络异常
        if (!NetworkUtil.hasNetWork(getActivity())) {
            loadingView.showError();
            onInitNoNetWork();
        } else {
            if (!mIsLoading) {
                mCurrentPage = DEFAULT_FIRST_PAGE_NUM;//页码回归
                getDataFromServer(isShowLoadingFirstTime(), mCurrentPage, REQUEST_TYPR_FIRST_TIME_LOAD);
                mIsLoading = true;
            }
        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:firstTimeLoad
     * @des:刷新
     * @date 创建时间:2016/4/20
     * @author Chuck
     **/
    public void refresh() {
        // 判断是否有网络,没有网络的话显示网络异常
        if (!NetworkUtil.hasNetWork(getActivity())) {
            loadingView.showError();
            onInitNoNetWork();
        } else {
            if (!mIsLoading) {
                mCurrentPage = DEFAULT_FIRST_PAGE_NUM;//页码回归
                getDataFromServer(true, mCurrentPage, REQUEST_TYPR_REFRESH);
                mIsLoading = true;
            }
        }
    }

    /**
     * @param :[]
     * @return type:void
     * @method name:onInitNoNetWork
     * @des:当首次进来就没有网络,会调用此方法
     * @date 创建时间:2016/9/10
     * @author Chuck
     **/
    protected void onInitNoNetWork() {
    }

    /**
     * @param :[]
     * @return type:boolean
     * @method name:isShowLoadingFirstTime
     * @des:首次加载时是否展示loading画面
     * @date 创建时间:2016/8/24
     * @author Chuck
     **/
    protected boolean isShowLoadingFirstTime() {
        return true;
    }

    /**
     * @return type:void
     * @des: 下拉刷新, 这个没有公开, 是因为某些界面是改变了某些参数再刷新的,
     * 此时要在子fragment里定义方法并接受接口参数(比如某种查询条件),供activity调用.因此这里不做公开
     * @date 创建时间：2015-8-10
     * @author Chuck
     */
    protected void refreshData() {
        refreshData(false);
    }

    /**
     * @return type:void
     * @des: 下拉刷新, 这个没有公开, 是因为某些界面是改变了某些参数再刷新的,
     * 此时要在子fragment里定义方法并接受接口参数(比如某种查询条件),供activity调用.因此这里不做公开
     * @date 创建时间：2015-8-10
     * @author Chuck
     */
    protected void refreshData(boolean showLoading) {
        if (!mIsLoading) {
            mCurrentPage = DEFAULT_FIRST_PAGE_NUM;//页码回归
            getDataFromServer(showLoading, mCurrentPage, REQUEST_TYPR_REFRESH);
            mIsLoading = true;
        }
    }

    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getFragmentTitleView
     * @des:子类listview上面的view
     * @date 创建时间:2016/7/6
     * @author Chuck
     **/
    protected View getFragmentTitleView() {
        return null;
    }


    /**
     * @return type:void
     * @des: 上拉加载
     * @date 创建时间：2015-8-10
     */
    protected void loadingMoreData() {
        // TODO: 2016/6/21 因为界面可能有手动删除的业务,故通过子类来设置,默认是不加这个判断的
        if (mAutoCancelLoadMore) {//数据不足pageSize时是否屏蔽上拉事件
            if (mListData.size() < mPageSize) {
                mRefreshRecyclerView.onNoMoreData();
                return;
            }
        }

        if (!mIsLoading) {//某一时间只允许一个请求
            mCurrentPage++;//页码加1
            getDataFromServer(false, mCurrentPage, REQUEST_TYPR_LOAD_MORE);
            mIsLoading = true;
        }

    }

    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView
     * @method name:getRecyclerView
     * @des:子类获取recyclerview
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView getRecyclerView() {
        return mRefreshRecyclerView.getmRecyclerView();
    }


    /**
     * @param :[isShowLoadingView, page, requestType]
     * @return type:void
     * @method name:getDataFromServer
     * @des:发请求
     * @date 创建时间:2016/5/10
     * @author Chuck
     **/
    private void getDataFromServer(boolean isShowLoadingView, int page, final int requestType) {

        if (isShowLoadingView) { // 是否显示加载框
            if (loadingView != null) {
                loadingView.showLoading();
            }
        } else {
            if (loadingView != null) {
                loadingView.dismiss();
            }
        }

        mCurrentRequestType = requestType;


        subscription = callApi(mApi, mCurrentPage, mPageSize)//子类实现
                .subscribeOn(Schedulers.io())//指定被观察者的执行线程
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程,因为如果正常反回了,我要将事件直接交给观察者,观察者通常在主线程
                .flatMap(RxHelper.getInstance(getActivity()).judgeSessionExpired(mObserver, requestType))//判断session是否过期,如果过期了,会抛出异常到事件流里
                .observeOn(Schedulers.io())//切换到子线程,执行retryWhen,里面有网络请求
                .retryWhen(RxHelper.getInstance(getActivity()).judgeRetry(mApi, getAutoLoginParameterMap()))//触发retry
                .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程

               /* .doOnNext(new Action1<QDongNetInfo>() {//在子线程将请求来的数据改动,此处封装的是请求类型:首次加载/刷新/加载
                    @Override
                    public void call(QDongNetInfo netInfo) {
                        try {
                            netInfo.setActionType(requestType);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })*/

                .subscribe(mObserver);//订阅


    }


    public List<T> getmListData() {
        return mListData;
    }

    public void setmListData(List<T> mListData) {
        this.mListData = mListData;
    }


    /**
     * MyHandler
     * 继承httpHandler,泛型是列表数据模型
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2016/5/10  11:14
     * Copyright : 2014-2015 深圳掌通宝科技有限公司-版权所有
     **/
    private static class MyHandler<F> extends Handler {
        WeakReference<BaseRefreshableListFragment> mActivityReference;

        public MyHandler(BaseRefreshableListFragment fragment) {
            mActivityReference = new WeakReference<BaseRefreshableListFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }


    /**
     * @param :
     * @return type:
     * @method name: setAdapterData
     * @des: 设置适配器数据
     * @date 创建时间：2016/2/19 13:40
     * @author hujie
     */
    protected void setAdapterData(List<T> data) {
        if (mAdapter != null) {
            mAdapter.setData(data);
        }
    }

    /**
     * @param :[data]
     * @return type:void
     * @method name:addData
     * @des:adapter添加数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    protected void addDatas(List<T> data) {
        if (mAdapter != null) {
            mAdapter.addDatas(data);
        }
    }

    /**
     * @param :[data]
     * @return type:void
     * @method name:addData
     * @des:adapter添加数据
     * @date 创建时间:2016/6/17
     * @author Chuck
     **/
    protected void addData(T data) {
        if (mAdapter != null) {
            mAdapter.addData(data);
        }
    }

    /**
     * @param :[isAuto]
     * @return type:void
     * @method name:resetAutoCancelLoadMore
     * @des:重写这个来决定,集合size小于pageSize时是否屏蔽上拉,父类默认不屏蔽
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected boolean resetAutoCancelLoadMore() {
        return false;
    }

    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView.ItemAnimator
     * @method name:getItemAnimator
     * @des:子类可以重写,以设置item的插入,删除动画
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView.ItemAnimator getRecyclerViewItemAnimator() {
        return new DefaultItemAnimator();
    }


    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView.ItemDecoration
     * @method name:getItemDecoration
     * @des:子类可以重写,以设置item分割线
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView.ItemDecoration getRecyclerViewItemDecoration() {
        return new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
    }

    /**
     * @param :[]
     * @return type:android.support.v7.widget.RecyclerView.LayoutManager
     * @method name:getRecyclerViewLayoutManager
     * @des:子类可以重写,以修改recyclerView的布局类型,是垂直list,还是水平list,或者gridview
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }


    /**
     * @param :[]
     * @return type:com.qdong.communal.library.widget.RefreshRecyclerView.manager.RecyclerMode
     * @method name:getRecyclerPullMode
     * @des:设置滑动模式: NONE, BOTH, TOP, BOTTOM
     * @date 创建时间:2016/6/21
     * @author Chuck
     **/
    protected RecyclerMode getRecyclerViewPullMode() {
        return RecyclerMode.BOTH;
    }


    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getHeadView
     * @des:为recyclerView添加headview
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected View getRecyclerViewHeadView() {
        return null;
    }

    /**
     * @param :[]
     * @return type:android.view.View
     * @method name:getFootView
     * @des:为recyclerView提供footview
     * @date 创建时间:2016/6/20
     * @author Chuck
     **/
    protected View getRecyclerViewFootView() {
        return null;
    }


    /**
     * @param :[]
     * @return type:void
     * @method name:unSubscribe
     * @des:注销监听,在fragment onDestroy记得调用
     * @date 创建时间:2016/6/25
     * @author Chuck
     **/
    protected void unSubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    /**
     * 重写此方法来给fragment来处理初始化结果
     *
     * @param isSuccessfuly 首次加载数据是否成功
     */
    protected void onInitDataResult(boolean isSuccessfuly) {
    }

    ;

    /**
     * 重写此方法来给fragment来处理刷新结果
     *
     * @param isSuccessfuly 刷新数据是否成功
     */
    protected void onRefreshDataResult(boolean isSuccessfuly) {
    }

    ;

    /**
     * 重写此方法来给fragment来处理加载更多的结果
     *
     * @param isSuccessfuly 加载更多数据是否成功
     */
    protected void onLoadMoreDataResult(boolean isSuccessfuly) {
    }

    ;


    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
            unSubscribe();



            for (Subscription s : mSubscriptions) {
                LogUtil.e("Subscription", s);
                if (s != null && !s.isUnsubscribed()) {
                    s.unsubscribe();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * 调用API,子类自己实现
     *
     * @param api
     * @param currentPage
     * @param pageSize
     */
    public abstract Observable<QDongNetInfo> callApi(QDongApi api, int currentPage, int pageSize);

    /***
     * 实现此方法提供请求的baseUrl
     *
     * @return
     */
    public abstract String getBaseUrl();

    /**
     * 实现此方法解析数据
     */
    public abstract List<T> resolveData(JsonElement jsonStr) throws JSONException;


    /**
     * 实现此方法初始化适配器
     */
    public abstract MyBaseRecyclerAdapter initAdapter();


    /**
     * 子类必须提供自动登录所需的参数map
     */
    public abstract HashMap<String, String> getAutoLoginParameterMap();


    protected ArrayList<Subscription> mSubscriptions = new ArrayList<>();

    public ArrayList<Subscription> getmSubscriptions() {
        return mSubscriptions;
    }

    /**
     * 操作完成，子类可重写此方法
     */
    protected void onComplete() {
    }

}
