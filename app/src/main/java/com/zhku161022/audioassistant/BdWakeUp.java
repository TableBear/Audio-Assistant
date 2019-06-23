package com.zhku161022.audioassistant;

import android.content.Context;
import android.util.AndroidRuntimeException;
import android.util.Log;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @Created by TableBear on 2018/5/6.
 * @Describe:
 */

public class BdWakeUp {
    public static final String TAG = BdWakeUp.class.getSimpleName();
    private EventManager mWpEventManager;
    private Context context;
    /**
     * 唤醒构造方法
     * @param context 一个上下文对象
     */
    public BdWakeUp(Context context) {
        this.context = context;
        //create方法示是一个静态方法，还有一个重载方法EventManagerFactory.create(context, name, version)
        //由于百度文档没有给出每个参数具体含义，我们只能按照官网给的demo写了
        mWpEventManager = EventManagerFactory.create(context, "wp");
        //注册监听事件
        mWpEventManager.registerListener(new MyEventListener());
    }
    /**
     * 开启唤醒功能
     */
    public void start() {
        HashMap<String, String> params = new HashMap<String, String>();
        // 设置唤醒资源, 唤醒资源请到 http://yuyin.baidu.com/wake#m4 来评估和导出
        params.put("kws-file", "assets:///WakeUp.bin");
        mWpEventManager.send("wp.start", new JSONObject(params).toString(), null, 0, 0);
        Log.d(TAG, "----->唤醒已经开始工作了");
    }
    /**
     * 关闭唤醒功能
     */
    public void stop() {
        // 具体参数的百度没有具体说明，大体需要以下参数
        // send(String arg1, byte[] arg2, int arg3, int arg4)
        mWpEventManager.send("wp.stop", null, null, 0, 0);
        Log.d(TAG, "----->唤醒已经停止");
    }
    private class MyEventListener implements EventListener
    {
        @Override
        public void onEvent(String name, String params, byte[] data, int offset, int length) {
            try {
                //解析json文件
                JSONObject json = new JSONObject(params);
                if ("wp.data".equals(name)) { // 每次唤醒成功, 将会回调name=wp.data的时间, 被激活的唤醒词在params的word字段
                    String word = json.getString("word"); // 唤醒词
                     /*
                      * 这里大家可以根据自己的需求实现唤醒后的功能，这里我们简单打印出唤醒词
                      */
                     Log.d("成功",word);
                    Log.d(TAG, "命令"+word);
                } else if ("wp.exit".equals(name)) {
                    // 唤醒已经停止
                }
            } catch (JSONException e) {
                throw new AndroidRuntimeException(e);
            }
        }
    }
}
