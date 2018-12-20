package com.turing.turingsdksample.activity_my4g;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qdreamer.utils.FileUtils;
import com.qdreamer.utils.HttpUtils;
import com.turing.semantic.SemanticManager;
import com.turing.semantic.entity.Behavior;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.tts.TTSListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.constants.ConstantsUtil;
import com.turing.turingsdksample.constants.FunctionConstants;
import com.turing.turingsdksample.demo_common.CrashHandler;
import com.turing.turingsdksample.demo_common.MediaMusicUtil;
import com.turing.turingsdksample.util.Logger;
import com.turing.turingsdksample.util.OSDataTransformUtil;
/**
 * @author：licheng@uzoo.com
 */

public class My4GBase {

    private String TAG = "My4GBase" ;

    private TTSListener TTSListener;
    private OnHttpRequestListener mSemanticListener;
    private boolean isDopost = false;//是否进行请求
    private boolean isTTs = false;//是否tts
    private Context mContext ;
    private String asrContent  = "";//语音识别 请求内容
    private int mPowerValue ;
    private int telState = 0  ;   //电话状态值  0电话无状态
    private String phoneNum = null ;

    private My4GBase() {
    }

    private static class SingletonHolder {
        private static final My4GBase INSTANCE = new My4GBase();
    }
    public static My4GBase getInstance() {
        return SingletonHolder.INSTANCE;
    }

    protected void setTTSListener(TTSListener TTSListener) {
        this.TTSListener = TTSListener;
    }

    protected void setSemanticListener(OnHttpRequestListener semanticListener) {
        this.mSemanticListener = semanticListener;
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
                Logger.d(TAG,"网络获取数据失败了。。。。");
                if(telState == 0){
                    TTSManager.getInstance().startTTS("这个我还不知道，请换一个吧",ttsCallBack);
                }
                return ;
            }

            int code = behavior.getIntent().getCode();
            Logger.i(TAG, "semanticListener code:" + code);

            //如果是音乐或者是故事，就不再往下走了
            if ( mSemanticListener != null) {
                if( FunctionConstants.STORY_CODE == code  || FunctionConstants.SONG_CODE == code ){
                    mSemanticListener.onSuccess(s);
                    return ;
                }
            }

            //不是音乐或者是故事
            if ( isTTs ) {
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
            if(phoneNum != null ){
                callPhone(phoneNum);
                phoneNum = null ;
            }

        }

        @Override
        public void onSpeakFailed() {

        }


    };


    public int getTelState() {
        return telState;
    }

    public void setTelState(int telState) {
        this.telState = telState;
    }

    /**
     * 语义
     **/
    public void doPost(String content) {
        asrContent = content ;
        //设置从云端返回数据读取的超时时间
        SemanticManager.getInstance().setReadTimeout(15 * 1000);
        //设置发送数据到云端写入的超时时间
        SemanticManager.getInstance().setWriteTimeout(15 * 1000);
        //设置连接超时时间
        SemanticManager.getInstance().setConnectTimeout(15 * 1000);
        SemanticManager.getInstance().requestSemantic(content, semanticListener);
    }

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

            }else if(code == 100302 ){       //接电话 挂电话
                if("10".equals(tts)){
                    callAnswerEnd(true);
                }else if("11".equals(tts)){
                    callAnswerEnd(false);
                }
            }else if(code == 200802){              // 打电话
                Logger.i(TAG,"telState:"+telState);
                if(telState == 0){  //电话空闲状态
                    JSONObject bigJson = JSON.parseObject( str );
                    String name = (((JSONObject) bigJson.getJSONArray("behaviors").get(0)).getJSONObject("intent")
                            .getJSONObject("parameters")).getString("people_name");
                    final String number = (((JSONObject) bigJson.getJSONArray("behaviors").get(0)).getJSONObject("intent")
                            .getJSONObject("parameters")).getString("phone_number");
                    Log.i(TAG, "name:" + name+"  number:"+number);

                    TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
                    String simSer = tm.getSimSerialNumber();
                    if(simSer == null || "".equals(simSer) ){
                        TTSManager.getInstance().startTTS("请先插入sim卡，再拨打电话", ttsCallBack);
                    }else if( number == null ){//||phone_number.length()!=11
                        TTSManager.getInstance().startTTS("请呼叫正确的电话号码", ttsCallBack);
                    }else{
                        TTSManager.getInstance().startTTS("正在呼叫"+name, ttsCallBack);
                        phoneNum  = number ;
                        CrashHandler.getInstance().writeFilePhone("正在呼叫:"+name+"   phone:"+number);
                    }
                    return ;
                }
            }

            if(telState == 0){  //电话空闲状态
                TTSManager.getInstance().startTTS(tts, ttsCallBack);
            }

        } catch (Exception e) {
            e.printStackTrace();
            TTSManager.getInstance().startTTS("网络不给力", ttsCallBack);
        }
    }

    /**
     * 调用拨号功能
     * @param phone 电话号码
     *              name----:13143407320
     */
    private void callPhone(String phone) {
        Logger.i(TAG,"phone:"+phone);
        Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone));
        try{
            mContext.startActivity(intent);
        }catch (Exception e){
            Log.e(TAG,"e.getMessage:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /*
   接听电话和关掉电话
    */
    private void callAnswerEnd(boolean flag){
        Intent intent = new Intent();
        if(flag){
            intent.setAction("android.intent.action.phone.answercall");
            mContext.sendBroadcast(intent);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent intent2 = new Intent();
//                    intent2.setAction("android.intent.action.phone.setSpeekMode");
//                    mContext.sendBroadcast(intent2);
//                }
//            }, 1000);
        }else {
            intent.setAction("android.intent.action.phone.endcall");
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 用于设置
     **/
    protected void setActionBo(boolean isdo, boolean isTt) {
        this.isDopost = isdo;
        this.isTTs = isTt;
    }

    /**
     * asr+语义+tts
     **/
    public void getAll(OnHttpRequestListener semanticClientListener, TTSListener ttsClientListener,Context context) {
        setSemanticListener(semanticClientListener);
        setTTSListener(ttsClientListener);
        setActionBo(true, true);
        TTSManager.getInstance().stopTTS();
        mContext = context;
    }

}
