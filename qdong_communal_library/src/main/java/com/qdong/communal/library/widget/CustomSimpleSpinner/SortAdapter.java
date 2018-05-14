package com.qdong.communal.library.widget.CustomSimpleSpinner;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qdong.communal.library.widget.CustomTagView.Tag;

import java.util.ArrayList;

/**
 * SortAdapter
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/7  19:51
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class SortAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Tag> datas ;

    public SortAdapter(Context context, ArrayList<Tag> datas){
        this.context = context;
        this.datas = datas==null?new ArrayList<Tag>():datas;
    }

    public int getPop_style() {
        return pop_style;
    }

    public void setPop_style(int pop_style) {
        this.pop_style = pop_style;
    }

    private int pop_style = 0;


    public void setDatas(ArrayList<Tag> datas){
        this.datas = datas;
    }

    public ArrayList<Tag> getDatas(){
        return datas;
    }

    @Override
    public int getCount() {
        if(null != datas){
            return datas.size();
        }
        return 0;
    }

    @Override
    public Tag getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if(null == convertView){
            holder = new ViewHolder();
            if (getPop_style() == 0)
            {
                convertView = LayoutInflater.from(context).inflate(
                        com.qdong.communal.library.R.layout.pop_selection_list_item,null);
            }
            else
            {
                convertView = LayoutInflater.from(context).inflate(com.qdong.communal.library.R.layout.pop_selection_list_item2,null);
            }

            holder.item_pic = (ImageView) convertView.findViewById(com.qdong.communal.library.R.id.pop_item_pic);
            holder.item_text = (TextView) convertView.findViewById(com.qdong.communal.library.R.id.pop_item_text);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String text = datas.get(position).getName();
        holder.item_text.setText(text);

        if(datas.get(position).isChecked()){
            if (getPop_style() == 0)
            {
                holder.item_pic.setVisibility(View.VISIBLE);
                holder.item_text.setTextColor(Color.parseColor("#FF36BFC7"));
            }
            else
            {
                holder.item_pic.setSelected(true);
            }

        }else{
            if (getPop_style() == 0)
            {
                holder.item_text.setTextColor(Color.parseColor("#666666"));
                holder.item_pic.setVisibility(View.GONE);
            }
            else
            {
                holder.item_pic.setSelected(false);
            }

        }

        return convertView;
    }

    private class ViewHolder{
        private TextView item_text;
        private ImageView item_pic;
    }
}
