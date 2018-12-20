package com.turing.turingsdksample.handler;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.turing.music.LogUtil;
import com.turing.music.MusicManager;
import com.turing.music.OnPlayerStateListener;
import com.turing.music.OnSearchListener;
import com.turing.music.bean.MusicEntity;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.music.TuringMusic;

import java.util.List;

/**
 * @author wuyihua
 * @since 2018/3/15
 */

public class MusicHandler extends Handler {
    private static final String TAG = MusicHandler.class.getSimpleName();
    public OnPlayerStateListener mOnMiGuPlayStateListener = null;

    public Context context;

    public static final int MSG_PREPARE_MIGU = 1;
    public static final int MSG_PREPARE_TURING = 2;
    public static final int MSG_TTS_FINISH = 3;
    public static final int MSG_MIGU_PREPARED = 4;
    public static final int MSG_TURING_PREPARED = 5;


    //TimeOut
    public static final int MSG_MIGU_TIMEOUT = 16;
    public static final int MSG_TURING_TIMEOUT = 17;
    //delay time
    public static final int TIME_MIGU_DELAY = 3000;
    public static final int TIME_TURING_DELAY = 3000;


    //migu search
    public static final int MSG_MIGU_SEARCH = 50;


    //prepare failed
    public static final int MSG_MIGU_PREPARE_FAILED = 70;
    public static final int MSG_TURING_PREPARE_FAILED = 71;


    private MusicEntity musicEntity;
    private String keyword;
    private String url;

    private boolean isTTSFinished = true;
    private boolean isMiguPrepared = false;
    private boolean isTuringPrepared = false;
    private boolean isAllPrepareFailed = false;
    public TuringMusic.MusicStateListener mOnTuringPlayStateListener;

    /**
     * -> finish  play
     * <p>
     * ->success     -> checkTTSFinish
     * -> not finish    isPrepared
     * <p>
     * TTS Begin -> migu search  -> migu prepare
     * <p>
     * ->finish play
     * -> success    cancalTimeOut
     * ->not finish   isPrepared
     * <p>
     * onFailed    -> Turing Prepare -> checkTimeOut   -> checkTTSFinish
     * <p>
     * <p>
     * -> onFailed  checkTimeOut
     */


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_MIGU_SEARCH:
                LogUtil.d(TAG, "MSG_MIGU_SEARCH");
                Bundle data = msg.getData();
                keyword = data.getString("name");
                url = data.getString("url");
                miguSearch(keyword);
                isTTSFinished = false;
                isMiguPrepared = false;
                isTuringPrepared = false;
                isAllPrepareFailed = false;
                musicEntity = null;
                break;
            case MSG_PREPARE_MIGU:
                LogUtil.d(TAG, "MSG_PREPARE_MIGU");
                miguPrepare();
                break;
            case MSG_PREPARE_TURING:
                isTTSFinished = false;
                isTuringPrepared = false;
                isAllPrepareFailed = false;
                LogUtil.d(TAG, "MSG_PREPARE_TURING");
                data = msg.getData();
                url = data.getString("url");
                turingPrepare(url);
                break;
            case MSG_TTS_FINISH:
                LogUtil.d(TAG, "MSG_TTS_FINISH");
                isTTSFinished = true;
                if (musicEntity != null && isMiguPrepared) {
                    //调用咪咕音乐播放
                    MusicManager.getInstance().setOnPlayStateListener(mOnMiGuPlayStateListener);
                    MusicManager.getInstance().play(musicEntity);
                    return;
                }

                if (isTuringPrepared) {
                    TuringMusic.getInstance().play();
                    return;
                }

                //两种都prepared失败
                if (isAllPrepareFailed) {
                    musicTimeout();
                }
                break;
            case MSG_MIGU_PREPARED:
                LogUtil.d(TAG, "MSG_MIGU_PREPARED");
                isMiguPrepared = true;
                if (isTTSFinished) {
                    MusicManager.getInstance().play(musicEntity);
                }
                break;
            case MSG_TURING_PREPARED:
                LogUtil.d(TAG, "MSG_TURING_PREPARED");
                LogUtil.d(TAG, "isTTSFinished =" + isTTSFinished);
                isTuringPrepared = true;
                if (isTTSFinished) {
                    TuringMusic.getInstance().play();
                }
                break;
            case MSG_MIGU_TIMEOUT:
                LogUtil.d(TAG, "MSG_MIGU_TIMEOUT");
                MusicManager.getInstance().setOnPlayStateListener(null);
                break;
            case MSG_TURING_TIMEOUT:
                LogUtil.d(TAG, "MSG_TURING_TIMEOUT");
                break;
            case MSG_MIGU_PREPARE_FAILED:
                LogUtil.d(TAG, "MSG_MIGU_PREPARE_FAILED");
                LogUtil.d(TAG, "URL = " + url);
                turingPrepare(url);
                break;
            case MSG_TURING_PREPARE_FAILED:
                TuringMusic.getInstance().reset();
                LogUtil.d(TAG, "MSG_TURING_PREPARE_FAILED");
                //错误处理
                if (isTTSFinished) {
                    musicTimeout();
                }
                isAllPrepareFailed = true;
                break;
            default:
                break;
        }
    }

    private void musicTimeout() {
        TTSManager.getInstance().startTTS(context.getString(R.string.network_error), null);
    }


    public void miguSearch(String keyWord) {
        MusicManager.getInstance().search(keyWord, new OnSearchListener() {
            @Override
            public void onSuccess(List<MusicEntity> list) {
                if (list == null || list.size() == 0) {
                    sendEmptyMessage(MSG_MIGU_PREPARE_FAILED);
                    return;
                }
                musicEntity = list.get(0);
                sendEmptyMessage(MSG_MIGU_PREPARED);
            }


            @Override
            public void onFailed(int i, String s) {
                sendEmptyMessage(MSG_MIGU_PREPARE_FAILED);
            }
        });
    }

    public void miguPrepare() {
        if (musicEntity == null) {
            sendEmptyMessage(MSG_MIGU_PREPARE_FAILED);
            return;
        }
        sendEmptyMessageDelayed(MSG_MIGU_TIMEOUT, TIME_MIGU_DELAY);
        MusicManager.getInstance().setOnPlayStateListener(new OnPlayerStateListener() {
            @Override
            public void onStart() {
                removeMessages(MSG_MIGU_TIMEOUT);
                if (!isTTSFinished) {
                    MusicManager.getInstance().pause();
                    sendEmptyMessage(MSG_MIGU_PREPARED);
                }
            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(int i, String s) {
                removeMessages(MSG_MIGU_TIMEOUT);
                sendEmptyMessage(MSG_MIGU_PREPARE_FAILED);
            }

            @Override
            public void onBufferingUpdate(int i) {

            }
        });
        MusicManager.getInstance().play(musicEntity);
    }


    public void turingPrepare(String url) {
        LogUtil.d(TAG, "turingPrepare");
        if (TextUtils.isEmpty(url)) {
            sendEmptyMessage(MSG_TURING_PREPARE_FAILED);
            return;
        }
        TuringMusic.getInstance().setMusicStateListener(mOnTuringPlayStateListener);
        TuringMusic.getInstance().prepare(url, new TuringMusic.PrepareListener() {
            @Override
            public void onSuccess() {
                LogUtil.d(TAG, "turingPrepare onSuccess");
                sendEmptyMessage(MSG_TURING_PREPARED);
            }

            @Override
            public void onFailed() {
                LogUtil.d(TAG, "turingPrepare onFailed");
                sendEmptyMessage(MSG_TURING_PREPARE_FAILED);
            }
        });
    }
}
