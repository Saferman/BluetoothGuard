package com.example.admin.bluetoothguard;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.bluetoothguard.guards.StatePerception;
import com.example.admin.bluetoothguard.voice.VoiceActivity;

public class LayoutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static boolean quit = false;  // 用于退出的时候双次退出
    private BlueTooth blueTooth = new BlueTooth();
    private TextView text;
    private Activity layoutActivity;
    private StatePerception statePerception;


    //各类操作请求码
    //打开蓝牙
    private final static int OPEN_BLUETOOTH_REQUEST_CODE = 1;
    //蓝牙搜索权限
    private final static int PERMISSION_REQUEST_COARSE_LOCATION = 2;
    //定位服务请求
    private final static int LOCATION_CODE = 3;
    //定位数据权限请求
    private final static int ACCESS_COARSE_LOCATION_CODE = 4;

    static public int get_OPEN_BLUETOOTH_REQUEST_CODE(){
        return OPEN_BLUETOOTH_REQUEST_CODE;
    }

    static public int get_PERMISSION_REQUEST_COARSE_LOCATION(){
        return PERMISSION_REQUEST_COARSE_LOCATION;
    }

    static public int get_LOCATION_CODE(){
        return LOCATION_CODE;
    }
    static public int get_ACCESS_COARSE_LOCATION_CODE(){
        return ACCESS_COARSE_LOCATION_CODE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);  //只需要服务的话这一行代码可以不要
//        Log.d("MyService", "LayoutActivity onCreate");


        // 获取当前活动
        layoutActivity = this;
        // 初始化安全防护算法类
        statePerception = new StatePerception(layoutActivity);

        text = (TextView) this.findViewById(R.id.textView);;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            if(quit==false){
//                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                quit=true;
//            }else{
//                super.onBackPressed();
//                this.finish();//退出当     前活动
//            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.show_BTdevices) {
            //清空显示的内容
            text.setText(null);
            //显示本机蓝牙
            text.append("本机蓝牙地址: " + blueTooth.getBlueToothAddress(this) + "\n");
            // 搜索附近的蓝牙设备，并测量距离
            blueTooth.searchBlueTooth(layoutActivity, BTReceiver);
//            // 打开蓝牙。直接用类实现，除非局部界面启动活动
//            blueTooth.openBlueTooth(this, OPEN_BLUETOOTH_REQUEST_CODE);
        } else if (id == R.id.tuling) {

        } else if (id == R.id.voiceInteraction) {
            Intent voiceIntent = new Intent(this, VoiceActivity.class);
            startActivity(voiceIntent);
        } else if (id == R.id.guards) {
            statePerception.start();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        //  下面这一行代码的作用https://blog.csdn.net/hxors/article/details/39212117
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case OPEN_BLUETOOTH_REQUEST_CODE:
                //当发起请求蓝牙打开事件时，会告诉你用户选择的结果
                if( resultCode == RESULT_OK) {
                    Toast.makeText(this, "蓝牙打开成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "蓝牙打开失败", Toast.LENGTH_SHORT).show();
                }
                break;
            case LOCATION_CODE:
                String s = String.valueOf(resultCode);
                Log.d("MyService", s);
//                if(resultCode == RESULT_OK){
//                    Toast.makeText(this, "打开了GPS定位服务", Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(this, "未打开GPS定位服务", Toast.LENGTH_SHORT).show();
//                }
                break;
            default:
                break;
        }
    }

    private BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            Log.d("MyService", action);
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 信号强度 https://blog.csdn.net/it_beecoder/article/details/61429473
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                String bondState = "";
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    bondState = "已配对";
                } else if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    bondState = "未配对";
                };
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                if(!blueTooth.searchRepeatCheck(deviceAddress)){
                    text.append("\n" + deviceName + "  " + deviceAddress  + "  " + bondState + "  " + rssi +"\n");
                }
            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                text.append("开始搜索附近蓝牙......\n");
            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                text.append("附近蓝牙搜索完毕\n");
                blueTooth.stopSearchBlueTooth(layoutActivity, BTReceiver);
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //回调，判断用户到底点击是还是否。
        //如果同时申请多个权限，可以for循环遍历
        if (requestCode == ACCESS_COARSE_LOCATION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //写入你需要权限才能使用的方法
            Toast.makeText(this,"获取到读取定位数据权限",Toast.LENGTH_SHORT).show();
        } else {
            // 没有获取 到权限，从新请求，或者关闭app
            Toast.makeText(this,"没有获取到读取定位数据权限",Toast.LENGTH_SHORT).show();
        }
    }

}
