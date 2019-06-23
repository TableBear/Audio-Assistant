package com.zhku161022.audioassistant.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @Created by TableBear on 2018/5/13.
 * @Describe: 翻译的
 */

public class TranslateUtils {


    private Callback callback;
    private TranslateAPI  translateAPI;
    private String appid = "20180513000157938";
    private static TranslateUtils translateUtils;
    private String securityKey = "n7F6NzT1xWw80O5dgyWb";
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    private TranslateUtils() {
        translateAPI = new TranslateAPI(appid,securityKey);
    }

    public static TranslateUtils instance(){
        if (translateUtils==null){
            synchronized (TranslateUtils.class){
                if (translateUtils==null){
                    translateUtils = new TranslateUtils();
                }
            }
        }
        return translateUtils;
    }

    public void translate(String query, String from, String to){
        translateAPI.bindParams(query,from,to);
        String url = getUrlWithQueryString(TRANS_API_HOST,translateAPI.getParams());
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }
        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }
        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }
            if (i != 0) {
                builder.append('&');
            }
            builder.append(key);
            builder.append('=');
            builder.append(encode(value));
            i++;
        }
        return builder.toString();
    }

    public static String encode(String input) {
        if (input == null) {
            return "";
        }
        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return input;
    }

}
