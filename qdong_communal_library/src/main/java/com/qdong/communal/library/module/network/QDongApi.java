package com.qdong.communal.library.module.network;

import com.qdong.communal.library.module.network.custom_gson_parser.YunQiTFSResponse;
import com.qdong.communal.library.util.Constants;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * QDongApi
 * 趣动api接口,Header没有用注解写,header添加的逻辑在RetrofitAPIManager里
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/28  11:19
 * Copyright : 2014-2016 深圳趣动智能科技有限公司-版权所有
 */
public interface QDongApi {

    /********************************************************************************
     * 公用模块
     */
    //获取加密字符串
    @GET(Constants.SERVER_PREFIX + "/app/user/queryCryptKey.do")
    Observable<QDongNetInfo> queryCryptKey();

    //查询系统参数
    @GET(Constants.SERVER_PREFIX + "/app/system/querySysVar.do")
    Observable<QDongNetInfo> querySysVar();

    //查询版本
    @GET(Constants.SERVER_PREFIX + "/app/system/findLatestVersion/{versionName}.do")
    Observable<QDongNetInfo> findLatestVersion(@Path("versionName") String versionName);


    //http://tryingdo.cn/AppServer/app/system/findLatestVersion/2.3.1.do

    //首次登陆
    @POST(Constants.SERVER_PREFIX + "/app/user/firstLogin.do")
    Observable<QDongNetInfo> firstLogin(@Body Map<String, String> map);

    //tokenLogin
    @POST(Constants.SERVER_PREFIX + "/app/user/tokenLogin.do")
    Observable<QDongNetInfo> tokenLogin(@Body Map<String, String> map);


    //登出
    @GET(Constants.SERVER_PREFIX + "/app/user/logout.do")
    Observable<QDongNetInfo> logout();

    //tfs多文件上传  ,注意,这里改了路径了
    @Multipart
    @POST("AppServer/app/file/multipleUpload.do")
    Observable<QDongNetInfo> uploadMultipleFile(@PartMap Map<String, RequestBody> params);

    //tfs多文件上传  ,注意,这里response 变了. 本app会访问两个服务
    @Multipart
    @POST("AppServer/app/file/multipleUpload.do")
    Observable<YunQiTFSResponse> uploadMultipleFile2(@PartMap Map<String, RequestBody> params);

    //tfs多文件上传  ,注意,这里response 变了. 本app会访问两个服务
    @Multipart
    @POST("AppServer/app/file/multipleUpload.do")
    Call<YunQiTFSResponse> uploadMultipleFile3(@PartMap Map<String, RequestBody> params);

    //tfs多文件上传  ,注意,这里response 变了. 本app会访问两个服务
    @Multipart
    @POST("AppServer/app/file/multipleUpload.do")
    Call<ResponseBody> uploadMultipleFile4(@PartMap Map<String, RequestBody> params);


    //登陆,用Call是为了获取响应里的cookie,拿到sessionId,使用token登陆
    @POST(Constants.SERVER_PREFIX + "user/login/v1.do")
    Call<QDongNetInfo> login(@Body Map<String, String> map);//为了获取头里面的sessionId


    //首次登陆
    @POST(Constants.SERVER_PREFIX + "user/login/v1.do")
    Call<QDongNetInfo> callFirstLogin(@Body Map<String, String> map);

    //tokenLogin
    @POST(Constants.SERVER_PREFIX + "/app/user/tokenLogin.do")
    Call<QDongNetInfo> callTokenLogin(@Body Map<String, String> map);



    /****************************************************************************************************************************************************************************************************
     * 骓驰项目用户模块
     */
    //身份认证
    @POST(Constants.SERVER_PREFIX + "/app/user/verifyIdCard.do")
    Observable<QDongNetInfo> verifyIdCard(@Body Map<String, String> map);

    //上传信鸽token
    @POST(Constants.SERVER_PREFIX + "/app/user/updateToken.do")
    Observable<QDongNetInfo> updateToken(@Body Map<String, String> map);

    //支付宝支付(押金,充值)
    @GET(Constants.SERVER_PREFIX + "/app/alipay/pay/{amount}/{type}.do")
    Observable<QDongNetInfo> alipay(@Path("amount") float amount,@Path("type") int type);

