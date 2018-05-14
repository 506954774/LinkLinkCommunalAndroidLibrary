package com.qdong.communal.library.module.SkimImage;

import android.content.ContentValues;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qdong.communal.library.R;
import com.qdong.communal.library.module.base.BaseActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * BaseSkimImageActivity
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/11/10  18:21
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class BaseSkimImageActivity   extends BaseActivity{

    public static  final String POSITION="POSITION";
    public static  final String IAMGE_PATH="IAMGE_PATH";


    protected ViewPager viewPager;
    protected SkimImagesAdapter adapter;
    protected ArrayList<String> list;// 存放url或id集合
    protected TextView currentPos;
    protected TextView totalNum;
    protected int selectPos;
    protected LinearLayout mLlTitleContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ft_skim_images);
        loadData();
    }




    public void loadData() {
        try {
            initView();
            initListener();
            getIntentData();
        } catch (Exception e) {

        }
    }

    /**
     * 取得intent传过来的数据
     */
    private void getIntentData() {
        selectPos = getIntent().getIntExtra(POSITION, 0);// 点击图片的位置
        list = getIntent().getStringArrayListExtra(IAMGE_PATH);

        if (list != null && list.size() > 0) {
            adapter = new SkimImagesAdapter(this, list);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(selectPos);
            totalNum.setText("/" + list.size());
            int p = selectPos + 1;
            currentPos.setText(p + "");
        }

    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.skim_imgs_viewpager);
        currentPos = (TextView) findViewById(R.id.skim_current_image);
        totalNum = (TextView)  findViewById(R.id.skim_total_images);
        mLlTitleContainer = (LinearLayout) findViewById(R.id.rl_photo_top);
        if (getTitleView() != null) {
            mLlTitleContainer.addView(getTitleView());
        }
    }

    private void initListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                selectPos = position;
                int p = position + 1;
                currentPos.setText(p + "");
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    public void onLongClick(){

    }


    /**
     * 给系统发送广播，通知sdcard更新
     */

    public void scanDirAsync(File file) {
        try {
            // 增加Android 内部媒体索引
            ContentValues values = new ContentValues();
            values.put("mime_type", "image/jpeg");
            values.put("_data", file.getAbsolutePath());
            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            // 刷新filePath的上一级目录
            MediaScannerConnection.scanFile(this,
                    new String[] { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
                            + "/" + file.getParent() },
                    null, null);
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        if (viewPager != null) {
            viewPager.clearAnimation();
            viewPager.removeAllViews();
            viewPager = null;
        }
        super.onDestroy();
    }









    /***
     * 子类提供titleview,里面可以加回退和标题,取消
     **/
    public abstract View getTitleView();










}
