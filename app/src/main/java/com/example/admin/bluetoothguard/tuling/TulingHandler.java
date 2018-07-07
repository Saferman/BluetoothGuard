package com.example.admin.bluetoothguard.tuling;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.admin.bluetoothguard.commons.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 2018/7/6.
 */


public class TulingHandler {
    private String APIKEY = "cb5c230802e642debf2d36030ce8f7af";
    private String userId = "Saferman";
    private String webAPI = "http://openapi.tuling123.com/openapi/api/v2";
    private Handler uiHandler;
    private TulingHandler selfTulingHandler = this;
    // 网络请求结束，得到图灵返回结果的发送消息，需要和TulingActivity 的统一
    private static final int TULING_HTTP_FINISHED_MESSAGE = 1;
    // 需要发送的JSON格式数据
    private JSONObject root;
    // 存放储存结果的数组，注意这里的只需要将结果放入就可以了，是TulingActivity传递过来的指针
    private ArrayList<String> tulingResArray;
    // 图灵其他操作
    private TulingAction tulingAction;

    private JSONObject createPostJson(String userInput){
        JSONObject root = new JSONObject();
        try {
            root.put("reqType", 0);
            JSONObject perception = new JSONObject();
            JSONObject inputText = new JSONObject();
            inputText.put("text", userInput);
//            JSONObject selfInfo = new JSONObject();
//            JSONObject location = new JSONObject();
//            location.put("city", "北京");
//            location.put("province", "北京");
//            location.put("street", "信息路");
//            selfInfo.put("location", location);
//            perception.put("selfInfo", selfInfo);
            perception.put("inputText", inputText);
            JSONObject userInfo = new JSONObject();
            userInfo.put("apiKey", APIKEY);
            userInfo.put("userId", userId);
            root.put("perception", perception);
            root.put("userInfo", userInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public void accessWebAPI(){
        String resJson = HttpUtil.httpPostJson(webAPI, root);
        parseResJSON(resJson);
    }

    public void parseResJSON(String resJson){
        //存储每个需要播放的语音文本
        String res;
        try {
            JSONObject jsonObject = new JSONObject(resJson);
            JSONArray jsonArray = jsonObject.optJSONArray("results");
            JSONObject jsonObject1 = jsonArray.optJSONObject(0);
            String resultType = jsonObject1.optString("resultType");
            if (resultType.equals("text")){
                // 说明返回的就是文本信息
                tulingAction.setOpt("text");
                JSONObject jsonObject2 = jsonObject1.optJSONObject("values");
                res = jsonObject2.optString("text");
                tulingResArray.add(res);
                // 如果是用户请求“新闻”命令，需要播放语音全部新闻
                if(res.equals("亲，已帮您找到相关新闻")){
                    JSONObject jsonObject3  = jsonArray.optJSONObject(1);
                    JSONObject jsonObject4 = jsonObject3.getJSONObject("values");
                    JSONArray jsonArray1 = jsonObject4.getJSONArray("news");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject item = jsonArray1.getJSONObject(i);//{"name":"我不是药神上映前 国务院医改领导小组收到新任务","info":"新浪新闻","icon":"//k.sinaimg.cn/n/front/708/w964h544/20180707/jHG1-hexfcvm2885932.jpg/w120h90l50t1cb7.jpg","detailurl":"https://news.sina.cn/gn/2018-07-07/detail-ihexfcvm2885677.d.html?vt=4&pos=8&cid=56261"}
                        res = item.getString("name");
                        tulingResArray.add(res);
                    }
                }
            }else if(resultType.equals("image")){
                // 返回的为图片
                tulingAction.setOpt("image");
                res= "返回图片";
                tulingResArray.add(res);
                JSONObject jsonObject2 = jsonObject1.optJSONObject("values");
                String imageUrl = jsonObject2.optString("image");// 图片链接
                Bitmap bitmap = HttpUtil.getHttpBitmap(imageUrl);
                tulingAction.setBitmap(bitmap);
            }else if(resultType.equals("url")){
                // url展示的结果
                tulingAction.setOpt("url");
                res= "返回超链接";
                tulingResArray.add(res);
                JSONObject jsonObject2 = jsonObject1.optJSONObject("values");
                String url = jsonObject2.optString("url"); // 需要打开的链接
                tulingAction.setUrl(url);
            }else{
                res = "发现新的resultType" + resultType;
                tulingResArray.add(res);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handle(ArrayList<String> tulingResArray, Handler uiHandler, String userInput, TulingAction tulingAction){
        selfTulingHandler.uiHandler = uiHandler;
        root = createPostJson(userInput);
        selfTulingHandler.tulingResArray = tulingResArray;
        selfTulingHandler.tulingAction = tulingAction;
        new Thread(new Runnable() {
            @Override
            public void run() {
                accessWebAPI();
                //新建一个Message对象，存储需要发送的消息
                Message message=new Message();
                message.what = TULING_HTTP_FINISHED_MESSAGE;
                selfTulingHandler.uiHandler.sendMessage(message);
            }
        }).start();

    }

}
