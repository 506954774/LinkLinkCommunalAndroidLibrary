package com.qdong.communal.library.module.PhotoChoose;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.qdong.communal.library.R;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FolderAdapter extends MyAdapter<ImageBean> {

    public FolderAdapter(Context context, ArrayList<ImageBean> objs) {
        super(context, objs);
    }

    @Override
    protected long getItemIndex(int position) {
        return position;
    }

    @Override
    protected View getContentView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo_list_group, parent, false);
            holder.myimage_view = (ImageView) convertView.findViewById(R.id.iv_item_photo_myimage_view);
            holder.folder_text = (TextView) convertView.findViewById(R.id.tv_item_photo_folder_text);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        ImageBean imageBean = objs.get(position);
        if (position == 0) {
            holder.folder_text.setText("");
            holder.myimage_view.setBackgroundResource(R.mipmap.photo_icon);
            holder.myimage_view.setImageResource(R.mipmap.rel_pro);
        } else {
            holder.folder_text.setText(imageBean.getFolderName() + "|" + imageBean.getImageCounts() + "张");
            /** Bugfix-修改图库图片加载方式-20160418-LHD-START */
            Glide.with(getContext()).load("file://" + imageBean.getTopImagePath()).dontAnimate()
                    .into(holder.myimage_view);
            /** Bugfix-修改图库图片加载方式-20160418-LHD-END */
            holder.myimage_view.setBackgroundResource(R.mipmap.albums_bg_nm);
        }
        return convertView;
    }

    private class ViewHolder {
        private ImageView myimage_view;
        private TextView folder_text/* , count_text */;
    }

}
