package com.example.admin.bluetoothguard.voice;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.bluetoothguard.R;
import com.example.admin.bluetoothguard.voice.signals.VoiceSignal;
import com.example.admin.bluetoothguard.voice.tasks.TaskManager;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;

import java.util.ArrayList;
import java.util.PriorityQueue;


public class VoiceActivity extends AppCompatActivity {
    private Activity voiceActivity;
    private TextView textView;
    private Button exitButton;
    private XunFeiVoice xunfeiVoice;
    private Handler mHandler;
    // 每次录音结束后，延时多久启动下一次录音
    private int loopTime;
    // 记录每次录音记录的字符串
    private StringBuilder singleRecordResult = new StringBuilder();
    // 命令关键字
    // 识别到到关键字的时候会语音告诉用户（下一次的录音延时到语音播放结束后，尽可能短）
    // 之后开启多次记录模式，直到识别到一次可执行任务，或者在单次录音中找到endCommand，并语音告诉（也需要延时下一次录音）
    private String startCommand = "你好";
    private boolean hasCommand = false;
    private String startResponse = "主人";
    private String endCommand = "结束";
    private String endResponse = "进入休息";
    // 记录多次录音记录的字符串
    private StringBuilder multipleRecordResult = new StringBuilder();

    // 语音交互的任务
    // 语音交互任务不抢占录音处于hasCommand的状态
    // 这种队列是线程不安全的
    private PriorityQueue<VoiceSignal> voiceQueue = new PriorityQueue<>();
    // 是否执行VoiceSignal 和 对应的新的Listener
    private boolean hasVoiceSignal = false;


    // 任务管理类
    private TaskManager taskManager = new TaskManager(voiceQueue);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        initPermission();
        voiceActivity = this;
        textView = (TextView) findViewById(R.id.textView);
        exitButton = (Button) findViewById(R.id.voiceExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceActivity.finish();
            }
        });
        xunfeiVoice = new XunFeiVoice(VoiceActivity.this);
        Log.d("XunFei", "create VoiceActivity");
        interaction();
    }

    private void interaction(){
        xunfeiVoice.Synthesize("智能语音管家为您服务，请开始说话");
        Log.d("XunFei", "Synthesize结束");//语音未播放完毕就执行了这一句
        mHandler = new Handler();
        //麦克风和音响独立不延时会影响识别
        mHandler.postDelayed(voiceRecognize, 5000);//延时五秒
    }

