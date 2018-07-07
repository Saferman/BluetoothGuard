package com.example.admin.bluetoothguard.tuling;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.bluetoothguard.LogUtil;
import com.example.admin.bluetoothguard.R;
import com.example.admin.bluetoothguard.voice.JsonParser;
import com.example.admin.bluetoothguard.voice.VoiceActivity;
import com.example.admin.bluetoothguard.voice.XunFeiVoice;
import com.example.admin.bluetoothguard.voice.programs.Tuling;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;

// 图灵聊天机器人，现在先单独分离出来做，以后集成进VoiceActivity

public class TulingActivity extends AppCompatActivity {

    private final String tulingName = "图灵机器人";
    private final String userName = "我";

    private boolean finishFlag;

    private Activity tulingActivity;
    private TextView textView;
    private Button exitButton;
    private XunFeiVoice xunfeiVoice;
    private Handler mHandler;
    // 保存用户单词单次录音识别的文本信息
    private StringBuilder singleRecordResult = new StringBuilder();
    // 每次录音结束后，延时多久启动下一次录音
    private int loopTime;
    // 图灵擦操作对象
    private TulingHandler tulingHandler = new TulingHandler();
    // 储存图灵反馈的语音列表
    //ArrayList根类为collection，所以ArrayList可以使用collection中定义的方法
    private ArrayList<String> tulingResArray = new ArrayList<String>();
    private int resArrayPos = 0;  // 标识上面数组播放的语音情况，只有当其对于数组长度才视为播放完数组语音
    // 用于储存其他图灵返回的结果需要进行的操作
    private TulingAction tulingAction = new TulingAction();
    // 用于处理网络操作反馈的handler
    // 当网络操作结束时，会发送消息让这个handler处理
    private static final int TULING_HTTP_FINISHED_MESSAGE = 1;
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what) {
                case TULING_HTTP_FINISHED_MESSAGE:
                    String tulingResponseText = tulingResArray.get(resArrayPos);
                    xunfeiVoice.SynthesizeWithListener(tulingResponseText, mTtsListener);
                    // 其余操作
                    if(tulingAction.getOpt() != null){
                        tulingAction.doAction(tulingActivity);
                        tulingAction.clearOpt();
                    }
                    textView.append(tulingName + " : " + tulingResponseText +" \n");
                    loopTime = 1000;// 怎么解决下次录音会记录播放的语音
                    mHandler.postDelayed(voiceRecognize, loopTime);// 延时x毫秒开始执行下一次录音
                    break;
                default:
                    break;
            }
        }
    };

    // 标识是否处于语音播放阶段,要求不能录音阶段调用播音功能
    private boolean isSynthesize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuling);
        initPermission();
        tulingActivity = this;
        finishFlag = false;
        textView = (TextView) findViewById(R.id.textView);
        exitButton = (Button) findViewById(R.id.tulingExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        xunfeiVoice = new XunFeiVoice(TulingActivity.this);
        isSynthesize = false;
        tulingResArray.clear();
        interaction();
    }

    private void interaction(){
        xunfeiVoice.SynthesizeWithListener(tulingName + "为您服务，请开始说话", mTtsListener);
        textView.setText(null);
        mHandler = new Handler();
        //麦克风和音响独立不延时会影响识别
        mHandler.postDelayed(voiceRecognize, 2000);//延时x秒，单位毫秒
    }

    //    private int delaytime = 2000;// 延时二秒
    // 唯一的语音识别调用接口
    private Runnable voiceRecognize = new Runnable() {
        @Override
        public void run() {
            // 执行任务
            if(isSynthesize){
                mHandler.postDelayed(voiceRecognize, 1000);
            }else {
                // 可以插入一个提示音字符
                if(!finishFlag){
                    xunfeiVoice.getSpeechWithMyListener(newRecoListener);
                }
            }
        }
    };



    private RecognizerListener newRecoListener = new RecognizerListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            singleRecordResult.append(text);

        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
            // 获取错误码描述
            Log.e("XunFei", "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
        }

        // 开始录音
        public void onBeginOfSpeech() {
            singleRecordResult.setLength(0);
        }

        //volume 音量值0~30， data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
        }

        // 结束录音
        public void onEndOfSpeech() {
            tulingResArray.clear();
            LogUtil.d("Tuling",String.valueOf(singleRecordResult.toString().length()));
            if(!finishFlag && singleRecordResult.toString().length() > 1){
                textView.append(userName + " ：" + singleRecordResult.toString() +"\n");
                tulingHandler.handle(tulingResArray, uiHandler, singleRecordResult.toString(), tulingAction);
            }else{
                mHandler.postDelayed(voiceRecognize, 500);//延时x秒，单位毫秒
            }

        }

        // 扩展用接口
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        finishFlag = true;
        Toast.makeText(this, tulingName+"活动销毁", Toast.LENGTH_SHORT).show();
    }

    /**
     * 语音合成回调
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {



        @Override
        public void onSpeakResumed() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSpeakProgress(int arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSpeakPaused() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSpeakBegin() {
            isSynthesize = true;
            resArrayPos += 1;
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

        }

        @Override
        public void onCompleted(SpeechError error) {
            if(resArrayPos < tulingResArray.size()){
                xunfeiVoice.SynthesizeWithListener(tulingResArray.get(resArrayPos),mTtsListener);
            }else{
                resArrayPos = 0;
                tulingResArray.clear();
                isSynthesize = false;
            }
        }

        @Override
        public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
            // TODO Auto-generated method stub

        }
    };


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();      //退出当前活动
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.INTERNET,
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(TulingActivity.this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
}
