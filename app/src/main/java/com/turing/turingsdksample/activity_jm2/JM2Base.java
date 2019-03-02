package com.turing.turingsdksample.activity_jm2;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.JsonObject;
import com.turing.semantic.SemanticManager;
import com.turing.semantic.entity.Behavior;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.tts.TTSListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.alarm.Alarm;
import com.turing.turingsdksample.alarm.AlarmManagerUtil;
import com.turing.turingsdksample.alarm.Database;
import com.turing.turingsdksample.constants.ConstantsUtil;
import com.turing.turingsdksample.constants.FunctionConstants;
import com.turing.turingsdksample.demo_common.MediaMusicUtil;
import com.turing.turingsdksample.demo_common.SharedPreferencesUtil;
import com.turing.turingsdksample.util.Logger;
import com.turing.turingsdksample.util.OSDataTransformUtil;


/**
 * @author：licheng@uzoo.com
 */

public class JM2Base {

    private String TAG = "JM2Base" ;

    private TTSListener TTSListener;
    private OnHttpRequestListener mSemanticListener;
    private OnEmotionListener onEmotionListener ;
    private boolean isDopost = false;//是否进行请求
    private boolean isTTs = false;//是否tts

    private int mPowerValue ;

    private JM2Base() {
    }

    private static class SingletonHolder {
        private static final JM2Base INSTANCE = new JM2Base();
    }
    public static JM2Base getInstance() {
        return SingletonHolder.INSTANCE;
    }

    protected void setTTSListener(TTSListener TTSListener) {
        this.TTSListener = TTSListener;
    }

    protected void setSemanticListener(OnHttpRequestListener semanticListener) {
        this.mSemanticListener = semanticListener;
    }

    protected void setEmotionListener(OnEmotionListener emotion) {
        this.onEmotionListener = emotion;
    }

    public void setPowerValue(int pv){
        mPowerValue = pv ;
    }


    protected OnHttpRequestListener semanticListener = new OnHttpRequestListener() {

        @Override
        public void onSuccess(String s) {

            Logger.i(TAG, "semanticListener onSuccess:" + s);

            Behavior behavior = OSDataTransformUtil.getBehavior(s);
            if( behavior == null ){
                TTSManager.getInstance().startTTS("这个我还不会，请换一个", ttsCallBack);
                Logger.i(TAG, "semanticListener 网络数据无法解析" );
                return;
            }
            String tts = behavior.getResults().get(0).getValues().getText();
            int code = behavior.getIntent().getCode();
            Logger.i(TAG, "semanticListener tts:" + tts + "   code:" + code);

            //唱歌 或者 讲故事
            if (FunctionConstants.STORY_CODE == code || FunctionConstants.SONG_CODE == code) {
                if (mSemanticListener != null) {
                    mSemanticListener.onSuccess(s);
                }
                return ;
            }

//            {"behaviors":[{"emotion":{"answerEmotionId":20300},"exception":"哎呀，没听清你在说什么，请再说一遍",
//                    "globalId":73321217122000001,"intent":{"appKey":"os.sys.chat","code":100000,"parameters":{},"type":"scene"},"results":
//                [{"resultType":"text","values":{"text":"这样说话是很没有礼貌的。"}}],"sequences":[{"service":"intent"}]}]}

            Behavior.Emotion emotion = behavior.getEmotion() ;
            if(emotion != null){
                onEmotionListener.OnEmotion(emotion.getAnswerEmotionId());
            }

            //聊天
            if (isTTs) {
                readTts(s);
            }
        }

        @Override
        public void onError(int i, String s) {
            if (mSemanticListener != null) {
                mSemanticListener.onError(i, s);
            }
        }

        @Override
        public void onCancel() {

        }
    };


