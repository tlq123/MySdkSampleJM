package com.turing.turingsdksample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.turing.semantic.SemanticManager;
import com.turing.semantic.entity.AppAndContactsBean;
import com.turing.semantic.entity.ClientInfo;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.semantic.util.LogUtil;
import com.turing.turingsdksample.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Semantic语义相关接口API演示
 *
 * @author zuojianfei@uzoo.cn
 */

public class SemanticFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = SemanticFragment.class.getSimpleName();
    private EditText et_timieout;
    private TextView tv_semantic_result;
    private EditText et_semantic_request;
    private EditText et_semantic_robot_id;
    private EditText et_semantic_robot_data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d(TAG, "ONCreate");
        View view = inflater.inflate(R.layout.fragment_semantic, null);
        view.findViewById(R.id.btn_request_semantic).setOnClickListener(this);
        view.findViewById(R.id.btn_request_semantic_clientinfo).setOnClickListener(this);
        view.findViewById(R.id.btn_cancel_semantic).setOnClickListener(this);
        view.findViewById(R.id.btn_upload_info).setOnClickListener(this);
        view.findViewById(R.id.btn_request_first).setOnClickListener(this);
        view.findViewById(R.id.btn_request_auto).setOnClickListener(this);
        view.findViewById(R.id.btn_connect_timeout).setOnClickListener(this);
        view.findViewById(R.id.btn_upload_file).setOnClickListener(this);
        et_timieout = (EditText) view.findViewById(R.id.et_timieout);
        et_semantic_request = (EditText) view.findViewById(R.id.et_semantic_request);
        et_semantic_robot_id = (EditText) view.findViewById(R.id.et_semantic_robot_id);
        et_semantic_robot_data = (EditText) view.findViewById(R.id.et_semantic_robot_data);
        tv_semantic_result = (TextView) view.findViewById(R.id.tv_semantic_result);
        tv_semantic_result.setMovementMethod(new ScrollingMovementMethod());
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_semantic:
                //正常交互语义请求,在回调中返回请求结果
                String requestContent = et_semantic_request.getText().toString();
                SemanticManager.getInstance().requestSemantic(requestContent, new SemanticListener());
                Log.d(TAG, "start requestSemantic");
                break;
            case R.id.btn_request_semantic_clientinfo:
                //正常交互语义请求,在回调中返回请求结果
                String requestContent2 = et_semantic_request.getText().toString();
                String robot_id = et_semantic_robot_id.getText().toString();
                if (TextUtils.isEmpty(robot_id)) {
                    Toast.makeText(getContext(), "请输入技能code", Toast.LENGTH_SHORT).show();
                    return;
                }
                String robot_data = et_semantic_robot_data.getText().toString();
                if (TextUtils.isEmpty(robot_data) || !robot_data.contains(":")) {
                    Toast.makeText(getContext(), "请输入要上传的技能参数", Toast.LENGTH_SHORT).show();
                    return;

                }
                Map<String, Object> dataMap = new HashMap<>();
                String[] datas = robot_data.split(";");
                for (String data : datas
                        ) {
                    String[] values = data.split(":");
                    if (values != null && values.length > 1) {
                        dataMap.put(values[0], values[1]);
                    }

                }
                if(dataMap==null||dataMap.size()<1){
                    Toast.makeText(getContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                    return;
                }
                ClientInfo clientInfo = new ClientInfo();
                HashMap<Integer, Map<String, Object>> userDatas = new HashMap<>();
                userDatas.put(Integer.valueOf(robot_id), dataMap);
                clientInfo.setRobotSkill(userDatas);
                SemanticManager.getInstance().requestSemantic(requestContent2, clientInfo, new SemanticListener());
                break;
            case R.id.btn_upload_info:
                AppAndContactsBean appAndContactsBean = new AppAndContactsBean();
                Map<String, String> appMap = new HashMap<>();
                //设备中的app名称与app包名放入Map中
                appMap.put("日历", "com.android.calendar");
                appMap.put("微信", "com.android.wechat");
                Map<String, String> contactMap = new HashMap<>();
                //设备中的通讯录中联系人名称与电话放入Map中
                contactMap.put("张三", "123456789");
                contactMap.put("李四", "987654321");
                appAndContactsBean.setAppsMap(appMap);
                appAndContactsBean.setContactMap(contactMap);
                //上传通讯录与APP列表,可以设置网络请求回调
                SemanticManager.getInstance().uploadAppsAndContacts(appAndContactsBean, new OnHttpRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "uploadAppsAndContacts.onSuccess.result:" + result);
                        tv_semantic_result.setText(getString(R.string.str_semantic_upload_success) + result);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        Log.e(TAG, "uploadAppsAndContacts.onError.code:" + code + ";msg:" + msg);
                        tv_semantic_result.setText(getString(R.string.str_semantic_upload_error) + +code + "|" + msg);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "uploadAppsAndContacts.onCancel");
                        tv_semantic_result.setText(R.string.str_semantic_upload_cancel);
                    }
                });
                Log.d(TAG, "start uploadAppsAndContacts");
                break;
            case R.id.btn_cancel_semantic:
                //取消正在发送的请求,如果请求被取消,在设置的回调中会触发取消回调
                SemanticManager.getInstance().cancelRequest();
                break;
            case R.id.btn_request_first:
                //请求打招呼接口,一般用于开机或机器人的第一次交互,返回的结果可以在图灵平台进行配置
                SemanticManager.getInstance().requestFirstConversion(new SemanticListener());
                Log.d(TAG, "start requestFirstConversion");
                break;
            case R.id.btn_request_auto:
                //请求主动交互,用于让机器人主动发起交互的场景,返回的结果可以在图灵平台进行配置
                SemanticManager.getInstance().requestAutoConversion(new SemanticListener());
                Log.d(TAG, "start requestAutoConversion");
                break;
            case R.id.btn_connect_timeout:
                int timeout = 1000;
                try {
                    String strTimeout = et_timieout.getText().toString();
                    if (!TextUtils.isEmpty(strTimeout)) {
                        timeout = Integer.parseInt(strTimeout);
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), getString(R.string.str_semantic_timeout_null), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                //设置请求的超时时间,有读超时,连接超时,写超时;用法见开发者文档或API Reference文档
                SemanticManager.getInstance().setReadTimeout(timeout);
                SemanticManager.getInstance().setConnectTimeout(timeout);
                SemanticManager.getInstance().setWriteTimeout(timeout);
                Log.d(TAG, "start setConnectTimeout:" + timeout);
                break;
            default:
                break;
        }
    }

    @Override
    public void exitLogic() {
        Log.d(TAG, "exitLogic");
        SemanticManager.getInstance().cancelRequest();
    }

    /**
     * 演示普通语义、打招呼、主动交互的请求回调
     */
    class SemanticListener implements OnHttpRequestListener {

        @Override
        public void onSuccess(String result) {
            //请求成功,result为返回结果
            Log.d(TAG, "SemanticListener.onSuccess.result:" + result);
            tv_semantic_result.setText(getString(R.string.str_semantic_success) + result);
        }

        @Override
        public void onError(int code, String msg) {
            //请求失败
            Log.e(TAG, "SemanticListener.onError.code:" + code + ";msg:" + msg);
            tv_semantic_result.setText(getString(R.string.str_semantic_error) + code + "|" + msg);
        }

        @Override
        public void onCancel() {
            //请求被取消
            Log.d(TAG, "SemanticListener.onCancel");
            tv_semantic_result.setText(R.string.str_semantic_cancel);
        }
    }
}
