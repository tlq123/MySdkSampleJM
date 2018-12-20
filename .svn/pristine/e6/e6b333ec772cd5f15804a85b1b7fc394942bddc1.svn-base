package com.turing.turingsdksample.demo_common;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.qdreamer.utils.HttpUtils;
import com.turing.bind.BindCodeResp;
import com.turing.bind.BindInitListener;
import com.turing.bind.BindManager;
import com.turing.iot.MQTTConnectListener;
import com.turing.iot.MQTTInitListener;
import com.turing.iot.MQTTManager;
import com.turing.iot.MQTTReceiverListener;
import com.turing.iot.bean.Topic;
import com.turing.turingsdksample.constants.ConstantsUtil;
import com.turing.turingsdksample.util.Logger;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
MQTT主要用于微信端和设备端通信使用，消息传递渠道 (1).MQTT初始化
 */
public class MQTTBindManager {

    private static String TAG = "MQTTBindManager";

    private static MQTTReceiveInterface mqttReceiveInterface ;
    public static void setOnMQTTReceiveListener(MQTTReceiveInterface mqttReceive){
        mqttReceiveInterface = mqttReceive;
    }


    /**
     * 进行设备绑定
     */
    public  static void bindDevice(Context context){

//      String params = "apiKey="+apiKey + "&uid="+uid + "&deviceId=" + deviceId + "&name=" + name + "&imageUrl=" + imageUrl;
        String url = "http://iot-ai.tuling123.com/app-author/bind";
        String params = SharedPreferencesUtil.getBindDeviceParams(context);
        Logger.i(TAG,"bindDevice params--:"+params);
        try {
            HttpUtils.doPostAsyn(url, params, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    Logger.i(TAG, "bindDevice result----:" + result);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void init(final Context mContext){

        //绑定功能不负责实际的绑定，只用于获取绑定信息
        BindManager.getInstance().init(ConstantsUtil.MQTT_APP_KEY, new BindInitListener() {
            @Override
            public void onSuccess(BindCodeResp bindCodeResp) {
                Logger.i(TAG,"bindCodeResp:"+bindCodeResp.toString());
            }

            @Override
            public void onFailed(int i, String s) {

            }
        });

        //MQTT主要用于微信端和设备端通信使用，消息传递渠道 (1).MQTT初始化
        MQTTManager.getInstance().init(mContext, ConstantsUtil.MQTT_APP_KEY, new MQTTInitListener() {
            @Override
            public void onSccess(Topic topic) {

                topic.setClientId(ConstantsUtil.MQTT_APP_KEY+"@"+ ConfigureNetworkUtil.getAIDeviceId(mContext));
                topic.setTopic("iot/"+ConstantsUtil.MQTT_APP_KEY+"/"+ConfigureNetworkUtil.getAIDeviceId(mContext));
                Logger.i(TAG,"MQTTManager.getInstance().getConnectState():"+MQTTManager.getInstance().getConnectState());
                connect(topic);
            }

            @Override
            public void onFailed(int i, String s) {

            }
        });
    }
    //MQTT连接
    private static void connect(Topic topic){
        MQTTManager.getInstance().connect(topic, ConstantsUtil.MQTT_NAME,  ConstantsUtil.MQTT_PWD, new MQTTConnectListener() {
            @Override
            public void onSuccess() {
                Logger.i(TAG,"mqttManagerConnect: onSuccess ");
                registerReceiveListener();
            }

            @Override
            public void onFailed(int i, String s) {

            }
        });
    }

    //MQTT接收消息
    public static void registerReceiveListener(){
        MQTTManager.getInstance().registerReceiveListener(new MQTTReceiverListener() {
            @Override
            public void onReceive(String s) {
                Logger.i(TAG,"onReceive  s:"+s);
                Gson gson=new Gson();
                MqttInfo mqttInfo = gson.fromJson(s , MqttInfo.class);//把JSON字符串转为对象\
                Logger.i(TAG,"mqttInfo.getFromUser():"+mqttInfo.getFromUser());
                Logger.i(TAG,"mqttInfo.getMessage().getArg():"+mqttInfo.getMessage().getArg());
                mqttReceiveInterface.onMQTTReceive(mqttInfo);
            }
        });
    }


    //上报设备播放状态
    public static void httpReportDeviceInfo(String title,int mediaId,int play,Context context){
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");//"类型,字节码"
        RequestInfo mRequestInfo=new RequestInfo(); /*** 利用Gson 将对象转json字符串*/
        mRequestInfo.setApiKey(ConstantsUtil.MQTT_APP_KEY);
//        mRequestInfo.setDeviceId("ai12345678901033");
        mRequestInfo.setDeviceId(ConfigureNetworkUtil.getAIDeviceId(context));
        mRequestInfo.setType(1);
        RequestInfo.Status mStatus = mRequestInfo.new Status();
        mStatus.setMediaId(mediaId);
        mStatus.setPlay(play);
        mStatus.setTitle(title);
        mRequestInfo.setStatus(mStatus);

        Gson gson=new Gson();
        String obj=gson.toJson(mRequestInfo);
        Logger.i(TAG,"obj:"+obj);

        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.通过RequestBody.create 创建requestBody对象
        RequestBody requestBody =RequestBody.create(mediaType, obj);
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://iot-ai.tuling123.com/iot/status/notify").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        okhttp3.Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.i(TAG,"onFailure:");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {

                Logger.i(TAG,"onResponse:"+response.body().string());
            }

        });
    }


