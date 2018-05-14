package com.qdong.communal.library.module.network.custom_gson_parser;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * GsonResponseBodyConverter2
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2018/5/8  9:55
 * Copyright : 2014-2015 深圳掌通宝科技有限公司-版权所有
 **/
public class GsonResponseBodyConverter2<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter2(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override public T convert(ResponseBody value) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());
        jsonReader.setLenient(true);

        try {
            return adapter.read(jsonReader);
        } finally {
            value.close();
        }
    }
}
