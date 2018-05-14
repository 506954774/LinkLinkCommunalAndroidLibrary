package com.qdong.communal.library.util;


import com.qdong.communal.library.module.VersionManager.TaskEntity;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * OkHttpUtil
 *
 * @author LHD
 * @date 2016-06-28 10:26
 *
 */
public class OkHttpUtil {

	public static OkHttpClient httpClient;
	private static final MediaType JSONTYPE = MediaType.parse("application/json; charset=utf-8");
	private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("application/octet-stream");

	static {
		/***
		 * 超时设置
		 */
		httpClient = new OkHttpClient.Builder()
				.connectTimeout(60, TimeUnit.SECONDS)
				.readTimeout(60*10, TimeUnit.SECONDS)
				.writeTimeout(60*10, TimeUnit.SECONDS).build();
		/*httpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(BicycleApplication.getContext()),
				CookiePolicy.ACCEPT_ORIGINAL_SERVER));*/
	}

	/**
	 * 使用GET请求数据
	 *
	 *            请求地址
	 * @return 成功：返回获取到的数据，失败：返回null
	 * @throws Exception
	 */
	public static String doGet(TaskEntity entity) throws Exception {
		Request request = new Request.Builder().url(entity.getUrl()).build();
		Call call = httpClient.newCall(request);
		entity.setCall(call);
		Response response = call.execute();
		if (response.isSuccessful())
			return response.body().string();
		return null;
	}

	/**
	 * 使用POST发送JSON数据到服务器
	 *
	 *            需要传递的数据
	 * @return 成功：返回获取到的数据，失败：返回null
	 * @throws Exception
	 */
	public static String doPost(TaskEntity entity) throws Exception {
		RequestBody post = RequestBody.create(JSONTYPE, entity.getRequest());
		Request request = new Request.Builder().url(entity.getUrl()).post(post).build();
		Call call = httpClient.newCall(request);
		entity.setCall(call);
		Response response = call.execute();
		if (response.isSuccessful())
			return response.body().string();
		return null;
	}





	/**
	 * 使用get请求获取数据
	 *
	 * @param URL
	 *            文件地址
	 */
	public static ResponseBody downFile(String URL) throws Exception {
		Request request = new Request.Builder().url(URL).build();
		Call call = httpClient.newCall(request);
		Response response = call.execute();
		if (response.isSuccessful())
			return response.body();
		return null;
	}
}