    //获取下一首歌曲信息
    public static void httpChangeSong(String changeType , final Context context){
        int type = 0;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");//"类型,字节码"
        RequestInfo mRequestInfo=new RequestInfo(); /*** 利用Gson 将对象转json字符串*/
        mRequestInfo.setApiKey(ConstantsUtil.MQTT_APP_KEY);
//        mRequestInfo.setDeviceId("ai12345678901033");
        mRequestInfo.setDeviceId(ConfigureNetworkUtil.getAIDeviceId(context));
        if("pre".equals(changeType)){
            type = 1;
        }else if("next".equals(changeType)){
            type = 0;
        }
        mRequestInfo.setType(type);

        Gson gson=new Gson();
        String obj=gson.toJson(mRequestInfo);
        Logger.i(TAG,"obj:"+obj);

        //1.创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.通过RequestBody.create 创建requestBody对象
        RequestBody requestBody =RequestBody.create(mediaType, obj);
        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder().url("http://iot-ai.tuling123.com/v2/iot/audio").post(requestBody).build();
        //4.创建一个call对象,参数就是Request请求对象
        okhttp3.Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.i(TAG,"onFailure:");
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String result = response.body().string() ;
                Logger.i(TAG,"changeSong result:"+result);
                Gson gson=new Gson();
                final ResponseInfo responseInfo = gson.fromJson(result , ResponseInfo.class);//把JSON字符串转为对象\
                Logger.i(TAG," getName:"+responseInfo.getPayload().getName());
                mqttReceiveInterface.onMQTTReceive(responseTomqtt(responseInfo));

                //上报设备播放状态
                int play = 1;// 播放状态 1-play 2-pause
                String title = responseInfo.getPayload().getName();
                int mediaId = responseInfo.getPayload().getId();
                httpReportDeviceInfo(title,mediaId,play,context);

            }

        });

    }

    //http请求响应数据转换成mqttInfo类型
    private static MqttInfo responseTomqtt(ResponseInfo responseInfo){
        MqttInfo mqttInfo = new MqttInfo();
        MqttInfo.Message message = mqttInfo.new Message();
        mqttInfo.setMessage(message);

        mqttInfo.getMessage().setArg(responseInfo.getPayload().getName());
        mqttInfo.getMessage().setMediaId(responseInfo.getPayload().getId());
        mqttInfo.getMessage().setOperate(1);//播放
        mqttInfo.getMessage().setTip(responseInfo.getPayload().getTip());
        mqttInfo.getMessage().setUrl(responseInfo.getPayload().getUrl());
        return mqttInfo;
    }

}