    //微信支付(押金,充值)
    @GET(Constants.SERVER_PREFIX + "/app/wx/pay/{amount}/{type}.do")
    Observable<QDongNetInfo> wxpay(@Path("amount") float amount,@Path("type") int type);

    //押金退款
    @GET(Constants.SERVER_PREFIX + "/app/user/refundCash.do")
    Observable<QDongNetInfo> refundCash();

    //账单列表
    @GET(Constants.SERVER_PREFIX + "/app/user/queryBillDetail/{pageIndex}/{pageSize}.do")
    Observable<QDongNetInfo> queryBillDetail(@Path("pageIndex") int pageIndex,@Path("pageSize") int pageSize);


    //座椅档位查询
    @GET(Constants.SERVER_PREFIX + "/app/user/queryGearsList.do")
    Observable<QDongNetInfo> queryGearsList();

    //检查用户权限
    @GET(Constants.SERVER_PREFIX + "/app/user/checkIfCanRent.do")
    Observable<QDongNetInfo> checkIfCanRent();

    //个人中心界面接口
    @GET(Constants.SERVER_PREFIX + "/app/user/queryRideData.do")
    Observable<QDongNetInfo> queryRideData();

    //个人信息
    @GET(Constants.SERVER_PREFIX + "/app/user/queryInfo.do")
    Observable<QDongNetInfo> queryInfo();

    //换头像
    @GET(Constants.SERVER_PREFIX + "/app/user/updateHeadPhoto/{headPhoto}.do")
    Observable<QDongNetInfo> updateHeadPhoto(@Path("headPhoto") String headPhoto);

    //换昵称
    @GET(Constants.SERVER_PREFIX + "/app/user/updateNickName/{nickName}.do")
    Observable<QDongNetInfo> updateNickName(@Path("nickName") String nickName);

    //换手机号码之前检查号码的合法性
    @GET(Constants.SERVER_PREFIX + "/app/user/verifyAccountIfExist/{oldTele}/{newTele}.do")
    Observable<QDongNetInfo> verifyAccountIfExist(@Path("oldTele") String oldTele,@Path("newTele") String newTele);

    //更新pushKey
    @GET(Constants.SERVER_PREFIX + "/app/user/updatePushKey/{registId}.do")
    Observable<QDongNetInfo> updatePushKey(@Path("registId") String registId);


    //更换手机号
    @POST(Constants.SERVER_PREFIX + "/app/user/updateTelephone.do")
    Observable<QDongNetInfo> updateTelephone(@Body Map<String, String> map);

    //换档位
    @GET(Constants.SERVER_PREFIX + "/app/user/updateHeight/{height}/{gears}.do")
    Observable<QDongNetInfo> updateHeight(@Path("height") String height,@Path("gears") int gears);

    //查询账户余额
    @GET(Constants.SERVER_PREFIX + "/app/user/queryBalance.do")
    Observable<QDongNetInfo> queryBalance();

    //插入常用地址
    @POST(Constants.SERVER_PREFIX + "/app/user/usualAdd/create.do")
    Observable<QDongNetInfo> usualAddCreate(@Body Map<String, String> map);

    //查询常用地址
    @GET(Constants.SERVER_PREFIX + "/app/user/queryUsualAdd.do")
    Observable<QDongNetInfo> queryUsualAdd();

    //查询Q&A
    @GET(Constants.SERVER_PREFIX + "/app/system/queryQAList.do")
    Observable<QDongNetInfo> queryQAList();

    /*********************************************************************************************
     * 微信登陆，这里是腾讯的接口
     */
    // 获取access_token
    @GET("sns/oauth2/access_token")
    Call<WechatAuth> getAccessToken(@Query("appid") String appid, @Query("secret") String secret, @Query("code") String code,@Query("grant_type") String grant_type);

    // 获取userinfo
    @GET("sns/userinfo")
    Call<WechatUserinfo> getUserinfo(@Query("access_token") String access_token, @Query("openid") String openid);


    /**************************************************************************************************
     * 开锁解锁
     *
     */
    //上传80字节索引
    @POST(Constants.SERVER_PREFIX + "/AppAdmin/aw/device/create.do")
    Observable<QDongNetInfo> create(@Body Map<String, String> map);

