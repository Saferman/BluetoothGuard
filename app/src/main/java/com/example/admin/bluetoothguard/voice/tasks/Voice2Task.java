package com.example.admin.bluetoothguard.voice.tasks;

import com.example.admin.bluetoothguard.voice.programs.Tuling;
import com.example.admin.bluetoothguard.voice.signals.Priority;
import com.example.admin.bluetoothguard.voice.signals.TaskSignal;
import com.example.admin.bluetoothguard.voice.signals.VoiceSignal;

import java.util.PriorityQueue;

/**
 * Created by admin on 2018/7/4.
 */

public class Voice2Task {
    private static final String[] taskCommandArray = {"图灵", "查询天气"};

    public static String[] getTaskCommandArray(){
        return taskCommandArray;
    }

    public static String voiceFoundedTaskMap(String c){
        String r = "未收到有效执行命令";
        if(c.equals("图灵")){
            r = "收到图灵命令请求";
        }
        return r;
    }

    public static int priorityMap(String c){
        int priority = Priority.getNormal();
        return priority;
    }

    public static void executeMap(String command, TaskSignal taskSignal){
        if(Tuling.getTriggerCommand() == command){
            new Tuling(taskSignal).interact();
        }
    }


}
