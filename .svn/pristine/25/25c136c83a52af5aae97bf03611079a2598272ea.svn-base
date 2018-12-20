package com.turing.turingsdksample.fragment;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.turing.asr.callback.AsrListener;
import com.turing.asr.engine.AsrManager;
import com.turing.asr.function.bean.ASRErrorMessage;
import com.turing.music.MusicManager;
import com.turing.music.OnPlayerStateListener;
import com.turing.semantic.SemanticManager;
import com.turing.semantic.entity.Behavior;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.tts.TTSListener;
import com.turing.tts.TTSManager;
import com.turing.tts.util.LogUtil;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.ability.Base;
import com.turing.turingsdksample.bean.ContentBean;
import com.turing.turingsdksample.callback.CategoryOperateCallback;
import com.turing.turingsdksample.callback.MusicCallback;
import com.turing.turingsdksample.constants.FunctionConstants;
import com.turing.turingsdksample.handler.MusicHandler;
import com.turing.turingsdksample.music.TuringMusic;
import com.turing.turingsdksample.ui.BottomLayout;
import com.turing.turingsdksample.ui.ContentLayout;
import com.turing.turingsdksample.ui.ParseLayout;
import com.turing.turingsdksample.util.Logger;
import com.turing.turingsdksample.util.OSDataTransformUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：licheng@uzoo.com
 */

