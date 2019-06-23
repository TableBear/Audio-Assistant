package com.zhku161022.audioassistant.Utils;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.zhku161022.audioassistant.Beans.AnswerBean;
import com.zhku161022.audioassistant.Beans.RecognizerResultBean;
import com.zhku161022.audioassistant.Beans.TranlateBean;

import java.io.IOException;
import java.util.List;

import okhttp3.Response;

/**
 * @Created by TableBear on 2018/3/24.
 * @Describe: 把
 */

public class BeansUtil {

    private static Gson gson = new Gson();

    public static String jsonTransfromResultBean(RecognizerResult results){
        RecognizerResultBean resultBean = gson.fromJson(results.getResultString(),RecognizerResultBean.class);
        StringBuilder stringBuilder = new StringBuilder();
        List<RecognizerResultBean.WsBean> wsBeanList = resultBean.getWs();
        for(int i=0;i<wsBeanList.size();i++){
            List<RecognizerResultBean.WsBean.CwBean> cwBeanList =  wsBeanList.get(i).getCw();
            stringBuilder.append(cwBeanList.get(0).getW());
        }
        return stringBuilder.toString();
    }

    public static String jsonTransfromAnswerBean(Response response) throws IOException {
        AnswerBean answerBean = gson.fromJson(response.body().string(),AnswerBean.class);
        System.out.println("AA"+answerBean.getText());
        return answerBean.getText();
    }

    public static String jsonTransfromTranlateBean(Response response) throws IOException {
        TranlateBean answerBean = gson.fromJson(response.body().string(),TranlateBean.class);
        System.out.println("AA"+answerBean.getTrans_result().get(0).getDst());
        return answerBean.getTrans_result().get(0).getDst();
    }
}
