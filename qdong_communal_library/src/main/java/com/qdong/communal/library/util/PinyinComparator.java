package com.qdong.communal.library.util;

import com.qdong.communal.library.module.CitySelect.CityModel;

import java.util.Comparator;

/**
 * 比较器
 * 
 * headnear_1.5
 * com.ztb.handnear.utils
 * PinyinComparator.java
 * 责任人:  Chuck
 * 创建/修改时间: 2015年7月13日-下午5:13:01
 */
public class PinyinComparator implements Comparator<CityModel> {

	@Override
	public int compare(CityModel lhs,CityModel rhs) {
		// TODO Auto-generated method stub
		if (lhs.getSortLetters().equals("@")
				|| rhs.getSortLetters().equals("#")) {
			return -1;
		} else if (lhs.getSortLetters().equals("#")
				|| rhs.getSortLetters().equals("@")) {
			return 1;
		} else {
			return lhs.getSortLetters().compareTo(rhs.getSortLetters());
		}
	}

}
