package com.qdong.communal.library.widget.Dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qdong.communal.library.R;


/**
 * 简单的弹出框
 * <p/>
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/2/2  14:06
 **/
public class CustomSimpleDialog extends AlertDialog implements View.OnClickListener {

    private Context mContext;//上下文
    private String mTitleText;//标题
    private String mContent;//展示文本
    private String mContentCenter;//展示文本
    private String mCommitBtnText;//确认按钮上的文本
    private String mCancelBtnText;//取消按钮上的文本
    private DialogBtnCallback mBtnClickCallback;//按钮点击的回调


    /**
     * UI
     **/
    private TextView mTvTitle;//标题
    private TextView mTvDContent;
    private TextView mTvContentCenter;
    private Button mBtnCancel;//取消按钮
    private Button mBtnCommit;//确认按钮
    private boolean mIsSetCandelButtonGone;

    /**
     * @return type:
     *
     * @method name:CustomSimpleDialog
     * @des: CustomSimpleDialog * mContext  上下文
     * [mContext, mTitleText, mContent, mCommitBtnText, mIsSetCandelButtonGone, mBtnClickCallback]lButtonGone  是否隐藏取消按钮
     * , m2016/9/20allback  按钮点击回调接口
     * ]
     * @date 创建时间:2016/9/20
     * @author Chuck
     **/


    public CustomSimpleDialog(
            Context mContext,
            String mTitleText,
            String mContent,
            String mCommitBtnText,
            boolean mIsSetCandelButtonGone,
            DialogBtnCallback mBtnClickCallback) {

        super(mContext, R.style.MyDialog);
        this.mContext = mContext;
        this.mTitleText = mTitleText;
        this.mContent = mContent;
        this.mCommitBtnText = mCommitBtnText;
        this.mIsSetCandelButtonGone = mIsSetCandelButtonGone;
        this.mBtnClickCallback = mBtnClickCallback;
    }

    public CustomSimpleDialog(
            Context mContext,
            String mTitleText,
            String mContent,
            String mCommitBtnText,
            String mCancelBtnText,
            boolean mIsSetCandelButtonGone,
            DialogBtnCallback mBtnClickCallback) {

        super(mContext, R.style.MyDialog);
        this.mContext = mContext;
        this.mTitleText = mTitleText;
        this.mCancelBtnText = mCancelBtnText;
        this.mContentCenter = mContent;
        this.mCommitBtnText = mCommitBtnText;
        this.mIsSetCandelButtonGone = mIsSetCandelButtonGone;
        this.mBtnClickCallback = mBtnClickCallback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_simple_);
        initUI();

    }


    /**
     * @param :[]
     * @return type:void
     *
     * @method name:initUI
     * @des:初始化UI
     * @date 创建时间:2016/2/18
     * @author Chuck
     **/
    private void initUI() {


        mTvTitle = (TextView) this.findViewById(R.id.tv_title);
        mTvTitle.setText(mTitleText);

        mTvDContent = (TextView) this.findViewById(R.id.tv_content);
        mTvDContent.setText(mContent);

        mTvContentCenter = (TextView) this.findViewById(R.id.tv_content_center);
        mTvContentCenter.setText(mContentCenter);

        mBtnCancel = (Button) this.findViewById(R.id.btn_cancel);
        mBtnCommit = (Button) this.findViewById(R.id.btn_commit);

        if (!TextUtils.isEmpty(mCommitBtnText)) {//确认按钮设置文本
            mBtnCommit.setText(mCommitBtnText);
        }
        if (!TextUtils.isEmpty(mCancelBtnText)) {
            mBtnCancel.setText(mCancelBtnText);
        }

        /**是否隐藏"取消"按钮*/
        if (mIsSetCandelButtonGone) {
            mBtnCancel.setVisibility(View.GONE);
        }

        setListener();

    }

    public Button getmBtnCancel() {
        return mBtnCancel;
    }

    /**
     * @param :[]
     * @return type:void
     *
     * @method name:setListener
     * @des:加监听器
     * @date 创建时间:2016/2/18
     * @author Chuck
     **/
    private void setListener() {
        mBtnCancel.setOnClickListener(this);
        mBtnCommit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_cancel) {
            dismiss();
            if (mBtnClickCallback != null) {
                mBtnClickCallback.handleCancel();
            }
        } else if (id == R.id.btn_commit) {
            dismiss();
            if (mBtnClickCallback != null) {
                mBtnClickCallback.handleSubmit(null);
            }
        }

       /* switch (v.getId()){
            case R.id.btn_cancel:
                dismiss();
                if(mBtnClickCallback!=null){
                    mBtnClickCallback.handleCancel();
                }
                break;

            case R.id.btn_commit:
                dismiss();
                if(mBtnClickCallback!=null){
                    mBtnClickCallback.handleSubmit(null);
                }
                break;

        }*/
    }


}
