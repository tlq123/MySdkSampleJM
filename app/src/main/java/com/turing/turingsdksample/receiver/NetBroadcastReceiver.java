package com.turing.turingsdksample.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.turing.turingsdksample.util.Logger;
import com.turing.turingsdksample.util.MyNetUtil;


/**
 * @author licheng@uzoo.cn
 * @data 2016/10/10 0010
 */
public class NetBroadcastReceiver extends BroadcastReceiver {
    private String TAG = NetBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MyNetUtil.isNetworkAvailable(context)) {
            Logger.d(Logger.TAG, TAG + "DID_NET");
            //有网络
            NetChangeManager.getInstance().handlerHasNet();
        } else {
            Logger.d(Logger.TAG, TAG + "NOT_NET");
            NetChangeManager.getInstance().handlerNoNet();
        }
    }


}
