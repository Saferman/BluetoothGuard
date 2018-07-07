package com.example.admin.bluetoothguard.tuling;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

import com.example.admin.bluetoothguard.commons.ImageDialog;
import com.example.admin.bluetoothguard.commons.SystemUtil;
import com.iflytek.cloud.thirdparty.S;

import java.io.ByteArrayOutputStream;

/**
 * Created by admin on 2018/7/7.
 */

public class TulingAction {
    // 指定图灵的一次反馈结果的操作类型
    // text、image、url
    private String opt = null;
    private Bitmap bitmap;
    private String url;

    public void doAction(Activity tulingActivity){
        switch (opt){
            case "image":
                // 弹出该图片一段时间
                Intent intent = new Intent(tulingActivity, ImageDialog.class);
                intent.putExtra("has", true);// 判断是否bitmap 有效
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitmapByte = baos.toByteArray();
                intent.putExtra("bitmap", bitmapByte);
                tulingActivity.startActivity(intent);
                break;
            case "url":
                SystemUtil.openUrlByBrowser(tulingActivity, this.url);
            default:
                break;
        }
    }

    public void setUrl(String url){
        this.url = url;

    }
    public String getOpt(){
        return opt;
    }
    public void clearOpt(){
        opt = null;
    }

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public void setOpt(String opt){
        this.opt = opt;
    }

}
