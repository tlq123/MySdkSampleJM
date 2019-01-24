package com.turing.turingsdksample.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.turing.turingsdksample.activity_jm2.JM2Activity;
import com.turing.turingsdksample.util.Logger;

/**
 * Created by tlq on 2019/01/18.
 * 闹钟广播接收
 * 暂时未用，接收直接在主界面接收了
 */
public class JMAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
//        String msg = intent.getStringExtra("msg");
        long intervalMillis = intent.getLongExtra("intervalMillis", 0);
        Logger.e("JMAlarmReceiver","intervalMillis:"+intervalMillis);
        if (intervalMillis != 0) {
            AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
                    intent);
        }
        //主界面接收
        Intent intent1 = new Intent(JM2Activity.ALARM_SHOW);
        context.getApplicationContext().sendBroadcast(intent1);

    }
}
