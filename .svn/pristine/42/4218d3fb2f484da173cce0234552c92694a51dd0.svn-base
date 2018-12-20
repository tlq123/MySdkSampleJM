package com.turing.turingsdksample.demo_common;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/*
录音工具类
 */
public class MediaRecoderUtil {

    private static MediaRecoderUtil mMdiaRecoderUtil ;
    private MediaRecorder mediaRecorder ;
    private String mFilePath ;

    public static MediaRecoderUtil getMediaRecoderUtil(){
        if(mMdiaRecoderUtil == null){
            synchronized (MediaRecoderUtil.class){
                mMdiaRecoderUtil = new MediaRecoderUtil();
            }
        }
        return  mMdiaRecoderUtil;
    }

    /*
    开始录音
     */
    public void startRecoder(){
        stopRecording();  //如果正在录音就停止从新录音
        setFilePath();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  ////从麦克风采集
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //录音文件保存的格式，这里保存为 mp4
        mediaRecorder.setOutputFile(mFilePath);// 设置录音文件的保存路径
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); //通用的AAC编码格式
        mediaRecorder.setAudioChannels(1);

        mediaRecorder.setAudioSamplingRate(44100);  //所有android系统都支持的适中采样的频率
        mediaRecorder.setAudioEncodingBitRate(192000);  //设置音质频率
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MediaRecoderUtil", "prepare() failed:"+e.getMessage());
        }
    }
    /*
        初始化了录音文件的名字和保存的路径
    */
    private void setFilePath(){
        if(ExistSDCard()){
            File file ;
            String fileName = "/cs_"+System.currentTimeMillis()+".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getPath()+fileName;
            file = new File(mFilePath);
            //判断文件夹是否存在，如果不存在就创建，否则不创建
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("MediaRecoderUtil", "createNewFile():"+e.getMessage());
                }
            }
            Log.e("MediaRecoderUtil", "setFilePath() getPath:"+file.getPath());
        }else {
            Log.e("MediaRecoderUtil", "no sdcard");
        }

    }

    private boolean ExistSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else{
            return false;
        }

    }


    /*
        停止录音
     */
    public void stopRecording(){
        if(mediaRecorder != null){
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null ;
        }
    }
    /*
        播放录音
     */
    MediaPlayer mp ;
    public void playRecord(){

        if( mFilePath == null ){
            return ;
        }

        stopRecording();  //停止录音

        try {
            if(mp != null ){
                mp.reset();
                mp = null ;
            }
            mp = new MediaPlayer();
            mp.setDataSource(mFilePath);
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mp.start();
                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.release();
                    mp = null ;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    停止播放录音
     */
    public void stopPlayRecord(){
        if(mp != null){
            mp.stop();
            mp.release();
            mp = null ;
        }
    }
}
