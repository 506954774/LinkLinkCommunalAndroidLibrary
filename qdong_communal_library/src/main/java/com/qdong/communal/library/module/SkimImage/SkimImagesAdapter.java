package com.qdong.communal.library.module.SkimImage;

import java.util.ArrayList;


import android.annotation.SuppressLint;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.qdong.communal.library.util.BitmapUtil;
import com.qdong.communal.library.util.Constants;

/**
 *
 *
 * @author LHD
 * @Date 2016-02-16
 *
 */

public class SkimImagesAdapter extends PagerAdapter {
	private BaseSkimImageActivity context;
	private ArrayList<String> imgUris;

	public SkimImagesAdapter(BaseSkimImageActivity context, ArrayList<String> imgUris) {
		this.context = context;
		this.imgUris = imgUris;
	}

	@Override
	public int getCount() {
		return imgUris.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@SuppressLint("SdCardPath")
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ZoomImageView imgView = new ZoomImageView(context);
		String path = imgUris.get(position);
		if (path.contains(Environment.getExternalStorageDirectory().getPath())) {
			BitmapUtil.loadPhoto(context, "file://" + path, imgView);
		} else {
			BitmapUtil.loadPhoto(context, Constants.TFS_READ_URL + path, imgView);
		}
		container.addView(imgView, 0);
		return imgView;
	}

}
