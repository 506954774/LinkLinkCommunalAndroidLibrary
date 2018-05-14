package com.qdong.communal.library.module.CitySelect;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * CityModel
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/10  15:05
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class CityModel implements Parcelable {

    private String pr;
    private String city;
    private String code;
    private String cityCode;

    /**显示数据拼音的首字母**/
    private String sortLetters;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pr);
        dest.writeString(this.city);
        dest.writeString(this.code);
        dest.writeString(this.cityCode);
        dest.writeString(this.sortLetters);
    }

    public CityModel() {
    }

    protected CityModel(Parcel in) {
        this.pr = in.readString();
        this.city = in.readString();
        this.code = in.readString();
        this.cityCode = in.readString();
        this.sortLetters = in.readString();
    }

    public static final Parcelable.Creator<CityModel> CREATOR = new Parcelable.Creator<CityModel>() {
        @Override
        public CityModel createFromParcel(Parcel source) {
            return new CityModel(source);
        }

        @Override
        public CityModel[] newArray(int size) {
            return new CityModel[size];
        }
    };

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    @Override
    public String toString() {
        return "CityModel{" +
                "pr='" + pr + '\'' +
                ", city='" + city + '\'' +
                ", code='" + code + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", sortLetters='" + sortLetters + '\'' +
                '}';
    }
}
