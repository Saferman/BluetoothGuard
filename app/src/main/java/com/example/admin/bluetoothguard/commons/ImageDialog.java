package com.example.admin.bluetoothguard.commons;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.admin.bluetoothguard.R;

/**
 * Created by admin on 2018/7/7.
 */

// 参考链接 https://stackoverflow.com/questions/7693633/android-image-dialog-popup

public class ImageDialog extends Activity {
    private ImageView mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedialog);
        mDialog = (ImageView) findViewById(R.id.myImage);

        Intent intent = getIntent();
        Boolean has = intent.getBooleanExtra("has", false);// 第二个参数意义何在？
        // 这方法中第一个参数是extra的名字。第二个参数是指定默认值，它在无法获取值时使用。
        if(has){
            byte[] bis = intent.getByteArrayExtra("bitmap");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
            mDialog.setImageBitmap(bitmap);
        }
        mDialog.setClickable(true);
        //finish the activity (dismiss the image dialog) if the user clicks
        //anywhere on the image
        mDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 额外添加的代码，让这个活动只存在三秒
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }
}
