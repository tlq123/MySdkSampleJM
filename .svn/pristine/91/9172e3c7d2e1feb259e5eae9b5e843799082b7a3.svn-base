package com.turing.turingsdksample.activity_my4g;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qdreamer.qvoice.BfioHelper;
import com.qdreamer.qvoice.QEngine;
import com.qdreamer.qvoice.QSession;
import com.turing.authority.authentication.AuthenticationListener;
import com.turing.authority.authentication.SdkInitializer;
import com.turing.iot.MQTTManager;
import com.turing.semantic.SemanticManager;
import com.turing.semantic.entity.AppAndContactsBean;
import com.turing.semantic.entity.Behavior;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.tts.TTSInitListener;
import com.turing.tts.TTSListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.bean.QASREntity;
import com.turing.turingsdksample.constants.ConstantsUtil;
import com.turing.turingsdksample.constants.FunctionConstants;
import com.turing.turingsdksample.demo_common.AudioManagerUtil;
import com.turing.turingsdksample.demo_common.ConfigureNetworkUtil;
import com.turing.turingsdksample.demo_common.MediaMusicUtil;
import com.turing.turingsdksample.util.CopyResUtil;
import com.turing.turingsdksample.util.Logger;
import com.turing.turingsdksample.util.MyNetUtil;
import com.turing.turingsdksample.util.OSDataTransformUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/*
  4G专案
 */
public class My4GActivity extends Activity {

    private String TAG = "My4GActivity--";
    /*第一次初始化*/
    private boolean isFirstInit ;
    private long initSDKTime = 0;
    private Context mContext ;
    private boolean  isAsrFlag ;
    private boolean isMedia = false ;//判断是否下一首
    /**
     * 当前返回的语义解析结果
     */
    private Behavior behavior;
    private String ttsCallbackState = "";//tts回调状态

    private String mPath ;
    private String mResBfio;// bfio参数
    private BfioHelper bfio;// 算法
    private QEngine asrEngine;//识别引擎
    private QSession qSession;
    private Handler asrHandler;
    private byte[] aecData;
    private long initSession;

