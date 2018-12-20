package com.turing.turingsdksample.demo_common;

import android.content.Context;
import android.media.AudioManager;

import com.turing.turingsdksample.util.Logger;

/*
声音管理工具类
 */
public class AudioManagerUtil {

    /*
    设置免提模式 HF(hands free)
     */
    public static void setHandsFreeMode(Context context){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Logger.d("AudioManagerUtil","AudioManagerUtil mode:"+am.getMode()+ "  am.isSpeakerphoneOn():"+am.isSpeakerphoneOn());
        if(am.getMode() != AudioManager.MODE_NORMAL ){
            am.setMode(AudioManager.MODE_NORMAL);
        }
        am.setMode(AudioManager.MODE_IN_CALL);
        if( !am.isSpeakerphoneOn() ){
            am.setSpeakerphoneOn(true);
        }

    }

    /*
    设置挂电话模式模式 HF(hands free)
     */
    public static void setHangUpMode(Context context){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_NORMAL);
        am.setSpeakerphoneOn(false);
    }

    public static int getMode(Context context){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int mode = am.getMode();
        return  mode ;
    }

}
