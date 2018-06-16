package com.example.admin.bluetoothguard;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

/**
 * Created by admin on 2018/6/16.
 */

public class BlueTooth {
    private BlueToothController mController;

    public BlueTooth() {
        //获取手机的蓝牙适配器，如果不为空手机支持蓝牙，为空不支持蓝牙
        mController = new BlueToothController();
    }

    public void searchBlueTooth(){

    }

    public void openBlueTooth(Activity activity, int requestCode){
        mController.turnOnBlueTooth(activity, requestCode);
    }


//    //得到蓝牙的适配器，null表示不支持蓝牙
//    BluetoothAdapter mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
//
//
//    private void checkBT(){
//        if(!mBluetoothAdapter.isEnabled()){
//            //若没打开则打开蓝牙
//            mBluetoothAdapter.enable();
//            mBluetoothAdapter.cancelDiscovery();
//        }
//    }
//
//    public void searchBT(){
//        checkBT();
//        mBluetoothAdapter.startDiscovery();
//    }

//    //定义广播
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action=intent.getAction();
//        if(action.equals(BluetoothDevice.ACTION_FOUND)){
//            BluetoothDevice device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
////          device.getBondState()==BluetoothDevice.BOND_BONDED//因此，我们还可以直接获取之前配对过的设备
//         }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
//            mBluetoothAdapter.cancelDiscovery();
//        }
//    }

}
