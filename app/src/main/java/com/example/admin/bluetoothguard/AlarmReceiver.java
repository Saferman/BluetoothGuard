package com.example.admin.bluetoothguard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by admin on 2018/6/15.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent tempIntent = new Intent(context, MyService.class);
        context.startService(tempIntent);
    }
}
