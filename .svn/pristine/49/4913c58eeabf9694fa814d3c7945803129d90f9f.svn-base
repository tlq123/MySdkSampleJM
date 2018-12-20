package com.turing.turingsdksample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.turing.authority.authentication.AuthenticationManager;
import com.turing.bind.BindCodeResp;
import com.turing.bind.BindInitListener;
import com.turing.bind.BindManager;
import com.turing.iot.MQTTConnectListener;
import com.turing.iot.MQTTInitListener;
import com.turing.iot.MQTTManager;
import com.turing.iot.MQTTReceiverListener;
import com.turing.iot.bean.Topic;
import com.turing.turingsdksample.R;
import com.util.LogUtil;

public class IOTFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = IOTFragment.class.getSimpleName();
    public static final String deviceType = "gh_ca69fb525566";
    public static String deviceId = "aiAA847303504c32";


    //beta
    public static String apikey = "34df741fd27044e4b11494925a41c0bf";
    private static String mMqttName = "tulingtest";
    private static String mPwdName = "3jJxWAUS";
    public static final String deivceName = "HelloKetty";


    private Topic mTopic;
    private TextView mContentTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_iot, null);
        view.findViewById(R.id.btn_mqtt_init).setOnClickListener(this);
        view.findViewById(R.id.btn_mqtt_connect).setOnClickListener(this);
        view.findViewById(R.id.btn_mqtt_send).setOnClickListener(this);
        view.findViewById(R.id.btn_mqtt_send).setVisibility(View.GONE);
        view.findViewById(R.id.btn_mqtt_disconnect).setOnClickListener(this);
        view.findViewById(R.id.btn_bind_code).setOnClickListener(this);
        view.findViewById(R.id.btn_mqtt_state).setOnClickListener(this);
        mContentTv = (TextView) view.findViewById(R.id.tv_content);
        deviceId = AuthenticationManager.getInstance().getDeviceId();
        MQTTManager.getInstance().registerReceiveListener(mqttReceiverListener);
        return view;
    }

    private MQTTReceiverListener mqttReceiverListener = new MQTTReceiverListener() {
        @Override
        public void onReceive(final String json) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "json=" + json, Toast.LENGTH_SHORT).show();
                }
            });
            Log.e(TAG, "onReceive: " + json);
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_mqtt_init:
                MQTTManager.getInstance().init(getActivity(), apikey, mqttInitListener);
                break;

            case R.id.btn_mqtt_connect:
                if (mTopic == null) {
                    Toast.makeText(getActivity(), getString(R.string.param_not_null), Toast.LENGTH_SHORT).show();
                    return;
                }

                MQTTManager.getInstance().connect(mTopic, mMqttName, mPwdName, mqttConnectListener);


                break;
            case R.id.btn_mqtt_send:
                break;
            case R.id.btn_mqtt_disconnect:

                try {
                    MQTTManager.getInstance().disconnected();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_bind_code:
                BindManager.getInstance().init(apikey, new BindInitListener() {
                    @Override
                    public void onSuccess(final BindCodeResp bindCodeResp) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mContentTv.setText("onRequestCompleted  " + bindCodeResp.toString());
                            }
                        });

                    }

                    @Override
                    public void onFailed(final int errorCode, final String errorMsg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mContentTv.setText("onFailed  errorCode=" + errorCode + "   errorMsg = " + errorMsg);
                            }
                        });
                    }
                });
//
                break;
//
            case R.id.btn_mqtt_state:
                int connectState = MQTTManager.getInstance().getConnectState();
                String connectStr = "";
                if (connectState == MQTTManager.CONNECTED) {
                    connectStr = getString(R.string.connected);
                } else if (connectState == MQTTManager.CONNECTING) {
                    connectStr = getString(R.string.connecting);
                } else {
                    connectStr = getString(R.string.disconnected);
                }
                mContentTv.setText(connectStr);

                break;
            default:
                break;
        }
    }

    private MQTTConnectListener mqttConnectListener = new MQTTConnectListener() {
        @Override
        public void onSuccess() {
            Log.e(TAG, "onSuccess: ");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "mqtt连接成功", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onFailed(int i, String s) {
            Toast.makeText(getActivity(), "mqtt连接失败", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "onFailed code =" + i + ":  errorMsg = " + s);
        }


    };

    private MQTTInitListener mqttInitListener = new MQTTInitListener() {
        @Override
        public void onSccess(Topic topic) {
            if (topic == null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "MQTT初始化失败.返回Topic为空", Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
            LogUtil.systemd(TAG, "onSccess: " + topic
                    .toString());
            mTopic = topic;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "MQTT初始化成功.", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public void onFailed(int errorCode, String errorMsg) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "MQTT初始化失败.", Toast.LENGTH_SHORT).show();
                }
            });

            Log.e(TAG, "onFailed: errorCode=" + errorCode + "    errorMsg=" + errorMsg);
        }
    };


    @Override
    public void exitLogic() {
        Log.d(TAG, "exitLogic");
            MQTTManager.getInstance().disconnected();
    }
}
