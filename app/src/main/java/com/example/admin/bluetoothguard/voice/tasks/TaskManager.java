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
    // 结束Handler的标识符
    private boolean finishFlag;
    // 标识是否已经开启了一个子线程
    private boolean existThread;

    // 当前正在执行的任务信号
    private TaskSignal currentTaskSignal = null;

    // 初始化
    public TaskManager(PriorityQueue<VoiceSignal> voiceQueue){
        taskBlockQueue = new PriorityBlockingQueue<>();
        this.voiceQueue = voiceQueue;
        taskCommandArray = Voice2Task.getTaskCommandArray();
        // 开启新线程执行TaskSignal
        finishFlag = false;
        existThread = false;
        taskHanlder = new Handler();
        taskHanlder.postDelayed(taskSignalHandler, 1000);//延时一秒
    }

    private Runnable taskSignalHandler = new Runnable() {
        @Override
        public void run() {
            // 执行taskSignal 处理的循环间隔时间，毫秒
            int delaytime = 2000;
            if(!existThread){
                new Thread(){
                    @Override
                    public void run(){
                        // 别忘了在run()里面清理掉你使用的资源(比如关闭打开的文件,数据库等)
                        super.run();
                        currentTaskSignal = taskBlockQueue.poll();
                        if(currentTaskSignal == null){
                        }else{
                            currentTaskSignal.executeTask(voiceQueue);
                        }
                        // TaskSignal处理结束后就将其清空
                        currentTaskSignal = null;
                        existThread = false;
                    }
                }.start();
                existThread = true;
            }else{
                // 已经存在一个子线程处理
                delaytime = 5000;
            }
            //在这里设置为2秒，因为是阻塞队列的非阻塞方法
            if(!finishFlag){
                taskHanlder.postDelayed(this, delaytime);
            }
        }
    };

    public boolean checkTask(String voiceText){
        // 这里容易出现子字符串包含导致的判断错误问题
        // 比如测试和详细测试都是任务命令，但是由于先判断测试，即使用户输入详细测试也会被识别为测试
        // 下述代码已解决问题
        String finalCommand = null;
        for(String command:taskCommandArray){
            if(voiceText.contains(command)){
                // 找到需要执行的任务，同时新建好TaskSignal，设置好发现该任务的回调语音
                if(finalCommand == null){
                    finalCommand = command;
                }else{
                    if(command.length() > finalCommand.length()){
                        finalCommand = command;
                    }
                }
            }
        }

        if(finalCommand == null){
            return false;
        }else{
            TaskSignal newTaskSignal = new TaskSignal(finalCommand);
            voiceFoundedTask = newTaskSignal.getVoiceFoundedTask();
            taskBlockQueue.add(newTaskSignal);
            // 我们认为每次用户的语音命令输入意图只是想执行一个任务
            return true;
        }
    }

    public String getVoiceFoundedTask(){
        return voiceFoundedTask;
    }

    public void finishTread(){
        finishFlag = true;
    }

}