    private GifImageView gifImageView ;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Logger.d(TAG,"onCreate...........................");
        initView();
        init();
    }

    private void initView (){

        gifImageView = (GifImageView) findViewById(R.id.gif_imageview);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.wake);
            gifImageView.setBackgroundDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setGifImageView(final boolean type){
        if(isAsrFlag = type){
            return ;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(type){
                    try {
                        GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.recording);
                        gifImageView.setBackgroundDrawable(gifDrawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.wake);
                        gifImageView.setBackgroundDrawable(gifDrawable);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    }

    private void init (){

        /*com.turing123.libs.android.utils.Logger.setLogOn(false);
        LogUtil.setDebugLogOn(false);
        com.turing.music.LogUtil.setDebugLogOn(false);
        com.util.LogUtil.setDebugLogOn(false);
        com.qdreamer.utils.Loger.isDebug = false ;*/

        mContext = this ;
        isFirstInit = true ;
        telState = 0 ;
        mPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        mResBfio = "role=bfio;cfg=" + mPath + "qvoice/bfio1.xzxz.0713.bin;use_oneMic=0;";
        //音频初始化以及播放状态
        MediaMusicUtil.getInstance().initMusic(this, new MediaMusicUtil.ImusicListener() {
            @Override
            public void OnMusicCompletion() {
                Logger.d(TAG,"OnMusicCompletion。。。isAsrFlag："+isAsrFlag);
                if( isMedia ){
                    My4GBase.getInstance().doPost("下一个");
                }
            }

            @Override
            public void OnMusicError() {
                Logger.d(TAG,"OnMusicError。。。");
                behavior = null ;
                TTSManager.getInstance().startTTS("网络缓存失败", itsCallback);
            }

            @Override
            public void OnMusicStop() {
                Logger.d(TAG, "OnMusicStop  ");

            }

            @Override
            public void OnMusicPause() {
                Logger.d(TAG,"OnMusicPause。。。");
            }
        });
        //开机提示语
        MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.happy_see_you);

        ConfigureNetworkUtil.toggleWiFi(this);  //开启wifi
        ConfigureNetworkUtil.playConnNetPromote(this); //网络提示语
        regBroadcast();//wifi 电量 广播

        CopyResUtil.copyRes(mContext,mPath);

        if( MyNetUtil.isNetworkAvailable(this) ){
            initTuing();
        }

        initPhoneListner() ;

        Logger.d(TAG,"getmode ==============="+AudioManagerUtil.getMode(this));
    }



    /*
    图灵
     */
    private void initTuing(){
        if ( !isFirstInit ){
            Logger.d(TAG,"网络变化 已经初始化过了");
            return ;
        }

        String macAdd = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        Logger.i(TAG, "macAdd:" + macAdd);
        if (macAdd != null && macAdd.startsWith("00:")) {
            MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.write_mac_first);
            return;
        }

        if (System.currentTimeMillis() - initSDKTime > 5 * 1000) {//防止重复调用
            initSDKTime = System.currentTimeMillis();
            Logger.d(TAG,"开始初始化了");
            SdkInitializer.init(mContext,  ConstantsUtil.API_KEY, ConstantsUtil.API_SECRET, authenticationListener);
        }

    }
    /*
    图灵 初始化的监听
     */
    private AuthenticationListener authenticationListener = new AuthenticationListener() {
        @Override
        public void onSuccess() {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    TTSManager.getInstance().init(mContext.getApplicationContext(), new TTSInitListener() {
                        @Override
                        public void onSuccess() {
                            Logger.e(TAG, "TTS init success");
                            My4GBase.getInstance().getAll(httpClientListener,itsCallback,mContext);
                            initQM();
                            bindPhoneNumber() ;  //上传通讯录与APP列表
                            isFirstInit = false ;
                        }

                        @Override
                        public void onFailed(int i, String s) {
                            Logger.e(TAG, "TTS init failed errorCode=" + i + "   errorMsg=" + s);
                        }
                    });

                }
            });
        }
        @Override
        public void onError(final int errorCode, final String s) {
            Logger.e(TAG, "init errorCode=" + errorCode + "   errorMsg=" + s);
            MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.initsdk_fail);

        }
    };


    // 奇梦语音识别 处理识别结果
    private void processAsr(String content) {
        Log.d(TAG, "识别结果==>：" + content);
        try {
            Gson gson = new Gson();
            QASREntity mQASREntity = gson.fromJson(content, QASREntity.class);
            String discernASR = mQASREntity.getRec().replaceAll("\\s*", "");
            Log.d(TAG, "识别结果 discernASR=======>: " + discernASR);
            My4GBase.getInstance().doPost(discernASR);
        }catch (Exception e){
            Log.d(TAG, "识别结果 错误: " + e.getMessage());
        }

    }

    /*
    上传通讯录与APP列表
     */
    private void bindPhoneNumber(){

        AppAndContactsBean appAndContactsBean = new AppAndContactsBean();
        Map<String, String> appMap = new HashMap<>();
//设备中的app名称与app包名放入Map中
        appMap.put("日历", "com.android.calendar");
        appMap.put("微信", "com.android.wechat");
        Map<String, String> contactMap = new HashMap<>();
//设备中的通讯录中联系人名称与电话放入Map中
        contactMap.put("大儿子", "0935108800");
        contactMap.put("小女儿", "0919248249");
        contactMap.put("小明", "13143407320");
        contactMap.put("喜羊羊", "13760316650");
        appAndContactsBean.setAppsMap(appMap);
        appAndContactsBean.setContactMap(contactMap);
        SemanticManager.getInstance().uploadAppsAndContacts(appAndContactsBean, new OnHttpRequestListener() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, "uploadAppsAndContacts.onSuccess.result:" + result);
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "uploadAppsAndContacts.onError.code:" + code + ";msg:" + msg);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /*
   奇梦
    */
    private void initQM(){
        // 识别引擎消息处理handler
        asrHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case QEngine.QENGINE_ASR_DATA:// 识别结果回调
                        if (msg.obj != null) {
                            byte[] data1 = (byte[]) msg.obj;
                            String result = new String(data1);
                            setAsrFlag(false);
                            processAsr(result);
                        }
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        qSession = new QSession(getApplicationContext());
        asrEngine = new QEngine();

        /*初始化全局回话*/
        initSession = qSession.initSession("fe3e6e26-e924-11e7-b526-00163e13c8a2", "925c0f1f-e505-30e3-ace7-159eca74a830");
        Logger.d(TAG, "session" + initSession);

        /*设置全局回话错误码回调*/
        qSession.setQSessionCallback(new QSession.QSessionCallBack() {

            @Override
            public void errorCode(String errorCode) {

                Logger.d(TAG, "error coed:" + errorCode);

            }
        });

        /* 获取阵列算法引擎实例 */
        bfio = BfioHelper.getInstance(getApplicationContext());

        // 初始化引擎
        bfio.init(initSession, mResBfio);

        /* 设置回调函数 */
        bfio.setListener(bfioListener);

        boolean init = asrEngine.init(initSession,
                "role=asr;cfg=" + mPath + "qvoice/xasr/cfg;use_bin=0;use_json=1;min_conf=1.9;asr_min_time=500;", asrHandler);// 初始化识别引擎
        // boolean init
        // =asrEngine.init(session,"role=asr;cfg=/sdcard/qvoice/xasr/cfg;use_bin=0;use_json=1;min_conf=1.9;",asrHandler);
        Logger.d(TAG, "asrEngine.init:" + init);

        boolean start_bfio = bfio.start(true);
        Logger.d(TAG, "start_bfio:" + start_bfio);

        if( start_bfio ) {
            TTSManager.getInstance().startTTS("好开心见到你,想跟我玩就说小智小智", itsCallback);
        }
    }

    /*
    网络请求返还的结果 音乐 和 故事 才会返回
     */
    private OnHttpRequestListener httpClientListener = new OnHttpRequestListener() {

        @Override
        public void onSuccess(final String s) {
            if (!TextUtils.isEmpty(s)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        processSequence(s);
                    }
                });
            }
        }

        @Override
        public void onError(int i, String s) {

        }

        @Override
        public void onCancel() {
            Logger.d(Logger.TAG, "httpClientListener|onCancel");
        }
    };

    /*
    tts监听 除了唱歌和讲故事
     */
    private TTSListener itsCallback = new TTSListener() {

        @Override
        public void onSpeakBegin(String s) {
            Logger.d(TAG,"itsCallback s==========="+s);
            ttsCallbackState = "speakBegin";
            if(ConstantsUtil.DORMANCY.equals(s)){
                ttsCallbackState = "dormancy";
            }
        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakCompleted() {

            Logger.d(TAG,"itsCallback onSpeakCompleted===========");
            //讲故事或者听歌
            if (behavior != null ) {
                int code = behavior.getIntent().getCode() ;
                if(code == FunctionConstants.SONG_CODE || code == FunctionConstants.STORY_CODE){
                    checkMusic(behavior.getIntent());
                    return ;
                }
            }

            //聊天
            if ("speakBegin".equals(ttsCallbackState)) {
                setAsrFlag(true);
                MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.speech_end_xiu);
            }else if("dormancy".equals(ttsCallbackState)){  //休眠
                close();
            }
        }

        @Override
        public void onSpeakFailed() {
            setAsrFlag(true);
        }
    };

    // 奇梦 bfio回调
    BfioHelper.QdreamerBfioListener bfioListener = new BfioHelper.QdreamerBfioListener() {

        @Override
        public void getMicData(byte[] bytes) {

        }

        @Override
        public void wakeUP() {
            Logger.d(TAG, "===wakeUP 成功唤醒" );
            exitLogic() ;
            checkRecordTimeout ();
        }

        @Override
        public void onSpeechStart() {
//            Logger.d(TAG, "onSpeechStart=======isAsrFlag："+isAsrFlag);
            if(isAsrFlag){
                asrEngine.start();
            }
        }

        @Override
        public void onSpeechEnd() {
//            Logger.d(TAG, "onSpeechEnd==========isAsrFlag："+isAsrFlag);
            if(isAsrFlag) {
                asrEngine.feed(aecData, QEngine.FeedType.QENGINE_FEED_END);//
                asrEngine.reset();// 重置识别引擎
            }
        }

        @Override
        public void getVolume(int volume) {
            // Logger.d(TAG, "volume:" + volume);

        }

        @Override
        public void getData(byte[] data) {
            //算法处理后的数据，可以送到识别引擎里
//             Logger.d(TAG, "getData bfio data:" + data.length);
//             saveWavData(data);
            aecData = data;
            if (isAsrFlag) {
                asrEngine.feed( aecData , QEngine.FeedType.QENGINE_FEED_DATA);
            }
        }

        @Override
        public void cancelAsr() {

//            Logger.d(TAG, "cancelEngine:");
            // 取消识别唤醒词
            asrEngine.cancelEngine();

        }

        @Override
        public void direction(String direction) {
            // TODO Auto-generated method stub
            //声源定位角度，单麦没有
            Logger.d(TAG, "direction:" + direction);
        }

    };

    //打断退出
    public void exitLogic() {

        if( !isFirstInit ){
            TTSManager.getInstance().stopTTS();
            MediaMusicUtil.getInstance().stopPlayMediaUrl();
            setAsrFlag(true);
            isMedia = false ;
            behavior = null ;
            MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.speech_end_xiu);
            Logger.d(TAG, "exitLogic isAsrFlag："+isAsrFlag);
        }

    }

    /*
    休眠，不能识别说话，除打断外
     */
    private void close(){
        Logger.i(TAG,"关闭引擎");
        setAsrFlag(false);
        if(asrEngine != null){
            asrEngine.feed(null, QEngine.FeedType.QENGINE_FEED_END);//
            asrEngine.reset();// 重置识别引擎
        }
        //关掉计时器
        if(timer != null){
            timer.cancel();
            timer = null ;
        }
    }

    /*
    设置可以进行说话识别状态
     */
    private void setAsrFlag(boolean flag){
        if(flag){
            recordTime = System.currentTimeMillis() ;
        }
        setGifImageView(flag);
        isAsrFlag = flag ;
    }

    /*
    2分钟不说话 进入休眠状态
     */
    private Timer timer ;
    private long recordTime = 0 ;
    private void checkRecordTimeout (){
        if( timer == null ){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Logger.d(TAG,"checkRecordTimeout isAsrFlag:"+isAsrFlag);
                    if(isAsrFlag){
                        if(System.currentTimeMillis() - recordTime > 120*1000){  //两分钟不说话 就进入休眠
                            close();
                        }
                    }
                }
            }, 500, 10000);
        }
    }


    private void processSequence(final String content) {
        Logger.d(TAG,"processSequence content:"+content);
        if(telState != 0){
            Logger.d(TAG,"processSequence 来电中。。。");
            return ;
        }

        behavior = OSDataTransformUtil.getBehavior(content);
        if(behavior == null ){
            Logger.d(TAG,"网络获取数据失败了。。。。");
            TTSManager.getInstance().startTTS("不好意思，这个我还不会",itsCallback);
            return ;
        }
        try {
            JSONObject jsonObject = new JSONObject(behavior.getIntent().getParameters().toString());
            String mediaUrl = jsonObject.getString("url");
            if(TextUtils.isEmpty(mediaUrl)){
                behavior = null ;
                TTSManager.getInstance().startTTS("不好意思，这个我还不会呢" , itsCallback);
            }else{
                String textValue = behavior.getResults().get(0).getValues().getText();
                Logger.d(TAG,"textValue:"+ textValue);
                TTSManager.getInstance().startTTS(textValue , itsCallback);
            }
        }catch (Exception e){
            Logger.d(TAG,"processSequence e:"+e.getMessage());
            behavior = null ;
            TTSManager.getInstance().startTTS("不好意思，这个我还不会呢",itsCallback);
        }
    }

    private void checkMusic(final Behavior.IntentInfo bean) {


        if ( bean == null ) {
            Logger.d(TAG, "checkMusic bean is null");
            return;
        }
        JsonObject parameters = bean.getParameters();
        Logger.d(TAG, "checkMusic parameters：" + parameters);
        try {
            JSONObject jsonObject = new JSONObject(parameters.toString());
            String mediaUrl = jsonObject.getString("url");
            Log.d(TAG, "checkMusic mediaUrl："+mediaUrl);
            if (TextUtils.isEmpty(mediaUrl)) {
                Log.d(TAG, "checkMusic url为空");
                behavior = null ;
                TTSManager.getInstance().startTTS("糟糕，这个我还不会，请换一个吧",itsCallback);
                return;
            }
            startMusic(mediaUrl);

        } catch (JSONException e) {
            Log.e(TAG, " parse exception");
            e.printStackTrace();
        }
    }

    /*
    播放音乐
     */
    public void startMusic(String mediaUrl) {
        try {
            mediaUrl = URLDecoder.decode(mediaUrl, "UTF-8");
            isMedia = true ;
            MediaMusicUtil.getInstance().startPlayMediaUrl(mediaUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*
    来电监听
     */
    private void initPhoneListner(){
        TelephonyManager telManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        TelListner listener = new TelListner();
        telManager.listen( listener , PhoneStateListener.LISTEN_CALL_STATE);
    }

    private int telState = 0 ; //电话状态值  0电话无状态  与My4GBase里面的telState值同步
    private class TelListner extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Log.i(TAG,"tel state=============="+state);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: /* 挂断电话 无任何状态 */
                    if(telState != TelephonyManager.CALL_STATE_IDLE){
                        stopBfio ();
                        AudioManagerUtil.setHangUpMode(mContext);
                    }
                    telState = TelephonyManager.CALL_STATE_IDLE ;
                    My4GBase.getInstance().setTelState(telState);

//                    MediaUtil.setVolumeMedium();

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:/* 电话接通中 */
                    telState = TelephonyManager.CALL_STATE_OFFHOOK ;
                    My4GBase.getInstance().setTelState(telState);
                    resetBfio ();
                    AudioManagerUtil.setHandsFreeMode(mContext);  //开启免提
                    break;
                case TelephonyManager.CALL_STATE_RINGING:/* 来电中，未接通 */
                    telState = TelephonyManager.CALL_STATE_RINGING ;
                    My4GBase.getInstance().setTelState(telState);
                    exitLogic();
//                    MediaUtil.setVolume();
                    break;
            }

        }
    }


    /*
    发送广播
     */
    public void sendBroad(String action){
        Intent mIntent = new Intent();
        mIntent.setAction(action);
        sendBroadcast(mIntent);
    }

    //注册广播接收器
    private static final String LONGPRESS_ACTION = "com.auric.intell.xld.os.back.longpress";
    private static final String KEYDOWN_ACTION = "com.auric.intell.xld.os.back.keydown";
    private static final String KEYUP_ACTION = "com.auric.intell.xld.os.back.keyup";
    /**
     注册广播接收器  电量和wifi监听
     */
    private void regBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.phone.NotificationMgr.MissedCall_intent");  //未接来电
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(LONGPRESS_ACTION);//长按事件
        filter.addAction(KEYDOWN_ACTION);//按下事件
        filter.addAction(KEYUP_ACTION);//抬起事件
        registerReceiver(mtouchReceiver, filter);
    }

    private long downTime = 0 ;
    /**
     * 广播接收器  电量和wifi监听
     */
    private BroadcastReceiver mtouchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.i(TAG,"CONNECTIVITY_ACTION action:" + action);
            //网络变化
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                Logger.i(TAG, "CONNECTIVITY_ACTION  NetUtil.isNetworkAvailable(mcontext):" + MyNetUtil.isNetworkAvailable(mContext));

                if (MyNetUtil.isNetworkAvailable(mContext)) {//有网络
                    sendBroad("android.intent.action.phone.openGprs");
                    MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.net_connected);
                    initTuing();
                } else {//无网络
                    MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.net_off_hint);
                }

            //电量变化
            }else if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
                int powerValue = intent.getIntExtra("level", 0);
                My4GBase.getInstance().setPowerValue(powerValue);
                Logger.d(TAG,"CONNECTIVITY_ACTION mPowerValue:"+powerValue);

            //长按配网
            }else if(LONGPRESS_ACTION.equals(action)){
                Logger.i(TAG,"------  LONGPRESS_ACTION ");
                sendBroad("android.intent.action.phone.closeGprs");
                ConfigureNetworkUtil.connNetAP(mContext);

             //按下事件
            }else if(KEYDOWN_ACTION.equals(action)){
                if(System.currentTimeMillis() - downTime > 3000){  //防止重复按下
                    downTime = System.currentTimeMillis() ;
                    exitLogic();
                    Logger.i(TAG,"------  KEYDOWN_ACTION telState："+telState);
                    if( telState == TelephonyManager.CALL_STATE_OFFHOOK){/* 接起电话  2*/
                        setAsrFlag(false);
                        sendBroad("android.intent.action.phone.endcall");
                    }else if( telState == TelephonyManager.CALL_STATE_RINGING){//电话进来 1
                        setAsrFlag(false);
                        sendBroad("android.intent.action.phone.answercall");
                    }
                }
            //抬起事件
            }else if(KEYUP_ACTION.equals(action)){

            }else if("com.android.phone.NotificationMgr.MissedCall_intent".equals(action)){  //未接来电
                int mMissCallCount = intent.getExtras().getInt("MissedCallNumber");
                Logger.i(TAG,"phone mMissCallCount："+mMissCallCount);
                Logger.i(TAG,"phone intent.getExtras()："+intent.getExtras().toString());
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.d(TAG,"onKeyDown keycode:"+keyCode);
        if(keyCode == 25 || keyCode == 24){  //24加  25减 音量
            MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.speech_end_xiu);
        }
        //14 15 还未知，暂时做了接电话挂电话
        if(keyCode == 15 || keyCode == 14){
            if(System.currentTimeMillis() - downTime > 3000){  //防止重复按下
                downTime = System.currentTimeMillis() ;
                exitLogic();
                Logger.i(TAG,"------  KEYDOWN_ACTION telState："+telState);
                if( telState == TelephonyManager.CALL_STATE_OFFHOOK){/* 接起电话  2*/
                    setAsrFlag(false);
                    sendBroad("android.intent.action.phone.endcall");
                }else if( telState == TelephonyManager.CALL_STATE_RINGING){//电话进来 1
                    setAsrFlag(false);
                    sendBroad("android.intent.action.phone.answercall");
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {

        unregisterReceiver(mtouchReceiver);

        //关掉计时器
        if(timer != null){
            timer.cancel();
            timer = null ;
        }

        if(bfio != null ){
            bfio.stop() ;
        }

        if (TTSManager.getInstance().isSpeaking()) {
            TTSManager.getInstance().stopTTS();
        }

        MediaMusicUtil.getInstance().releasePlayMedia();

        try {
            MQTTManager.getInstance().disconnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


//===========================================================
//改变录音的识别

    private boolean mRunning;
    private void resetBfio(){
        if(bfio == null){
            return;
        }
        bfio.stop();
        bfio.start(false);
        mRunning = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                run_recorder();
            }
        }).start();
    }

    void stopBfio (){
        mRunning = false ;
        boolean flag2 = bfio.stop();
        boolean flag = bfio.start(true);
        Logger.e(TAG, "stop==================flag:"+flag+ "    flag2 "+flag2);
    }

    public void run_recorder() {

        Logger.i(TAG,"run_recorder 开始录音");
        //16K采集率
        int frequency = 16000;
        //格式
        int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
        //16Bit
        //生成PCM文件
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        try {

//            File file = new File("/mnt/sdcard" + "/mic_new.pcm");
//            File file2 = new File("/mnt/sdcard" + "/mic_new2.pcm");
//            Logger.i(TAG,"生成文件"+file.getPath());
//            Logger.i(TAG,"生成文件2"+file2.getPath());
//            //如果存在，就先删除再创建
//            if (file.exists())
//                file.delete();
//            if (file2.exists())
//                file2.delete();
//            Logger.i(TAG,"删除文件");
//            try {
//                file.createNewFile();
//                Logger.i(TAG,"创建文件");
//            } catch (IOException e) {
//                Logger.i(TAG,"未能创建");
//                throw new IllegalStateException("未能创建" + file.toString());
//            }
//            try {
//                file2.createNewFile();
//                Logger.i(TAG,"创建文件2");
//            } catch (IOException e) {
//                Logger.i(TAG,"未能创建2");
//                throw new IllegalStateException("未能创建2" + file.toString());
//            }
//            OutputStream os = new FileOutputStream(file);
//            BufferedOutputStream bos = new BufferedOutputStream(os);
//            DataOutputStream dos = new DataOutputStream(bos);
//
//            OutputStream os2 = new FileOutputStream(file2);
//            BufferedOutputStream bos2 = new BufferedOutputStream(os2);
//            DataOutputStream dos2 = new DataOutputStream(bos2);

            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            byte [] buffer = new byte[bufferSize];
            audioRecord.startRecording();
            Logger.i(TAG,"run_recorder 开始录音2222222222");

            while (mRunning) {
                int bufferReadResult =  audioRecord.read(buffer, 0, bufferSize);
                byte [] temp = new byte[bufferReadResult] ;


                for (int i = 0; i < bufferReadResult-3; i=i+4) {
                    temp[i] = buffer[i+2];
                    temp[i+1] = buffer[i+3];
                    temp[i+2] = buffer[i];
                    temp[i+3] = buffer[i+1];
                }

                if (temp != null) {
                    boolean var6 =  bfio.feedData(temp);
//                        Log.i(TAG, "var6==============="+var6);
                }

//                dos.write(temp);
//                dos2.write(buffer);
            }
//            dos.close();
//            dos2.close();
            audioRecord.stop();
            Log.i(TAG, "停止录音");
        } catch (Throwable t) {
            Log.e(TAG, "录音失败");
        }
    }

}
