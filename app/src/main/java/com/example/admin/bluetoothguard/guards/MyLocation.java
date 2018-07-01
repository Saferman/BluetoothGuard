package com.example.admin.bluetoothguard.guards;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.admin.bluetoothguard.LayoutActivity;

import java.util.List;

/**
 * Created by admin on 2018/6/26.
 * https://www.cnblogs.com/zj0208/p/6207077.html
 * https://blog.csdn.net/zheng0906/article/details/80354641
 */

public class MyLocation {
    private boolean hasPermission = false;
    private boolean hasGPS = false;
    boolean success;
    double latitude;
    double longitude;
    private LocationManager locationManager;
    private boolean hasSetLocationListener = false;


    public boolean checkPermission(Activity activity){
        // 权限申请
        if (Build.VERSION.SDK_INT >= 23) {
            //如果超过6.0才需要动态权限，否则不需要动态权限
            //如果同时申请多个权限，可以for循环遍历
            int check = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (check == PackageManager.PERMISSION_GRANTED) {
                Log.d("MyService", "高于6.0已经授权");
                //写入你需要权限才能使用的方法
                hasPermission = true;
            } else {
                hasPermission = false;
                //手动去请求用户打开权限(可以在数组中添加多个权限) 1 为请求码 一般设置为final静态变量
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LayoutActivity.get_ACCESS_COARSE_LOCATION_CODE());
            }
        } else {
            //写入你需要权限才能使用的方法
            Log.d("MyService", "低于6.0");
            hasPermission = true;

        }
        //GPS定位服务打开
        LocationManager lm = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(ok){
            //开了定位服务
            Toast.makeText(activity, "系统检测到开启GPS定位服务", Toast.LENGTH_SHORT).show();
            hasGPS = true;
        }else{
            Toast.makeText(activity, "系统检测到未开启GPS定位服务,请开启", Toast.LENGTH_SHORT).show();
            displayPromptForEnablingGPS(activity);
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                activity.startActivityForResult(intent, LayoutActivity.get_LOCATION_CODE());
            // 这里不会阻塞，但是蓝牙打开请求的时候会阻塞的原因是因为这里的意图只是打开定位服务的设置界面！！
            //可能有用的参考链接：https://blog.csdn.net/ww897532167/article/details/70162678
            // https://www.cnblogs.com/arxive/p/6243802.html Android中如何监听GPS开启和关闭
            Log.d("MyService", "GPS服务开启请求结束");
            hasGPS = false;
            return false;
        }
        return true;
    }

    private static void displayPromptForEnablingGPS(final Activity activity)
    {

        final AlertDialog.Builder builder =  new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "程序需要GPS定位服务";

        builder.setMessage(message)
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();//dismiss方法在隐藏之后，会释放对话框所占用的资源，而hide则不会。
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    private void setLocationListener(Activity activity){
        if(hasPermission && hasGPS){
            //选择位置提供器，初始化使用GPS
            // 最佳provider选择的方法   https://blog.csdn.net/zheng0906/article/details/80354641
            String provider = LocationManager.GPS_PROVIDER;
            //获取定位服务
            locationManager = (LocationManager) activity.getSystemService(activity.LOCATION_SERVICE);
//            //获取当前可用的位置控制器
//            List<String> list = locationManager.getProviders(true);
//
//            if (list.contains(LocationManager.GPS_PROVIDER)) {
//                //是否为GPS位置控制器
//                provider = LocationManager.GPS_PROVIDER;
//            }
//            else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
//                //是否为网络位置控制器
//                provider = LocationManager.NETWORK_PROVIDER;
//
//            } else {
//            }
            //再次检查权限
            if (Build.VERSION.SDK_INT >= 23) {
                int check = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
                // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                if (check == PackageManager.PERMISSION_GRANTED) {
                    //写入你需要权限才能使用的方法
                    hasPermission =true;

                } else {
                    hasPermission = false;
                }
            } else {
                //写入你需要权限才能使用的方法;
                hasPermission = true;
            }
            if(hasPermission){
//                Location location = locationManager.getLastKnownLocation(provider);
                //这个方法获取一次可能会失败
//                Log.d("MyService", "获取经度纬度对象");
//                if (location != null) {
//                    //获取当前位置，这里只用到了经纬度
//                    Log.d("MyService", "获取经度纬度对象不为空");
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
//                    success = true;
//                }
                //新方法
                locationManager.requestLocationUpdates(provider, 3000, 0, mLocationListener);
                hasSetLocationListener = true;
            }
        }else{
            // GPS 或者 动态权限没有申请到哟
        }
    }

    /**
     *
     * 返回包括三个key关键值维度latitude、经度longitude、是否有效success
     */
    public MyResult getLocation(Activity activity){
        MyResult r = new MyResult();
        if(!hasSetLocationListener){
            setLocationListener(activity);
        }
        r.putBoolean("success", success);
        r.putDouble("latitude", latitude);
        r.putDouble("longitude", longitude);
        return r;
    }

    private void updateToNewLocation(Location location){
        if (location != null){
            success = true;
            latitude =  location.getLatitude();
            longitude = location.getLongitude();
        }else{
            success = false;
            latitude = 0.0;
            longitude = 0.0;
        }
    }

    public final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            updateToNewLocation(null);
        }
    };

}
