package com.qdong.communal.library.module.network;

import android.text.TextUtils;

import com.google.gson.JsonElement;
import com.qdong.communal.library.BuildConfig;

import java.io.Serializable;

/**
 * QDongNetInfo
 * 接口返回数据的总模型
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/6/28  11:22
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class QDongNetInfo implements Serializable {

    private static final long serialVersionUID = 19840902L;
    /**
     * success : false
     * code : 010035
     * message : 请登录！
     * result : null
     */



    private boolean success;
    private String code;
    private String errorCode;
    private String message;
    private JsonElement result;


    /**客户端定义的属性actionType,这个属性不是服务器定的.是为了给同一个接口赋予不同的action.比如同一个列表请求,
     * 我们可以定义不同的动作,如刷新,加载,过滤刷新等**/
    private int actionType;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public boolean isSuccess() {
        //return success;
        if(success==true){//兼容云骑的返回实体
           return true;
        }
        return success= TextUtils.isEmpty(code)|| BuildConfig.OK_CODE.equals(code);
        //return success= code==null||"".equals(code)|| BuildConfig.OK_CODE.equals(code);
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonElement getResult() {
        return result;
    }

    public void setResult(JsonElement result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "QDongNetInfo{" +
                "success=" + success +
                ", code='" + code + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                ", actionType=" + actionType +
                '}';
    }
}
