package com.turing.turingsdksample.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.turing.asr.callback.InitialListener;
import com.turing.asr.engine.AsrManager;
import com.turing.authority.authentication.AuthenticationListener;
import com.turing.authority.authentication.SdkInitializer;
import com.turing.music.InitListener;
import com.turing.music.MusicManager;
import com.turing.tts.TTSInitListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.callback.SelectFragmentCallback;
import com.turing.turingsdksample.receiver.NetChangeManager;
import com.turing.turingsdksample.ui.HeadLayout;


/**
 * @author：licheng@uzoo.com
 */

public class BaseFragmentActivity extends FragmentActivity implements SelectFragmentCallback, NetChangeManager.NetChangeCallback {
    private String TAG = BaseFragmentActivity.class.getSimpleName();
    protected FragmentManager mFragMgr;
    protected HeadLayout headLayout;
    public boolean isTTSInit=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        init();
    }

    private void init() {
        initView();
        mFragMgr = getSupportFragmentManager();
        //设置网络监听回调
        NetChangeManager.getInstance().add(BaseFragmentActivity.this);
    }



    private AuthenticationListener authenticationListener = new AuthenticationListener() {
        @Override
        public void onSuccess() {
            Log.d(TAG,"authenticationListener success");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TTSManager.getInstance().init(BaseFragmentActivity.this, new TTSInitListener() {
                        @Override
                        public void onSuccess() {

                            Log.d(TAG, "TTS init success");
                            isTTSInit = true;
                        }

                        @Override
                        public void onFailed(int i, String s) {
                            isTTSInit = false;

                            Log.e(TAG, "TTS init failed errorCode=" + i + "   errorMsg=" + s);
                        }
                    });

                    AsrManager.getInstance().init(BaseFragmentActivity.this, new InitialListener() {

                        @Override
                        public void onInitialSuccess() {

                            Log.d(TAG, "ASR init success");
                        }

                        @Override
                        public void onInitialError(int errorCode, String asrErrorMessage) {

                            Log.e(TAG, "ASR init failed errorCode=" + errorCode + "   errorMsg=" + asrErrorMessage);
                        }
                    });

                    MusicManager.getInstance().init(BaseFragmentActivity.this, new InitListener() {
                        @Override
                        public void onSuccess() {

                            Log.d(TAG, "Music init success");
                        }

                        @Override
                        public void onFailed(int i, String s) {

                            Log.e(TAG, "ASR init failed errorCode=" + i + "   errorMsg=" + s);
                        }
                    });

                }
            });

        }

        @Override
        public void onError(int errorCode, String s) {
            Log.e(TAG, "errorCode=" + errorCode + "   errorMsg=" + s);
        }
    };

    private void initSDK() {
        SdkInitializer.init(this, authenticationListener);
    }


    /**
     * 更新用户自定义识别词库
     */
    private void updateLexicon() {

    }

    private void initView() {
        //头部
        headLayout = (HeadLayout) findViewById(R.id.base_header);
        headLayout.setCallback(this);
    }


    @Override
    public void onSelectFragment(String str) {

    }

    /**
     * 右边的回调按钮
     **/
    @Override
    public void onLeftBtn() {
        finish();
    }

    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    /**
     * 有网络时的回调
     **/
    @Override
    public void onHasNet() {
        toast(getString(R.string.has_net));
    }

    /**
     * 没有网络时的回调
     **/
    @Override
    public void onNotNet() {
        toast(getString(R.string.not_net));
    }
}
