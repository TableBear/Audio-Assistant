package com.zhku161022.audioassistant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by cqw on 2018/3/24.
 */

public class MyHelper extends SQLiteOpenHelper {

    public MyHelper(Context context) {
        super(context,"yyzs",null,2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("开始创建","开始创建");
        db.execSQL("CREATE TABLE question(zw VARCHAR(20) PRIMARY KEY)");
        db.execSQL("CREATE TABLE verb(zw VARCHAR(20) PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
