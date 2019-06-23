package com.zhku161022.audioassistant;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @Created by TableBear on 2018/3/24.
 * @Describe: 图灵机器人
 */

public class TulinRobot {

    private static Callback callback;
    private static TulinRobot tulinRobot;
    private static final String path = "http://www.tuling123.com/openapi/api?key=a9b24328c5af4d4281b2f18877503e48&info=%s";

    private TulinRobot(){
    }

    public static void initTulinRobot(){
        if (tulinRobot==null){
            synchronized (TulinRobot.class){
                if (tulinRobot==null){
                    tulinRobot = new TulinRobot();
                }
            }
        }
    }

    public static void communicate(String question){
        if (callback==null){
            return;
        }
        String url = String.format(path,question);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void setCallback(Callback callback){
        TulinRobot.callback = callback;
    }

}
