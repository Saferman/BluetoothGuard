package com.example.admin.bluetoothguard.voice.signals;

import android.support.annotation.NonNull;

import com.example.admin.bluetoothguard.LogUtil;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by admin on 2018/7/4.
 */

public class VoiceSignal implements Comparable<VoiceSignal> {
    // 语音信号的类型
    // 我们认为程序发出的语音信号有三种类型
    // 1. 问答语音信息号，执行的任务需要用户做出某项决策或者输入信息都需要这种信号；
    // 提出问题后需要得到程序设定的接受范围答案后就结束
    // 2. 通知类信息，仅仅是执行任务的程序需要语音告知用户一些情况的时候使用
    // 3. 长久语音交互? 暂时不考虑这种情况
    private int priority = Priority.getNormal();

    // 这段VoiceSignal执行需要传递给用户的语音
    private String notificationVoiceText;
    // 没有找到用户语音输入的有效命令
    private String unfoundedVoiceText = "输入无效";
    public String getUnfoundedVoiceText(){
        return unfoundedVoiceText;
    }
    // 找到用户输入的有效命令
    private String founedVoiceText = "成功，您的输入是";
    public String getFounedVoiceText(){
        return founedVoiceText + validResult;
    }
    // 可以接受的用户反馈关键字，或逻辑
    private String[] validResultArray;
    // 标识是否得到有效的结果
//    private boolean hasValidResult = false;
    // 最终得到有效回答关键字
    // 此队列参考链接：https://blog.csdn.net/tianshi_kco/article/details/53026173
//    private ArrayBlockingQueue<String> validResultQueue =new ArrayBlockingQueue<String>(1);
    // 注意这个只能设置一次，因为任务子线程会实时读取！任务调用getValidResult后无需释放，因为foundedVoiceText还需要
    private String validResult = null;

    // 初始化
    public VoiceSignal(String notificationVoiceText, String [] validResultArray){
        this.notificationVoiceText = notificationVoiceText;
        this.validResultArray = validResultArray;
    }

    public String getNotificationVoiceText(){
        return notificationVoiceText;
    }

    public boolean checkValid(String voiceText){
        // 检查一段语音识别的文字是否包含有效的回答关键字
        // 这里容易出现子字符串包含导致的判断错误问题
        // 比如满意，不满意为可以接受的结果，但是由于先判断满意，即使用户输入不满意也会被识别为满意
        // 下述代码已解决问题
        String tempValidResult = null;
        for(String validResult:validResultArray){
            if(voiceText.contains(validResult)){
                if(tempValidResult == null){
                    tempValidResult = validResult;
                }else{
                    if(validResult.length() > tempValidResult.length()){
                        tempValidResult = validResult;
                    }
                }
            }
        }
        this.validResult = tempValidResult;
        if(this.validResult == null){
            return false;
        }else{
            return true;
        }
    }


    public String getValidResult(){
        return this.validResult;
    }

    public int getPriority(){
        return priority;
    }

    public void setPriority(int p){
        priority = p;
    }

    /**
     *
     * @Description:当前对象和其他对象做比较，当前优先级大就返回-1，优先级小就返回1
     * 值越小优先级越高
     * @date 2018/7/4
     */
    @Override
    public int compareTo(@NonNull VoiceSignal voiceSignal) {
        if (this.priority > voiceSignal.getPriority()){
            return 1;
        }else if(this.priority == voiceSignal.getPriority()){
            return 0;
        }else  if(this.priority < voiceSignal.getPriority()){
            return -1;
        }
        return 0;
        // 要想使用下一句，必须使用Integer 对象
//        return this.priority.compareTo(voiceSignal.getPriority());
    }
}
