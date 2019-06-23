package com.zhku161022.audioassistant;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.zhku161022.audioassistant.Utils.SQLUtils;
import com.zhku161022.audioassistant.Utils.TranslateAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @Created by TableBear on 2018/3/24.
 * @Describe: The entrance of Application
 */

public class AudioAssistant extends Application {



    private static HashMap<String, List<String>> allContacts;
    private BdWakeUp bdWakeUp;





    @Override
    public void onCreate() {
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID +"=58fb11c6");
        SQLUtils.init(getApplicationContext());
        SQLUtils.insert();
        initAllContacts();
        bdWakeUp = new BdWakeUp(getApplicationContext());
        bdWakeUp.start();
        super.onCreate();
    }

    public HashMap<String, List<String>> getAllContacts() {
        return allContacts;
    }

    public void initAllContacts(){
        allContacts = new HashMap<String,List<String>>();
        // 使用ContentResolver查找联系人数据
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null,
                null, null);
        // 遍历查询结果，获取系统中所有联系人
        while (cursor.moveToNext())
        {
            // 获取联系人ID
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            // 获取联系人的名字
            String name = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));
            ArrayList<String> detail = new ArrayList<>();
            allContacts.put(name,detail);
            Log.d("联系人姓名",name);
            // 使用ContentResolver查找联系人的电话号码
            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = " + contactId, null, null);
            // 遍历查询结果，获取该联系人的多个电话号码
            while (phones.moveToNext())
            {
                // 获取查询结果中电话号码列中数据
                String phoneNumber = phones.getString(phones
                        .getColumnIndex(ContactsContract
                                .CommonDataKinds.Phone.NUMBER));
                Log.d("   号码", phoneNumber);
                detail.add( phoneNumber);
            }
            phones.close();
        }
        cursor.close();
    }

}
