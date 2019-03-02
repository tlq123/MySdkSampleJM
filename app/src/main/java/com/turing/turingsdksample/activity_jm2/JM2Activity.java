package com.turing.turingsdksample.activity_jm2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.qdreamer.qvoice.BfioHelper;
import com.qdreamer.qvoice.QEngine;
import com.qdreamer.qvoice.QSession;
import com.turing.authority.authentication.AuthenticationListener;
import com.turing.authority.authentication.SdkInitializer;
import com.turing.iot.MQTTManager;
import com.turing.semantic.entity.Behavior;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.tts.TTSInitListener;
import com.turing.tts.TTSListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.alarm.Alarm;
import com.turing.turingsdksample.alarm.AlarmManagerUtil;
import com.turing.turingsdksample.alarm.Database;
import com.turing.turingsdksample.bean.QASREntity;
import com.turing.turingsdksample.constants.ConstantsUtil;
import com.turing.turingsdksample.constants.FunctionConstants;
import com.turing.turingsdksample.demo_common.ConfigureNetworkUtil;
import com.turing.turingsdksample.demo_common.MQTTBindManager;
import com.turing.turingsdksample.demo_common.MQTTReceiveInterface;
import com.turing.turingsdksample.demo_common.MediaMusicUtil;
import com.turing.turingsdksample.demo_common.MediaRecoderUtil;
import com.turing.turingsdksample.demo_common.MqttInfo;
import com.turing.turingsdksample.util.CopyResUtil;
import com.turing.turingsdksample.util.Logger;
import com.turing.turingsdksample.util.MyNetUtil;
import com.turing.turingsdksample.util.OSDataTransformUtil;
import com.turing.turingsdksample.util.ThreadPoolManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/*
京密项目 双核的cpu
 */
public class JM2Activity extends Activity implements MQTTReceiveInterface {

