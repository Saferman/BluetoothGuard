package com.example.admin.bluetoothguard;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.admin.bluetoothguard.guards.StatePerception;

/**
 * Created by admin on 2018/6/14.
 */

// 服务自己关闭的方法Service.storSelf()

public class MyService extends Service {


    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Intent nfIntent = new Intent(this, LayoutActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, nfIntent, 0);
        // 在API11之后构建Notification的方式
//        Context context = this.getApplicationContext();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Bluetooth guard")
                .setContentText("安全防护算法运行中......")
                .setWhen(System.currentTimeMillis())//毫秒为单位
                .setSmallIcon(R.drawable.bluetooth)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bluetooth))
                .setAutoCancel(true)
                .setContentIntent(pi);
        Notification notification = builder.build(); // 获取构建好的Notification
        startForeground(1, notification);

        //  安全防护算法执行的线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //处理具体逻辑
            }
        }).start();
        //设置可唤醒CPU的定时器
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int interval = 1 * 5 * 1000; // 转换成毫秒数
        //二中方案如果是本地算法，需要长期运行，如果是远程算法可以快速短时间重启，读取数据 和远程交互
        long triggerAtTime = SystemClock.elapsedRealtime() + interval;
        Intent tempIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent broadcastPi = PendingIntent.getBroadcast(this, 0, tempIntent, 0);
        // 注意复制过来的代码一定要仔细检查！！之前启动程pi了23333
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, broadcastPi);//setExact() 方法精确无误的执行时间
//        Log.d("MyService", "onStartCommand executed");
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
//        Log.d("MyService", "onDestroy executed");
    }
}