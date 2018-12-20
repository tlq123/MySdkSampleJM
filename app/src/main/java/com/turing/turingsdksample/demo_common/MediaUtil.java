//package com.turing.turingsdksample.demo_common;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.util.Log;
//
//import com.turing.turingsdksample.util.Logger;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
///**
// * Created by tlq on 2017/11/9.
// */
//public class MediaUtil {
//
//    private static String TAG = "MediaUtil";
//    private static Context mcontext;
//    private static MediaUtil mInstance;
//    private MediaPlayer mp = null;
//
//    public static MediaUtil getInstance(Context context){
//        mcontext = context;
//        if(mInstance == null){
//            synchronized (MediaUtil.class){
//                mInstance = new MediaUtil();
//            }
//        }
//        return mInstance;
//    }
//
//    /**
//     * 播放音乐
//     */
//    public void startPlayMedia(int res){
//
//        Logger.i(TAG, "startPlayMedia----mp:"+mp);
//        if(mp != null){
//            stopPlayMedia() ;
//        }
//        mp = MediaPlayer.create(mcontext, res);
//        mp.setLooping(false);
//        mp.start();
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                Logger.i(TAG, "setOnCompletionListener----:");
//                stopPlayMedia();
//            }
//        });
//        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                Logger.i(TAG, "setOnErrorListener----:");
//                stopPlayMedia();
//                return false;
//            }
//        });
//
//    }
//
//    public void startPlayMediaPowerOff(int res){
//
//        Logger.i(TAG, "startPlayMedia----mp:"+mp);
//        if(mp != null){
//            stopPlayMedia() ;
//        }
//        mp = MediaPlayer.create(mcontext, res);
//        mp.setLooping(false);
//        mp.start();
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mediaPlayer) {
//                Intent intent = new Intent();
//                intent.setAction("com.auric.intell.xld.os.poweroff");
//                mcontext.sendBroadcast(intent);
//            }
//        });
//        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
//                Intent intent = new Intent();
//                intent.setAction("com.auric.intell.xld.os.poweroff");
//                mcontext.sendBroadcast(intent);
//                return false;
//            }
//        });
//
//    }
//
//    /**
//     * 暂停音乐
//     */
//    public void stopPlayMedia(){
//        Logger.i(TAG, "stopPlayMedia----:");
//        if(mp!=null){
//            mp.stop();
//            mp.release();
//            mp = null;
//        }
//    }
//
//    /*
//    语义调节音乐声音
//     */
//    public String setVolume(int operateState,String tts){
//        //改变音量
//        AudioManager am = (AudioManager) mcontext.getSystemService(Context.AUDIO_SERVICE);
//        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 1
//        int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//        Logger.i(TAG, "系统音量值：" + max + "-" + current);
//        if(operateState == 1010){//音量变大
//            if(current<max){
//                am.setStreamVolume(AudioManager.STREAM_MUSIC, current+1, AudioManager.FLAG_PLAY_SOUND);
//            }else{
//                tts = "已经是最大音量了";
//            }
//        }else if(operateState == 1011){//音量变小
//            if(current>1){
//                am.setStreamVolume(AudioManager.STREAM_MUSIC, current-1, AudioManager.FLAG_PLAY_SOUND);
//            }else{
//                tts = "已经是最小音量了";
//            }
//        }
//        return  tts ;
//    }
//
//    private static int tempV  ;
//    /*
//        设置来电铃声
//     */
//    public static void setVolume() {
//        //改变音量
//        final AudioManager am = (AudioManager) mcontext.getSystemService(Context.AUDIO_SERVICE);
//        final int max = am.getStreamMaxVolume(AudioManager.STREAM_RING);//  STREAM_MUSIC
//        int current = am.getStreamVolume(AudioManager.STREAM_RING);
//        tempV = 1 ;
//        final Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                int volume = max - tempV ;
//                tempV ++ ;
//                Logger.i(TAG, "系统音量值:" + volume + "  tempV:"+tempV);
//                if(volume <= 2) {
//                    timer.cancel();
//                }else {
//                    am.setStreamVolume(AudioManager.STREAM_RING, volume , AudioManager.FLAG_PLAY_SOUND);
//                }
//
//            }
//        }, 500, 1000);
//    }
//
//    /*
//    将铃声调整到中间
//     */
//    public static void setVolumeMedium() {
//        //改变音量
//        final AudioManager am = (AudioManager) mcontext.getSystemService(Context.AUDIO_SERVICE);
//        final int max = am.getStreamMaxVolume(AudioManager.STREAM_RING);//  STREAM_MUSIC
//        am.setStreamVolume(AudioManager.STREAM_RING, max/2 , AudioManager.FLAG_PLAY_SOUND);
//    }
//
//}
