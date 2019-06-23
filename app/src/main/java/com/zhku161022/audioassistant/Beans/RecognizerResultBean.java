package com.zhku161022.audioassistant.Beans;

import java.util.List;

/**
 * @Created by TableBear on 2018/3/24.
 * @Describe: 识别结果的Bean类
 */

public class RecognizerResultBean {

    /**
     * sn : 1
     * ls : false
     * bg : 0
     * ed : 0
     * ws : [{"bg":0,"cw":[{"w":"我想听","sc":0}]},{"bg":0,"cw":[{"w":"拉德斯基进行曲","sc":0},{"w":"拉得斯进行曲","sc":0}]}]
     */

    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    /**
     * bg : 0
     * cw : [{"w":"我想听","sc":0}]
     */

    private List<WsBean> ws;

    public int getSn() {
        return sn;
    }

    public void setSn(int sn) {
        this.sn = sn;
    }

    public boolean isLs() {
        return ls;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public int getBg() {
        return bg;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getEd() {
        return ed;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public List<WsBean> getWs() {
        return ws;
    }

    public void setWs(List<WsBean> ws) {
        this.ws = ws;
    }

    public static class WsBean {
        private int bg;
        /**
         * w : 我想听
         * sc : 0
         */

        private List<CwBean> cw;

        public int getBg() {
            return bg;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public List<CwBean> getCw() {
            return cw;
        }

        public void setCw(List<CwBean> cw) {
            this.cw = cw;
        }

        public static class CwBean {
            private String w;
            private int sc;

            public String getW() {
                return w;
            }

            public void setW(String w) {
                this.w = w;
            }

            public int getSc() {
                return sc;
            }

            public void setSc(int sc) {
                this.sc = sc;
            }
        }
    }
}
