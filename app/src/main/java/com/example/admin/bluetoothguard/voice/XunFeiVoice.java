package com.example.admin.bluetoothguard.voice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by admin on 2018/7/4.
 */

public class XunFeiVoice {
    private Activity activity;
    private Gson mGson;

    public XunFeiVoice(Activity a){
        activity = a;
        mGson = new Gson();
        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在“ =”与 appid 之间添加任务空字符或者转义符
        // 将下面 APPID修改成你的，不要在等号后面加任何空白或者转义字符
        SpeechUtility.createUtility(activity, SpeechConstant.APPID +"=56f22e12");
    }

    // 参考 https://blog.csdn.net/dddxxxx/article/details/52458899
    public void getSpeechWithMyListener(RecognizerListener newRecognizerListener){
        //1. 创建SpeechRecognizer对象，第二个参数： 本地识别时传 InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(activity, null); //语音识别器
        //2. 设置听写参数，详见《 MSC Reference Manual》 SpeechConstant类
        mIat.setParameter(SpeechConstant. DOMAIN, "iat" );// 短信和日常用语： iat (默认)
        mIat.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mIat.setParameter(SpeechConstant. ACCENT, "mandarin" );// 设置普通话
        //3. 开始听写
        mIat.startListening(newRecognizerListener);
    }

    // 参考 https://blog.csdn.net/dddxxxx/article/details/52458899
    public void getSpeech(){
        //1. 创建SpeechRecognizer对象，第二个参数： 本地识别时传 InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer(activity, null); //语音识别器
        //2. 设置听写参数，详见《 MSC Reference Manual》 SpeechConstant类
        mIat.setParameter(SpeechConstant. DOMAIN, "iat" );// 短信和日常用语： iat (默认)
        mIat.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mIat.setParameter(SpeechConstant. ACCENT, "mandarin" );// 设置普通话
        //3. 开始听写
        mIat.startListening(mRecoListener);
    }

    // 听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        // 听写结果回调接口 (返回Json 格式结果，用户可参见附录 13.1)；
//一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
//关于解析Json的代码可参见 Demo中JsonParser 类；
//isLast等于true 时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e ("XunFei", results.getResultString());
            String text = JsonParser.parseIatResult(results.getResultString());
        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
            // 获取错误码描述
            Log. e("XunFei", "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
        }

        // 开始录音
        public void onBeginOfSpeech() {
        }

        //volume 音量值0~30， data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
        }

        // 结束录音
        public void onEndOfSpeech() {
        }

        // 扩展用接口
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
        }
    };

    // 语音合成
    public void Synthesize(String textVoice) {
        //1.创建 SpeechSynthesizer 对象, 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(activity, null);
        //2.合成参数设置，详见《 MSC Reference Manual》 SpeechSynthesizer 类
        //设置发音人（更多在线发音人，用户可参见 附录13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
        mTts.startSpeaking(textVoice, null);
    }

}
