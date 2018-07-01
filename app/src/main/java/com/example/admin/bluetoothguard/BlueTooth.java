package com.example.admin.bluetoothguard;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Vector;

/**
 * Created by admin on 2018/6/16.
 */

public class BlueTooth {
    private BlueToothController mController;
    private Vector<String> deviceAddresses = new Vector<String>();


    public BlueTooth() {
        //获取手机的蓝牙适配器，如果不为空手机支持蓝牙，为空不支持蓝牙
        mController = new BlueToothController();
    }

    private void checkPermission(Activity activity, int PERMISSION_REQUEST_COARSE_LOCATION){
        //蓝牙搜索权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }


    public void searchBlueTooth(Activity activity, BroadcastReceiver BTReceiver){
        this.checkPermission(activity, LayoutActivity.get_PERMISSION_REQUEST_COARSE_LOCATION());
        if(!mController.getBlueToothStatus()){
            mController.turnOnBlueTooth(activity, LayoutActivity.get_OPEN_BLUETOOTH_REQUEST_CODE());
        }
        if(mController.getBlueToothStatus()){
            // https://blog.csdn.net/qq_25827845/article/details/52997523/
            IntentFilter filter_started = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            activity.registerReceiver(BTReceiver,filter_started );
            IntentFilter filter_founded=new IntentFilter(BluetoothDevice.ACTION_FOUND);
            activity.registerReceiver(BTReceiver, filter_founded);
            IntentFilter filter_finished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            activity.registerReceiver(BTReceiver,filter_finished);
            //清空deviceAddresses
            deviceAddresses.clear();
            mController.startDiscovery();
        }
    }

    public boolean searchRepeatCheck(String deviceAddress){
        for(String item : deviceAddresses ){
            if(item.equals(deviceAddress)){
                return true;
            }
        }
        deviceAddresses.add(deviceAddress);
        return false;
    }

    public void stopSearchBlueTooth(Activity activity, BroadcastReceiver BTReceiver){
        mController.cancelDiscovery();
        activity.unregisterReceiver(BTReceiver);
    }

    public void openBlueTooth(Activity activity, int requestCode){
        mController.turnOnBlueTooth(activity, requestCode);
    }

    /**
     * 得到本机的蓝牙地址
     * @return
     */
    public String getBlueToothAddress(Activity activity){
        return android.provider.Settings.Secure.getString(activity.getContentResolver(), "bluetooth_address");
    };


}
