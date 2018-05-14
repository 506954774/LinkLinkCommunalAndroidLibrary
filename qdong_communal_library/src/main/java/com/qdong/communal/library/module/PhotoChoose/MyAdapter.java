package com.qdong.communal.library.module.PhotoChoose;

import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * adapter 工具类
 *
 * @author Administrator
 *
 * @param <T>
 *            需指定数据对象类型
 *            List的类型全为Yyh所添加.
 */
public abstract class MyAdapter<T> extends BaseAdapter {

    public ArrayList<T> objs;
    public List<T> list_objs;
    public Context context;

    /**
     * 实例化集合
     *
     * @param objs
     */
    public void setobjs(ArrayList<T> objs) {
        if (objs != null) {
            this.objs = objs;
        } else {
            this.objs = new ArrayList<T>();
        }
    }

    /**
     * 实例化集合
     *
     * @param objs
     */
    public void setListObjs(List<T> objs) {
        if (objs != null) {
            this.list_objs = objs;
        } else {
            this.list_objs = new ArrayList<T>();
        }
    }


    /**
     * 替换适配器中的数据集，并刷新liseview
     *
     * @param objs
     */
    public void chageData(ArrayList<T> objs) {
        this.setobjs(objs);
        this.notifyDataSetChanged();
    }

    public void changDate(List<T> objs){
        this.setListObjs(objs);
        this.notifyDataSetChanged();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void chageData(List objs) {
        this.list_objs = objs;
        this.notifyDataSetChanged();
    }

    /**
     * 构造方法，初始时需传入数据对象集和上下文对象
     *
     * @param context
     *            上下文对象
     * @param objs
     *            数据
     */
    public MyAdapter(Context context, ArrayList<T> objs) {
        this.context = context;
        this.setobjs(objs);
    }

    public MyAdapter(Context context, List<T> objs) {
        this.context = context;
        this.setListObjs(objs);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
        if (objs != null) {
            return objs.size();
        }else{
            return list_objs.size();
        }
    }

    @Override
    public T getItem(int position) {
        if (objs != null) {
            return objs.get(position);
        }else{
            return list_objs.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItemIndex(position);
    }

    /**
     * 获取具体对象的id
     *
     * @param position
     *            位置
     * @return 具体对象id
     */
    protected abstract long getItemIndex(int position);

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // 返回item界面
        return getContentView(position, view, parent);
    }

    /**
     * 获取具体的item界面
     *
     * @param position
     *            位置

     *            view对象
     * @param parent
     * @return 需要显示的view对象
     */
    protected abstract View getContentView(int position, View view, ViewGroup parent);

}
