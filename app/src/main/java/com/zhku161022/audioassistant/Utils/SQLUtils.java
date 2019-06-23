package com.zhku161022.audioassistant.Utils;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhku161022.audioassistant.MyHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cqw on 2018/3/24.
 */

public class SQLUtils {

    static MyHelper mh;
    public static void init(Context context){
        mh = new MyHelper(context);
    }

    public static void insert(){
        SQLiteDatabase db = mh.getReadableDatabase();
        String strs[] = new String[]{"是不是","吗","怎么","哪"};
        for(int i=0;i<strs.length;i++) {
            ContentValues values = new ContentValues();
            //values.put("name","是不是");
            values.put("zw", strs[i]);
            db.insert("question", null, values);
        }
        strs = new String[]{"打","打开","听","看","发送"};
        for(int i=0;i<strs.length;i++){
            ContentValues values = new ContentValues();
            values.put("zw", strs[i]);
            db.insert("verb", null, values);
        }
    }

    //查找所有的疑问词
    public static List<String> queryQuestion(){
        SQLiteDatabase db = mh.getReadableDatabase();
        List<String> list = new ArrayList<String>();
        Cursor c = db.rawQuery("select * from question",null);
        while(c.moveToNext())
            list.add(c.getString(0));
        db.close();
        return list;
    }

    //查找所有的动词
    public static List<String> queryVerb(){
        SQLiteDatabase db = mh.getReadableDatabase();
        List<String> list = new ArrayList<String>();
        Cursor c = db.rawQuery("select * from verb",null);
        while(c.moveToNext())
            list.add(c.getString(0));
        db.close();
        return list;
    }

}
