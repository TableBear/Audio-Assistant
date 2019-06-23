package com.zhku161022.audioassistant.Utils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Created by TableBear on 2018/5/13.
 * @Describe:
 */

public class TranslateAPI {

    private String appid;
    private String securityKey;
    Map<String, String> params;

    public Map<String, String> getParams() {
        return params;
    }

    public TranslateAPI(String appid, String securityKey) {
        this.appid = appid;
        this.securityKey = securityKey;
    }

    public Map<String, String> bindParams(String query, String from, String to){
        params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);
        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);
        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.getMD5Code(src));
        return params;
    }

}
