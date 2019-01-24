package com.turing.turingsdksample.demo_common;

import android.content.Context;
import android.content.SharedPreferences;

/*
保存数据工具
 */
public class SharedPreferencesUtil {

    private final static String SHARE_NAME = "CHUANGSHI";

    public static void saveBindDeviceParams(String params, Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("params" , params);
        editor.commit();
    }

    public static String getBindDeviceParams(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        String params = sharedPreferences.getString("params",null);
        return params;
    }

    /*
    存储广播的序号，也是广播的ID
     */
    public static void saveBroadcastReceiverIndex(int index, Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("broadcastIndex" , index);
        editor.commit();
    }
    /*
        获取广播的序号，也是广播的ID
    */
    public static int getBroadcastReceiverIndex(Context context){
        SharedPreferences sharedPreferences= context.getSharedPreferences(SHARE_NAME, 0);
        int index = sharedPreferences.getInt("broadcastIndex",2);
        return index;
    }
}
