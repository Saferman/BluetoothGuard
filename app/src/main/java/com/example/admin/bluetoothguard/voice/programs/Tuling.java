package com.example.admin.bluetoothguard.voice.programs;

import com.example.admin.bluetoothguard.voice.signals.TaskSignal;

/**
 * Created by admin on 2018/7/4.
 */

public class Tuling {
    private static final String triggerCommand = "图灵";
    public static String getTriggerCommand(){
        return triggerCommand;
    }
    // 实例区域
    // 实例化并调用该类方法的taskSignal
    private TaskSignal bindTaskSignal;

    public Tuling(TaskSignal taskSignal){
        bindTaskSignal = taskSignal;
    }

    public void interact(){
        // 如果遇到需要语音交互的地方，新建一个VoiceSignal,需要指定提出的问题，和可以接受的答案范围，并且调用bindTaskSignal.addVoiceSignal（）
        // 使用bindTaskSignal.getVoiceSignalResult 等待结果！！
        // 只能有一个VoiceSignal
    }
}
