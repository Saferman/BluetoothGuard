package com.example.admin.bluetoothguard;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Created by admin on 2018/6/16.
 *
 * https://www.jianshu.com/p/a1c8e9802373
 */

public class BlueToothController {

    //蓝牙适配器
    private BluetoothAdapter mAdapter;

    /**
     * 跟类名相同的方法名被称作构造方法，其作用是用于当一个类被new成对象时，对象需要声明的一些变量的构造声明。
     */
    public BlueToothController() {
        //获取手机的蓝牙适配器，如果不为空手机支持蓝牙，为空不支持蓝牙
        mAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 是否支持蓝牙
     */
    public boolean isSupportBlueTooth(){
        if(mAdapter != null){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * 开始发现蓝牙
     */
    public void startDiscovery(){
        assert(mAdapter!=null);
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
        mAdapter.startDiscovery();
    }

    /**
     * 关闭发现蓝牙
     */
    public void cancelDiscovery(){
        assert(mAdapter!=null);
        if (mAdapter.isDiscovering()) {
            mAdapter.cancelDiscovery();
        }
    }

    /**
     * 判断蓝牙当前状态 true 打开,false 关闭
     */
    public boolean getBlueToothStatus(){
        //asserts断言， 只有为true才继续执行下去
        assert(mAdapter!=null);
        return mAdapter.isEnabled();
    }

    /**
     * 打开蓝牙
     */
    //    我们一般打开蓝牙会使用到系统提供的一个Activity。这个Activity的action为：
    //      BluetoothAdapter.ACTION_REQUEST_ENABLE。
    //    然后我们要使用startActivityForResult()这个方法来启动它。
    //    这个Activity是有返回值的，如果用户选择的是打开，我们应该可以收到一个RESULT_OK
    //    如果用户选择的是取消，我们应该可以收到一个RESULT_CANCELED。
    public void turnOnBlueTooth(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
//        mAdapter.enable();
    }

    /**
     * 关闭蓝牙
     */
    public void turnOffBlueTooth(){
        mAdapter.disable();
    }
}
