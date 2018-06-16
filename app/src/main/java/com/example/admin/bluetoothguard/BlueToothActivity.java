package com.example.admin.bluetoothguard;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class BlueToothActivity extends AppCompatActivity {
    //BlueToothController 里面启动蓝牙的请求码
    public static final int REQUEST_CODE = 0;
    private BlueToothController mController = new BlueToothController();

    /**
     * 蓝牙开关广播
     * @param savedInstanceState
     */
//    当蓝牙的状态发生改变时，系统是会发出一个为BluetoothAdapter.ACTION_STATE_CHANGED的广播。
//    该广播携带两个参数，一个是BluetoothAdapter.EXTRA_PREVIOUS_STATE，表示之前的蓝牙状态
//    另一个是BluetoothAdapter.EXTRA_STATE，表示当前的蓝牙状态。而它们的值为以下四个：
//    BluetoothAdapter.STATE_TURNING_ON;
//    BluetoothAdapter.STATE_ON;
//    BluetoothAdapter.STATE_TURNING_OFF;
//    BluetoothAdapter.STATE_OFF;
//    分别代表，打开中，已打开，关闭中，已关闭。
    private BroadcastReceiver onoffreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch(state){
                case BluetoothAdapter.STATE_OFF:
//                    showToast("STATE_OFF");
                    break;
                case BluetoothAdapter.STATE_ON:
//                    showToast("STATE_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
//                    showToast("STATE_TURNING_ON");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
//                    showToast("STATE_TURNING_OFF");
                    break;
                default:
//                    showToast("Unkown STATE");
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //意图过滤器
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        //receiver监听filter广播
        registerReceiver(onoffreceiver, filter);
    }

    @Override
    protected void onDestroy() {
        //注销广播
        unregisterReceiver(onoffreceiver);
        super.onDestroy();
    }

//    public void requestTurnOnBlueTooth(){
//        mController.turnOnBlueTooth(this, REQUEST_CODE);
//    }
    // 打开蓝牙必须  mController.turnOnBlueTooth(this, REQUEST_CODE);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        //当发起请求蓝牙打开事件时，会告诉你用户选择的结果
        if( resultCode == RESULT_OK) {
            Toast.makeText(this, "打开成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "打开失败", Toast.LENGTH_SHORT).show();
        }
    }
}
