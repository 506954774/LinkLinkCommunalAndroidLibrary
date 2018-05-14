package com.qdong.communal.library.module.network;

/**
 * 微信授权登陆获取access_token
 * WechatAuth
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2017/1/16  19:36
 * Copyright : 2014-2016 深圳趣动只能科技有限公司-版权所有
 **/
public class WechatAuth {


    /**
     * access_token : LWlQSlZt09qkEZ5JlWgpuF8SVL9KQ4XbqXRiHA7iL-kg5Hj-uIYI1OE0ahZK6J1bB2Fw3sSqu8rxiTtbdV35xyzRW2MoFViqEOLeAAGbijM
     * expires_in : 7200
     * refresh_token : mV8ljzYmQ_Ut_GZmuPj1eBtTonQTrdMy9WxIBVh3wBmAu6oZSCnA0p-FJEboHMXpH3kIMM8_om91nbGfhk917885kHbx6bRGk_vzmMVvuFU
     * openid : odw58v4LcYQ-8H5bqK52xxdaAMAw
     * scope : snsapi_userinfo
     * unionid : orX5dxAF80WuqW_oV2gYvsGgdE3c
     */

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
