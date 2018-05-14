package com.qdong.communal.library.module.CitySelect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.qdong.communal.library.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * CreateDB
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/9/10  14:57
 * Copyright : 趣动智能科技有限公司-版权所有
 **/
public class CreateDB {

    public static String TABLE_NAME_CITY="map";

    public static SQLiteDatabase toDB(Context context) {
        try {
            int BUFFER_SIZE = 50000;
            String DB_NAME = "citycode.db";
            String PACKAGE_NAME = context.getPackageName();
            String DB_PATH = "/data"
                    + Environment.getDataDirectory().getAbsolutePath() + "/"
                    + PACKAGE_NAME + "/databases/";
            File destDir = new File(DB_PATH);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            String file = DB_PATH + DB_NAME;
            if (!(new File(file).exists())) {
                InputStream is = context.getResources().openRawResource(
                        R.raw.citycode);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(file, null);
            return db;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