    private String TAG = "JM2Activity";
    /*第一次初始化*/
    private static boolean isFirstInit = false ;
    private long initSDKTime = 0;
    private Context mContext ;
    private boolean  isAsrFlag ;
    private boolean isWakeUpExit = true;  //判断是唤醒退出、返回键退出；或者是点播退出
    private boolean isRecorder = false ;  //判断是否正在录音
    private boolean isMusic = false ;     //判断是否正在唱歌或者讲故事
    private boolean isQSessionError = false ;  //奇梦返回的错误标识
    public static   boolean isAPFlag ;  // ap配网，进入后，屏蔽些提示
    public static final String ALARM_SHOW = "com.alarm.clock.show";
    private boolean isNetFlag ;  //当前是否联网
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
    private TextView tvCount ;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Activity上面的状态栏
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_demo);

        Logger.d(TAG,"onCreate...........................");
        initView();

        init();
    }

    private void initView (){

        tvCount = (TextView) findViewById(R.id.tv_count);
        gifImageView = (GifImageView) findViewById(R.id.gif_imageview);
        try {
            GifDrawable gifDrawable = new GifDrawable(getResources(), R.drawable.change_dizzy);
            gifImageView.setBackgroundDrawable(gifDrawable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int gifType = 0 ;
    private void setGifImageView(final int type){
        if(gifType == type){
            return ;
        }
        gifType = type ;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    GifDrawable gifDrawable ;
                    switch (type){
                        case 0:  //默认状态
                            gifDrawable = new GifDrawable(getResources(), R.drawable.change_dizzy);
                            gifImageView.setBackgroundDrawable(gifDrawable);
                            break ;
                        case 1:  //打断说话状态
                            gifDrawable = new GifDrawable(getResources(), R.drawable.recording);
                            gifImageView.setBackgroundDrawable(gifDrawable);
                            break;
                        case  2:  //休眠状态
                            gifDrawable = new GifDrawable(getResources(), R.drawable.wake);
                            gifImageView.setBackgroundDrawable(gifDrawable);
                        break;
                        default:
                            gifDrawable = new GifDrawable(getResources(), R.drawable.change_dizzy);
                            gifImageView.setBackgroundDrawable(gifDrawable);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void init(){

        isNetFlag = false ;
        isAPFlag = false ;
        isQSessionError = false ;
        mContext = this ;
        isFirstInit = true ;
        isRecorder = false ;
        mPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        mResBfio = "role=bfio;cfg=" + mPath + "qvoice/bfio1.xzxz.20190112.bin;use_oneMic=0;";
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



        //音频初始化以及播放状态
        MediaMusicUtil.getInstance().initMusic(this, new MediaMusicUtil.ImusicListener() {
            @Override
            public void OnMusicCompletion() {
                Logger.d(TAG,"OnMusicCompletion。。。isAsrFlag："+isAsrFlag+ "  mediaType:"+mediaType);
                isMusic = false ;
                if(isAPFlag){
                    Logger.d(TAG,"OnMusicCompletion。。。配网中。。。。");
                    return;
                }

                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        if( MyNetUtil.isNetworkAvailable(mContext) ){
                            if( 1 == mediaType ){
                                startMusic( lastUrl , 2 );
                            }else if( 2 == mediaType ){
                                curPosition = 0;
                                //播完接着请求播下一首
                                MQTTBindManager.httpChangeSong("next", mContext);
                            }else if( 3 == mediaType ){
                                if( !isAsrFlag ){
                                    JM2Base.getInstance().doPost("下一个");
                                }
                            }
                        }else {
                            MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.net_off_hint);
                        }
                    }
                });
            }

            @Override
            public void OnMusicError() {
                Logger.d(TAG,"OnMusicError。。。");
                isMusic = false ;
                behavior = null ;
                TTSManager.getInstance().startTTS("网络缓存失败",itsCallback);
            }

            @Override
            public void OnMusicStop() {
                isMusic = false ;
                Logger.d(TAG, "OnMusicStop  mMqttInfo:"+mMqttInfo);
                //语音打断，才上报状态
                //上报设备暂停状态
                if(mMqttInfo != null && mediaType != 3 && isWakeUpExit ){   //3不是点播类型的媒体
                    int play = 2;// 播放状态 1-play 2-pause
                    MQTTBindManager.httpReportDeviceInfo( mMqttInfo.getMessage().arg , mMqttInfo.getMessage().mediaId , play , mContext);
                }
            }

            @Override
            public void OnMusicPause() {
                Logger.d(TAG,"OnMusicPause。。。");
            }
        });
        //开机提示语
        MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.happy_see_you);

        ConfigureNetworkUtil.toggleWiFi(this);  //开启wifi

        regBroadcast();//wifi 电量 广播

        CopyResUtil.copyRes(mContext,mPath);

        ConfigureNetworkUtil.playConnNetPromote(this); //网络提示语
        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if( MyNetUtil.isNetworkAvailable(mContext) ){
                    initTuing();
                }
            }
        });

        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                initDatabase();
            }
        });
    }
    /*
    初始化闹钟
     */
    private void initDatabase(){
        Database.init(mContext);  //初始化闹钟的数据库
        List<Alarm> list = Database.getAll();
        for(Alarm alarm : list){
            String times[] = alarm.getAlarmTimeString().split(":");
            Logger.e(TAG,"闹钟ID："+alarm.getId()+"  时间："+alarm.getAlarmTimeString());
            AlarmManagerUtil.setAlarm(mContext, 1, Integer.parseInt(times[0]), Integer.parseInt
                    (times[1]), alarm.getId(), 0, "闹钟响了", 1);
        }
    }

    public  String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Logger.e(TAG,"e:"+ ex.getMessage());
        }
        return "00:00:00:00:00:00";
    }

    /*
    图灵
     */
    private void initTuing(){
        if ( !isFirstInit ){
            Logger.d(TAG,"已经初始化过了");
            //点播
            MQTTBindManager.bindDevice(mContext);
            MQTTBindManager.init(mContext);
            return ;
        }
//        String macAdd = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
        String macAdd = getMacAddr();
        Logger.e(TAG, "macAdd:" + macAdd);
        if (macAdd != null && macAdd.startsWith("00:")) {
            MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.write_mac_first);
            return;
        }

        if (System.currentTimeMillis() - initSDKTime > 5 * 1000) {//防止重复调用
            initSDKTime = System.currentTimeMillis();
            if(isFirstInit){
                SdkInitializer.init(mContext,  ConstantsUtil.API_KEY, ConstantsUtil.API_SECRET, authenticationListener);
            }
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

                    if(isFirstInit){

                        TTSManager.getInstance().init(mContext.getApplicationContext(), new TTSInitListener() {
                            @Override
                            public void onSuccess() {
                                Logger.e(TAG, "TTS init success");
                                JM2Base.getInstance().getAll(httpClientListener,mEmotionListener,itsCallback,mContext);
                                initQM();
//                                点播
                                MQTTBindManager.setOnMQTTReceiveListener(JM2Activity.this);
                                MQTTBindManager.bindDevice(mContext);
                                MQTTBindManager.init(mContext);
                                isFirstInit = false ;
                            }

                            @Override
                            public void onFailed(int i, String s) {
                                Logger.e(TAG, "TTS init failed errorCode=" + i + "   errorMsg=" + s);
                            }
                        });

                    }

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
            setAsrFlag(false);
            Gson gson = new Gson();
            QASREntity mQASREntity = gson.fromJson(content, QASREntity.class);
            String discernASR = mQASREntity.getRec().replaceAll("\\s*", "");
            Log.d(TAG, "识别结果 discernASR=======>: " + discernASR);
            if(!TextUtils.isEmpty(discernASR)){
                JM2Base.getInstance().doPost(discernASR);
                setGifImageView(0);
            }

        }catch (Exception e){
            Log.d(TAG, "识别结果 出错了 "+e.getMessage() );
        }
    }

    /*
   奇梦
    */
    private void initQM(){


        qSession = new QSession(getApplicationContext());
        asrEngine = new QEngine();

        /*初始化全局回话*/
        initSession = qSession.initSession("fe3e6e26-e924-11e7-b526-00163e13c8a2", "925c0f1f-e505-30e3-ace7-159eca74a830");
        Logger.d(TAG, "session" + initSession);

        /*设置全局回话错误码回调*/
        qSession.setQSessionCallback(new QSession.QSessionCallBack() {

            @Override
            public void errorCode(String errorCode) {

                Logger.d(TAG, "error coed:" + errorCode+ "  isQSessionError:"+isQSessionError+"  isMusic:"+isMusic);

                if(isQSessionError){
                    return ;
                }
                if( isMusic || isAPFlag){
                    isQSessionError = true ;
                    return ;
                }
                isQSessionError = true ;
                MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.qm_net_error);
                if(errorCode.contains("11204")) {
                    bfio.stop();
                    boolean flag = bfio.start(true);
                    Logger.e(TAG,"11204 错误从启 flag ："+flag );
                }
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

    }

    /*
    网络请求返还的结果
     */
    private OnHttpRequestListener httpClientListener = new OnHttpRequestListener() {

        @Override
        public void onSuccess(final String s) {
            mediaType = 3 ;
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
            mediaType = 3 ;
            TTSManager.getInstance().startTTS("网络不好，请检查下网络", itsCallback);
        }

        @Override
        public void onCancel() {
            Logger.d(Logger.TAG, "httpClientListener|onCancel");
        }
    };

    /*
    表情回调
     */
    private OnEmotionListener mEmotionListener = new OnEmotionListener() {
        @Override
        public void OnEmotion(int emotion) {
            setGifImageView(emotion);
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
            //歌曲 或者 故事
            if (behavior != null ) {
                int code = behavior.getIntent().getCode() ;
                if(code == FunctionConstants.STORY_CODE || code == FunctionConstants.SONG_CODE){
                    checkMusic(behavior.getIntent());
                    return ;
                }
            }
            //聊天
            if ("speakBegin".equals(ttsCallbackState)) {
                MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.speech_end_xiu);
                setAsrFlag(true);
                setGifImageView(1);
//                AsrManager.getInstance().startAsr(asrListener);
             //休眠
            }else if("dormancy".equals(ttsCallbackState)){
                close();
            }

        }

        @Override
        public void onSpeakFailed() {
            setAsrFlag(true);
        }
    };


    /*
    保存录音文件
     */
    public  void writeBytesToFile(byte[] bs) {
        try {
            File file = new File("/sdcard/qmVoice.pcm");
            if(!file.exists()){
                file.createNewFile();
            }
            ByteBuffer bb = ByteBuffer.wrap(bs);
//            FileChannel fc = new FileOutputStream(file).getChannel();
            FileChannel fc = new FileOutputStream(file,true).getChannel(); //叠加的录音
            fc.write(bb);
            fc.close();
            Logger.d(TAG, "录音中保存成功:"+file.getPath() );
        } catch (Exception e) {
            e.printStackTrace();
            Logger.d(TAG, "录音中保存失败:"+e.getMessage() );
        }

    }

    // 奇梦 bfio回调
    BfioHelper.QdreamerBfioListener bfioListener = new BfioHelper.QdreamerBfioListener() {

        @Override
        public void getMicData(byte[] bytes) {
//            writeBytesToFile(bytes);
        }

        @Override
        public void wakeUP() {

            if(isRecorder){
                Logger.d(TAG, "录音中===wakeUP 不能唤醒" );
                return ;
            }
            Logger.d(TAG, "===wakeUP 成功唤醒" );
            isWakeUpExit = true ;

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
            if (isAsrFlag) {
                aecData = data;
                asrEngine.feed(data, QEngine.FeedType.QENGINE_FEED_DATA);
            }
        }

        @Override
        public void cancelAsr() {

            Logger.d(TAG, "cancelEngine:");
            // 取消识别唤醒词
            asrEngine.cancelEngine();

        }

        @Override
        public void direction(String direction) {
            // TODO Auto-generated method stub
            //声源定位角度，单麦没有
//            Logger.d(TAG, "direction:" + direction);
        }
    };




    //打断退出
    public void exitLogic() {

        isMusic = false ;
        behavior = null ;
        if(!isFirstInit){
            TTSManager.getInstance().stopTTS();
        }
        MediaMusicUtil.getInstance().stopPlayMediaUrl();
        MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.speech_end_xiu);
        setAsrFlag(true);
        isQSessionError = false ;  //奇梦错误标识
        Logger.d(TAG, "exitLogic isAsrFlag："+isAsrFlag);
        setGifImageView(1);
    }

    //打断退出 点播
    public void exitLogic2() {

        isMusic = false ;
        behavior = null ;
        if(!isFirstInit){
            TTSManager.getInstance().stopTTS();
        }
        MediaMusicUtil.getInstance().stopPlayMediaUrl();
        Logger.d(TAG, "exitLogic 2：");
        setGifImageView(1);
    }

    //闹钟提示
    public void alarmShow() {
        isMusic = false ;
        behavior = null ;
        setAsrFlag(false);
        if(!isFirstInit){
            TTSManager.getInstance().stopTTS();
        }
        MediaMusicUtil.getInstance().stopPlayMediaUrl();
        MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.alarm_tts);
        Logger.d(TAG, "闹钟提示开始 播放闹钟提示音");
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
        setGifImageView(2);
    }

    /*
    设置可以进行说话识别状态
     */
    private void setAsrFlag(boolean flag){
        if(flag){
            recordTime = System.currentTimeMillis() ;
        }
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


    /*
         检查url是否存在，然后进行提示
      */
    private void processSequence(final String content) {
        Logger.d(TAG,"processSequence content:"+content);
        behavior = OSDataTransformUtil.getBehavior(content);
        if(behavior == null ){
            TTSManager.getInstance().startTTS("不好意思，这个我还不会呢",itsCallback);
            Logger.d(TAG,"processSequence 网络获取数据失败:");
            return;
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

    /*
    根据url播放歌曲
     */
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
                TTSManager.getInstance().startTTS("糟糕！网络缓存失败",itsCallback);
                return;
            }
            startMusic(mediaUrl,3);

        } catch (JSONException e) {
            Log.e(TAG, " parse exception");
            e.printStackTrace();
        }
    }


    private  int curPosition;  //点播歌曲的播放进度位置
    private  boolean isLastSong = false;//是否是之前同一首歌曲
    private  String lastUrl = "";
    private int mediaType = 0 ;  //0表示无类型  1表示点播新歌曲 2表示点播暂停播放  3表示奇梦搜索的歌曲
    private MqttInfo mMqttInfo ; //点播歌曲的信息

    /*
    处理点播音频
     */
    private  void dealMusic(MqttInfo mqttInfo){

        if(mqttInfo == null ){
            return ;
        }
        exitLogic2();
        setAsrFlag(false);
        mMqttInfo = mqttInfo ;

        if(mMqttInfo.getMessage().getOperate() == 1){//播放
            isWakeUpExit = false ;
            if(mMqttInfo.getMessage().url.equals(lastUrl)){//暂停之后继续播放
                isLastSong = true;
                startMusic(lastUrl , 2);
            }else{
                lastUrl = mMqttInfo.getMessage().url;
                isLastSong = false;
                startMusic(mMqttInfo.getMessage().tip , 1);
            }
        }else if(mMqttInfo.getMessage().getOperate() == 2){//暂停
            curPosition = MediaMusicUtil.getInstance().getCurPosition();
            MediaMusicUtil.getInstance().stopPlayMediaUrl();
            Logger.i(TAG,"curPosition:"+curPosition);
            return;
        }
    }

    /*
    开始播放音频
     */
    public void startMusic(String mediaUrl , int type) {
        Log.e(TAG, "media url=" + mediaUrl);
        isMusic = true ;
        mediaType = type;
        try {
            mediaUrl = URLDecoder.decode(mediaUrl, "UTF-8");
            if(isLastSong){
                MediaMusicUtil.getInstance().startPlayMediaUrl(mediaUrl ,curPosition);
            }else{
                MediaMusicUtil.getInstance().startPlayMediaUrl(mediaUrl);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        setGifImageView(0);
    }


    /*
    点播回调的内容
     */
    @Override
    public void onMQTTReceive(MqttInfo mqttInfo) {
        Logger.i(TAG,"mqttInfo.getFromUser():"+mqttInfo.getFromUser());
        Logger.i(TAG,"mqttInfo.getMessage().getArg():"+mqttInfo.getMessage().getArg());
        if(isRecorder){
            Logger.d(TAG, "录音中===不能进行点播播放" );
            int play = 2;// 播放状态 1-play 2-pause
            MQTTBindManager.httpReportDeviceInfo( mqttInfo.getMessage().arg , mqttInfo.getMessage().mediaId , play , mContext);
            return ;
        }
        dealMusic(mqttInfo);
        setGifImageView(0);
    }



    /**
     注册广播接收器  电量和wifi监听
     */
    private void regBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(AlarmManagerUtil.ALARM_ACTION);
        registerReceiver(mtouchReceiver, filter);
    }

    /**
     * 广播接收器  电量和wifi监听
     */
    private BroadcastReceiver mtouchReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Logger.i(TAG,"CONNECTIVITY_ACTION action:"+action);
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {//网络变化

                ThreadPoolManager.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean netFlag = MyNetUtil.isNetworkAvailable(mContext) ;
                        Logger.i(TAG, "CONNECTIVITY_ACTION  NetUtil.isNetworkAvailable(mcontext):" +netFlag );
                        if (netFlag) {//有网络
                            isAPFlag = false ;
                            if(!isMusic && !isNetFlag){  //当播放歌曲时，不进行提示，播完后进行提示
                                MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.net_connected);
                            }
                            isNetFlag = true  ;
                            initTuing();
                        } else {//无网络
                            if(!isMusic && !isAPFlag && isNetFlag) {
                                MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.net_off_hint);
                            }
                            isNetFlag = false  ;
                        }
                    }
                });

            }else if(Intent.ACTION_BATTERY_CHANGED.equals(action)){
                int powerValue = intent.getIntExtra("level", 0);
                JM2Base.getInstance().setPowerValue(powerValue);
                Logger.d(TAG,"CONNECTIVITY_ACTION mPowerValue:"+powerValue);
            }else if(AlarmManagerUtil.ALARM_ACTION.equals(action)){  //闹钟提示
                long intervalMillis = intent.getLongExtra("intervalMillis", 0);
                Logger.e(TAG,"intervalMillis:"+intervalMillis);
                if (intervalMillis != 0) {

                    AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
                            intent);
                }
                alarmShow();
            }
        }
    };

    private long downTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.i(TAG, "keyCode:" + keyCode);