public class AllFragment extends BaseFragment implements CategoryOperateCallback, MusicCallback {
    private final String TAG = AllFragment.class.getSimpleName();
    private ContentLayout userLayout, robotLayout;
    private ArrayList<ContentBean> userlist, robotlist;
    private ParseLayout parselayout;
    private BottomLayout bottomLayout;
    private RelativeLayout rel_status;
    private TextView tv_status;
    private volatile boolean isReadyPlay = false;
    private MusicHandler musicHandler = new MusicHandler();
    private Behavior.IntentInfo curentBehavior = null;
    /**
     * 当前返回的语义解析结果
     */
    private Behavior behavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_all, null);
        //用户的userId
        userLayout = (ContentLayout) view.findViewById(R.id.userlayout);
        userLayout.setCategory(getString(R.string.user));
        userlist = userLayout.getListsData();
        userlist.add(new ContentBean());
        userlist.add(new ContentBean());
        //机器人回答
        robotLayout = (ContentLayout) view.findViewById(R.id.robotlayout);
        robotLayout.setCategory(getString(R.string.robot));
        robotLayout.setStartTextShow(getString(R.string.tts_start));
        robotLayout.setEndTextShow(getString(R.string.tts_stop));
        robotLayout.setStartViewShow(View.VISIBLE);
        robotLayout.setEndViewShow(View.VISIBLE);
        robotLayout.setMusicShow(View.VISIBLE);
        robotLayout.setUploadViewShow(View.VISIBLE);
        setMusicView(false);
        robotLayout.setMusicCallback(this);
        robotLayout.setOnHttpRequestListener(deviceInfoUploadListener);
        robotlist = robotLayout.getListsData();
        robotlist.add(new ContentBean());
        robotlist.add(new ContentBean());
        robotlist.add(new ContentBean());
        //返回结果
        parselayout = (ParseLayout) view.findViewById(R.id.parselayout);
        parselayout.setCategory(getString(R.string.request_data));
        //底部
        bottomLayout = (BottomLayout) view.findViewById(R.id.base_bottom);
        bottomLayout.setCallback(this);
        //状态
        rel_status = (RelativeLayout) view.findViewById(R.id.rel_status);
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        musicHandler.mOnMiGuPlayStateListener = mOnPlayerStateListener;
        musicHandler.mOnTuringPlayStateListener = mOnTuringPlayStateListener;
        musicHandler.context = this.getContext().getApplicationContext();
        com.freedomer.uploadcommon.LogUtil.setDebugLogOn(false);
        return view;
    }

    private TuringMusic.MusicStateListener mOnTuringPlayStateListener = new TuringMusic.MusicStateListener() {
        @Override
        public void onStart() {
            Log.d(TAG, "onStart");
        }

        @Override
        public void onStop() {
            Log.d(TAG, "onStop");

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "onComplete");

        }

        @Override
        public void onError(int errorCode) {
            Log.d(TAG, "onError");

        }
    };

    /**
     * 用户自己的
     **/
    private void operateUserCallback(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setEndASRLayout();
                userlist.get(0).setText(str);
                userLayout.notifyDataChanged();
            }
        });
    }


    public void processSequence(final String content) {
        behavior = OSDataTransformUtil.getBehavior(content);
        curentBehavior = behavior.getIntent();
        final Behavior.IntentInfo intent = behavior.getIntent();
        final List<String> ttsSequenceList = new ArrayList<>();
        List<Behavior.ResponseResult> results = behavior.getResults();
        Behavior.ResponseResult responseResult = results.size() == 0 ? null : results.get(0);

        boolean hasSequence = behavior.getSequences() != null && behavior.getSequences().size() > 0 && !TextUtils.isEmpty(behavior.getSequences().get(0).getText());
        boolean hasValue = responseResult != null && responseResult.getValues() != null;


        //表情
        //心情
        Behavior.Emotion osEmotion = behavior.getEmotion();
        if (osEmotion != null) {
            Log.d("osEmotion", osEmotion.toString());
            int robot_emotion = osEmotion.getAnswerEmotionId();
            //机器人的
            robotlist.get(1).setEmotion(robot_emotion);
            //用户的
            userlist.get(1).setEmotion(osEmotion.getSentenceEmotion());
        }


        if (intent != null) {
            Log.d("osIntentBean", intent.toString());
            int robot_code = intent.getCode();
            robotlist.get(2).setCode(robot_code);
        }


        if (hasSequence) {
            List<Behavior.Sequence> sequences = behavior.getSequences();
            switch (sequences.size()) {
                case 1:
                    String textValue = hasValue ? responseResult.getValues().getText() : "";
                    String service = sequences.get(0).getService();
                    if (!TextUtils.isEmpty(service) && "intent".equals(service)) {
                        if (!TextUtils.isEmpty(textValue)) {
                            ttsSequenceList.add(textValue);
                        }
                    }
                    break;
                case 2:
                    String sequenceBefore = sequences.get(0).getText();
                    if (behavior.getIntent().getCode() == FunctionConstants.MUSICAL_CODE) {
                        sequenceBefore = sequenceBefore.replace("#{name}", behavior.getIntent().getParameters().get("name").getAsString());
                    }
                    if (behavior.getIntent().getCode() == FunctionConstants.ANIMA_CODE) {
                        sequenceBefore = sequenceBefore.replace("#{name}", behavior.getIntent().getParameters().get("name").getAsString());
                    }
                    if (!TextUtils.isEmpty(sequenceBefore)) {
                        ttsSequenceList.add(sequenceBefore);
                    }

                    service = sequences.get(1).getService();
                    textValue = hasValue ? responseResult.getValues().getText() : "";
                    if (TextUtils.isEmpty(service) && "intent".equals(service)) {
                        if (!TextUtils.isEmpty(textValue)) {
                            ttsSequenceList.add(textValue);
                        }
                    }
                    break;
                case 3:
                    sequenceBefore = sequences.get(0).getText();
                    if (behavior.getIntent().getCode() == FunctionConstants.MUSICAL_CODE) {
                        sequenceBefore = sequenceBefore.replace("#{name}", behavior.getIntent().getParameters().get("name").getAsString());
                    }
                    if (behavior.getIntent().getCode() == FunctionConstants.ANIMA_CODE) {
                        sequenceBefore = sequenceBefore.replace("#{name}", behavior.getIntent().getParameters().get("name").getAsString());
                    }

                    if (!TextUtils.isEmpty(sequenceBefore)) {
                        ttsSequenceList.add(sequenceBefore);
                    }
                    service = sequences.get(1).getService();
                    textValue = hasValue ? responseResult.getValues().getText() : "";
                    if (TextUtils.isEmpty(service) && "intent".equals(service)) {
                        if (!TextUtils.isEmpty(textValue)) {
                            ttsSequenceList.add(textValue);
                        }
                    }

                    String sequenceAfter = sequences.get(2).getText();
                    if (!TextUtils.isEmpty(sequenceAfter)) {
                        ttsSequenceList.add(sequenceAfter);
                    }

                    break;
                default:
                    break;
            }
        } else {
            String textValue = hasValue ? responseResult.getValues().getText() : "";
            if (!TextUtils.isEmpty(textValue)) {
                ttsSequenceList.add(textValue);
            }
        }

        parselayout.setTextContent(content);
        //
        setStatusLayout();
        //清空输入
        bottomLayout.clearInputDate();

        Log.d(TAG, "ttsSequenceList SIZE = " + ttsSequenceList.size());
        if (ttsSequenceList.size() > 0) {
            robotlist.get(0).setText(ttsSequenceList.get(0));
            TTSManager.getInstance().startTTS(ttsSequenceList.get(0), new TTSListener() {
                @Override
                public void onSpeakBegin(String s) {
                    Log.d(TAG, "ttsSequenceList onSpeakBegin");
                    if (isMusic(intent) && intent != null && intent.getParameters() != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("name", intent.getParameters().get("name").getAsString());
                        if (intent.getParameters().get("url") != null) {
                            bundle.putString("url", intent.getParameters().get("url").getAsString());
                        }
                        Message message = new Message();
                        message.setData(bundle);
                        message.what = MusicHandler.MSG_MIGU_SEARCH;
                        musicHandler.sendMessage(message);
                    }
                    checkMusic(behavior.getIntent());
                }

                @Override
                public void onSpeakPaused() {

                }

                @Override
                public void onSpeakResumed() {

                }

                @Override
                public void onSpeakCompleted() {
                    Log.d(TAG, "ttsSequenceList complete");
                    if (isMusic(intent)||isMedia(intent)) {
                        musicHandler.sendEmptyMessage(MusicHandler.MSG_TTS_FINISH);
                        return;
                    }
                }

                @Override
                public void onSpeakFailed() {

                }
            });
        } else {
            Log.d(TAG, "ttsSequenceList size <= 0");
            checkMusic(behavior.getIntent());
        }

        robotLayout.notifyDataChanged();
        userLayout.notifyDataChanged();
    }


    private OnPlayerStateListener mOnPlayerStateListener = new OnPlayerStateListener() {
        @Override
        public void onStart() {

        }

        @Override
        public void onComplete() {
            Log.d(TAG, "onComplete");
        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onBufferingUpdate(int i) {

        }
    };


    public boolean isMusic(Behavior.IntentInfo bean) {
        if (!FunctionConstants.musicCodeList.contains(bean.getCode())) {
            Log.e(TAG, "不是音乐");
            return false;
        }
        return true;
    }

    public boolean isMedia(Behavior.IntentInfo bean){
        if (!FunctionConstants.mediaCodeList.contains(bean.getCode())) {
            Log.d(TAG, "不是音频资源");
            return false;
        }
        return true;
    }

    public void resetToDefault() {
        hiddenCircleDialog();
        tv_status.setVisibility(View.GONE);
        bottomLayout.setTextBtnASR(getString(R.string.click_asr_start));

    }


    /**
     * APP列表与联系人列表上传回调
     */
    private OnHttpRequestListener deviceInfoUploadListener = new OnHttpRequestListener() {
        @Override
        public void onSuccess(String s) {
            parselayout.setTextContent("upload success");
        }

        @Override
        public void onError(int i, String s) {
            parselayout.setTextContent("upload fail errorCode:" + i + ";errorMsg:" + s);
        }

        @Override
        public void onCancel() {

        }

    };

    private OnHttpRequestListener httpClientListener = new OnHttpRequestListener() {

        @Override
        public void onSuccess(final String s) {
            if (!TextUtils.isEmpty(s)) {
                Logger.d(Logger.TAG, "httpClientListener|onSuccess|response:" + s);


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processSequence(s);
                    }
                });
            } else {
                onErrorCallback(getString(R.string.str_response_null));
            }
        }

        @Override
        public void onError(int i, String s) {
            Logger.d(Logger.TAG, "httpClientListener|onError|errorCode:" + i + "|errorMsg:" + s);
            onErrorCallback(s);
        }

        @Override
        public void onCancel() {
            Logger.d(Logger.TAG, "httpClientListener|onCancel");
        }
    };

    private AsrListener asrListener = new AsrListener() {

        @Override
        public void onResults(List<String> list) {
            if (list != null && list.size() > 0 && !TextUtils.isEmpty(list.get(0))) {
                operateUserCallback(list.get(0));
            } else {
                Toast.makeText(getActivity(), "ASR没有结果", Toast.LENGTH_SHORT).show();
                tv_status.setVisibility(View.GONE);
                bottomLayout.setTextBtnASR(getString(R.string.click_asr_start));
                rel_status.setVisibility(View.GONE);
            }
        }

        @Override
        public void onStartRecord() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //显示
                    setStartASRLayout();
                }
            });
        }

        @Override
        public void onEndOfRecord() {

        }


        @Override
        public void onError(ASRErrorMessage errorMessage) {
            onErrorCallback(errorMessage.getMessage());
        }

        @Override
        public void onVolumeChange(int i) {

        }
    };
    private TTSListener itsCallback = new TTSListener() {

        @Override
        public void onSpeakBegin(String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakCompleted() {
            Log.d(TAG, "onSpeakCompleted");
            if (behavior != null) {
                Behavior.IntentInfo intentInfo = behavior.getIntent();
                if (intentInfo != null) {
                    checkMusic(intentInfo);
                }
            }
        }

        @Override
        public void onSpeakFailed() {

        }


    };


    @Override
    public String getFragmentTag() {
        return getString(R.string.personvsrobot);
    }

    @Override
    public void operateAll() {

        if (!isASRing()) {
            Log.d(TAG, "operateAll not asr");
            if (rel_status.getVisibility() == View.GONE) {
                Log.d(TAG, "operateAll getVisibility GONE");
                Base.getInstance().getAll(asrListener, httpClientListener, itsCallback);
            }
        } else {
            Log.d(TAG, "operateAll  asring");
            AsrManager.getInstance().stop();
        }
    }

    @Override
    public void operateTextForTTS(String str) {
        userlist.get(0).setText(str);
        userLayout.notifyDataChanged();
        if (TuringMusic.getInstance().isPlaying()) {
            TuringMusic.getInstance().stop();
        }
        if (MusicManager.getInstance().isPlaying()) {
            MusicManager.getInstance().stop();
        }
        //设置从云端返回数据读取的超时时间
        SemanticManager.getInstance().setReadTimeout(15 * 1000);
        //设置发送数据到云端写入的超时时间
        SemanticManager.getInstance().setWriteTimeout(15 * 1000);
        //设置连接超时时间
        SemanticManager.getInstance().setConnectTimeout(15 * 1000);
        SemanticManager.getInstance().requestSemantic(str, httpClientListener);
    }

    @Override
    public void operateOnlyASR() {

    }

    @Override
    public void operateOnlyTTS(String str) {

    }

    /**
     * 状态
     **/
    private void setStatusView(int flag) {
        rel_status.setVisibility(flag);
    }


    /**
     * 当开始ASR时
     **/
    private void setStartASRLayout() {
        TuringMusic.getInstance().stop();
        TTSManager.getInstance().stopTTS();
        if (MusicManager.getInstance().isPlaying()) {
            MusicManager.getInstance().stop();
        }
        setStatusView(View.VISIBLE);
        tv_status.setText(R.string.asring);
        tv_status.setVisibility(View.VISIBLE);
        bottomLayout.setTextBtnASR(getString(R.string.click_asr_stop));
    }

    /**
     * 当asr结束
     **/
    private void setEndASRLayout() {
        setStatusView(View.VISIBLE);
        tv_status.setText(R.string.paresing);
        bottomLayout.setTextBtnASR(getString(R.string.click_asr_start));
    }

    /**
     * 当解析完成后，改变状态
     **/
    private void setStatusLayout() {
        setStatusView(View.GONE);
    }

    /**
     * 判断当前是不是asring
     **/
    private boolean isASRing() {
        return AsrManager.getInstance().isRecording();
    }

    /**
     * 当解析出错时
     */
    private void onErrorCallback(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
                bottomLayout.setTextBtnASR(getString(R.string.click_asr_start));
                setStatusView(View.GONE);
            }
        });
    }


    private TuringMusic.MusicStateListener musicStateListener = new TuringMusic.MusicStateListener() {
        @Override
        public void onStart() {
            hiddenCircleDialog();
        }

        @Override
        public void onStop() {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(int errorCode) {

        }
    };


    /**
     * 开始播放音乐
     **/
    @Override
    public void startMusic(String mediaUrl) {
        Log.e(TAG, "media url=" + mediaUrl);
        setCircleDialogMessage(getActivity(), R.string.http_ing_resource);
        try {
            mediaUrl = URLDecoder.decode(mediaUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        LogUtil.d(TAG, "mediaUrl = " + mediaUrl);
        TuringMusic.getInstance().setMusicStateListener(musicStateListener);
        TuringMusic.getInstance().prepare(mediaUrl, new TuringMusic.PrepareListener() {
            @Override
            public void onSuccess() {
                TuringMusic.getInstance().play();
            }

            @Override
            public void onFailed() {
                TTSManager.getInstance().startTTS(getString(R.string.network_error), new TTSListener() {
                    @Override
                    public void onSpeakBegin(String s) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resetToDefault();
                            }
                        });
                    }

                    @Override
                    public void onSpeakPaused() {

                    }

                    @Override
                    public void onSpeakResumed() {

                    }

                    @Override
                    public void onSpeakCompleted() {

                    }

                    @Override
                    public void onSpeakFailed() {

                    }
                });


            }
        });
    }

    private void checkMusic(final Behavior.IntentInfo bean) {
        Log.d(TAG, "check Music = " + bean.getCode());
        if (!isMedia(bean)) {
            Log.d(TAG, "不是音频资源");
            return;
        }
        JsonObject parameters = bean.getParameters();
        Log.d(TAG, " judge parameters before");
        if (parameters == null) {
            Log.d(TAG, "parameters is null");
            return;
        }
        try {
            Log.d(TAG, " json object1 =" + parameters.toString());
            JSONObject jsonObject = new JSONObject(parameters.toString());
            String mediaUrl = "";
            if (bean.getCode() == FunctionConstants.ANIMA_CODE || bean.getCode() == FunctionConstants.NATURE_CODE || bean.getCode() == FunctionConstants.MUSICAL_CODE) {
                mediaUrl = jsonObject.optJSONObject("resources").optString("url");
            } else if (bean.getCode() == FunctionConstants.SHOUT_CODE) {
                mediaUrl = jsonObject.optJSONObject("voice").optString("url");
            } else {
                mediaUrl = jsonObject.getString("url");
            }

            Log.d(TAG, " judge mediaUrl before");
            if (TextUtils.isEmpty(mediaUrl)) {
                Log.d(TAG, "url为空");
                return;
            }

            if (bean.getOperateState() == 1003 || bean.getOperateState() == 1001) {
                Log.d(TAG, "暂停或者停止操作");
                TuringMusic.getInstance().stop();
                return;
            }
            final String finalMediaUrl = mediaUrl;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    robotLayout.setMusicSelectable(true);
//                    startMusic(finalMediaUrl);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", finalMediaUrl);
                    Message message = new Message();
                    message.setData(bundle);
                    message.what = MusicHandler.MSG_PREPARE_TURING;
                    musicHandler.sendMessage(message);
                    setMusicView(true);
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, " parse exception");
            e.printStackTrace();
        }
    }

    /**
     * @param isClick 播放资源是否可以按压
     **/
    private void setMusicView(boolean isClick) {
        robotLayout.setMusicSelectable(isClick);
    }

    @Override
    public void exitLogic() {
        LogUtil.d(TAG, "exitLogic");
        if (AsrManager.getInstance().isRecording()) {
            AsrManager.getInstance().cancel();
        }
        if (TTSManager.getInstance().isSpeaking()) {
            TTSManager.getInstance().stopTTS();
        }
        if (MusicManager.getInstance().isPlaying()) {
            MusicManager.getInstance().stop();
        }
        if (TuringMusic.getInstance().isPlaying()) {
            TuringMusic.getInstance().stop();
        }
    }


}
