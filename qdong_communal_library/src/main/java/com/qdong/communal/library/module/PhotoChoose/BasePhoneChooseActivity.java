package com.qdong.communal.library.module.PhotoChoose;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qdong.communal.library.R;
import com.qdong.communal.library.module.base.BaseActivity;
import com.qdong.communal.library.util.BitmapUtil;
import com.qdong.communal.library.util.CharacterParser;
import com.qdong.communal.library.util.Constants;
import com.qdong.communal.library.util.DateFormatUtil;
import com.qdong.communal.library.util.FileUtils;
import com.qdong.communal.library.util.LogUtil;
import com.qdong.communal.library.util.ToastUtil;
import com.qdong.communal.library.widget.CustomMaskLayerView.CustomMaskLayerView;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * BasePhoneChooseActivity
 * <p/>
 * 创建/修改时间: 2016/8/29  17:57
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public abstract class BasePhoneChooseActivity extends BaseActivity {

    public static final String SURPLUS_COUNT = "SURPLUS_COUNT";//传值,最多几张图
    public static final String NEED_COMPRESS = "NEED_COMPRESS";//是否需要压缩,默认true
    private static final long FILE_LENGTH_LIMIT = 1 * 1024*1024;//1M,这个单位是B

    private GridView gridview; // 显示图片
    private TextView group_text, total_text; // 目录名称和图片张数
    private GridView group_listview; // 显示目录


    private CustomMaskLayerView mLoadingView;//loadingView
    private LinearLayout mLlTitleContainer;//标题栏的父容器
    // 文件夹对应的图片路径集合
    private HashMap<String, ArrayList<String>> mGruopMap = new HashMap<String, ArrayList<String>>();
    // 文件夹对象集合
    private ArrayList<ImageBean> imgBeanLists = new ArrayList<ImageBean>();
    // 所有的图片
    private ArrayList<String> mAllImgs = new ArrayList<String>();

    private final static int NULL_IMGS = 0;// 没有图片信息
    private final static int SCAN_OK = 1; // 搜索所有图片OK
    private final static int SCAN_FOLDER_OK = 2; // 搜索文件夹OK
    private final static int SKIP_CAMERA = 3; // 跳转相机
    private final static int SCAN_ALL_PIC = 4;// 显示所有图片
    // 显示图片目录listView父View
    private RelativeLayout list_layout;
    // 相册文件夹显示目录视频器
    private FolderAdapter listAdapter;

    private int surplus_count = 5; // 剩余添加图片数
    private int select_folder_count = 0; // 选择的目录
    private Animation toUp, toDown; // 目录视图显示和隐藏动画
    private ArrayList<String> addedPath = new ArrayList<String>(); // 选中图片集合
    private ArrayList<String> nowStrs = new ArrayList<String>(); // 需要显示的图片路径集合
    private PhotoAdapter photoAdapter; // 显示图片

    private boolean isGroupShow = true; // 目录是否布局显示

    private final String name = DateFormatUtil.getDate("yyyyMMddHHmmss") + ".jpg";
    private final String path = BitmapUtil.getSDcard() + "/" + name;

    // 图片加载刷新
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            dismiss();
            switch (msg.what) {
                case SCAN_OK:
                    listAdapter.chageData(imgBeanLists);
                    break;
                case SCAN_FOLDER_OK:
                    photoAdapter.changeData(nowStrs, addedPath);
                    break;
                case SCAN_ALL_PIC:
                    photoAdapter.changeData(mAllImgs, addedPath);
                    break;
                case NULL_IMGS:
                    ToastUtil.showCustomMessage(BasePhoneChooseActivity.this, getString(R.string.no_photos));

                case SKIP_CAMERA:
                    // 写跳转相机代码
                    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(path)));
                    startActivityForResult(intent, 200);
                    break;
            }
        }
    };
    private boolean mNeedCompress=true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initView();
        scanAll();
        initData();
        setListener();
    }


    private void initView() {
        gridview = (GridView) findViewById(R.id.gv_photo_imgs);
        gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        group_text = (TextView) findViewById(R.id.tv_photo_total_group);
        total_text = (TextView) findViewById(R.id.tv_photo_total_number);
        group_listview = (GridView) findViewById(R.id.lv_photo_groups);
        group_listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        list_layout = (RelativeLayout) findViewById(R.id.rl_photo_list_layout);
        mLoadingView = (CustomMaskLayerView) findViewById(R.id.loading_view);
        mLoadingView.setTransparentMode(CustomMaskLayerView.STYLE_TRANSPARENT_ON);
        mLlTitleContainer = (LinearLayout) findViewById(R.id.rl_photo_top);
        if (getTitleView() != null) {
            mLlTitleContainer.addView(getTitleView());
        }


    }


    private void scanAll() {
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                ToastUtil.showCustomMessage(BasePhoneChooseActivity.this, getString(R.string.no_sd_card));
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(Environment.getExternalStorageDirectory()); // 指定SD卡路径
                mediaScanIntent.setData(contentUri);
                sendBroadcast(mediaScanIntent);
            } else {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            }
        } catch (Exception e) {

        }
    }

    private void initData() {
        surplus_count = getIntent().getIntExtra(SURPLUS_COUNT, 1);
        mNeedCompress = getIntent().getBooleanExtra(NEED_COMPRESS, true);
        // 扫描完成SD卡获取图片
        listAdapter = new FolderAdapter(this, imgBeanLists);
        group_listview.setAdapter(listAdapter);
        photoAdapter = new PhotoAdapter(this, nowStrs, addedPath);
        gridview.setAdapter(photoAdapter);
        getImages();
    }

    /**
     * 添加监听器
     */
    private void setListener() {
        total_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // addedPath返回给上个页面-----这里只选择相册里的
                if (isGroupShow)
                    return;
                resultImaList(addedPath);
            }
        });


        group_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (isGroupShow)
                    return;
                changeGroupShow();
            }
        });

        // 目录选择监听器
        group_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long index) {
                // 调用系统相机
                if (position == 0) {
                    System.gc();
                    mHandler.sendEmptyMessageDelayed(SKIP_CAMERA, 500);
                    return;
                }
                changeGroupShow();
                if (position == select_folder_count) {
                    mHandler.sendEmptyMessageDelayed(SCAN_ALL_PIC, 500);
                    return;
                }
                select_folder_count = position;
                mLoadingView.showLoading(getString(R.string.loading));
                // 点击刷新对应的视图
                if (position == 1) {
                    // 不做操作，返回
                    mHandler.sendEmptyMessageDelayed(SCAN_ALL_PIC, 500);
                } else {
                    // 刷新当前的GridView
                    nowStrs.clear();
                    String fa_path = imgBeanLists.get(position).getFa_filepath();
                    nowStrs.addAll(mGruopMap.get(fa_path));
                    mHandler.sendEmptyMessageDelayed(SCAN_FOLDER_OK, 500);
                }
            }
        });

        // 图片选择监听器
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long index) {
                String path = photoAdapter.getItem(position);
                ImageView iv = (ImageView) view.findViewById(R.id.grid_img);
                if (addedPath.contains(path)) {
                    addedPath.remove(path);
                    iv.setImageResource(R.mipmap.pic_select_no);
                } else {
                    // 判断是否超过限制数量
                    if (surplus_count != 1) {
                        if (addedPath.size() >= surplus_count) {
                            createDialog(surplus_count);
                            return;
                        }
                        addedPath.add(path);
                        iv.setImageResource(R.mipmap.pic_select_sel);
                    } else {
                        addedPath.add(path);
                        iv.setImageResource(R.mipmap.pic_select_sel);
                        if (addedPath.size() >= surplus_count) {
                            resultImaList(addedPath);
                            return;
                        }
                    }
                }
                selImgUpdate();
            }
        });
    }

    /**
     * 改变图片个数显示
     */
    private void selImgUpdate() {
        int count = addedPath.size();
        if (count > 0) {
            total_text.setText(getString(R.string.commit) + "(" + count + ")");
            total_text.setBackgroundResource(getSelectedColor());
        } else {
            total_text.setText(getString(R.string.commit));
            total_text.setBackgroundResource(getUnSelectedColor());
        }
    }

    protected int getSelectedColor() {
        return R.color.blue;
    }

    protected int getUnSelectedColor() {
        return R.color.bottom_bar;
    }

    /**
     * 改变相册显示
     */
    private void changeGroupShow() {
        if (toUp == null) {
            toUp = AnimationUtils.loadAnimation(this, R.anim.act_bottom_to_top);
        }
        if (toDown == null) {
            toDown = AnimationUtils.loadAnimation(this, R.anim.act_top_to_bottom);
        }
        if (isGroupShow) {
            isGroupShow = false;
            list_layout.startAnimation(toDown);
            list_layout.setVisibility(View.GONE);
            group_listview.setVisibility(View.GONE);
        } else {
            isGroupShow = true;
            list_layout.startAnimation(toUp);
            list_layout.setVisibility(View.VISIBLE);
            group_listview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {// 从相机取得返回的图片
            addedPath.add(path);
            resultImaList(addedPath);
        }

    }

    /**
     * 返回图片集合给调用者
     */
    private void resultImaList(final ArrayList<String> imgList) {

        /***
         * 如果路径里包含中文,则通过工具类,子线程里拷贝一个图片到另外的路径,以此保证,选取图片后得到的路径集合都不包含中文
         */
        if (imgList != null && imgList.size() > 0) {

            mLoadingView.showLoading();

            new Thread() {
                @Override
                public void run() {

                    try {

                        compress(imgList);

                        copyImage(imgList);

                        /***
                         * 全部遍历完成之后,回主线程,返回
                         */
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingView.dismiss();
                                onChooseCompleted(imgList);
                            }
                        });

                    }
                    catch (Exception e){
                        LogUtil.e("compress","压缩失败!"+e.getMessage());
                    }
                }
            }.start();

        } else {//为空的话,直接结束
            finish();
        }


    }

    /**
     * @method name:copyImage
     * @des:去除带中文的路径
     * @param :[imgList]
     * @return type:void
     * @date 创建时间:2016/11/16
     * @author Chuck
     **/
    private void copyImage(ArrayList<String> imgList) {

        for (int i = 0; i < imgList.size(); i++) {
            if (CharacterParser.isContainsChinese(imgList.get(i))) {//包含中文
                String newName = Constants.getGlideImageRootDir() + System.currentTimeMillis() + ".png";

                FileUtils.fileChannelCopy(new File(imgList.get(i)), new File(newName));//拷贝文件,避免有中文

                boolean is = new File(newName).exists();
                if (is) {//替换为新的路径
                    imgList.remove(i);
                    imgList.add(i, newName);
                }
            }
        }
    }

    /**
     * @method name:compress
     * @des:压缩大图片
     * @param :[imgList]
     * @return type:void
     * @date 创建时间:2016/11/16
     * @author Chuck
     **/
    private void compress(ArrayList<String> imgList) throws Exception{

        //先判断是不是需要压缩
        if(mNeedCompress){
            for (int i = 0; i < imgList.size(); i++) {

                String path=imgList.get(i);

                if(!TextUtils.isEmpty(path)){
                    File file=new File(path);

                    LogUtil.e("compress","压缩前,legnth:"+file.length());

                    if(file.exists()&&file.length()>=FILE_LENGTH_LIMIT){

                        /***
                         * 全部遍历完成之后,回主线程,返回
                         */
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mLoadingView.showLoading("图片过大,正在压缩,请稍等...");
                            }
                        });

                        LogUtil.e("compress","准备压缩");

                        FileInputStream stream = new FileInputStream(file);
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inSampleSize = (int)(file.length()/FILE_LENGTH_LIMIT);
                        Bitmap bitmap = BitmapFactory.decodeStream(stream , null, opts);

                        File dest= com.qdong.communal.library.imageLoader.BitmapUtil.writeToSDCard(
                                com.qdong.communal.library.imageLoader.BitmapUtil.compressBmpToBytes(bitmap,FILE_LENGTH_LIMIT)
                        );
                        bitmap.recycle();
                        bitmap=null;

                        if(dest.exists()){
                            LogUtil.e("compress","压缩结束,length:"+dest.length());
                            imgList.remove(i);
                            imgList.add(i,dest.getPath());
                        }

                    }
                }

            }
        }
    }


    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
     */
    private void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showCustomMessage(this, getString(R.string.no_sd_card));
            return;
        }
        // 显示进度条
        mLoadingView.showLoading(getString(R.string.loading));
        new Thread() {
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_ADDED + " DESC");
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    // 获取该图片的父路径名
                    File pa_file = new File(path).getParentFile();
                    String parentName = pa_file.getAbsolutePath();
                    mAllImgs.add(path);
                    // 根据父路径名将图片放入到mGruopMap中
                    if (!mGruopMap.containsKey(parentName)) {
                        ArrayList<String> chileList = new ArrayList<String>();
                        chileList.add(path);
                        mGruopMap.put(parentName, chileList);
                    } else {
                        mGruopMap.get(parentName).add(path);
                    }
                }
                mCursor.close();
                ArrayList<ImageBean> imgBean = subGroupOfImage(mGruopMap);
                if (imgBean == null || imgBean.isEmpty() || mAllImgs.isEmpty()) {
                    mHandler.sendEmptyMessage(NULL_IMGS);
                    return;
                }
                imgBeanLists = imgBean;
                ImageBean bean = new ImageBean();
                bean.setFolderName(getString(R.string.take_photo));
                imgBeanLists.add(0, bean);
                bean = new ImageBean();
                bean.setFolderName(getString(R.string.all_photos));
                bean.setImageCounts(mAllImgs.size());
                bean.setTopImagePath(mAllImgs.get(0));
                imgBeanLists.add(1, bean);
                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(SCAN_OK);
            }
        }.start();

    }

    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中 所以需要遍历HashMap将数据组装成List
     *
     * @return
     */
    private ArrayList<ImageBean> subGroupOfImage(HashMap<String, ArrayList<String>> gruopMap) {
        if (gruopMap.size() == 0) {
            return null;
        }
        ArrayList<ImageBean> list = new ArrayList<ImageBean>();
        Iterator<Map.Entry<String, ArrayList<String>>> it = gruopMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = it.next();
            ImageBean mImageBean = new ImageBean();
            String key = entry.getKey();
            List<String> value = entry.getValue();
            File dir_file = new File(key);
            mImageBean.setFolderName(dir_file.getName());
            mImageBean.setImageCounts(value.size());
            mImageBean.setTopImagePath(value.get(0));// 获取该组的第一张图片
            mImageBean.setFa_filepath(key);
            list.add(mImageBean);
        }
        return list;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mLoadingView != null && mLoadingView.isShowing()) {
            mLoadingView.dismiss();
        }
    }

    /**
     * 清除dialog
     */
    private void dismiss() {
        if (mLoadingView != null && mLoadingView.isShowing()) {
            mLoadingView.dismiss();
        }
    }

    @Override
    public void onStop() {
        System.gc();
        super.onStop();
    }

    /**
     * 创建提示Dialog
     */
    private void createDialog(Integer number) {
        final Dialog mLoadingView = new Dialog(this);
        mLoadingView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoadingView.setCanceledOnTouchOutside(true);
        mLoadingView.setContentView(R.layout.ft_photo_dialog);
        mLoadingView.show();
        TextView dialog_content = (TextView) mLoadingView.findViewById(R.id.dialog_content);
        dialog_content.setText(MessageFormat.format(getString(R.string.limit_quantitiy), number + ""));
        TextView dialog_check = (TextView) mLoadingView.findViewById(R.id.dialog_check);
        dialog_check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mLoadingView.dismiss();
            }
        });
        Window win = mLoadingView.getWindow();
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        mLoadingView.show();
    }

    /**
     * 子类实现,选取结果处理
     ***/
    public abstract void onChooseCompleted(ArrayList<String> imgList);

    /***
     * 子类提供titleview,里面可以加回退和标题,取消
     **/
    public abstract View getTitleView();

}