/*        单独测试触摸键 出现一直按下的情况 调试使用
        if ( keyCode == 10 || keyCode == 4) {
            MediaMusicUtil.getInstance().startPlayMediaRes2(R.raw.speech_end_xiu);
            return true ;
        }*/

        if ( keyCode == 10 || keyCode == 4) {

            if(isRecorder){
                Logger.d(TAG, "录音中===不能打断，不能配网" );
                return true;
            }
            if (event.getRepeatCount() == 0){
                downTime = System.currentTimeMillis();
            }
            if(System.currentTimeMillis()-downTime > 5*1000){  //进入配网
                downTime = System.currentTimeMillis()  * 2 ; //只是让这个判断不重复进来
                exitLogic2();
                isAPFlag = true ;
                ConfigureNetworkUtil.connNetAP(mContext);
                setGifImageView(0);
            }
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_0){  // 7表示开始录音
            isRecorder = true ;
            exitLogic();
            setAsrFlag(false);
            downTime = System.currentTimeMillis();
            MediaRecoderUtil.getMediaRecoderUtil().startRecoder();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_1){  // 8表示停止录音
            isRecorder = false ;
            downTime = System.currentTimeMillis();
            MediaRecoderUtil.getMediaRecoderUtil().stopRecording();
            MediaMusicUtil.getInstance().startPlayMediaRes(R.raw.speech_end_xiu);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_2){  // 9表示播放录音
            isRecorder = false ;
            downTime = System.currentTimeMillis();
            exitLogic();
            setAsrFlag(false);
            MediaRecoderUtil.getMediaRecoderUtil().playRecord();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_4){  // 11表示播放录音
            downTime = System.currentTimeMillis();
            MediaRecoderUtil.getMediaRecoderUtil().stopPlayRecord();
            return true;
        }
        return true ; //让其他按键无效
//        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(isRecorder){
            Logger.d(TAG, "onKeyUp 录音中===" );
            return true;
        }
        if( keyCode == 10 || keyCode == 4){
            if( (System.currentTimeMillis()-downTime) < 4000  && (System.currentTimeMillis()-downTime)>0 ){
                isWakeUpExit = true ;
                exitLogic();
            }
        }
        return true ; //让其他按键无效
//        return super.onKeyUp(keyCode, event);
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
}
