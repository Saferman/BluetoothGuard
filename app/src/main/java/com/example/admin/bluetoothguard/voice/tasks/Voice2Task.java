package com.example.admin.bluetoothguard.voice.tasks;

import com.example.admin.bluetoothguard.voice.programs.FuncTest;
import com.example.admin.bluetoothguard.voice.programs.Tuling;
import com.example.admin.bluetoothguard.voice.signals.Priority;
import com.example.admin.bluetoothguard.voice.signals.TaskSignal;
import com.example.admin.bluetoothguard.voice.signals.VoiceSignal;

import java.util.PriorityQueue;

/**
 * Created by admin on 2018/7/4.
 */

public class Voice2Task {
    // 执行任务的触发命令不能包括系统指令（数组和数组元素都不能包含系统指令）
    // 优先系统指令？ 系统指令危险操作需要重复确认，还是不需要系统指令，统一归到任务命令
    // 各类VoiceSignal 允许包括系统指令，在我的程序中可视为新的执行空间

    private static final String[] taskCommandArray = {"图灵", "测试"};

    public static String[] getTaskCommandArray(){
        return taskCommandArray;
    }

    public static String voiceFoundedTaskMap(String c){
        String r = "未收到有效执行命令";
        if(c.equals("图灵")){
            r = "收到图灵命令请求";
        }
        if(c.equals("测试")){
            r = "收到测试命令请求";
        }
        return r;
    }

    public static int priorityMap(String c){
        int priority = Priority.getNormal();
        return priority;
    }

    public static void executeMap(String command, TaskSignal taskSignal){
        if(Tuling.getTriggerCommand().equals(command)){
            new Tuling(taskSignal).interact();
        }
        if(FuncTest.getTriggerCommand().equals(command)){
            new FuncTest(taskSignal).test();
        }
    }


}
