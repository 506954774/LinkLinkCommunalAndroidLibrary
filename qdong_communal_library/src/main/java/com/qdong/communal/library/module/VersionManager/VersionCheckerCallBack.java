package com.qdong.communal.library.module.VersionManager;

/**
 * LoadingViewProvider
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/6/13  19:44
 * Copyright : 2014-2016 深圳趣动只能科技有限公司-版权所有
 */
public interface VersionCheckerCallBack {
    public void showLoadingView();
    public void dismissLoadingView();
    public void onError();
    public void noNewVersion();
    public void forceCloseApp();
}
