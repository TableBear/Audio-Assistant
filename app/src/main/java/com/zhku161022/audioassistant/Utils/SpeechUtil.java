package com.zhku161022.audioassistant.Utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * @Created by TableBear on 2018/5/6.
 * @Describe: 语音合成的功能类
 */

public class SpeechUtil {


    private static final String LANGUAGE = "zh_cn";//语言
    private static final String ACCENT = "mandarin";//口音
    private static final String VOICE_NAME = "vinn";//声音类型

    private static SpeechUtil speechUtil;

    private Context context;
    private static SpeechRecognizer speechRecognizer;
    private static SpeechSynthesizer speechSynthesizer;
    @NonNull
    private static RecognizerListener recognizerListener;//语音识别监听器对象
    @NonNull
    private static SynthesizerListener synthesizerListener;//语音合成监听器对象

    private SpeechUtil(Context context) {
        this.context = context;
        speechRecognizer = SpeechRecognizer.createRecognizer(context,null);
        initSpeechRecognizer();
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(context,null);
        initSpeechSynthesizer();
    }
    /*
     * @Created by TableBear on 2018/5/6.
     * @Describe: 唯一获得SpeechUtil对象的方法
     * @Rturn:SpeechUtil
     */
    public static void initSpeechUtil(Context context){
        if (speechUtil==null){
            synchronized (SpeechUtil.class){
                if (speechUtil==null){
                    speechUtil = new SpeechUtil(context);
                }
            }
        }
    }

    /*
     * @Created by TableBear on 2018/5/6.
     * @Describe: 初始化语音识别对象
     * @Rturn:空
     */
    private static void initSpeechRecognizer(){
        //设置语音引擎
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD);
        //设置应用领域
        speechRecognizer.setParameter(SpeechConstant.DOMAIN,"iat");
        //设置语言为中文
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, LANGUAGE);
        //设置方言为中国大陆
        speechRecognizer.setParameter(SpeechConstant.ACCENT, ACCENT);
        //设置音频的保存路径
        speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/mIat.wav");
    }

    /*
     * @Created by TableBear on 2018/5/6.
     * @Describe: 初始化语音合成对象
     * @Rturn:空
     */
    private void initSpeechSynthesizer(){
        //设置语音引擎
        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置语言为中文
        speechSynthesizer.setParameter(SpeechConstant.LANGUAGE,LANGUAGE);
        //设置声音类型
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, VOICE_NAME);
        //播放合成语音是打断音乐播放
        speechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

    }
    /*
     * @Created by TableBear on 2018/5/6.
     * @Describe: 绑定声音识别监听器
     * @Rturn:空
     */
    public static void addRecognizerListener(RecognizerListener recognizerListener){
        SpeechUtil.recognizerListener = recognizerListener;
    }

    public static void removeRecognizerListener(){
        SpeechUtil.recognizerListener = null;
    }
     /*
     * @Created by TableBear on 2018/5/6.
     * @Describe: 绑定声音合成监听器
     * @Rturn:空
     */
     public static void addSynthesizerListener(SynthesizerListener synthesizerListener){
         SpeechUtil.synthesizerListener = synthesizerListener;
     }
    public static void removeSynthesizerListener(){
        SpeechUtil.synthesizerListener = null;
    }

    /*
    * @Created by TableBear on 2018/5/6.
    * @Describe: 开始语音合成
    * @Param:String 要合成字符串
    * @Rturn:Boolean false表示失败 true 表示成功
    */
     public  static boolean  beginSpeechSynthesizer(String strTextToSpeech){
         int code = speechSynthesizer.startSpeaking(strTextToSpeech,synthesizerListener);
         if(code!= ErrorCode.SUCCESS){
             return false;
         }
         return true;
     }

    /*
    * @Created by TableBear on 2018/5/6.
    * @Describe: 开始语音识别
    * @Param:空
    * @Rturn:空
    */
    public static void beginSpeechRecognizer(){
         speechRecognizer.startListening(recognizerListener);
    }

}
