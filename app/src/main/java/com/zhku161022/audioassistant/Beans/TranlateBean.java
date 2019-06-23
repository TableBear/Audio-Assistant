package com.zhku161022.audioassistant.Beans;

import java.util.List;

/**
 * @Created by TableBear on 2018/5/13.
 * @Describe:
 */

public class TranlateBean {

    /**
     * from : zh
     * to : en
     * trans_result : [{"src":"高度600米","dst":"Height of 600 meters"}]
     */

    private String from;
    private String to;
    /**
     * src : 高度600米
     * dst : Height of 600 meters
     */
    private List<TransResultBean> trans_result;
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public List<TransResultBean> getTrans_result() {
        return trans_result;
    }
    public void setTrans_result(List<TransResultBean> trans_result) {
        this.trans_result = trans_result;
    }
    public static class TransResultBean {
        private String src;
        private String dst;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getDst() {
            return dst;
        }
        public void setDst(String dst) {
            this.dst = dst;
        }
    }
}
