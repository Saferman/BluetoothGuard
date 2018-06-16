package com.example.admin.bluetoothguard;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by admin on 2018/6/15.
 */

public class TestService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //处理具体逻辑
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}