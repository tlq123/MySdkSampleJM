package com.turing.turingsdksample.demo;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qdreamer.utils.HttpUtils;
import com.turing.semantic.SemanticManager;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.tts.TTSListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.constants.ConstantsUtil;
import com.turing.turingsdksample.constants.FunctionConstants;
import com.turing.turingsdksample.demo_common.MediaMusicUtil;
import com.turing.turingsdksample.util.Logger;
import com.turing.turingsdksample.util.OSDataTransformUtil;


/**
 * @author：licheng@uzoo.com
 */

public class DemoBase {

    private String TAG = "DemoBase" ;

    private TTSListener TTSListener;
    private OnHttpRequestListener mSemanticListener;
    private boolean isDopost = false;//是否进行请求
    private boolean isTTs = false;//是否tts
    private Context mContext ;
    private String asrContent  = "";//语音识别 请求内容
    private int mPowerValue ;

    private DemoBase() {
    }

    private static class SingletonHolder {
        private static final DemoBase INSTANCE = new DemoBase();
    }
    public static DemoBase getInstance() {
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
            String tts = (String) OSDataTransformUtil.getResultBean(s).getValues().getText();
            int code = OSDataTransformUtil.getIntent(s).getCode();
            Logger.i(TAG, "semanticListener tts:" + tts + "   code:" + code);

            //中科汇联需要此处的代码  需要注释掉京密
//            if (code == 100000) {//os.sys.chat 语音聊天
//                doPostChar(s);
//            }else {
//                if (mSemanticListener != null) {
//                    mSemanticListener.onSuccess(s);
//                }
//                if (isTTs) {
//                    readTts(s);
//                }
//            }

            //京密项目  需要注释掉中科汇联
            if (mSemanticListener != null) {
                mSemanticListener.onSuccess(s);
            }
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
        asrContent = content ;
        //设置从云端返回数据读取的超时时间
        SemanticManager.getInstance().setReadTimeout(15 * 1000);
        //设置发送数据到云端写入的超时时间
        SemanticManager.getInstance().setWriteTimeout(15 * 1000);
        //设置连接超时时间
        SemanticManager.getInstance().setConnectTimeout(15 * 1000);
        SemanticManager.getInstance().requestSemantic(content, semanticListener);
    }

    /*
    从新请求聊天的内容
     */
    public void doPostChar(final String s){
        String url = "http://www.aikf.com/api/part/qa/7be7e45112e543e67a410c9200555d2f.htm";
        String qResult = asrContent;//语音识别结果
        Logger.i(TAG, "qResult----:" + qResult);
        String params = "fromuser=1366666666666&ip=172.10.10.11&reqtype=1&q=" + qResult + "&noFilter=false&phone=true";

        try {
            HttpUtils.doPostAsyn(url, params, new HttpUtils.CallBack() {
                @Override
                public void onRequestComplete(String result) {
                    JSONObject resultJson = JSON.parseObject(result);
                    String ansContent = resultJson.getJSONObject("text").getString("content");
                    Logger.i(TAG, "ansContent----:" + ansContent);

                    JSONObject bigJson = JSON.parseObject(s);
                    JSONObject json = ((JSONObject) ((JSONObject) bigJson.getJSONArray("behaviors").get(0)).getJSONArray("results").get(0)).getJSONObject("values");
                    json.put("text", ansContent);
                    String changeJsonStr = bigJson.toJSONString();
                    Logger.i(TAG, "changeJsonStr----:" + changeJsonStr);

                    if (mSemanticListener != null) {
                        mSemanticListener.onSuccess(changeJsonStr);
                    }
                    if (isTTs) {
                        readTts(changeJsonStr);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读
     **/
    protected void readTts(String str) {
        try {
            String tts = (String) OSDataTransformUtil.getResultBean(str).getValues().getText();
            int code = OSDataTransformUtil.getIntent(str).getCode();

            Logger.i(TAG, "readTts tts:" + tts +" code:"+code);
            if (FunctionConstants.mediaCodeList.contains(code)) {
                return;
            }
            if (FunctionConstants.musicCodeList.contains(code)){
                return;
            }

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

            }

            TTSManager.getInstance().startTTS(tts, ttsCallBack);

        } catch (Exception e) {
            e.printStackTrace();
            TTSManager.getInstance().startTTS("网络不给力", ttsCallBack);
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
