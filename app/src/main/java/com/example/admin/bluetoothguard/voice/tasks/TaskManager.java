package com.example.admin.bluetoothguard.voice.tasks;

import android.os.Handler;

import com.example.admin.bluetoothguard.voice.signals.Priority;
import com.example.admin.bluetoothguard.voice.signals.TaskSignal;
import com.example.admin.bluetoothguard.voice.signals.VoiceSignal;

import org.apache.http.conn.scheme.HostNameResolver;

import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by admin on 2018/7/4.
 * 这个类用于处理用户输入的语音中的功能，并实现VoiceSignal的放置和反馈
 *
 */

public class TaskManager {
    private PriorityBlockingQueue<TaskSignal> taskBlockQueue;
    private PriorityQueue<VoiceSignal> voiceQueue;
    // 执行任务的关键词
    private String[] taskCommandArray;
    // 发现需要执行的任务回调语音
    private String voiceFoundedTask;
    private Handler taskHanlder;

    // 当前正在执行的任务信号
    private TaskSignal currentTaskSignal = null;

    // 初始化
    public TaskManager(PriorityQueue<VoiceSignal> voiceQueue){
        taskBlockQueue = new PriorityBlockingQueue<>();
        this.voiceQueue = voiceQueue;
        taskCommandArray = Voice2Task.getTaskCommandArray();
        // 开启新线程执行TaskSignal
        taskHanlder = new Handler();
        taskHanlder.postDelayed(taskSignalHandler, 1000);//延时一秒
    }

    private Runnable taskSignalHandler = new Runnable() {
        @Override
        public void run() {
            // 执行任务
            int delaytime = 0;
            currentTaskSignal = taskBlockQueue.poll()
            if(currentTaskSignal == null){
                delaytime = 2000;
            }else{
                currentTaskSignal.executeTask(voiceQueue);
            }
            // 在这里设置为，因为是阻塞队列的非阻塞方法
            taskHanlder.postDelayed(this, delaytime);
        }
    };

    public boolean checkTask(String voiceText){
        for(String command:taskCommandArray){
            if(voiceText.contains(command)){
                // 找到需要执行的任务，同时新建好TaskSignal，设置好发现该任务的回调语音
                TaskSignal newTaskSignal = new TaskSignal("command");
                voiceFoundedTask = newTaskSignal.getVoiceFoundedTask();
                taskBlockQueue.add(newTaskSignal);
                // 我们认为每次用户的语音命令输入意图只是想执行一个任务
                return true;
            }
        }
        return false;
    }

    public String getVoiceFoundedTask(){
        return voiceFoundedTask;
    }

}
