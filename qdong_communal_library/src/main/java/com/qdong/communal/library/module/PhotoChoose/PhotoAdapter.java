package com.qdong.communal.library.module.PhotoChoose;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.qdong.communal.library.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 相机图片显示适配器
 *
 * @author LHD
 *
 */
public class PhotoAdapter extends MyAdapter<String> {
    private Context context;
    private ArrayList<String> addedPath;

    public PhotoAdapter(Context context, ArrayList<String> objs, ArrayList<String> addedPath) {
        super(context, objs);
        this.context = context;
        this.addedPath = addedPath;
    }

    public void changeData(ArrayList<String> objs, ArrayList<String> addedPath) {
        this.addedPath = addedPath;
        super.chageData(objs);
    }

    @Override
    protected long getItemIndex(int position) {
        return position;
    }

    @Override
    protected View getContentView(int position, View view, ViewGroup parent) {
        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_photo_grid_img, parent, false);
            holder = new ViewHolder();
            holder.grid_image = (ImageView) view.findViewById(R.id.grid_image);
            holder.grid_img = (ImageView) view.findViewById(R.id.grid_img);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String path = getItem(position);
        if (addedPath != null && addedPath.contains(path)) {
            holder.grid_img.setImageResource(R.mipmap.pic_select_sel);
        } else {
            holder.grid_img.setImageResource(R.mipmap.pic_select_no);
        }
        holder.grid_img.setVisibility(View.VISIBLE);
        /** Bugfix-修改图库图片加载方式-20160418-LHD-START */
        Glide.with(getContext()).load("file://" + path).dontAnimate().into(holder.grid_image);
        /** Bugfix-修改图库图片加载方式-20160418-LHD-END */
        return view;
    }

    private class ViewHolder {
        private ImageView grid_image;
        private ImageView grid_img;
    }

}