    /**
     * 读
     **/
    protected void readTts(String str) {
        try {
            String tts = OSDataTransformUtil.getResultBean(str).getValues().getText();
            int code = OSDataTransformUtil.getIntent(str).getCode();
            Logger.i(TAG, "readTts tts:" + tts +" code:"+code);

            if(code == 900110){        //改变音量  //"appKey":"os.sys.setting","code":900110,"

                int operateState = OSDataTransformUtil.getIntent(str).getOperateState();
                if(operateState == 1010){//音量变大
                    tts = MediaMusicUtil.getInstance().setVolume(operateState,tts);
                }else if(operateState == 1011){//音量变小
                    tts = MediaMusicUtil.getInstance().setVolume(operateState,tts);
                }else if (operateState == 1030){  //当前电量
                    tts ="当前电量值为百分之"+ mPowerValue ;
                }

            }else if(code == 900101){  //"appKey":"os.sys.exit", "code":900101,
                int operateState = OSDataTransformUtil.getIntent(str).getOperateState();
                if(operateState == 1003){  //再见，拜拜，
                    tts = ConstantsUtil.DORMANCY;
                }
            }else if(code == 100000){
                int operateState = OSDataTransformUtil.getIntent(str).getOperateState();
                if(operateState == 2001){  //开机提示语
                    JsonObject object = OSDataTransformUtil.getIntent(str).getParameters();
                    tts = object.getAsJsonArray("tasklist").get(0).getAsJsonObject().get("text").getAsString();
                }
            }else if(code == 200710){  //闹钟
                JsonObject object = OSDataTransformUtil.getIntent(str).getParameters();
                String time = object.get("time").getAsString();
                String times [] = time.split(":");

                String action = object.get("action").getAsString();
                Logger.e(TAG,"time:"+time +" action:"+action);
                if("add".equals(action)){
                    if(times.length == 3){
                        int cycleType = object.get("cycleType").getAsInt() ;
                        int id = SharedPreferencesUtil.getBroadcastReceiverIndex(mContext);
                        id ++ ;
                        SharedPreferencesUtil.saveBroadcastReceiverIndex( id ,mContext );
                        if ( cycleType == 1) {//是每天的闹钟
                            Logger.e(TAG,"每天闹钟id:"+id );
                            Alarm alarm = new Alarm();
                            alarm.setId(id);
                            alarm.setAlarmTime(times[0]+":"+times[1]);
                            AlarmManagerUtil.setAlarm(mContext, 1, Integer.parseInt(times[0]), Integer.parseInt
                                    (times[1]), id, 0, "闹钟响了", 1);
                            Database.create(alarm);
                        } else {//是只响一次的闹钟
                            Logger.e(TAG,"单次闹钟id:"+id );
                            AlarmManagerUtil.setAlarm(mContext, 0, Integer.parseInt(times[0]), Integer.parseInt
                                    (times[1]), id, 0, "闹钟响了", 1);
                        }
                    }else {
                        tts = "闹钟添加失败，请从新设置闹钟";
                    }
                }
            }

            TTSManager.getInstance().startTTS(tts, ttsCallBack);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.i(TAG, "readTts 解析出错了e:"+e.getMessage());
//            TTSManager.getInstance().startTTS("网络不给力", ttsCallBack);
        }
    }

    protected TTSListener ttsCallBack = new TTSListener() {

        @Override
        public void onSpeakBegin(String s) {
            if (TTSListener != null) {
                TTSListener.onSpeakBegin(s);
            }
        }

        @Override
        public void onSpeakPaused() {
            if (TTSListener != null) {
                TTSListener.onSpeakPaused();
            }
        }

        @Override
        public void onSpeakResumed() {
            if (TTSListener != null) {
                TTSListener.onSpeakResumed();
            }
        }

        @Override
        public void onSpeakCompleted() {
            if (TTSListener != null) {
                TTSListener.onSpeakCompleted();
            }
        }

        @Override
        public void onSpeakFailed() {

        }
    };

    /**
     * 语义
     **/
    public void doPost(String content) {
        Logger.e(TAG,"content===="+content);
        if(TextUtils.isEmpty(content)){
            return;
        }
        //设置从云端返回数据读取的超时时间
        SemanticManager.getInstance().setReadTimeout(15 * 1000);
        //设置发送数据到云端写入的超时时间
        SemanticManager.getInstance().setWriteTimeout(15 * 1000);
        //设置连接超时时间
        SemanticManager.getInstance().setConnectTimeout(15 * 1000);
        SemanticManager.getInstance().requestSemantic(content, semanticListener);
    }

    //开机提示语
    public void doPostFirstConversion() {

        //设置从云端返回数据读取的超时时间
        SemanticManager.getInstance().setReadTimeout(15 * 1000);
        //设置发送数据到云端写入的超时时间
        SemanticManager.getInstance().setWriteTimeout(15 * 1000);
        //设置连接超时时间
        SemanticManager.getInstance().setConnectTimeout(15 * 1000);
        SemanticManager.getInstance().requestFirstConversion(semanticListener);
    }


    /**
     * 用于设置
     **/
    protected void setActionBo(boolean isdo, boolean isTt) {
        this.isDopost = isdo;
        this.isTTs = isTt;
    }

    private Context mContext ;
    /**
     * asr+语义+tts
     **/
    public void getAll(OnHttpRequestListener semanticClientListener,OnEmotionListener emotionListener, TTSListener ttsClientListener,Context context) {
        mContext = context ;
        setSemanticListener(semanticClientListener);
        setEmotionListener(emotionListener);
        setTTSListener(ttsClientListener);
        setActionBo(true, true);
        doPostFirstConversion(); //开机提示语
    }

}
