package com.zhku161022.audioassistant.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.zhku161022.audioassistant.AudioAssistant;
import com.zhku161022.audioassistant.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Created by TableBear on 2018/4/16.
 * @Describe:
 */

public class PhoneUtil {

    private static People ans = null;
    private static Context context = null;
    public static boolean sendMessage(String str, AudioAssistant application){
        context = application;
        HashMap<String, List<String>> map = application.getAllContacts();
        //        System.out.println("表"+ map.size());
        if (map.isEmpty()){
            return false;
        }
//        System.out.println("图"+map.toString());
        Set<Map.Entry<String,List<String>>> s = map.entrySet();
        Iterator<Map.Entry<String,List<String>>> it = s.iterator();
        String phone,name;
        String strpy = getPinyin(str);
        System.out.println("语句："+strpy);
        ArrayList<People> arrayList = new ArrayList<>();
        while(it.hasNext()) {
            Map.Entry<String,List<String> > e = it.next();
            //System.out.println("姓名：" + e.getKey() + " 电话:" + e.getValue().size());
            phone = e.getValue().get(0);
            name = e.getKey();
            // System.out.println(phone + "," + name);
            System.out.println("ee"+strpy+","+getPinyin(name));
            double d = LCS(strpy, getPinyin(name));
            People p = new People(phone, name, d);
            System.out.println(p);
            arrayList.add(p);
        }
        Collections.sort(arrayList);
        ans = null;
        double ratio = -0.1;
        for(int i=0;i<map.size()*0.1;i++){
            People p = arrayList.get(i);
            System.out.println("调出来"+p+"bl:"+ratio);
            double d = LCS(str,p.name); //对中文名再LCS
            if(d>ratio){
                ans = p;
                ratio = d;
            }
        }
        if (ans.ratio<0.5){
            return false;
        }
        System.out.println("答案"+ ans);
        SpeechUtil.addSynthesizerListener(synthesizerListener);
        SpeechUtil.beginSpeechSynthesizer("请输入你要发送的内容");

        return true;

    }
    private static void pushMessage(String mess, String phone, Context application){
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+phone));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("sms_body", mess);
        application.startActivity(intent);
    }
    static StringBuffer sb = new StringBuffer();
    private static RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            MainActivity.showTip("当前正在说话，音量大小：" + volume);
        }
        @Override
        public void onBeginOfSpeech() {
            MainActivity.showTip("开始说话");
        }
        @Override
        public void onEndOfSpeech() {
            MainActivity.showTip("结束说话");
        }
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
//            Log.d(TAG, results.getResultString());
            MainActivity.printResult(results);
            if (isLast) {

                // TODO 最后的结果
//                if (!mTranslateEnable)
//                    judge(stringBuffer.toString());
//                else{
//                    translateUtils.translate(stringBuffer.toString(),"zh","en");
//                }
                pushMessage(MainActivity.stringBuffer.toString(),ans.phone,context);
                MainActivity.stringBuffer.delete(0,MainActivity.stringBuffer.length());
                SpeechUtil.removeRecognizerListener();
//                mTranslateEnable = false;
            }
        }
        @Override
        public void onError(SpeechError speechError) {
            System.out.println(speechError.getPlainDescription(true));
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private static SynthesizerListener synthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {
        }

        @Override
        public void onCompleted(SpeechError speechError) {
            System.out.println("完成了！！！");
            SpeechUtil.removeSynthesizerListener();
            SpeechUtil.addRecognizerListener(recognizerListener);
            SpeechUtil.beginSpeechRecognizer();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    private static String getMessage(String phone,Context application) {

        return null;
    }


    public static String getPinyin(String str){
        Hanyu hy = new Hanyu();
        String pinyin = hy.getStringPinYin(str);
        System.out.println(pinyin);
        return pinyin;
    }

    public static boolean isCall(String str){
        if (str.contains("电话")){
            return true;
        }
        return false;
    }

    public static boolean call(String str,AudioAssistant application){
        HashMap<String, List<String>> map = application.getAllContacts();
//        System.out.println("表"+ map.size());
        if (map.isEmpty()){
                return false;
        }
//        System.out.println("图"+map.toString());
        Set<Map.Entry<String,List<String>>> s = map.entrySet();
        Iterator<Map.Entry<String,List<String>>> it = s.iterator();
        String phone,name;
        String strpy = getPinyin(str);
        System.out.println("语句："+strpy);
        ArrayList<People> arrayList = new ArrayList<>();
        while(it.hasNext()) {
            Map.Entry<String,List<String> > e = it.next();
           //System.out.println("姓名：" + e.getKey() + " 电话:" + e.getValue().size());
            phone = e.getValue().get(0);
            name = e.getKey();
           // System.out.println(phone + "," + name);
            System.out.println("ee"+strpy+","+getPinyin(name));
            double d = LCS(strpy, getPinyin(name));
            People p = new People(phone, name, d);
            System.out.println(p);
            arrayList.add(p);
        }
        Collections.sort(arrayList);
        People ans = null;
        double ratio = -0.1;
        for(int i=0;i<map.size()*0.1;i++){
            People p = arrayList.get(i);
            System.out.println("调出来"+p+"bl:"+ratio);
            double d = LCS(str,p.name); //对中文名再LCS
            if(d>ratio){
                ans = p;
                ratio = d;
            }
        }
        if (ans.ratio<0.5){
            return false;
        }
        System.out.println("答案"+ans);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+ans.phone));
        application.startActivity(intent);
        return true;
    }

    private static double LCS(String statement ,String name){
        int[][] matrix = new int[statement.length() + 1][name.length() + 1];//建立二维矩阵
        // 初始化边界条件
        for (int i = 0; i <= statement.length(); i++) {
            matrix[i][0] = 0;//每行第一列置零
        }
        for (int j = 0; j <= name.length(); j++) {
            matrix[0][j] = 0;//每列第一行置零
        }
        // 填充矩阵
        for (int i = 1; i <= statement.length(); i++) {
            for (int j = 1; j <= name.length(); j++) {
                if (statement.charAt(i - 1) == name.charAt(j - 1)) {
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;
                } else {
                    matrix[i][j] = (matrix[i - 1][j] >= matrix[i][j - 1] ? matrix[i - 1][j]
                            : matrix[i][j - 1]);
                }
            }
        }
        // System.out.println(matrix[statement.length()][name.length()]);
        return (double)(matrix[statement.length()][name.length()])/(name.length());
    }

    public static boolean isSendMessage(String str) {
        if (str.contains("短信")||str.contains("消息")||str.contains("信息")){
            return true;
        }
        return false;
    }
}
class People implements  Comparable<People>{
    String phone ;
    String name ;
    double ratio ;
    public People(String phone, String name,double ratio){
        this.phone = phone;
        this.name = name;
        this.ratio = ratio;
    }
    @Override
    public int compareTo(@NonNull People o) {
        if(ratio>o.ratio){
            return -1;
        }
        else if(ratio == o.ratio){
            return 0;
        }
        else {
            return 1;
        }
    }
    @Override
    public String toString() {
        return "好友："+name+","+phone+","+ratio;
    }
}