//    private int delaytime = 2000;// 延时二秒
    private Runnable voiceRecognize = new Runnable() {
        @Override
        public void run() {
            // 执行任务
            if(hasVoiceSignal){
                xunfeiVoice.getSpeechWithMyListener(voiceSignalRecoListener);
            }else{
                xunfeiVoice.getSpeechWithMyListener(newRecoListener);
            }

//            mHanlder.postDelayed(this, delaytime);//毫秒,再次执行task本身,实现了循环的效果
        }
    };


    // 听写监听器
    // singleRecordResult 用于 监听启动关键词
    // multipleRecordResult 用于识别用户的命令
    private RecognizerListener newRecoListener = new RecognizerListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            textView.append(text);
            singleRecordResult.append(text);
            if(!hasCommand){
                if (text.contains(startCommand)){
                    // 发现关键字
                    hasCommand = true;
                    xunfeiVoice.Synthesize(startResponse);
                }
            }
            if(hasCommand){
                multipleRecordResult.append(text);
            }

        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
            // 获取错误码描述
            Log.e("XunFei", "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
        }

        // 开始录音
        public void onBeginOfSpeech() {
            singleRecordResult.setLength(0);
            if(!hasCommand){
                multipleRecordResult.setLength(0);
            }
            loopTime = 0;
        }

        //volume 音量值0~30， data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
        }

        // 结束录音
        public void onEndOfSpeech() {
            textView.append("---录音结束\n");
            if(!hasCommand){
                if(singleRecordResult.toString().contains(startCommand)){
                    // 发现关键字
                    hasCommand = true;
                    xunfeiVoice.Synthesize(startResponse);
                }
            }
            if(hasCommand){
//                textView.append("此时的multipleRecordResult:"+multipleRecordResult+"\n");
                if(multipleRecordResult.toString().contains(endCommand)){
                    // 开启线程执行功能
                    hasCommand = false;
                    xunfeiVoice.Synthesize(endResponse);
                    loopTime = 2000;// 保证语音读完才开始继续录音
                }else if(taskManager.checkTask(multipleRecordResult.toString())){
                    // 语音告诉用户程序捕获到需要执行的命令
                    String voiceFoundedTask = taskManager.getVoiceFoundedTask();
                    xunfeiVoice.Synthesize(voiceFoundedTask);
                    loopTime = 2000;// 保证语音读完才开始继续录音
                    hasCommand = false;
                }
            }else{
                // 不抢占hasCommand状态
                checkVoiceSignal();
            }

            mHandler.postDelayed(voiceRecognize, loopTime);// 延时x毫秒开始执行下一次录音
        }

        // 扩展用接口
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
        }
    };

    private void checkVoiceSignal(){
        this.currentVoiceSignal = voiceQueue.poll();
        if(this.currentVoiceSignal != null){
            // voiceQueue队列不为空
            // 将启动一段录音程序得到用户符合voiceSingal中可选择答案的结果后结束
            // 或者启动一段新的长时间交互录音状态
            // 二种方案：重新设置mHandller的Runnable对象，或者重新设置监听器
            // 新加入的VoiceSignal 并没有独立的储存录入的字符串的空间，和主录音一致
            hasVoiceSignal = true;
            xunfeiVoice.Synthesize(this.currentVoiceSignal.getNotificationVoiceText());
            singleRecordResult.setLength(0);
            multipleRecordResult.setLength(0);
            textView.setText(null);
        }
    }

    // 编写voiceSingalRecoListener
    // 正在执行的voiceSignal
    // singleRecordResult只是用于方便每次结束multiple添加内容
    private VoiceSignal currentVoiceSignal; //设置的值在语音结束后会残留，小心调用
    private RecognizerListener voiceSignalRecoListener = new RecognizerListener() {
        @Override
        public void onResult(RecognizerResult results, boolean b) {
            String text = JsonParser.parseIatResult(results.getResultString());
            textView.append(text);
            singleRecordResult.append(text);
        }

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {
            singleRecordResult.setLength(0);
            loopTime = 0;
        }

        @Override
        public void onEndOfSpeech() {
            textView.append("---voiceSignal录音结束\n");
            multipleRecordResult.append(singleRecordResult.toString());
            String playedVoice;
            // 检验现在用户输入的语音是否是有效（期望的答案）
            if(currentVoiceSignal.checkValid(multipleRecordResult.toString())){
                playedVoice = currentVoiceSignal.getFounedVoiceText();// 得到有效回答时播放的反馈语音，可优化加入具体的得到的结果
                xunfeiVoice.Synthesize(playedVoice);
                // 恢复主录音参数
                hasVoiceSignal = false;
                textView.setText(null);
                singleRecordResult.setLength(0);
                multipleRecordResult.setLength(0);
            }else{
                // 如果没找到，只保留这次的录音
                playedVoice = currentVoiceSignal.getUnfoundedVoiceText(); //没得到有效回答时播放的反馈语音
                xunfeiVoice.Synthesize(playedVoice);
                multipleRecordResult.setLength(0);
                multipleRecordResult.append(singleRecordResult.toString());
            }
            // 根据前面的语音输出动态设置这个值
            loopTime = 2000;
            mHandler.postDelayed(voiceRecognize, loopTime);// 延时x毫秒开始执行下一次录音
        }



        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 改写
     */
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();      //退出当前活动
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        taskManager.finishTread();
        Toast.makeText(this, "智能语音管家活动销毁", Toast.LENGTH_SHORT).show();
    }


    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        String permissions[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(VoiceActivity.this, perm)) {
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
