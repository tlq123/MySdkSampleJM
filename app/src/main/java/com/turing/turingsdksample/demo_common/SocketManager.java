package com.turing.turingsdksample.demo_common;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.turing.turingsdksample.constants.ConstantsUtil;
import com.turing.turingsdksample.util.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/*
配网使用socket通讯
 */
public class SocketManager {

    private String TAT = "SocketManager";
    private ServerSocket server;
//    private Handler handler = null;
    private boolean threadFlag ;
    private String ip ;
    private Context mContext ;

    public SocketManager(Context context){
//        this.handler = handler;
        mContext = context ;
        int port = 9999;
        threadFlag = true ;
        while(port > 9000){
            try {
                server = new ServerSocket(port);
                break;
            } catch (Exception e) {
                port--;
            }
        }
        Logger.e(TAT,"port:"+port);
//        SendMessage(1, port);
        Thread receiveFileThread = new Thread(new Runnable(){
            @Override
            public void run() {
                while(threadFlag){
                    ReceiveString();
                }
            }
        });
        receiveFileThread.start();
    }
//    void SendMessage(int what, Object obj){
//        if (handler != null){
//            Message.obtain(handler, what, obj).sendToTarget();
//        }
//    }

    void ReceiveString(){
        try{
            Socket data = server.accept();
            InputStream dataStream = data.getInputStream();
            int count = 0 ;
            int len = 0 ;
            while (len == 0){
                len = dataStream.available();
                count ++ ;
                if(count > 100){
                    len = 1024 ;
                    break;
                }
                Logger.e(TAT,"dataStream.available():"+len);
            }
            Logger.e(TAT,"dataStream.available()==="+len+"  count:"+count);
            byte[] buffer = new byte[len];
            String content = "";
            while (dataStream.read(buffer) != -1){
                content= content + (new String(buffer,"UTF-8"));
            }
            dataStream.close();
            data.close();
            content = content.trim() ;
            Logger.e(TAT,"content==="+content);
            if(!TextUtils.isEmpty(content)){
                doMessages(content);
            }
        }catch(Exception e){
//            SendMessage(0, "接收错误:\n" + e.getMessage());
            Logger.e(TAT,"接收错误::"+e.getMessage());
        }
    }

    /*
    处理接收的消息
     */
    private void doMessages(String content){
        if(content.startsWith("ip:")){
            ip = content.split(":")[1];
            String str = "message:"+ConstantsUtil.MQTT_APP_KEY +":" + ConfigureNetworkUtil.getAIDeviceId(mContext);
            Logger.e(TAT,"发送的IP:"+ip);
            SendString(str, ip,9999);
        }
    }

    public void SendString(String content, String ipAddress, int port){
        try {
            Socket data = new Socket(ipAddress, port);
            OutputStream outputData = data.getOutputStream();
            InputStream Input = new ByteArrayInputStream(content.getBytes());
            int size = -1;
            byte[] buffer = new byte[1024];
            while((size = Input.read(buffer, 0, 1024)) != -1){
                outputData.write(buffer, 0, size);
            }
            outputData.close();
            Input.close();
            data.close();
//            SendMessage(0, "发送完成");
            Logger.e(TAT,"发送完成:");
        } catch (Exception e) {
//            SendMessage(0, "发送错误:\n" + e.getMessage());
            Logger.e(TAT,"接收错误::"+e.getMessage());
        }
    }

    public void close(){
        threadFlag = false ;
        if(server != null){
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        server = null ;
    }
}
