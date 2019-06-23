package com.zhku161022.audioassistant;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.zhku161022.audioassistant.Utils.BeansUtil;
import com.zhku161022.audioassistant.Utils.PhoneUtil;
import com.zhku161022.audioassistant.Utils.SQLUtils;
import com.zhku161022.audioassistant.Utils.SpeechUtil;
import com.zhku161022.audioassistant.Utils.TranslateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 0;
    private PermissionsChecker mPermissionsChecker;
    static final String TAG = IatActivity.class.getSimpleName();
    ImageButton send;
    private long exitTime = 0;
    final int left = Gravity.LEFT;
    final int right = Gravity.RIGHT;
    private int count = 0;
    private TranslateUtils translateUtils = TranslateUtils.instance();//语音识别和合成对象
    public static StringBuffer stringBuffer = new StringBuffer();
     static Toast iatToast;
    private boolean mTranslateEnable = false;

    private Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            SpeechUtil.beginSpeechSynthesizer("图灵机器人听不清，请重新说一遍");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String answerBean = BeansUtil.jsonTransfromAnswerBean(response);
            Message message = Message.obtain();
            message.obj = answerBean;
            handler.sendMessage(message);
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
    //更新UI
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            addText(msg.obj.toString(),left);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.iv_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count >= 30){
                    remove();
                }
                SpeechUtil.beginSpeechRecognizer();
            }
        });
        TulinRobot.initTulinRobot();
        TulinRobot.setCallback(callback);
        SpeechUtil.initSpeechUtil(getApplicationContext());
        SpeechUtil.addRecognizerListener(recognizerListener);
        SpeechUtil.addSynthesizerListener(synthesizerListener);
        iatToast = Toast.makeText(this,"",Toast.LENGTH_LONG);
        translateUtils.setCallback(callback2);
        mPermissionsChecker = new PermissionsChecker(this);
    }
    private void addText(String text ,int fx){
        LinearLayout la = findViewById(R.id.ll);
        TextView tv = new TextView(MainActivity.this);
        tv.setId(count);
        tv.setText(text);
        tv.setTextSize(20);
        tv.setGravity(fx);
        if (fx==left){
//            tv.setBackgroundColor(Color.RED);
            tv.setPadding(5,10,100,10);
        }else{
//            tv.setBackgroundColor(Color.GREEN);
            tv.setPadding(100,10,5,10);
        }
        la.addView(tv);
        count++;
        ScrollView sc = findViewById(R.id.sc);
//        sc.fullScroll(View.FOCUS_DOWN);
//        sc.scrollTo(sc.getLeft(),sc.getHeight());
    }

    private void remove(){
        LinearLayout la = findViewById(R.id.ll);
//        for (int i = 0; i <10 ; i++) {
        la.removeAllViews();
//        }
        count = 0;
    }

    public RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据："+data.length);
        }
        @Override
        public void onBeginOfSpeech() {
            showTip("开始说话");
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
                addText(stringBuffer.toString(),right);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          if ((System.currentTimeMillis() - exitTime) > 2000) {//
              // 如果两次按键时间间隔大于2000毫秒，则不退出
              Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
              exitTime = System.currentTimeMillis();// 更新mExitTime
          } else {
              finish();
          }
          return true;
      }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this,"正在退出",Toast.LENGTH_SHORT);
    }

    public static void showTip(String string){
        iatToast.setText(string);
        iatToast.show();
    }

    public static void printResult(RecognizerResult results) {
        String res = BeansUtil.jsonTransfromResultBean(results);
//        addText(res,right);
//        edtShow.append(res);
        stringBuffer.append(res);
    }

//    public void printResultLast(RecognizerResult results) {
//        String res = BeansUtil.jsonTransfromResultBean(results);
//        stringBuffer.append(res);
//
//    }

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

    private void openApp(String str) {
        PackageManager packageManager = MainActivity.this.getPackageManager();
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

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS };
//    //还需申请的权限列表
//    private List<String> permissionsList = new ArrayList<String>();
//    //申请权限后的返回码
//    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;

//    private void checkRequiredPermission(final Activity activity){
//        for (String permission : permissionsArray) {
//            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
//                permissionsList.add(permission);
//            }
//        }
//        ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
//    }

//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                for (int i=0; i<permissions.length; i++) {
//                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        Toast.makeText(MainActivity.this, "做一些申请成功的权限对应的事！"+permissions[i], Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(MainActivity.this, "权限被拒绝： "+permissions[i], Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

}