    //获取解锁秘钥
    @POST(Constants.SERVER_PREFIX + "/AppAdmin/aw/device/queryEncryptInfoV3.do")
    Observable<QDongNetInfo> queryEncryptInfoV3(@Body Map<String, String> map);

    //获取解锁秘钥
    @POST(Constants.SERVER_PREFIX + "/app/system/queryEncryptKey.do")
    Observable<QDongNetInfo> queryEncryptKey(@Body Map<String, String> map);

    //获取上锁锁秘钥
    @POST(Constants.SERVER_PREFIX + "/app/system/queryCurTransAndEncryptKey.do")
    Observable<QDongNetInfo> queryCurTransAndEncryptKey(@Body Map<String, String> map);

    //更新交易记录
    @POST(Constants.SERVER_PREFIX + "/app/dev/updateDevTransInfo.do")
    Observable<QDongNetInfo> updateDevTransInfo(@Body Map<String, String> map);

    //gsm解锁
    @GET(Constants.SERVER_PREFIX + "/app/dev/updateUnLockStatus/{devId}/{seatLevel}.do")
    Observable<QDongNetInfo> updateUnLockStatus(@Path("devId") String devId,@Path("seatLevel") int seatLevel);

    //gsm上锁
    @GET(Constants.SERVER_PREFIX + "/app/dev/updateLockStatus/{transId}.do")
    Observable<QDongNetInfo> updateLockStatus(@Path("transId") int transId);

    //gsm解锁状态查询
    @GET(Constants.SERVER_PREFIX + "/app/device/queryDevLockStatus/{transId}.do")
    Observable<QDongNetInfo> queryDevLockStatus(@Path("transId") int transId);

    //gsm解锁状态查询
    @GET(Constants.SERVER_PREFIX + "/app/dev/queryTransLockStatus/{transId}.do")
    Observable<QDongNetInfo> queryTransLockStatus(@Path("transId") int transId);

    //gsm解锁状态查询
    @GET(Constants.SERVER_PREFIX + "/app/dev/queryTransLockStatus/{transId}/{type}.do")
    Observable<QDongNetInfo> queryTransLockStatus(@Path("transId") int transId,@Path("type") int type);

    //车牌号获取蓝牙ID
    @GET(Constants.SERVER_PREFIX + "/app/dev/queryDeviceBlueId/{devCarNo}.do")
    Observable<QDongNetInfo> queryDeviceBlueId(@Path("devCarNo") String devCarNo);

    //查询当前交易ID
    @GET(Constants.SERVER_PREFIX + "/app/user/queryCurTransId.do")
    Observable<QDongNetInfo> queryCurTransId();




    /**************************************************************************************************
     * 车辆预约
     *
     */
    //预约单车
    @POST(Constants.SERVER_PREFIX + "/app/dev/devReserve.do")
    Observable<QDongNetInfo> devReserve(@Body Map<String, String> map);

    //取消预约
    @GET(Constants.SERVER_PREFIX + "/app/dev/cancelReserve/{reserveId}.do")
    Observable<QDongNetInfo> cancelReserve(@Path("reserveId") int reserveId);

    //获取预约信息
    @GET(Constants.SERVER_PREFIX + "/app/dev/queryDevReserve.do")
    Observable<QDongNetInfo> queryDevReserve();







    /**************************************************************************************************
     * 车辆骑行位置数据
     *
     */
    //查询所有设备位置
    @POST(Constants.SERVER_PREFIX + "/app/dev/queryDevLocationList.do")
    Observable<QDongNetInfo> queryDevLocationList(@Body Map<String, String> map);

    //上传gps
    @POST(Constants.SERVER_PREFIX + "/app/dev/createGps.do")
    Observable<QDongNetInfo> createGps(@Body Map<String, String> map);

    //查询设备骑行数据
    @GET(Constants.SERVER_PREFIX + "/app/dev/queryTransStat/{transId}.do")
    Observable<QDongNetInfo> queryTransStat(@Path("transId") int transId);

    //查询设备骑行GPS
    @GET(Constants.SERVER_PREFIX + "/app/dev/queryTransGpsList/{transId}.do")
    Observable<QDongNetInfo> queryTransGpsList(@Path("transId") int transId);

