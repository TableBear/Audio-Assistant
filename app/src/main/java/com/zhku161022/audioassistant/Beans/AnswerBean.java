package com.zhku161022.audioassistant.Beans;

/**
 * @Created by TableBear on 2018/3/24.
 * @Describe: 图灵机器人回答的Bean类
 */

public class AnswerBean {

    /**
     * code : 消息标识码
     * text : 文本结果
     * url : 超链接
     */

    private String code;
    private String text;
    private String url;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
