package com.example.admin.bluetoothguard;

/**
 * 我爱小萌花
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.admin.bluetoothguard.guards.StatePerception;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //启动我的服务
        Intent startIntent = new Intent(this, MyService.class);
        startService(startIntent);

        Intent uiIntent = new Intent(this, LayoutActivity.class);
        startActivity(uiIntent);

        // 执行安全防护算法
//        //Handler开启线程定时循环
//        new StatePerception().evaluate(this);
        Log.d("MyService", "MainActivity continue");//会被执行
    }

    @Override
    public void onDestroy(){

        //关闭我的服务
        Intent stopIntent = new Intent(this, MyService.class);
        stopService(stopIntent);

        super.onDestroy();
    }
}
