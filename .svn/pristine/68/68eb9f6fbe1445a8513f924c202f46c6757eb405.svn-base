package com.turing.turingsdksample.util;


import com.google.gson.Gson;
import com.turing.semantic.entity.Behavior;
import com.turing.semantic.entity.Behaviors;
import com.turing.semantic.entity.UploadDeviceInfoResponse;


/**
 * Http请求数据转换类
 *
 * @author licheng@uzoo.cn
 */

public class OSDataTransformUtil {

    private final static String TAG = OSDataTransformUtil.class.getSimpleName();

    public static Behavior getBehavior(String json) {
        Gson gson = new Gson();
        Behaviors behaviors = null;
        behaviors = gson.fromJson(json, Behaviors.class);
        Behavior mData = null ;
        if( behaviors != null ){
            mData = behaviors.getBehaviors().get(0);
        }
        return mData;
    }

    /**
     * 获得intent
     *
     * @param json json
     * @return OSIntentBean
     **/
    public static Behavior.IntentInfo getIntent(String json) {
        Behavior.IntentInfo intentInfo = null;
        Behavior behavior = getBehavior(json);
        if(behavior != null){
            intentInfo = behavior.getIntent();
        }
        return intentInfo;
    }


    /**
     * 获得result的结果
     *
     * @param json string
     * @return OSResultsBean
     **/
    public static Behavior.ResponseResult getResultBean(String json) {
        Behavior.ResponseResult responseResult = null;
        try {
            Behavior behavior = getBehavior(json);
            if (behavior != null && behavior.getResults() != null && !behavior.getResults().isEmpty()) {
                responseResult = behavior.getResults().get(0);
            }
        } catch (Exception e) {
            Logger.d(TAG, "OSDataTransformUtil()>>getResultBean()>>Exception:" + e.getMessage());
        }
        return responseResult;

    }

    /**
     * 获得emotion的结果
     *
     * @param json json
     * @return OsEmotion
     **/
    public static Behavior.Emotion getOsEmtion(String json) {
        Behavior.Emotion emotion = null;
        try {
            Behavior behavior = getBehavior(json);
            emotion = behavior.getEmotion();
        } catch (Exception e) {
            Logger.d(TAG, "OSDataTransformUtil()>>getOsEmtion()>>Exception:" + e.getMessage());
        }
        return emotion;

    }

    /**
     * 获取上传通讯录与APP列表的结果
     *
     * @param json
     * @return
     */
    public static UploadDeviceInfoResponse getUploadDeviceInfoResponse(String json) {
        Gson gson = new Gson();
        UploadDeviceInfoResponse uploadDeviceInfoResponse = null;
        uploadDeviceInfoResponse = gson.fromJson(json, UploadDeviceInfoResponse.class);
        return uploadDeviceInfoResponse;
    }
}
