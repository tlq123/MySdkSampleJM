package com.turing.turingsdksample.fragment;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.turing.asr.SDKCode;
import com.turing.asr.callback.AsrListener;
import com.turing.asr.callback.InitialListener;
import com.turing.asr.engine.AsrManager;
import com.turing.asr.engine.HciOnlineCNASREngine;
import com.turing.asr.function.bean.ASRErrorMessage;
import com.turing.turingsdksample.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;


/**
 * ASR功能演示
 *
 * @author HomorSmith
 */
public class ASRFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "ASRFragment";
    private TextView contentTv;
    private boolean isAuto = true;
    private boolean isASRLoop = false;
    private boolean isASRStop = false;
    private Button enableVadBtn;
    private SeekBar beforeVadSeekBar;
    private SeekBar afterVadSeekBar;
    /**
     * 前置静音检测最小值
     */
    private static final int MIN_BEFORE_VAD = 2000;
    /**
     * (5000-2000)/100 = 30最大的减去最小的得到一个单位
     */
    private static final int BEFORE_VAD_UNIT = 30;
    /**
     * 当前VAD.默认最小
     */
    private int mCurrentBeforeVad = MIN_BEFORE_VAD;


    /**
     * 后置静音检测最小值
     */
    private static final int MIN_AFTER_VAD = 800;
    /**
     * (5000-2000)/100 = 30最大的减去最小的得到一个单位
     */
    private static final int AFTER_VAD_UNIT = 28;
    /**
     * 当前VAD.默认最小
     */
    private int mCurrentAfterVad = MIN_AFTER_VAD;
    private TextView beforeVadTv;
    private TextView afterVadTv;
    private Button loopBtn;
    private Button audioBtn;
    private HciOnlineCNASREngine hciOnlineCNASREngine;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asr, null);
        view.findViewById(R.id.btn_init).setOnClickListener(this);
        view.findViewById(R.id.btn_start).setOnClickListener(this);
        view.findViewById(R.id.btn_stop).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        beforeVadTv = (TextView) view.findViewById(R.id.tv_before_vad);
        afterVadTv = (TextView) view.findViewById(R.id.tv_after_vad);
        beforeVadSeekBar = (SeekBar) view.findViewById(R.id.sb_before_vad);
        afterVadSeekBar = (SeekBar) view.findViewById(R.id.sb_after_vad);
        enableVadBtn = (Button) view.findViewById(R.id.btn_enable);
        loopBtn = (Button) view.findViewById(R.id.btn_loop);
        loopBtn.setOnClickListener(this);
        audioBtn = (Button) view.findViewById(R.id.btn_audio);
        audioBtn.setOnClickListener(this);
        view.findViewById(R.id.btn_enable).setOnClickListener(this);
        contentTv = (TextView) view.findViewById(R.id.tv_content);
        initLogic();
        return view;
    }

    SeekBar.OnSeekBarChangeListener onBeforeSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mCurrentBeforeVad = MIN_BEFORE_VAD + (progress * BEFORE_VAD_UNIT);
                beforeVadTv.setText(String.format(getString(R.string.before_wrap), mCurrentBeforeVad));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    SeekBar.OnSeekBarChangeListener onAfterSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mCurrentAfterVad = MIN_AFTER_VAD + (progress * AFTER_VAD_UNIT);
                afterVadTv.setText(String.format(getString(R.string.after_vad), mCurrentAfterVad));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    private void initLogic() {
        beforeVadSeekBar.setOnSeekBarChangeListener(onBeforeSeekBarChangeListener);
        afterVadSeekBar.setOnSeekBarChangeListener(onAfterSeekBarChangeListener);
        beforeVadTv.setText(String.format(getString(R.string.before_wrap), MIN_BEFORE_VAD));
        afterVadTv.setText(String.format(getString(R.string.after_vad), MIN_AFTER_VAD));
    }


    private InitialListener initialListener = new InitialListener() {
        @Override
        public void onInitialSuccess() {
            Log.d(TAG, "onInitialSuccess");
            Toast.makeText(getActivity(), getString(R.string.initsdk_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onInitialError(int errorCode, String msg) {
            Log.e(TAG, "onInitialError: " + msg);
            Toast.makeText(getActivity(), getString(R.string.init_failed) + msg, Toast.LENGTH_SHORT).show();
        }
    };

    private void startRecord() {
        Log.d(TAG, "startRecord: " );
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AsrManager.getInstance().startAsr(asrListener);
    }

    private AsrListener asrListener = new AsrListener() {
        @Override
        public void onResults(final List<String> result) {
            if (result == null || result.size() == 0) {
                if (isASRLoop && !isASRStop) {
                    startRecord();
                }
                return;
            }
            Log.d(TAG, "onResults: " + result.get(0));
            contentTv.setText(result.get(0));
            if (isASRLoop && !isASRStop) {
                startRecord();
            }
        }

        @Override
        public void onStartRecord() {
            Log.d(TAG, "onStartRecord");

        }

        @Override
        public void onEndOfRecord() {
            Log.d(TAG, "onEndOfRecord: ");
        }

        @Override
        public void onError(ASRErrorMessage errorMessage) {
            contentTv.setText(MessageFormat.format("onError,ErrorCode: {0}|ErrorMsg:{1}", errorMessage.getCode(), errorMessage.getMessage()));
            Log.e(TAG, "onError,ErrorCode: " + errorMessage.getCode() + "|ErrorMsg:" + errorMessage.getMessage());
            if (isASRLoop && !isASRStop) {
                if (errorMessage.getCode() == SDKCode.ASR_NO_DATA_CODE
                        || errorMessage.getCode() == SDKCode.ASR_TIMEOUT_ERROR) {
                    startRecord();
                }
            }
        }

        @Override
        public void onVolumeChange(int volume) {
            Log.d(TAG, "IS RECORDING:" + AsrManager.getInstance().isRecording());
        }
    };

    public void testAudio(View view) {
        AssetManager assetManager = getActivity().getAssets();
        try {
            InputStream inputStream = assetManager.open("sample.pcm");
            byte[] buffer = new byte[1024 * 100];
            while (true) {
                int length = inputStream.read(buffer);
                if (length == -1) {
                    break;
                }
            }
            AsrManager.getInstance().startAsr(asrListener, buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_init:
                AsrManager.getInstance().init(getActivity(), initialListener);
                AsrManager.getInstance().setVadEnable(true);
                break;
            case R.id.btn_start:
                isASRStop = false;
                AsrManager.getInstance().setOptionVAD(mCurrentBeforeVad, mCurrentAfterVad);
                if (!AsrManager.getInstance().isRecording()) {
                    AsrManager.getInstance().startAsr(asrListener);
                }
                break;
            case R.id.btn_loop:
                isASRLoop = !isASRLoop;
                String text = isASRLoop ? getString(R.string.asr_loop_mode) : getString(R.string.asr_single_mode);
                loopBtn.setText(text);
                break;
            case R.id.btn_stop:
                isASRStop = true;
                boolean recording = AsrManager.getInstance().isRecording();
//                LogUtil.d(TAG, "recording = " + recording);
                if (recording) {
                    AsrManager.getInstance().stop();
                }
                break;
            case R.id.btn_cancel:
                isASRStop = true;
                AsrManager.getInstance().cancel();
                break;
            case R.id.btn_enable:
                isAuto = !isAuto;
                String modeStr = isAuto ? getString(R.string.autostr) : getString(R.string.manualstr);
                enableVadBtn.setText(modeStr);


                break;
            case R.id.btn_audio:
                AsrManager.getInstance().setVadEnable(isAuto);
                testAudio(v);
                break;
            default:
                break;
        }
    }


    @Override
    public void exitLogic() {
        Log.d(TAG,"exitLogic");
        if (AsrManager.getInstance().isRecording()) {
            AsrManager.getInstance().cancel();
        }
    }
}
