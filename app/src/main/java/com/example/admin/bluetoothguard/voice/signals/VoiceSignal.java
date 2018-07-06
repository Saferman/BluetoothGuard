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
    private int priority = Priority.getNormal();

    // 这段VoiceSignal执行需要传递给用户的语音
    private String notificationVoiceText;
    // 没有找到用户语音输入的有效命令
    private String unfoundedVoiceText = "输入无效";
    public String getUnfoundedVoiceText(){
        return unfoundedVoiceText;
    }
    // 找到用户输入的有效命令
    private String founedVoiceText = "成功";
    public String getFounedVoiceText(){
        return founedVoiceText;
    }
    // 可以接受的用户反馈关键字，或逻辑
    private String[] validResultArray;
    // 标识是否得到有效的结果
//    private boolean hasValidResult = false;
    // 最终得到有效回答关键字
    // 此队列参考链接：https://blog.csdn.net/tianshi_kco/article/details/53026173
//    private ArrayBlockingQueue<String> validResultQueue =new ArrayBlockingQueue<String>(1);
    // 注意这个只能设置一次，因为任务子线程会实时读取！
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
