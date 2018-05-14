package com.qdong.communal.library.module.network;/**
 * Created by AA on 2016/7/11.
 */

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.text.TextUtils;

import com.qdong.communal.library.BuildConfig;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.SharedPreferencesUtil;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * RxHelper
 * RxJava辅助类
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/7/11  9:20
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class RxHelper {

    public static final String THROWABLE_MESSAGE_LOGIN_FAILED="login failed";
    public static final int RETRY_TIMES_LIMIT = 3;//重试总次数
    private static final int RETRY_DELAY = 100;//每次重试的delay时间
    private static RxHelper ourInstance;

    private Context context;

    public static RxHelper getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new RxHelper(context);
        }
        return ourInstance;
    }

    private RxHelper(Context context) {
        this.context=context;
    }
    private static final String SESSION_ERROR= BuildConfig.SESSION_ERROR_CODE;
    private static final String SET_COOKIE="Set-Cookie";
    private static final String SESSION_ID="JSESSIONID=";
    private static final String SPLIT_SIGN=";";
    private static final String EQUAL_SIGN="=";
    private static final String EXCEPTION_SESSION_IS_ERROR="session is error!";
    private static final String THROWABLE_MESSAGE_AUTO_LOGIN_FAILED="auto login failed";


    /**
     * @method name:unsubscribe
     * @des:消除订阅.最后释放资源时,一定要调用,不然会有内存溢出的风险
     * @param :[]
     * @return type:void
     * @date 创建时间:2016/6/28
     * @author Chuck
     **/
    public void unsubscribe(Subscription subscription) {

        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


    /**
     * @method name:judgeSessionExpired
     * 重载这个是为了给相同请求(比如同一个接口,可以有刷新,加载的不同动作)的加一个类型
     * @des:判断session有无过期,判断的逻辑根据服务器定义的错误码来判断
     * 返回的值作为flatMap的参数,也就是把一个源数据通过一个Func函数变为一个被观察者对象(可以是异常对象)
     * @param :[observer :观察者;actionType:请求类型,这个是专门为下拉刷新的界面做的]
     * @return type:rx.functions.Func1<com.rengwuxian.rxjavasamples.qdong_test.QDongNetInfo,rx.Observable<com.rengwuxian.rxjavasamples.qdong_test.QDongNetInfo>>
     * @date 创建时间:2016/6/29
     * @author Chuck
     **/
    public Func1<QDongNetInfo, Observable<QDongNetInfo>> judgeSessionExpired(final Observer<QDongNetInfo> observer, final int actionType){

        return
                new Func1<QDongNetInfo, Observable<QDongNetInfo>>(){
                    @Override
                    public Observable<QDongNetInfo> call(QDongNetInfo netInfo) {

                        LogUtil.e("RxJava", "flatMap判断session是否失效,线程id:" + Thread.currentThread().getId() + ",netInfo:" + netInfo.toString());

                        if(netInfo==null){//网络错误
                            LogUtil.e("RxJava", "flatMap,====>session失效,线程id:" + Thread.currentThread().getId() + ",第一次请求就没有正常返回,框架将处理这个");
                            return  Observable.<QDongNetInfo>error(new NetworkErrorException());
                        }
                        else {

                            netInfo.setActionType(actionType);//赋值给它,这样,观察者可以通过他的actionType来判断请求类型

                            if (SESSION_ERROR.equals(netInfo.getCode())) {//服务器返回"000003",表示session已经过期,则再获取一次tocken
                                LogUtil.e("RxJava", "flatMap,====>session失效,线程id:" + Thread.currentThread().getId() + ",准备抛出异常,触发retry");

                                /**抛出这个异常**/
                                return Observable.<QDongNetInfo>error(new QDongException(EXCEPTION_SESSION_IS_ERROR));//返回一个thrawable
                            }
                            else {
                                LogUtil.e("RxJava", "flatMap,====>session没有失效,线程id:" + Thread.currentThread().getId() + ",准备触发观察者的回调");
                                try {
                                    if(observer!=null){
                                        observer.onNext(netInfo);//手动调用观察者的回调
                                    }
                                } catch (Exception e) {
                                    //这种事框架的问题,会抛出异常,但是,其实上面的手动回调是执行了的.!!!!这里特别要注意
                                    e.printStackTrace();
                                    return  Observable.<QDongNetInfo>empty();
                                }

                                /**返回这个后,事件结束**/
                                return  Observable.<QDongNetInfo>empty();
                            }
                        }
                    }
                };
    }



    /**
     * @method name:judgeSessionExpired
     * @des:判断session有无过期,判断的逻辑根据服务器定义的错误码来判断
     * 返回的值作为flatMap的参数,也就是把一个源数据通过一个Func函数变为一个被观察者对象(可以是异常对象)
     * @param :[observer :观察者]
     * @return type:rx.functions.Func1<com.rengwuxian.rxjavasamples.qdong_test.QDongNetInfo,rx.Observable<com.rengwuxian.rxjavasamples.qdong_test.QDongNetInfo>>
     * @date 创建时间:2016/6/29
     * @author Chuck
     **/
    public Func1<QDongNetInfo, Observable<QDongNetInfo>> judgeSessionExpired(final Observer<QDongNetInfo> observer){

        return
                new Func1<QDongNetInfo, Observable<QDongNetInfo>>(){
                    @Override
                    public Observable<QDongNetInfo> call(QDongNetInfo netInfo) {

                        LogUtil.e("RxJava", "flatMap判断session是否失效,线程id:" + Thread.currentThread().getId() + ",netInfo:" + netInfo.toString());

                        if(netInfo==null){//网络错误
                            LogUtil.e("RxJava", "flatMap,====>session失效,线程id:" + Thread.currentThread().getId() + ",第一次请求就没有正常返回,框架将处理这个");
                            return  Observable.<QDongNetInfo>error(new NetworkErrorException());
                        }
                        else {

                            LogUtil.e("RxJava", "flatMap,====>QDongNetInfo:" +  netInfo.toString());


                            if (SESSION_ERROR.equals(netInfo.getCode())) {//服务器返回"010035",表示session已经过期,则再获取一次tocken
                                LogUtil.e("RxJava", "flatMap,====>session失效,线程id:" + Thread.currentThread().getId() + ",准备抛出异常,触发retry");

                                /**抛出这个异常**/
                                return Observable.<QDongNetInfo>error(new QDongException(EXCEPTION_SESSION_IS_ERROR));//返回一个thrawable
                            }
                            else {
                                LogUtil.e("RxJava", "flatMap,====>session没有失效,线程id:" + Thread.currentThread().getId() + ",准备触发观察者的回调");
                                try {
                                    if(observer!=null){
                                        observer.onNext(netInfo);//手动调用观察者的回调
                                    }
                                } catch (Exception e) {
                                    //这种事框架的问题,会抛出异常,但是,其实上面的手动回调是执行了的.!!!!这里特别要注意
                                    e.printStackTrace();
                                    return  Observable.<QDongNetInfo>empty();
                                }

                                /**返回这个后,事件结束**/
                                return  Observable.<QDongNetInfo>empty();
                            }
                        }
                    }
                };
    }



  /**
     * @method name:judgeRetry
     * @des:判断要不要执行retry
     * @param :[service :api类, action:请求动作,HashMap<String, String> map:登录参数map]
     * @return type:rx.functions.Func1<rx.Observable<? extends java.lang.Throwable>,rx.Observable<?>>
     * @date 创建时间:2016/6/29
     * @author Chuck
     **/
    public  Func1<Observable<? extends Throwable>, Observable<?>> judgeRetry(final QDongApi service, final Observable<QDongNetInfo> action,final HashMap<String, String> map){

        return
                new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?>      call(Observable<? extends Throwable> observable) {

                        return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                            @Override
                            public Observable<?> call(Throwable throwable) {

                                LogUtil.e("RxJava", "retryWhen执行,线程id:" + Thread.currentThread().getId());

                                if (throwable !=null && throwable instanceof QDongException && EXCEPTION_SESSION_IS_ERROR.equals(throwable.getMessage())) {

                                    LogUtil.e("RxJava", "retryWhen执行,线程id:" + Thread.currentThread().getId() + "====>是session过期的问题!");


                                    return autoLogin(service,action,map);

                                }
                                else {

                                    LogUtil.e("RxJava", "retryWhen执行,线程id:" + Thread.currentThread().getId() + "====>不是session过期的问题,将返回Observable.error(throwable)" );


                                    return Observable.error(throwable);
                                }

                            }
                        });
                    }
                };
    }

    /**
     * @method name:judgeRetry
     * @des:判断要不要执行retry
     * @param :[service :api类, HashMap<String, String> map:登录参数map]
     * @return type:rx.functions.Func1<rx.Observable<? extends java.lang.Throwable>,rx.Observable<?>>
     * @date 创建时间:2016/6/29
     * @author Chuck
     **/
    public  Func1<Observable<? extends Throwable>, Observable<?>> judgeRetry(final QDongApi service,final HashMap<String, String> map){
        return new RetryWithDelay(RETRY_TIMES_LIMIT, RETRY_DELAY,service,map);//每次停100毫秒,总共试3次
    }


    /**
     * RetryWithDelay
     * 重试次数的封装类
     * 责任人:  Chuck
     * 修改人： Chuck
     * 创建/修改时间: 2016/10/18  17:49
     * Copyright : 趣动智能科技有限公司-版权所有
     **/
    class RetryWithDelay implements Func1<Observable<? extends Throwable>, Observable<?>> {

        private final int maxRetries;//重试总次数
        private final int retryDelayMillis;//重试每次间隔时间
        private int retryCount;//当前重试的次数

        private QDongApi service;//serverce
        private HashMap<String, String> map;//自动登录的map


        public RetryWithDelay(int maxRetries, int retryDelayMillis,QDongApi service,HashMap<String, String> map) {
            this.maxRetries = maxRetries;
            this.retryDelayMillis = retryDelayMillis;
            this.service=service;
            this.map=map;
        }



        @Override
        public Observable<?> call(Observable<? extends Throwable> attempts) {
            return attempts
                    .flatMap(new Func1<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> call(Throwable throwable) {
                            if (++retryCount <= maxRetries) {//retryCount是当前的重试次数,如果小于最大值,就执行,否则抛出异常
                                // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).

                                LogUtil.e("RxJava", "retryWhen执行,线程id:" + Thread.currentThread().getId());

                                /**如果是我们指定的异常,则执行retry**/
                                if (throwable !=null && throwable instanceof QDongException && EXCEPTION_SESSION_IS_ERROR.equals(throwable.getMessage())) {

                                    LogUtil.e("RxJava", "retryWhen执行,线程id:" + Thread.currentThread().getId() + "====>是session过期的问题!");

                                    /**调用自动登录获取最新sessonId,并根据登录结果返回Observable对象**/
                                    return autoLogin(service , map);

                                }
                                else {

                                    LogUtil.e("RxJava", "retryWhen执行,线程id:" + Thread.currentThread().getId() + "====>不是session过期的问题,将返回Observable.error(throwable)" );

                                    /**其他的异常直接抛回给框架**/
                                    return Observable.error(throwable);
                                }


                            }
                            // Max retries hit. Just pass the error along.
                            return Observable.error(throwable);
                        }
                    });
        }
    }


    /**
     * @method name:autoLogin
     * @des:登录接口,在获取用户信息的同时,保存最新sessionId
     * @param :[service, map]
     * @return type:rx.Observable<com.qdong.communal.library.module.network.QDongNetInfo>
     * @date 创建时间:2016/7/11
     * @author Chuck
     **/
    public Observable<QDongNetInfo> loginAndSaveSession (QDongApi service,HashMap<String, String> map){

        LogUtil.e("RxJava", "login,线程id:" + Thread.currentThread().getId() );

        boolean success=false;//是否获取到了最新的token(即是是否登录成功)
        String errorMsg=null;
        Response<QDongNetInfo> response = null;//定义响应体

        try {
            Call<QDongNetInfo> call = service.callFirstLogin(map);//同步调用,为何要同步?因为我要抓取cookie里面的sessionId
            response = call.execute();//执行

            LogUtil.e("RxJava", "flatMap,线程id:" + Thread.currentThread().getId() + ",responseResponse:" + response.raw().headers().toString());

            if(!response.body().isSuccess()){
                /**错误码直接封装到事件流里**/
                errorMsg=response.body().getCode();
            }
            else if(response.body().isSuccess()){//成功了
                /****请求成功!登录成功,之前服务器有这种现象:登录成功了,但是没有sessionId,所以这里以是否有错误码为准,不以是否有返回session为准***/
                success=true;

                Headers headers = response.raw().headers();//获取头

                String cookiesString = headers.get(SET_COOKIE);

                if(!TextUtils.isEmpty(cookiesString)){
                    String[] cookies = cookiesString.split(SPLIT_SIGN);//拆分
                    for (String s : cookies) {
                        if (!TextUtils.isEmpty(s)) {
                            if (s.startsWith(SESSION_ID)) {
                                String sessionId = s.substring((s.indexOf(EQUAL_SIGN) + 1));
                                LogUtil.e("RxJava", "==============>获取sessionId成功!--->" + sessionId);
                                RetrofitAPIManager.JSESSIONID = sessionId;//赋值
                                /***
                                 * 存起来
                                 */
                                SharedPreferencesUtil.getInstance(context).putString(Constants.SESSION_ID,sessionId);
                            }
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("RxJava", "flatMap catch,==============>线程id:" + Thread.currentThread().getId() + ",error:" + e.toString());
        }
        finally {
            if(success){
                /**获取成功*/
                return  Observable.just(response.body());
            }
            else {
                /**获取失败,如果是010008或者020003,则抛出这个异常,否则,抛出"login failed"**/
                return Observable.error(new Throwable(TextUtils.isEmpty(errorMsg)?THROWABLE_MESSAGE_LOGIN_FAILED:errorMsg));
            }
        }


    }

    /**
     * @method name:autoLogin
     * @des:登录接口,在获取用户信息的同时,保存最新sessionId
     * @param :[service, map]
     * @return type:rx.Observable<com.qdong.communal.library.module.network.QDongNetInfo>
     * @date 创建时间:2016/7/11
     * @author Chuck
     **/
    public Observable<QDongNetInfo> firstLoginAndSaveSession (QDongApi service,HashMap<String, String> map){

        LogUtil.e("RxJava", "login,线程id:" + Thread.currentThread().getId() );

        boolean success=false;//是否获取到了最新的token(即是是否登录成功)
        String errorMsg=null;
        Response<QDongNetInfo> response = null;//定义响应体

        try {
            Call<QDongNetInfo> call = service.callFirstLogin(map);//同步调用,为何要同步?因为我要抓取cookie里面的sessionId
            response = call.execute();//执行

            LogUtil.e("RxJava", "flatMap,线程id:" + Thread.currentThread().getId() + ",responseResponse:" + response.raw().headers().toString());

            if(!response.body().isSuccess()){
                /**错误码直接封装到事件流里**/
                errorMsg=response.body().getCode();
            }
            else if(response.body().isSuccess()){//成功了
                /****请求成功!登录成功,之前服务器有这种现象:登录成功了,但是没有sessionId,所以这里以是否有错误码为准,不以是否有返回session为准***/
                success=true;

                Headers headers = response.raw().headers();//获取头

                String cookiesString = headers.get(SET_COOKIE);

                if(!TextUtils.isEmpty(cookiesString)){
                    String[] cookies = cookiesString.split(SPLIT_SIGN);//拆分
                    for (String s : cookies) {
                        if (!TextUtils.isEmpty(s)) {
                            if (s.startsWith(SESSION_ID)) {
                                String sessionId = s.substring((s.indexOf(EQUAL_SIGN) + 1));
                                LogUtil.e("RxJava", "==============>获取sessionId成功!--->" + sessionId);
                                RetrofitAPIManager.JSESSIONID = sessionId;//赋值
                                /***
                                 * 存起来
                                 */
                                SharedPreferencesUtil.getInstance(context).putString(Constants.SESSION_ID,sessionId);
                            }
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("RxJava", "flatMap catch,==============>线程id:" + Thread.currentThread().getId() + ",error:" + e.toString());
        }
        finally {
            if(success){
                /**获取成功*/
                return  Observable.just(response.body());
            }
            else {
                /**获取失败,如果是010008或者020003,则抛出这个异常,否则,抛出"login failed"**/
                return Observable.error(new Throwable(TextUtils.isEmpty(errorMsg)?THROWABLE_MESSAGE_LOGIN_FAILED:errorMsg));
            }
        }


    }

    /**
     * @method name:autoLogin
     * @des:登录接口,在获取用户信息的同时,保存最新sessionId
     * @param :[service, map]
     * @return type:rx.Observable<com.qdong.communal.library.module.network.QDongNetInfo>
     * @date 创建时间:2016/7/11
     * @author Chuck
     **/
    public Observable<QDongNetInfo> tokenLoginAndSaveSession (QDongApi service,HashMap<String, String> map){

        LogUtil.e("RxJava", "login,线程id:" + Thread.currentThread().getId() );

        boolean success=false;//是否获取到了最新的token(即是是否登录成功)
        String errorMsg=null;
        Response<QDongNetInfo> response = null;//定义响应体

        try {
            Call<QDongNetInfo> call = service.callTokenLogin(map);//同步调用,为何要同步?因为我要抓取cookie里面的sessionId
            response = call.execute();//执行

            LogUtil.e("RxJava", "flatMap,线程id:" + Thread.currentThread().getId() + ",responseResponse:" + response.raw().headers().toString());

            if(!response.body().isSuccess()){
                /**错误码直接封装到事件流里**/
                errorMsg=response.body().getCode();
            }
            else if(response.body().isSuccess()){//成功了
                /****请求成功!登录成功,之前服务器有这种现象:登录成功了,但是没有sessionId,所以这里以是否有错误码为准,不以是否有返回session为准***/
                success=true;

                Headers headers = response.raw().headers();//获取头

                String cookiesString = headers.get(SET_COOKIE);

                if(!TextUtils.isEmpty(cookiesString)){
                    String[] cookies = cookiesString.split(SPLIT_SIGN);//拆分
                    for (String s : cookies) {
                        if (!TextUtils.isEmpty(s)) {
                            if (s.startsWith(SESSION_ID)) {
                                String sessionId = s.substring((s.indexOf(EQUAL_SIGN) + 1));
                                LogUtil.e("RxJava", "==============>获取sessionId成功!--->" + sessionId);
                                RetrofitAPIManager.JSESSIONID = sessionId;//赋值
                                /***
                                 * 存起来
                                 */
                                SharedPreferencesUtil.getInstance(context).putString(Constants.SESSION_ID,sessionId);
                            }
                        }
                    }
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("RxJava", "flatMap catch,==============>线程id:" + Thread.currentThread().getId() + ",error:" + e.toString());
        }
        finally {
            if(success){
                /**获取成功*/
                return  Observable.just(response.body());
            }
            else {
                /**获取失败,如果是010008或者020003,则抛出这个异常,否则,抛出"login failed"**/
                return Observable.error(new Throwable(TextUtils.isEmpty(errorMsg)?THROWABLE_MESSAGE_LOGIN_FAILED:errorMsg));
            }
        }


    }

    /**
     * @method name:autoLogin
     * @des:登录接口,(在获取用户信息的同时,保存最新sessionId)
     * @param :[service:请求实例, map:登录参数map]
     * @return type:rx.Observable<com.qdong.communal.library.module.network.QDongNetInfo>
     * @date 创建时间:2016/7/11
     * @author Chuck
     **/
    public Func1<Long, Observable<QDongNetInfo>> login (final QDongApi service, final HashMap<String, String> map){
        return new Func1<Long, Observable<QDongNetInfo>>() {
            @Override
            public Observable<QDongNetInfo> call(Long aLong) {
                return loginAndSaveSession(service,map);
            }
        };
    }


    /**
     * @method name:getFlatMapForSynchronizationLogin
     * @des:返回一个FlatMap对象,接受登录参数的hashMAP,调用同步登录,并把结果发射出去.为何同步?因为要获取head里面的sessionId
     * @param :[service]
     * @return type:rx.functions.Func1<java.util.HashMap<java.lang.String,java.lang.String>,rx.Observable<com.qdong.communal.library.module.network.QDongNetInfo>>
     * @date 创建时间:2016/9/18
     * @author Chuck
     **/
    public Func1<HashMap<String, String>, Observable<QDongNetInfo>> getFlatMapForSynchronizationLogin (final QDongApi service){
        return new Func1<HashMap<String, String>, Observable<QDongNetInfo>>() {
            @Override
            public Observable<QDongNetInfo> call(HashMap<String, String> map) {
                return loginAndSaveSession(service,map);
            }
        };
    }
    /**
     * @method name:getFlatMapForSynchronizationLogin
     * @des:返回一个FlatMap对象,接受登录参数的hashMAP,调用同步登录,并把结果发射出去.为何同步?因为要获取head里面的sessionId
     * @param :[service]
     * @return type:rx.functions.Func1<java.util.HashMap<java.lang.String,java.lang.String>,rx.Observable<com.qdong.communal.library.module.network.QDongNetInfo>>
     * @date 创建时间:2016/9/18
     * @author Chuck
     **/
    public Func1<HashMap<String, String>, Observable<QDongNetInfo>> getFlatMapForSynchronizationFirstLogin (final QDongApi service){
        return new Func1<HashMap<String, String>, Observable<QDongNetInfo>>() {
            @Override
            public Observable<QDongNetInfo> call(HashMap<String, String> map) {
                return firstLoginAndSaveSession(service,map);
            }
        };
    }
    /**
     * @method name:getFlatMapForSynchronizationLogin
     * @des:返回一个FlatMap对象,接受登录参数的hashMAP,调用同步登录,并把结果发射出去.为何同步?因为要获取head里面的sessionId
     * @param :[service]
     * @return type:rx.functions.Func1<java.util.HashMap<java.lang.String,java.lang.String>,rx.Observable<com.qdong.communal.library.module.network.QDongNetInfo>>
     * @date 创建时间:2016/9/18
     * @author Chuck
     **/
    public Func1<HashMap<String, String>, Observable<QDongNetInfo>> getFlatMapForSynchronizationTokenLogin (final QDongApi service){
        return new Func1<HashMap<String, String>, Observable<QDongNetInfo>>() {
            @Override
            public Observable<QDongNetInfo> call(HashMap<String, String> map) {
                return tokenLoginAndSaveSession(service,map);
            }
        };
    }



  /**
     * @method name:autoLogin
     * @des:自动登录
     * @param :[service,
     * action,
     * map:登录需要的参数map
     * ]
     * @return type:rx.Observable<?>
     * @date 创建时间:2016/8/22
     * @author Chuck
     **/
    private Observable<?> autoLogin(QDongApi service,Observable<QDongNetInfo> action,HashMap<String, String> map) {

        boolean success=false;//是否获取到了最新的token

        Response<QDongNetInfo> response = null;//定义响应体

        try {
            Call<QDongNetInfo> call = service.login(map);//同步调用,为何要同步?因为我只需要抓取cookie里面的sessionId
            response = call.execute();//执行

            LogUtil.e("RxJava", "flatMap,线程id:" + Thread.currentThread().getId() + ",responseResponse:" + response.raw().headers().toString());

            Headers headers = response.raw().headers();//获取头

            String cookiesString = headers.get(SET_COOKIE);

            if(!TextUtils.isEmpty(cookiesString)){
                String[] cookies = cookiesString.split(SPLIT_SIGN);//拆分
                for (String s : cookies) {
                    if (!TextUtils.isEmpty(s)) {
                        if (s.startsWith(SESSION_ID)) {
                            String sessionId = s.substring((s.indexOf(EQUAL_SIGN) + 1));
                            LogUtil.e("RxJava", "==============>获取sessionId成功!--->" + sessionId);
                            RetrofitAPIManager.JSESSIONID = sessionId;//赋值


                            SharedPreferencesUtil.getInstance(context).putString(Constants.SESSION_ID,sessionId);

                            success=true;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("RxJava", "flatMap catch,==============>线程id:" + Thread.currentThread().getId() + ",error:" + e.toString());
        }
        finally {
            if(success){
                return  action;
            }
            else {
                return Observable.error(new Throwable(THROWABLE_MESSAGE_AUTO_LOGIN_FAILED));
            }
        }
    }

    /**
     * @method name:autoLogin
     * @des:自动登录
     * @param :[service,
     * action,
     * map:登录需要的参数map
     * ]
     * @return type:rx.Observable<?>
     * @date 创建时间:2016/8/22
     * @author Chuck
     **/
    private Observable<?> autoLogin(QDongApi service,HashMap<String, String> map) {

        boolean success=false;//是否获取到了最新的token

        Response<QDongNetInfo> response = null;//定义响应体

        try {
            Call<QDongNetInfo> call = service.login(map);//同步调用,为何要同步?因为我只需要抓取cookie里面的sessionId
            response = call.execute();//执行

            /**
             * 之前是判断获取到sessionId才算自动登录成功,现在改为请求成功就是成功
             */
            if(response!=null&&response.body()!=null&&response.body().isSuccess()){
                success=true;
            }

            LogUtil.e("RxJava", "flatMap,线程id:" + Thread.currentThread().getId() + ",responseResponse:" + response.raw().headers().toString());

            Headers headers = response.raw().headers();//获取头

            String cookiesString = headers.get(SET_COOKIE);

            if(!TextUtils.isEmpty(cookiesString)){
                String[] cookies = cookiesString.split(SPLIT_SIGN);//拆分
                for (String s : cookies) {
                    if (!TextUtils.isEmpty(s)) {
                        if (s.startsWith(SESSION_ID)) {
                            String sessionId = s.substring((s.indexOf(EQUAL_SIGN) + 1));
                            LogUtil.e("RxJava", "==============>获取sessionId成功!--->" + sessionId);
                            RetrofitAPIManager.JSESSIONID = sessionId;//赋值

                            /***
                             * 存起来
                             */
                            SharedPreferencesUtil.getInstance(context).putString(Constants.SESSION_ID,sessionId);

                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.e("RxJava", "flatMap catch,==============>线程id:" + Thread.currentThread().getId() + ",error:" + e.toString());
        }
        finally {
            if(success){
                /**获取成功,返回一个Observable类型,否则不会执行retry*/
                //return  action;
                return  Observable.just(new QDongNetInfo());
            }
            else {
                /**获取失败,抛出指定的异常:重新登陆失败,交给观察者处理**/
                return Observable.error(new Throwable(THROWABLE_MESSAGE_AUTO_LOGIN_FAILED));
            }
        }
    }


    /**
     * @method name:executeTaskAutoRetry
     * @des:执行网络任务,session过期自动登录,然后重试一次
     * 注意:事件源的api必须与第二个参数的api是同一个实例,否则会抛出异常
     * @param :[observable:事件源;api:接口主机; observer:观察者;loginArgumentsMap:自动登录所需的参数map]
     * @return type:rx.Subscription
     * @date 创建时间:2016/10/14
     * @author Chuck
     **/
    public Subscription  executeTaskAutoRetry(
            Observable<QDongNetInfo> observable,
            QDongApi api,
            Observer<QDongNetInfo> observer,
            HashMap<String, String> loginArgumentsMap){

        return  observable
                .subscribeOn(Schedulers.io())//指定被观察者的执行线程
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程,因为如果正常反回了,我要将事件直接交给观察者,观察者通常在主线程
                .flatMap(judgeSessionExpired(observer))//判断session是否过期,如果过期了,会抛出异常到事件流里
                .observeOn(Schedulers.io())//切换到子线程,执行retryWhen,里面有网络请求
                .retryWhen(judgeRetry(api ,loginArgumentsMap ))//触发retry,里面要给一个事件,当前给的是获取加密字符串
                .observeOn(AndroidSchedulers.mainThread())//指定观察者的执行线程
                .subscribe(observer);//订阅
    }

}
