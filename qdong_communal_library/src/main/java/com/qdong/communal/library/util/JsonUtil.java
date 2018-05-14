package com.qdong.communal.library.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * JSON解析工具类
 *
 * @author LHD
 * @Date 2016-02-27 下午13：33
 *
 */
public class JsonUtil {

	/**
	 * 将任意对象生成JSON
	 *
	 * @param object
	 *            需要转换为JSon的字符串
	 * @return 返回JSon串
	 */
	public static String toJson(Object object) {
		return new GsonBuilder().serializeNulls().create().toJson(object);
	}

	/**
	 * 解析单个String类型数据
	 *
	 * @param key
	 *            键
	 * @param json
	 *            json数据
	 * @return
	 * @throws JSONException
	 */
	public static String getString(String json, String key) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getString(key);
	}

	/**
	 * 解析单个Int类型数据
	 *
	 * @param key
	 *            键
	 * @param json
	 *            json数据
	 * @return
	 * @throws JSONException
	 */
	public static int getInt(String json, String key) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getInt(key);
	}

	/**
	 * 解析单个Int类型数据
	 *
	 * @param key
	 *            键
	 * @param json
	 *            json数据
	 * @return
	 * @throws JSONException
	 */
	public static double getDouble(String json, String key) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getDouble(key);
	}

	/**
	 * 解析单个boolean类型数据
	 *
	 * @param key
	 *            键
	 * @param json
	 *            json数据
	 * @return
	 * @throws JSONException
	 */
	public static boolean getBoolean(String json, String key) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getBoolean(key);
	}

	/**
	 * 解析单个long类型数据
	 *
	 * @param json
	 *            json数据
	 * @param key
	 *            键
	 * @return
	 * @throws JSONException
	 */
	public static long getLong(String json, String key) throws JSONException {
		JSONObject jsonObject = new JSONObject(json);
		return jsonObject.getLong(key);
	}

	/**
	 * 解析json中的json实体
	 *
	 * @param result
	 * @param keyName
	 *            json对象键名
	 * @param t
	 * @return
	 * @throws JSONException
	 */
	public static <T> T getObject(String result, String keyName, Class<T> t) throws JSONException {
		JSONObject obj = new JSONObject(result).getJSONObject(keyName);
		return (T) new Gson().fromJson(obj.toString(), t);
	}



	public static <T> T fromJson(JsonElement json, Class<T> t) {
		return new Gson().fromJson(json, t);
	}


	/**
	 * 解析JSON数据
	 *
	 * @param json
	 *            JSON字符串
	 * @param t
	 *            需要解析数据类型
	 * @return 解析完成对应类型
	 */
	public static <T> T fromJson(String json, Class<T> t) {
		return new Gson().fromJson(json, t);
	}

	/**
	 * 解析任意集合对象
	 *
	 * @param json
	 *            JSON字符串
	 * @param name
	 *            JSON数组字段名称
	 * @param t
	 *            泛型
	 * @return
	 * @throws JSONException
	 */
	public static <T> ArrayList<T> toList(String json, String name, Class<T> t) throws JSONException {
		JSONArray array = null;
		if (!TextUtils.isEmpty(name))
			array = new JSONObject(json).getJSONArray(name);
		else
			array = new JSONArray(json);
		ArrayList<T> entitys = new ArrayList<T>();
		Gson gson = new Gson();
		for (int i = 0; i < array.length(); i++) {
			T t1 = (T) gson.fromJson(array.getString(i), t);
			entitys.add(t1);
		}
		return entitys;
	}

	public static <T> ArrayList<T> toList(JsonElement jsonStr, Class<T> t) throws JSONException {
		ArrayList<T> entitys = new ArrayList<T>();
		if (jsonStr == null) {
			return entitys;
		} else {
			JsonArray array = jsonStr.getAsJsonArray();
			Gson gson = new Gson();
			for (int i = 0; i < array.size(); i++) {
				T t1 = (T) gson.fromJson(array.get(i), t);
				entitys.add(t1);
			}
			return entitys;
		}
	}

}
