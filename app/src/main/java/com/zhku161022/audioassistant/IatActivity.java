package com.zhku161022.audioassistant;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.zhku161022.audioassistant.Utils.BeansUtil;
import com.zhku161022.audioassistant.Utils.PhoneUtil;
import com.zhku161022.audioassistant.Utils.SQLUtils;
import com.zhku161022.audioassistant.Utils.SpeechUtil;
import com.zhku161022.audioassistant.Utils.TranslateUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class IatActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = IatActivity.class.getSimpleName();
    public static EditText edtShow;
    private Button btnStart1;
    private static Toast iatToast;
    private Button btnStart2;
    private TranslateUtils translateUtils = TranslateUtils.instance();
    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            SpeechUtil.beginSpeechSynthesizer("图灵机器人听不清，请重新说一遍");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String answerBean = BeansUtil.jsonTransfromAnswerBean(response);
            System.out.println(answerBean);
            SpeechUtil.beginSpeechSynthesizer(answerBean);
        }
    };
    private Callback callback2 = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            SpeechUtil.beginSpeechSynthesizer("图灵机器人听不清，请重新说一遍");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String answerBean = BeansUtil.jsonTransfromTranlateBean(response);
            System.out.println("答案"+answerBean);
            SpeechUtil.beginSpeechSynthesizer(answerBean);
            judge(answerBean);
            Message message = Message.obtain();
            message.obj=answerBean;
            handler.sendMessage(message);
        }
    };
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            edtShow.setText(msg.obj.toString());
        }
    };

    public static StringBuffer stringBuffer = new StringBuffer();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iat);
        TulinRobot.initTulinRobot();
        TulinRobot.setCallback(callback);
        SpeechUtil.initSpeechUtil(getApplicationContext());
        SpeechUtil.addRecognizerListener(recognizerListener);
        SpeechUtil.addSynthesizerListener(synthesizerListener);
        translateUtils.setCallback(callback2);
        iatToast = Toast.makeText(this,"",Toast.LENGTH_LONG);
        edtShow = findViewById(R.id.edt_show);
        btnStart1 = findViewById(R.id.btn_start1);
        btnStart2 = findViewById(R.id.btn_start2);
        btnStart1.setOnClickListener(this);
        btnStart2.setOnClickListener(this);
    }



    private SynthesizerListener synthesizerListener = new SynthesizerListener() {
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
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
    };

//    private InitListener initListener = new InitListener() {
//        @Override
//        public void onInit(int code) {
//            Log.d(TAG, "SpeechRecognizer init() code = " + code);
//            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败，错误码：" + code);
//            }
//        }
//    };

    private boolean mTranslateEnable = false;


    public RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }
        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
            edtShow.setText("");
        }
        @Override
        public void onEndOfSpeech() {
            showTip("结束说话");
        }
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);
            if (isLast) {
                // TODO 最后的结果
                if (!mTranslateEnable)
                    judge(stringBuffer.toString());
                else{
                    translateUtils.translate(stringBuffer.toString(),"zh","en");
                }
                stringBuffer.delete(0,stringBuffer.length());
                mTranslateEnable = false;
            }
        }
        @Override
        public void onError(SpeechError speechError) {
            if(mTranslateEnable && speechError.getErrorCode() == 14002) {
                showTip( speechError.getPlainDescription(true)+"\n请确认是否已开通翻译功能" );
            } else {
                showTip(speechError.getPlainDescription(true));
            }
        }
        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

//    private void printTransResult (RecognizerResult results) {
//        String trans  = JsonParser.parseTransResult(results.getResultString(),"dst");
//        String oris = JsonParser.parseTransResult(results.getResultString(),"src");
//
//        if( TextUtils.isEmpty(trans)|| TextUtils.isEmpty(oris) ){
//            showTip( "解析结果失败，请确认是否已开通翻译功能。" );
//        }else{
//            speechUtil.beginSpeechSynthesizer(trans);
//            edtShow.setText("");
//            edtShow.setText( "原始语言:\n"+oris+"\n目标语言:\n"+trans );
//        }
//    }

    public static void printResult(RecognizerResult results) {
        String res = BeansUtil.jsonTransfromResultBean(results);
        edtShow.append(res);
        stringBuffer.append(res);
    }

    public static void showTip(String string){
        iatToast.setText(string);
        iatToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View view) {
        edtShow.setText("");
        switch (view.getId()){
            case R.id.btn_start1:
                SpeechUtil.addRecognizerListener(recognizerListener);
                SpeechUtil.beginSpeechRecognizer();
                break;
            case R.id.btn_start2:
                mTranslateEnable = true;
                SpeechUtil.addSynthesizerListener(synthesizerListener);
                SpeechUtil.beginSpeechRecognizer();
                break;

        }
    }

    public static void sendMessage(){

    }

    private void openApp(String str) {
        PackageManager packageManager = IatActivity.this.getPackageManager();
        // 获取手机里的应用列表
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pInfo.size(); i++) {
            PackageInfo p = pInfo.get(i);
            // 获取相关包的<application>中的label信息，也就是-->应用程序的名字
            String label = packageManager.getApplicationLabel(p.applicationInfo).toString();
            System.out.println("label"+label);
            if (str.contains(label)) { //比较label
                String pName = p.packageName; //获取包名
                //获取intent
                Intent intent = packageManager.getLaunchIntentForPackage(pName);
                startActivity(intent);
                return;
            }
        }

    }
    public void judge(String str){
        System.out.println("in+++++");
        if(PhoneUtil.isCall(str)){
            PhoneUtil.call(str,(AudioAssistant) getApplication());
        }
        else if(PhoneUtil.isSendMessage(str)){
            PhoneUtil.sendMessage(str,(AudioAssistant) getApplication());
        }
        else if(isQuestion(str)){
            //聊天或者上网
            TulinRobot.communicate(stringBuffer.toString());
        }
        else{
            String verb = hasVerb(str);
            if(verb!=null){
                System.out.println("正在打开app");
                openApp(str);
            }
            else{
                //聊天
                TulinRobot.communicate(stringBuffer.toString());
            }
        }
    }

    public boolean isQuestion(String str){
        List<String> list = SQLUtils.queryQuestion();
        boolean flag = false;
        for(int i=0;i<list.size();i++){
            if(str.contains(list.get(i)))
                flag = true;
        }
        System.out.println(str+":"+flag);
        return flag;
    }

    public String hasVerb(String str){
        List<String> list = SQLUtils.queryVerb();
        boolean flag = false;
        String verb = null;
        System.out.println(list.size());
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
            if(str.contains(list.get(i))) {
                flag = true;
                verb = list.get(i);
            }
        }
        System.out.println(str+":"+flag+":"+verb);
        return verb;
    }

}
