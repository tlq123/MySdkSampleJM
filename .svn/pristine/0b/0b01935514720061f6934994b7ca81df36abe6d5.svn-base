package com.turing.turingsdksample.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.turing.tts.TTSInitListener;
import com.turing.tts.TTSListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TTSFragment extends BaseFragment implements View.OnClickListener {

    private EditText contentEt;
    private static final String TAG = TTSFragment.class.getSimpleName();
    private Spinner mTTSTypeSpinner;
    private Spinner mToneSpinner;
    private int mTone = TTSManager.XIXI_TONE;
    private String mEffect = TTSManager.BASE_EFFECT;
    private boolean isInited = false;
    private SeekBar mSeekBar;
    private ArrayAdapter<String> mTTSTypeAdapter;
    private ArrayAdapter<String> toneAdapter;
    private TextView mToneTv;
    private TextView mEnginTv;
    private Button mInitBtn;
    private boolean isOnlineTTS = false;
    private Button mInitBtnType;
    private Button mEnhancerBtn;
    private SeekBar mEnhancerSeekbar;
    private SeekBar mSpeedSeekBar;
    private Spinner mSpinnerEffect;
    private TextView mEffectTv;
    private TextView mEnhancerTv;
    private List<String> mToneList;
    private int mCurEngineType = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tts, null);
        initActivity(view);
        refreshView();
        return view;
    }


    private void refreshView() {
        String[] mToneDescs = getResources().getStringArray(R.array.tone_offline);
        mToneList = new ArrayList<>();
        mToneList.addAll(Arrays.asList(mToneDescs));
        toneAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mToneList);
        mToneSpinner.setAdapter(toneAdapter);
        mToneSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "mToneSpinner onItemSelected" + i);

                if (mCurEngineType == TTSManager.TYPE_ONLINE_ENGINE) {
                    mTone = i;
                } else {
                    switch (i) {
                        //离线音色的选择
                        case TTSManager.LOCAL_XIXI_TONE:
                        case TTSManager.LOCAL_BAISONG_TONE:
                        case TTSManager.LOCAL_ZHANGNAN_TONE:
                            mTone = i;
                            break;
                        default:
                            mTone = TTSManager.LOCAL_XIXI_TONE;
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        String[] stringArray = getResources().getStringArray(R.array.type_array);
        mTTSTypeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, stringArray);
        mTTSTypeSpinner.setAdapter(mTTSTypeAdapter);
        mTTSTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "mTTSTypeSpinner onItemSelected" + i);
                mCurEngineType = i;

                if (isInited) {
                    TTSManager.getInstance().switchTTSEngine(i, switchListener);
                    updateSpinner(i);
                    Log.d(TAG, "mToneList size = " + mToneList.size());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean isUser) {
                if (isUser) {
                    Log.d(TAG, "setOnSeekBarChangeListener progress = " + i);
                    TTSManager.getInstance().setSpeed(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean isUser) {
                if (isUser) {
                    Log.d(TAG, "setOnSeekBarChangeListener progress = " + i);
                    TTSManager.getInstance().setVolume(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mEnhancerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    TTSManager.getInstance().setEnhancerTargetGain(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        final String[] effectArray = getResources().getStringArray(R.array.effect_array);
        ArrayAdapter<String> effectAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, effectArray);
        mSpinnerEffect.setAdapter(effectAdapter);
        mSpinnerEffect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mEffect = effectArray[position];
                TTSManager.getInstance().setEffect(mEffect);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

    }


    private TTSInitListener switchListener = new TTSInitListener() {
        @Override
        public void onSuccess() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "切换成功", Toast.LENGTH_SHORT).show();
                    mTTSTypeSpinner.setSelection(TTSManager.getInstance().getTTSType());
                    updateSpinner(TTSManager.getInstance().getTTSType());
                }
            });

        }

        @Override
        public void onFailed(int errorCode, String errorInfo) {
            Toast.makeText(getActivity(), "切换失败 errorCode = " + errorCode + "  errorInfo = " + errorInfo, Toast.LENGTH_SHORT).show();

        }
    };


    private void updateSpinner(int i) {
        mSpinnerEffect.setSelection(0);
        if (TTSManager.TYPE_ONLINE_ENGINE == i) {
            mToneList.clear();
            mToneList.addAll(Arrays.asList(getResources().getStringArray(R.array.tone_array)));
            toneAdapter.notifyDataSetChanged();
            mToneSpinner.setSelection(0);
            mTone = 0;
        } else {
            mToneList.clear();
            mToneList.addAll(Arrays.asList(getResources().getStringArray(R.array.tone_offline)));
            toneAdapter.notifyDataSetChanged();
        }
    }

    void initActivity(View view) {
        view.findViewById(R.id.btn_start).setOnClickListener(this);
        view.findViewById(R.id.btn_init).setOnClickListener(this);
        view.findViewById(R.id.btn_stop).setOnClickListener(this);
        mInitBtn = (Button) view.findViewById(R.id.btn_init);
        mToneTv = (TextView) view.findViewById(R.id.tv_tone);
        mEnginTv = (TextView) view.findViewById(R.id.tv_engine);
        mTTSTypeSpinner = (Spinner) view.findViewById(R.id.spinner_tts_type);
        contentEt = (EditText) view.findViewById(R.id.et_content);
        mToneSpinner = (Spinner) view.findViewById(R.id.spinner_tone);
        mSeekBar = (SeekBar) view.findViewById(R.id.seekBar);
        mSpeedSeekBar = (SeekBar) view.findViewById(R.id.speed_seekBar);
        mSpinnerEffect = (Spinner) view.findViewById(R.id.spinner_effect);
        mEffectTv = (TextView) view.findViewById(R.id.tv_effect);
        mInitBtnType = (Button) view.findViewById(R.id.btn_init_type);
        mEnhancerBtn = (Button) view.findViewById(R.id.btn_enhancer);
        mEnhancerSeekbar = (SeekBar) view.findViewById(R.id.enhancer_seekbar);
        mEnhancerTv = (TextView) view.findViewById(R.id.enhancer_tv);
        mInitBtnType.setOnClickListener(this);
        mEnhancerBtn.setOnClickListener(this);

    }


    private TTSListener ttsListener = new TTSListener() {
        @Override
        public void onSpeakBegin(String content) {
            Log.d(TAG, "onSpeakBegin " + content);
        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakCompleted() {
            Log.d(TAG, "onSpeakCompleted ");

        }

        @Override
        public void onSpeakFailed() {
            Log.d(TAG, "onSpeakFailed ");

        }
    };


    private TTSInitListener ttsInitListener = new TTSInitListener() {


        @Override
        public void onSuccess() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isInited = true;
                    mInitBtn.setVisibility(View.GONE);
                    mToneTv.setVisibility(View.VISIBLE);
                    mEnginTv.setVisibility(View.VISIBLE);
                    mToneSpinner.setVisibility(View.VISIBLE);
                    mTTSTypeSpinner.setVisibility(View.VISIBLE);
                    mEnhancerBtn.setVisibility(View.VISIBLE);
                    mEnhancerSeekbar.setVisibility(View.VISIBLE);
                    mEffectTv.setVisibility(View.VISIBLE);
                    mSpinnerEffect.setVisibility(View.VISIBLE);
                    mEnhancerTv.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), getString(R.string.initsdk_success), Toast.LENGTH_SHORT).show();
                    mTTSTypeSpinner.setSelection(TTSManager.getInstance().getTTSType());
                    updateSpinner(TTSManager.getInstance().getTTSType());
                }
            });

        }

        @Override
        public void onFailed(int errorCode, String errorInfo) {
            Toast.makeText(getActivity(), getString(R.string.init_failed) + ":code=" + errorCode + "   errorInfo=" + errorInfo, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_start:
                if (TextUtils.isEmpty(contentEt.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.param_cannot_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                //开始TTS
                Log.d(TAG, String.format("content = %s  tone = %d", contentEt.getText().toString(), mTone));
                TTSManager.getInstance().setEffect(mEffect);
                TTSManager.getInstance().startTTS(contentEt.getText().toString(), mTone, ttsListener);
                break;
            case R.id.btn_init:
                //初始化
                TTSManager.getInstance().init(getActivity(), ttsInitListener);
                break;
            case R.id.btn_stop:
                //停止TTS
                TTSManager.getInstance().stopTTS();
                break;
            case R.id.btn_init_type:
                final int ttsType = isOnlineTTS ? TTSManager.TYPE_ONLINE_ENGINE : TTSManager.TYPE_OFFLINE_ENGINE;
                String text = isOnlineTTS ? getString(R.string.online_tts) : getString(R.string.offline_tts);
                mInitBtnType.setText(text);
                isOnlineTTS = !isOnlineTTS;
                TTSManager.getInstance().init(getActivity(), ttsType, new TTSInitListener() {
                    @Override
                    public void onSuccess() {
                        isInited = true;
                        mInitBtn.setVisibility(View.GONE);
                        mToneTv.setVisibility(View.VISIBLE);
                        mEnginTv.setVisibility(View.VISIBLE);
                        mToneSpinner.setVisibility(View.VISIBLE);
                        mTTSTypeSpinner.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), getString(R.string.initsdk_success), Toast.LENGTH_SHORT).show();
                        mTTSTypeSpinner.setSelection(ttsType);
                    }

                    @Override
                    public void onFailed(int errorCode, String errorInfo) {

                    }
                });
                break;
            case R.id.btn_enhancer:
                int curTTSType = TTSManager.getInstance().getTTSType();
                if (curTTSType != TTSManager.TYPE_ONLINE_ENGINE) {
                    Toast.makeText(getActivity(), "不是在线TTS,无法使用增强效果", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean enhancerEnable = TTSManager.getInstance().getEnhancerEnable();
                TTSManager.getInstance().setEnhancerEnable(!enhancerEnable);
                String enHancerStr = TTSManager.getInstance().getEnhancerEnable() ? getString(R.string.open_enhancer) : getString(R.string.close_enhancer);
                mEnhancerBtn.setText(enHancerStr);
                break;
            default:
                break;
        }
    }

    @Override
    public void exitLogic() {
        Log.d(TAG, "exitLogic");
        if (TTSManager.getInstance().isSpeaking()) {
            TTSManager.getInstance().stopTTS();
        }
    }
}
