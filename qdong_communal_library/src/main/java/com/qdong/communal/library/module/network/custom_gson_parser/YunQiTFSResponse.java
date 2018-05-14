package com.qdong.communal.library.module.network.custom_gson_parser;

import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.List;

/**
 * YunQiTFSResponse
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2018/5/8  10:48
 * Copyright : 2014-2015 深圳掌通宝科技有限公司-版权所有
 **/
public class YunQiTFSResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * success : true
     * errorCode : null
     * message : null
     * result : ["T1JaZTBXDv1RCvBVdK"]
     */

    private boolean success;
    private Object errorCode;
    private Object message;
    private JsonElement result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Object errorCode) {
        this.errorCode = errorCode;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
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
        return "YunQiTFSResponse{" +
                "success=" + success +
                ", errorCode=" + errorCode +
                ", message=" + message +
                ", result=" + result +
                '}';
    }
}
