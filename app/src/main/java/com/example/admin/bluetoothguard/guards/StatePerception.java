package com.example.admin.bluetoothguard.guards;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.example.admin.bluetoothguard.R;



/**
 * Created by admin on 2018/6/26.
 */

public class StatePerception {
    private boolean isReady;
    private boolean hasThread;
    private MyLocation mylocation;
    private Activity uiActivity;  //先使用UI界面的活动，以后再说
    private TextView textView;


    public StatePerception(Activity activity){
        isReady = false;
        mylocation = new MyLocation();
        uiActivity = activity;
        hasThread = false;
        textView = (TextView) uiActivity.findViewById(R.id.textView);
    }

    public void start(){
        // 确保用户多次点击不会重新开启线程等
        // 强制用户申请权限，就算没权限不影响yunxing
        Log.d("MyService", "StatePerception start");
        if(getReady()){
            isReady = true;
        }
        if(isReady){
            //清空显示的内容
            textView.setText(null);
            textView.append("成功启动安全防护算法^_^\n");
            //避免用户多次点击开启多个线程
            if(!hasThread){
                Log.d("MyService", "新的安全防护算法线程");
                mHanlder.postDelayed(task, 1000);//第一次调用,延迟1秒执行task
                hasThread = true;
            }
        }
    }

    public boolean getReady(){
        // 定位权限 和 GPS服务打开申请
        if(!mylocation.checkPermission(uiActivity)){
            return false;
        }
        return true;
    }

    public void evaluate(){
        // 位置经度纬度获取
        MyResult locationResult = mylocation.getLocation(uiActivity);
        String showString;
        if (locationResult.getBoolean("success")){
            showString= "维度为：" + locationResult.getDouble("latitude") + "  经度为："
                    + locationResult.getDouble("longitude");

        }else{
            showString = "位置获取失败";
        }
        textView.append(showString+"\n");
    }


    private Handler mHanlder = new Handler();
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            /**
             * 此处执行任务
             * */
            evaluate();
            mHanlder.postDelayed(this, 5 * 1000);//延迟5秒,再次执行task本身,实现了循环的效果
        }
    };
}
