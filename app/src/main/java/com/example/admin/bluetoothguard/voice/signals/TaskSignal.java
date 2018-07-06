package com.example.admin.bluetoothguard.voice.signals;

import android.support.annotation.NonNull;

import com.example.admin.bluetoothguard.voice.tasks.Voice2Task;

import java.util.PriorityQueue;

/**
 * Created by admin on 2018/7/4.
 */

public class TaskSignal implements Comparable<VoiceSignal>  {
    private String command;
    private String voiceFoundedTask;
    private int priority;
    private PriorityQueue<VoiceSignal> voiceQueue;
    private TaskSignal taskSignal;
    private VoiceSignal voiceSignal;

    public TaskSignal(String command){
        this.command = command;
        voiceFoundedTask = Voice2Task.voiceFoundedTaskMap(command);
        setPriority(Voice2Task.priorityMap(command));
        taskSignal = this;
    }

    public int getPriority(){
        return this.priority;
    }

    public void setPriority(int p){
        priority = p;
    }

    public String getVoiceFoundedTask(){
        return voiceFoundedTask;
    }

    public void executeTask(PriorityQueue<VoiceSignal> voiceQueue){
        this.voiceQueue = voiceQueue;
        Voice2Task.executeMap(command, this);
    }

    public void addVoiceSignal(VoiceSignal voiceSignal){
        this.voiceSignal = voiceSignal;
        voiceQueue.add(voiceSignal);
    }

    public String getVoiceSignalResult(){
        // 怎么在这里实现阻塞
        while(this.voiceSignal.getValidResult() == null){
        }
        return voiceSignal.getValidResult();
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
