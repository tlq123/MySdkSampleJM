package com.turing.turingsdksample.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.turing.asr.callback.InitialListener;
import com.turing.asr.engine.AsrManager;
import com.turing.authority.authentication.AuthenticationListener;
import com.turing.authority.authentication.SdkInitializer;
import com.turing.music.InitListener;
import com.turing.music.MusicManager;
import com.turing.tts.TTSInitListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;
import com.util.LogUtil;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * @author wuyihua
 * @Date 2017/9/30
 * @todo
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private static final String KEY = "key";
    private static final String SECRET = "secret";
    private EditText apiKeyEt;
    private EditText secretEt;
    private String TAG = LoginActivity.class.getSimpleName();
    private Button mLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        apiKeyEt = (EditText) findViewById(R.id.et_apikey);
        secretEt = (EditText) findViewById(R.id.et_secret);
        mLoginBtn = (Button) findViewById(R.id.btn_login);
        findViewById(R.id.btn_login).setOnClickListener(this);
        initPremission();
    }


    private void initPremission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.RECORD_AUDIO)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            Toast.makeText(LoginActivity.this, getString(R.string.no_promission), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private AuthenticationListener authenticationListener = new AuthenticationListener() {
        @Override
        public void onSuccess() {
            mLoginBtn.setEnabled(true);
            Log.d(TAG, "authenticationListener success");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    TTSManager.getInstance().init(LoginActivity.this.getApplicationContext(), new TTSInitListener() {
                        @Override
                        public void onSuccess() {
                            LogUtil.e(TAG, "TTS init success");
                        }

                        @Override
                        public void onFailed(int i, String s) {
                            LogUtil.e(TAG, "TTS init failed errorCode=" + i + "   errorMsg=" + s);
                        }
                    });

                    AsrManager.getInstance().init(LoginActivity.this.getApplicationContext(), new InitialListener() {

                        @Override
                        public void onInitialSuccess() {
                            LogUtil.e(TAG, "ASR init success");
                        }

                        @Override
                        public void onInitialError(int errorCode, String asrErrorMessage) {
                            LogUtil.e(TAG, "ASR init failed errorCode=" + errorCode + "   errorMsg=" + asrErrorMessage);
                        }
                    });

                    MusicManager.getInstance().init(LoginActivity.this.getApplicationContext(), new InitListener() {
                        @Override
                        public void onSuccess() {
                            LogUtil.e(TAG, "Music init success");
                        }

                        @Override
                        public void onFailed(int i, String s) {
                            LogUtil.e(TAG, "ASR init failed errorCode=" + i + "   errorMsg=" + s);
                        }
                    });
                    startMainActivity();


                }
            });
        }

        @Override
        public void onError(final int errorCode, final String s) {
            mLoginBtn.setEnabled(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, "错误码:" + errorCode + "  错误信息：" + s, Toast.LENGTH_SHORT).show();

                }
            });
            Log.e(TAG, "errorCode=" + errorCode + "   errorMsg=" + s);
        }
    };


    private void initSDK() {
        SdkInitializer.init(this, apiKeyEt.getText().toString(), secretEt.getText().toString(), authenticationListener);
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainFragmentActivity.class);
        this.startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(final View v) {
        v.setEnabled(false);
        initSDK();
    }
}
