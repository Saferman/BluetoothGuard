package com.example.admin.bluetoothguard.commons;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by admin on 2018/7/7.
 */

public class SystemUtil {

    public static void openUrlByBrowser(Activity activity, String url){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }
}
