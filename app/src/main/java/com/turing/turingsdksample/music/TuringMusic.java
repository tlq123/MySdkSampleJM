package com.turing.turingsdksample.music;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.turing.music.LogUtil;

import java.io.IOException;

/**
 * @author wuyihua
 * @todo
 * @since 2018/1/4
 */

public class TuringMusic {
    private static final String TAG = TuringMusic.class.getSimpleName();
    private static TuringMusic mTuringMusic = new TuringMusic();
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private boolean isPrepared = false;
    private MusicStateListener mMusicStateListener;
    private PrepareListener mMusicPrepareListener;
    private static final int MSG_PREPARE_FAILED = 1;
    private static final int DELAY_PREPARE_FAILED = 6000;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_PREPARE_FAILED:
                    mMediaPlayer.reset();
                    if (mMusicPrepareListener != null) {
                        mMusicPrepareListener.onFailed();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public boolean isPrepared() {
        return isPrepared;
    }


    public MusicStateListener getMusicStateListener() {
        return mMusicStateListener;
    }

    public void setMusicStateListener(MusicStateListener mMusicStateListener) {
        this.mMusicStateListener = mMusicStateListener;
    }

    private TuringMusic() {
    }

    public static TuringMusic getInstance() {
        return mTuringMusic;
    }


    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.d(TAG,"onPrepared");
            handler.removeMessages(MSG_PREPARE_FAILED);
            isPrepared = true;
            if (mMusicPrepareListener != null) {
                mMusicPrepareListener.onSuccess();
            }
        }
    };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG,"onError = "+ what);

            if (mMusicStateListener != null) {
                mMusicStateListener.onError(what);
            }
            return false;
        }
    };

    public void play() {
        mMediaPlayer.start();
        if (mMusicStateListener != null) {
            mMusicStateListener.onStart();
        }
    }

    //设置播放位置点
    public void setSeekPlay(int position){
        mMediaPlayer.seekTo(position);
        play();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }


    public void stop() {
        mMediaPlayer.stop();
        if (mMusicStateListener != null) {
            mMusicStateListener.onStop();
        }
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    //获取当前播放位置
    public int getCurPosition(){
        return mMediaPlayer.getCurrentPosition();
    }


    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            Log.d(TAG,"onCompletion listener = "+mMusicStateListener);
            if (mMusicStateListener!=null){
                mMusicStateListener.onComplete();
            }
        }
    };

    public void prepare(String url, PrepareListener prepareListener) {
        this.mMusicPrepareListener = prepareListener;
        mMediaPlayer.reset();
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        try {
            mMediaPlayer.setDataSource(url);
            LogUtil.d(TAG,"set datasource");

        } catch (IOException e1) {
            LogUtil.d(TAG,"set datasource failed");
            e1.printStackTrace();
        }
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.prepareAsync();
        LogUtil.d(TAG,"prepareAsync");

        handler.sendEmptyMessageDelayed(MSG_PREPARE_FAILED, DELAY_PREPARE_FAILED);
    }

    public void reset() {
        isPrepared = false;
        mMediaPlayer.reset();
    }


    public interface MusicStateListener {
        void onStart();

        void onStop();

        void onComplete();

        void onError(int errorCode);
    }

    public interface PrepareListener {
        void onSuccess();

        void onFailed();
    }
}