    //骑行行程历史
    @GET(Constants.SERVER_PREFIX + "/app/dev/queryUsrTransList/{pageIndex}/{pageSize}.do")
    Observable<QDongNetInfo> queryUsrTransList(@Path("pageIndex") int pageIndex,@Path("pageSize") int pageSize);



    /**************************************************************************************************
     * 车辆举报,维护相关
     *
     */
    //查询单车编号
    @GET(Constants.SERVER_PREFIX + "/app/dev/getDevCarNo/{blueId}.do")
    Observable<QDongNetInfo> getDevCarNo(@Path("blueId") String blueId);

    //车辆报错
    @POST(Constants.SERVER_PREFIX + "/app/user/report/create.do")
    Observable<QDongNetInfo> createReport(@Body Map<String, String> map);

    /**************************************************************************************************
     * 活动,推送
     *
     */
    //查询活动
    @GET(Constants.SERVER_PREFIX + "/app/activity/queryActivity.do")
    Observable<QDongNetInfo> queryActivity();



    /**************************************************************************************************
     * 云骑售后app
     *
     */
    //查询设备信息
    @GET(Constants.SERVER_PREFIX + "dev/info/{imei}/v1.do")
    Observable<QDongNetInfo> queryDeviceDetail(@Path("imei") String imei);

    //总体报表
    @GET(Constants.SERVER_PREFIX + "report/ensemble/v1.do")
    Observable<QDongNetInfo> ensemble();

    //日报
    @GET(Constants.SERVER_PREFIX + "report/daily/v1.do")
    Observable<QDongNetInfo> daily();

    //月报销售
    @GET(Constants.SERVER_PREFIX + "report/monthly/sale/v1.do")
    Observable<QDongNetInfo> monthlySale();

    //月报售后
    @GET(Constants.SERVER_PREFIX + "report/monthly/after/sale/v1.do")
    Observable<QDongNetInfo> monthlyAfterSale();

    //月报激活
    @GET(Constants.SERVER_PREFIX + "report/monthly/active/v1.do")
    Observable<QDongNetInfo> monthlyActive();

    //月报欠费
    @GET(Constants.SERVER_PREFIX + "report/monthly/owe/v1.do")
    Observable<QDongNetInfo> monthlyOwe();

    //月报保险
    @GET(Constants.SERVER_PREFIX + "report/monthly/insur/v1.do")
    Observable<QDongNetInfo> monthlyInsur();

    //月报骑友圈
    @GET(Constants.SERVER_PREFIX + "report/monthly/ride/v1.do")
    Observable<QDongNetInfo> monthlyRide();

    //获取保险类型
    @GET(Constants.SERVER_PREFIX + "dev/insru/list/v1.do")
    Observable<QDongNetInfo> getInsruTypeList();

    //获取流量套餐类型
    @GET(Constants.SERVER_PREFIX + "dev/flow/list/v1.do")
    Observable<QDongNetInfo> getFlowTypeList();

    //售后信息入库
    @POST(Constants.SERVER_PREFIX + "sale/info/insert/v1.do")
    Observable<QDongNetInfo> insert(@Body Map<String, Object> map);

    //仓库发货
    @POST(Constants.SERVER_PREFIX + "dev/deliver/v1.do")
    Observable<QDongNetInfo> deliver(@Body Map<String, Object> map);

    //发货
    @POST(Constants.SERVER_PREFIX + "sale/send/info/insert/v1.do")
    Observable<QDongNetInfo> send(@Body Map<String, Object> map);

    //指令
    @POST(Constants.SERVER_PREFIX + "dev/command/v1.do")
    Observable<QDongNetInfo> command(@Body Map<String, String> map);




    //查询设备附加信息,灯,电量
    @GET(Constants.SERVER_PREFIX + "dev/ref/{imei}/v1.do")
    Observable<QDongNetInfo> queryDeviceRef(@Path("imei") String imei);

    //登出
    @GET(Constants.SERVER_PREFIX + "user/logout/v1.do")
    Observable<QDongNetInfo> logout2();

