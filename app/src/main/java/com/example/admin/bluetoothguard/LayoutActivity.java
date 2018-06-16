package com.example.admin.bluetoothguard;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class LayoutActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static boolean quit = false;  // 用于退出的时候双次退出
    private BlueTooth blueTooth = new BlueTooth();

    //各类操作请求码
    //打开蓝牙
    private final int OPEN_BLUETOOTH_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);  //只需要服务的话这一行代码可以不要
//        Log.d("MyService", "LayoutActivity onCreate");


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
            // Handle the camera action
        } else if (id == R.id.open_BT) {
            Log.d("MyService", "Click open_BT");
            //直接用类实现，除非局部界面启动活动
            blueTooth.openBlueTooth(this, OPEN_BLUETOOTH_REQUEST_CODE);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

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
                    Toast.makeText(this, "打开成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "打开失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }


    }
}
