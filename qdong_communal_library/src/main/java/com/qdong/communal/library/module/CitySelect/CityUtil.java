package com.qdong.communal.library.module.CitySelect;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 城市工具对象
 *
 * @author LHD
 * @Date 2016-02-19 晚上 20:49
 *
 */
public class CityUtil {


	/**
	 * @method name:getCityList
	 * @des:最好在子线程执行
	 * @param :[context]
	 * @return type:java.util.ArrayList<com.qdong.communal.library.module.CitySelect.CityModel>
	 * @date 创建时间:2016/9/10
	 * @author Chuck
	 **/
	public static ArrayList<CityModel> getCityList(Context context) {

		ArrayList<CityModel> mDatas=new ArrayList<CityModel>();

		SQLiteDatabase db = CreateDB.toDB(context);
		Cursor cursor = db.query(CreateDB.TABLE_NAME_CITY,null,null,null,null,null,null);

		if (cursor != null) {

			while (cursor.moveToNext()) {
				CityModel bean = new CityModel();
				bean.setPr(cursor.getString(cursor.getColumnIndex("pr")));
				bean.setCity(cursor.getString(cursor.getColumnIndex("city")));
				bean.setCode(cursor.getString(cursor.getColumnIndex("code")));
				mDatas.add(bean);
			}
			cursor.close();
		}

		db.close();
		db = null;
		return mDatas;
	}


	public static String getCity(Context context, String cityCode) {
		String city = null;
		String sql = "select city from map where code=" + "\"" + cityCode + "\"";
		SQLiteDatabase db = CreateDB.toDB(context);
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				city = cursor.getString(0);
			}
			cursor.close();
		}
		db.close();
		db = null;
		return city;
	}


	public static String getCityCode(Context context, String city) {
		if(TextUtils.isEmpty(city)){
			return "101280601";
		}
		else{
			if (city.length() > 2) {
				if (city.contains("市")) {
					city = city.substring(0, city.length() - 1);
				}
			}
		}

		String code = null;
		String sql = "select code from map where city=" + "\"" + city + "\"";
		SQLiteDatabase db = CreateDB.toDB(context);
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				code = cursor.getString(0);
			}
			cursor.close();
		}
		db.close();
		db = null;
		return code;
	}


	// public static void update(Context context, String city, String cityCode)
	// {
	// boolean flag = false;
	// String sql = "select city from map where city='" + city + "'";
	// SQLiteDatabase db = CreateDB.toDB(context);
	// Cursor cursor = db.rawQuery(sql, null);
	// if (cursor != null && cursor.moveToNext()) {
	// flag = true;
	// cursor.close();
	// }
	// if (flag) {
	// sql = "UPDATE map SET cityCode = '" + cityCode + "' WHERE city = '" +
	// city + "'";
	// db.execSQL(sql);
	// }
	// db.close();
	// }
}