    //获取设备列表
    @GET(Constants.SERVER_PREFIX + "dev/list/{keyWords}/v1.do")
    Observable<QDongNetInfo> getDevList(@Path("keyWords") String keyWords);

    //获取设备列表
    @GET(Constants.SERVER_PREFIX + "dev/unBind/{deviceId}/v1.do")
    Observable<QDongNetInfo> unBind(@Path("deviceId") long deviceId);

    //套餐更新
    @GET(Constants.SERVER_PREFIX + "dev/fee/model/update/{devId}/{feeModel}/v1.do")
    Observable<QDongNetInfo> updateFeeModel(@Path("devId") long devId,@Path("feeModel") long feeModel);

    //客户类型更新
    @GET(Constants.SERVER_PREFIX + "dev/buyer/update/{devId}/{buyerType}/v1.do")
    Observable<QDongNetInfo> updateBuyerType(@Path("devId") long devId,@Path("buyerType") long buyerType);

    //更新告警设置
    @GET(Constants.SERVER_PREFIX + "dev/alarm/update/{accId}/{alarm}/{alarmSound}/v1.do")
    Observable<QDongNetInfo> updateAlarm(@Path("accId") long accId,@Path("alarm") int alarm,@Path("alarmSound") int alarmSound);

    //更新设防,灯
    @GET(Constants.SERVER_PREFIX + "dev/update/status/{devId}/{authDefense}/{light}/v1.do")
    Observable<QDongNetInfo> updateLightStatus(@Path("devId") long devId,@Path("authDefense") int authDefense,@Path("light") int light);

    //撤销为未激活
    @GET(Constants.SERVER_PREFIX + "dev/delete/pred/charge/{devId}/v1.do")
    Observable<QDongNetInfo> clearActiveStatus(@Path("devId") long devId);

    //密码重置
    @GET(Constants.SERVER_PREFIX + "user/update/pwd/{accId}/v1.do")
    Observable<QDongNetInfo> resetPassword(@Path("accId") long accId);

    //售后待发货
    @GET(Constants.SERVER_PREFIX + "sale/info/list/{pageindex}/{pageSize}/v1.do")
    Observable<QDongNetInfo> getOrders2Send(@Path("pageindex") int pageindex,@Path("pageSize") int pageSize);

    //售后历史
    @GET(Constants.SERVER_PREFIX + "sale/send/info/list/{pageindex}/{pageSize}/v1.do")
    Observable<QDongNetInfo> getOrdersHistory(@Path("pageindex") int pageindex,@Path("pageSize") int pageSize);

    //工厂发货历史
    @GET(Constants.SERVER_PREFIX + "sale/deliver/info/list/{pageIndex}/{pageSize}/v1.do")
    Observable<QDongNetInfo> getDeliverHistory(@Path("pageIndex") int pageIndex,@Path("pageSize") int pageSize);

    //更新差异
    @GET(Constants.SERVER_PREFIX + "report/update/diff/{devDiff}/v1.do")
    Observable<QDongNetInfo> diff(@Path("devDiff") int devDiff);

    //工厂发货详情
    @GET(Constants.SERVER_PREFIX + "sale/deliver/detail/{trackNo}/v1.do")
    Observable<QDongNetInfo> getDeliverDetails(@Path("trackNo") String trackNo);

    //确认收到货
    @GET(Constants.SERVER_PREFIX + "sale/confirm/{saleId}/v1.do")
    Observable<QDongNetInfo> receivedConfirm(@Path("saleId") int saleId);









    //云骑----------------------------------------获取设备最后的轨迹
    @GET("/AppServer/app/dev/track/locs/latestTrkV1/{deviceId}.do")
    Observable<QDongNetInfo> latestTrkV1(@Path("deviceId") long deviceId);

    //云骑----------------------------------------获取设备轨迹
    @GET("/AppServer/app/dev/trackInWeb/locs/{devId}/{st}/{et}.do")
    Observable<QDongNetInfo> gpsList(@Path("devId") long devId,@Path("st") long st,@Path("et") long et);

    //云骑----------------------------------------获取轨迹列表
    @POST("/AppServer/app/dev/track/listV1.do")
    Observable<QDongNetInfo> trackList(@Body Map<String, String> map);


}
