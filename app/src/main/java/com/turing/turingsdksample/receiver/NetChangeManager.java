package com.turing.turingsdksample.receiver;

import java.util.ArrayList;

/**
 * 网络变化的回调
 *
 * @author：licheng@uzoo.com
 */

public class NetChangeManager {
    private ArrayList<NetChangeCallback> callbackArrayList;
    private static NetChangeManager instance;

    public static NetChangeManager getInstance() {
        if (instance == null) {
            instance = new NetChangeManager();
            instance.callbackArrayList = new ArrayList<NetChangeCallback>(2);
        }
        return instance;
    }

    /**
     * 添加
     **/
    public void add(NetChangeCallback callback) {
        if (callback != null) {
            callbackArrayList.add(callback);
        }
    }

    /**
     * 处理有网的回调
     **/
    public void handlerHasNet() {
        for (NetChangeCallback callback : callbackArrayList) {
            callback.onHasNet();
        }
    }

    /**
     * 处理有网的回调
     **/
    public void handlerNoNet() {
        for (NetChangeCallback callback : callbackArrayList) {
            callback.onNotNet();
        }
    }

    /**
     * 网络处理回到接口
     **/
    public interface NetChangeCallback {
        public void onHasNet();

        public void onNotNet();
    }
}
