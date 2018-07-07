package com.example.admin.bluetoothguard.commons;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by admin on 2018/7/7.
 */

public class HttpUtil {

    public static Bitmap getHttpBitmap(String url){
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String httpPostJson(String urlString, JSONObject obj) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        HttpURLConnection urlConnection = null;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoInput(true);  // 允许输入流，即允许下载
            urlConnection.setDoOutput(true); // 允许输出流，即允许上传
            urlConnection.setUseCaches(false);

            // 发送
            String postData = obj.toString();
//            try {
//                String strGBK = URLEncoder.encode(postData, "GBK");
//                String strUTF8 = URLDecoder.decode(postData, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            outputStream = urlConnection.getOutputStream();
            DataOutputStream writer = new DataOutputStream(outputStream);
            //  https://blog.csdn.net/qqaazz211/article/details/52136187
//            writer.writeBytes(postData);// 有问题!
            writer.write(postData.getBytes()); // 正确
            writer.flush();
            writer.close();
            outputStream.close();

            // 接受
            int HttpResult = urlConnection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                reader.close();
            }else{
                return String.valueOf(HttpResult);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response.toString();
    }
}
