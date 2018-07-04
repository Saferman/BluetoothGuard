package com.example.admin.bluetoothguard.voice.signals;

import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by admin on 2018/7/4.
 */

public class VoiceSignal implements Comparable<VoiceSignal> {
    private int priority = Priority.getNormal();

    // 这段VoiceSignal执行需要传递给用户的语音
    private String notificationVoiceText;
    // 可以接受的用户反馈关键字，或逻辑
    private String[] validResultArray;
    // 标识是否得到有效的结果
    private boolean hasValidResult = false;
    // 最终得到有效回答关键字
    // 此队列参考链接：https://blog.csdn.net/tianshi_kco/article/details/53026173
    private ArrayBlockingQueue<String> validResultQueue =new ArrayBlockingQueue<String>(1);

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
        for(String validResult:validResultArray){
            if(voiceText.contains(validResult)){
                this.validResultQueue.add(validResult);
                return true;
            }
        }
        return false;
    }

    public String getValidResult(){
        return this.validResultQueue.take();
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
