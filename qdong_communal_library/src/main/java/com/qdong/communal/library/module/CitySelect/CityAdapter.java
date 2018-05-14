package com.qdong.communal.library.module.CitySelect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.qdong.communal.library.R;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.SharedPreferencesUtil;
import com.qdong.communal.library.widget.CustomGridView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * CityAdapter
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/10  16:19
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class CityAdapter   extends BaseAdapter implements SectionIndexer {

    private ArrayList<CityModel> mAllCitys;
    private ArrayList<CityModel> mRecentlyCitys;
    private ArrayList<CityModel> mHotCitys;
    private OnCitySelectedListener mListener;

    private Context mContext;
    private SubGridViewAdapter mRencentySelectedCityAdapter;
    private SubGridViewAdapter mHotCityAdapter;
    private CityModel mCurrentModel;
    private boolean mIsShowRecentAndHotCitys=true;

    public CityAdapter(Context mContext,ArrayList<CityModel> mAllCitys,CityModel mCurrentModel,OnCitySelectedListener mListener) {
        this.mAllCitys = mAllCitys==null?new ArrayList<CityModel>():mAllCitys;
        this.mContext = mContext;
        this.mCurrentModel=mCurrentModel;
        this.mListener=mListener;
        mRecentlyCitys=getRecentyCity();
        mHotCitys=getHotCitys();
        mRencentySelectedCityAdapter=new SubGridViewAdapter(mRecentlyCitys);
        mHotCityAdapter=new SubGridViewAdapter(mHotCitys);

    }

    private ArrayList<CityModel> getHotCitys() {
        String [] citys={"北京","上海","广州","深圳","武汉","长沙","重庆","天津"};

        ArrayList <CityModel> cityModels=new ArrayList<>();
        for(String s :citys){
            CityModel cityModel=new CityModel();
            cityModel.setCity(s);
            cityModel.setCode(CityUtil.getCityCode(mContext,cityModel.getCity()));
            cityModels.add(cityModel);
        }
        return cityModels;
    }

    private ArrayList<CityModel> getRecentyCity() {

        String[] citys = getStrings();

        ArrayList <CityModel> cityModels=new ArrayList<>();
        for(String s :citys){
            CityModel cityModel=new CityModel();
            cityModel.setCity(s);
            cityModel.setCode(CityUtil.getCityCode(mContext,cityModel.getCity()));
            cityModels.add(cityModel);
        }
        return cityModels;
    }

    /**
     * @method name:getStrings
     * @des:获取城市名数组(最近使用过的)
     * @param :[]
     * @return type:java.lang.String[]
     * @date 创建时间:2016/9/10
     * @author Chuck
     **/
    @NonNull
    private String[] getStrings() {

        String [] citys=null;

        String last= SharedPreferencesUtil.getInstance(mContext).getString(Constants.RECENTLY_CITY,null);
        if(TextUtils.isEmpty(last)){//为空
            if(mCurrentModel!=null){
                SharedPreferencesUtil.getInstance(mContext).putString(Constants.RECENTLY_CITY,mCurrentModel.getCity());
                citys =new String[]{mCurrentModel.getCity()};
            }
            else {
                SharedPreferencesUtil.getInstance(mContext).putString(Constants.RECENTLY_CITY,"深圳");
                citys =new String[]{"深圳"};
            }
        }
        else {//拆分为数组
            citys=last.split(",");
        }
        return citys;
    }


    @Override
    public int getCount() {
        return mAllCitys.size()+2;
    }

    @Override
    public CityModel getItem(int position) {
        int index=position-2;
        if(index>=0&&index<mAllCitys.size()){
            return  mAllCitys.get(index);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }





    /**
     * 当ListView数据发生变化时更新ListView
     *
     * @param list
     */
    public void refreshListView(ArrayList<CityModel> list) {
        //this.mAllCitys.clear();
        mAllCitys=list;
        notifyDataSetChanged();
    }

    /**
     * 当ListView数据发生变化时更新ListView
     *
     * @param list
     */
    public void refreshListView(ArrayList<CityModel> list,boolean mIsShowRecentAndHotCitys) {
        //this.mAllCitys.clear();//这里不要使用addAll去给值,因为实际调用的时候,有两个集合,一个是总集合,一个是sub集合,如果直接addAll,则还原不了原总数据了
        mAllCitys=list;
        this.mIsShowRecentAndHotCitys=mIsShowRecentAndHotCitys;
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder holder = null;
        int index=position-2;

        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.listview_item_city_select
                    , null);
            holder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            holder.tvNickName = (TextView) view.findViewById(R.id.tv_nickname);

            holder.mLlSubs= (LinearLayout) view.findViewById(R.id.rl_hot);
            holder.mLlItems= (LinearLayout) view.findViewById(R.id.ll_citys);

            holder.mHint= (TextView) view.findViewById(R.id.tv_hint);
            holder.mGv= (CustomGridView) view.findViewById(R.id.gridview);

            view.setTag(holder);


        } else {
            holder = (ViewHolder) view.getTag();
        }


        if(position==0){//最近使用的城市
              holder.mLlSubs.setVisibility(View.VISIBLE);
              holder.mLlItems.setVisibility(View.GONE);
              holder.mHint.setText(mContext.getString(R.string.recently_use));
              holder.mGv.setAdapter(mRencentySelectedCityAdapter);

              /**根据外界提供的变量,决定是否展示最近使用的城市和热门城市 by:chuck 2016/09/12***/
              holder.mLlSubs.setVisibility(mIsShowRecentAndHotCitys?View.VISIBLE:View.GONE);

        }
        else if(position==1){//热门城市
            holder.mLlSubs.setVisibility(View.VISIBLE);
            holder.mLlItems.setVisibility(View.GONE);
            holder.mHint.setText(mContext.getString(R.string.hot_city));
            holder.mGv.setAdapter(mHotCityAdapter);

            /**根据外界提供的变量,决定是否展示最近使用的城市和热门城市 by:chuck 2016/09/12***/
            holder.mLlSubs.setVisibility(mIsShowRecentAndHotCitys?View.VISIBLE:View.GONE);
        }
        else {
                    final CityModel bean = mAllCitys.get(index);

                    holder.mLlSubs.setVisibility(View.GONE);
                    holder.mLlItems.setVisibility(View.VISIBLE);
                    // 根据position获取分类的首字母的Char ascii值
                    int section = getSectionForPosition(index);
                    // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
                    if (index == getPositionForSection(section)) {
                        holder.tvLetter.setVisibility(View.VISIBLE);
                        holder.tvLetter.setText(bean.getSortLetters());
                    } else {
                        holder.tvLetter.setVisibility(View.GONE);
                    }

                    holder.tvNickName.setText(bean.getCity());


                    holder.mLlItems.setTag(bean);
                    holder.mLlItems.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CityModel model= (CityModel) v.getTag();
                            if(model!=null){
                                saveRecentlySelectedCityNames(model);
                                if(mListener!=null){
                                    mListener.onCitySelected(model);
                                }
                            }

                        }
                    });
        }



        return view;
    }

    private class ViewHolder {

        LinearLayout mLlSubs;
        LinearLayout   mLlItems;

        TextView      mHint;
        CustomGridView  mGv;

        TextView tvLetter;// 昵称首字母
        TextView tvNickName;// 好友昵称

    }



    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(position>=0&&position<mAllCitys.size()){
            return mAllCitys.get(position).getSortLetters().charAt(0);
        }
        return -1;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    @SuppressLint("DefaultLocale")
    public int getPositionForSection(int section) {
        for (int i = 0; i <mAllCitys.size(); i++) {
            String sortStr = mAllCitys.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }


    private class SubGridViewAdapter extends  BaseAdapter{
        ArrayList<CityModel> datas;

        public SubGridViewAdapter(ArrayList<CityModel> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.gridview_item_city,null);
            TextView textView= (TextView) convertView.findViewById(R.id.tv_current_city);
            textView.setText(datas.get(position).getCity());
            textView.setTag(datas.get(position));

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CityModel model= (CityModel) v.getTag();
                    if(model!=null){
                        saveRecentlySelectedCityNames(model);
                        /**点击事件传递**/
                        if(mListener!=null){
                            mListener.onCitySelected(model);
                        }
                    }

                }
            });
            return convertView;
        }
    }

    /**
     * @method name:saveRecentlySelectedCityNames
     * @des:保存最近使用过的城市name,用","拼接,最后加入的在最前面
     * @param :[model]
     * @return type:void
     * @date 创建时间:2016/9/10
     * @author Chuck
     **/
    private void saveRecentlySelectedCityNames(CityModel model) {

        if(model==null){
            return;
        }

        String[] before=getStrings();

        if(before==null||before.length==0){//一个也没有
            SharedPreferencesUtil.getInstance(mContext).putString(Constants.RECENTLY_CITY,model.getCity());
        }
        else  {//之前就有

            ArrayList<String> temp=new ArrayList<String>();//转为集合,方便操作

            int count=before.length<=4?before.length:4;//取最多四个


            for (int i=0;i<count;i++){
                temp.add(before[i]);
            }

            boolean isExsit=false;
            int index=-1;

            for (int i=0;i<count;i++){
                if(model.getCity().equals(temp.get(i))){
                    index=i;
                    isExsit=true;
                }
            }

            if(isExsit){//之前就存在,修改顺序
                 temp.remove(index);
                 temp.add(0,model.getCity());
            }
            else {//之前不存在
                 temp.add(0,model.getCity());
            }

            int newCount=temp.size()<=4?temp.size():4;//取最多四个
            //转为数组
            String [] result= (String[]) temp.toArray(new String[0]);

            StringBuilder sb =new StringBuilder();
            for (int i=0;i<newCount;i++){
                 sb.append(result[i]);
                 if(i!=newCount-1){
                     sb.append(",");//以","拼接起来
                 }
            }
            /**存起来*/
            SharedPreferencesUtil.getInstance(mContext).putString(Constants.RECENTLY_CITY,sb.toString());
        }
    }

}
