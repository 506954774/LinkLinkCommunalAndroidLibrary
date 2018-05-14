package com.qdong.communal.library.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * 数据格式化
 *
 * @author LHD
 *
 */
public class DecimalUtil {

	/**
	 * 解析距离
	 *
	 * @param value
	 *            距离值 单位km
	 * @return 解析后的带单位的字符串
	 */
	public static String formatDist(double value) {
		if (value < 1) {
			DecimalFormat format = new DecimalFormat("#");
			value = value * 1000;
			return format.format(value) + "m";
		} else {
			DecimalFormat format = new DecimalFormat("#.0");
			return format.format(value) + "km";
		}
	}

	/**
	 * 解析距离
	 *
	 * @param value
	 *            距离值
	 * @return 解析后的不带单位的字符串
	 */
	public static String formatDouble(double value) {
		if (value < 1) {
			DecimalFormat format = new DecimalFormat("#");
			value = value * 1000;
			return format.format(value);
		} else {
			DecimalFormat format = new DecimalFormat("#.0");
			return format.format(value);
		}
	}

	/**
	 * 获取整数
	 *
	 * @param value
	 * @return
	 */
	public static String getInteger(double value) {
		DecimalFormat format = new DecimalFormat("#");
		return format.format(value);
	}

	/**
	 * 获取一位小数
	 *
	 * @param value
	 * @return
	 */
	public static String getOneDecimal(double value) {
		DecimalFormat format = new DecimalFormat("#0.#");
		return format.format(value);
	}

	/**
	 * 获取一位小数
	 *
	 * @param value
	 * @return
	 */
	public static double oneDec(double value) {
		return new BigDecimal(value).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 解析double类型数据并转换为字符串，并拼接上单位
	 *
	 * @param tag
	 *            标签
	 * @param unit
	 *            单位
	 * @param value
	 *            数据
	 * @return
	 */
	public static String format(String tag, String unit, double value) {
		if (value <= 0)
			return tag + "--";
		DecimalFormat format = new DecimalFormat("#0.#");
		return tag + format.format(value) + unit;
	}

	/**
	 * 获取一位小数
	 *
	 * @param value
	 * @return
	 */
	public static String getBigDecimal(double value, int scale) {
		BigDecimal mData = new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP);
		return mData.toString();
	}

	/**
	 * 计算文件M数
	 *
	 * @param fileSize
	 *            文件字节数
	 * @return
	 */
	public static String getFileSize(long fileSize, String pattern) {
		double value = (fileSize * 1.0) / (1024 * 1024);
		return DecimalUtil.getDecimal(value, pattern);
	}

	/**
	 * 根据指定条件进行格式化
	 *
	 * @param value
	 * @return
	 */
	public static String getDecimal(double value, String pattern) {
		DecimalFormat format = new DecimalFormat(pattern);
		return format.format(value);
	}
}
