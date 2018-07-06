package com.example.admin.bluetoothguard.voice.programs;

import com.example.admin.bluetoothguard.LogUtil;
import com.example.admin.bluetoothguard.voice.signals.TaskSignal;
import com.example.admin.bluetoothguard.voice.signals.VoiceSignal;

/**
 * Created by admin on 2018/7/5.
 */

public class FuncTest {
    private static final String triggerCommand = "测试";
    public static String getTriggerCommand(){
        return triggerCommand;
    }
    // 实例区域
    // 实例化并调用该类方法的taskSignal
    private TaskSignal bindTaskSignal;

    public FuncTest(TaskSignal taskSignal){
        bindTaskSignal = taskSignal;
    }

    public void test(){
        // 如果遇到需要语音交互的地方，新建一个VoiceSignal,需要指定提出的问题，和可以接受的答案范围，并且调用bindTaskSignal.addVoiceSignal（）
        // 使用bindTaskSignal.getVoiceSignalResult 等待结果！！
        // 只能有一个VoiceSignal,因为 add VoiceSignal 是覆盖变量不是写入列表队列
        // 任何指定的回答不能包括系统指令
        String[] validResultArray = {"满意", "不满意"};
        VoiceSignal voiceSignal = new VoiceSignal("请给测试功能做出评价", validResultArray);
        bindTaskSignal.addVoiceSignal(voiceSignal);
        String result = bindTaskSignal.getVoiceSignalResult();
        LogUtil.d("FuncTest", result);
    }
}
