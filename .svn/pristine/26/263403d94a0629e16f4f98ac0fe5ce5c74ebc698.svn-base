package com.turing.turingsdksample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.turing.authority.authentication.AuthenticationListener;
import com.turing.authority.authentication.AuthenticationManager;
import com.turing.authority.authentication.SdkInitializer;
import com.turing.authority.http.userid.data.UserIdUtil;
import com.turing.turingsdksample.R;


/**
 * Authority测试程序
 *
 * @author HomorSmith
 */
public class AuthorityFragment extends BaseFragment implements View.OnClickListener {
    private String TAG = AuthorityFragment.class.getSimpleName();
    private EditText mApiKeyEt;
    private EditText mSecretEt;
    private EditText mUniqueTypeEt;
    private EditText mUniqueIdEt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authority, null);
        initActivity(view);
        return view;
    }

    private void initActivity(View view) {
        view.findViewById(R.id.btn_init_manifest).setOnClickListener(this);
        view.findViewById(R.id.btn_init_set).setOnClickListener(this);
        view.findViewById(R.id.btn_device_id).setOnClickListener(this);
        view.findViewById(R.id.btn_device_type).setOnClickListener(this);
        view.findViewById(R.id.btn_apikey).setOnClickListener(this);
        view.findViewById(R.id.btn_secret).setOnClickListener(this);
        view.findViewById(R.id.btn_userid).setOnClickListener(this);
        mApiKeyEt = (EditText) view.findViewById(R.id.et_apikey);
        mSecretEt = (EditText) view.findViewById(R.id.et_secret);
        mApiKeyEt.setText(AuthenticationManager.getInstance().getApiKey());
        mApiKeyEt.setEnabled(false);
        mSecretEt.setEnabled(false);
        mSecretEt.setText(AuthenticationManager.getInstance().getSecret());
        mUniqueTypeEt = (EditText) view.findViewById(R.id.et_unique_type);
        mUniqueIdEt = (EditText) view.findViewById(R.id.et_uniqueid);
    }


    private AuthenticationListener authenticationListener = new AuthenticationListener() {
        @Override
        public void onSuccess() {
            AuthenticationManager.getInstance().isAuthentication();
            Toast.makeText(getActivity(), getString(R.string.authority_success) + UserIdUtil.getUserID(getActivity().getApplicationContext()) + "   status=" + AuthenticationManager.getInstance().isAuthentication(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(int errorCode, String errorMsg) {

            Toast.makeText(getActivity(), getString(R.string.authority_failed) + errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_init_manifest:
                SdkInitializer.init(getActivity(), AuthenticationManager.getInstance().getApiKey(), AuthenticationManager.getInstance().getSecret(), authenticationListener);
                break;
            case R.id.btn_init_set:
                if (mUniqueTypeEt.getText().toString().equals("custom")) {
                    SdkInitializer.init(mApiKeyEt.getText().toString(), mSecretEt.getText().toString(), mUniqueIdEt.getText().toString(), getActivity(), authenticationListener);
                } else {
                    SdkInitializer.init(getActivity(), mApiKeyEt.getText().toString(), mSecretEt.getText().toString(), mUniqueTypeEt.getText().toString(), authenticationListener);
                }
                break;
            case R.id.btn_device_id:
                String deviceId = AuthenticationManager.getInstance().getDeviceId();
                Toast.makeText(getActivity(), "device id=" + deviceId, Toast.LENGTH_LONG).show();
                Log.d(TAG, "deviceId = " + deviceId);
                break;
            case R.id.btn_device_type:
                String uniqueType = AuthenticationManager.getInstance().getUniqueType();
                Toast.makeText(getActivity(), "device type=" + uniqueType, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_apikey:
                String apiKey = AuthenticationManager.getInstance().getApiKey();
                Toast.makeText(getActivity(), "apiKey =" + apiKey, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_secret:
                String secret = AuthenticationManager.getInstance().getSecret();
                Toast.makeText(getActivity(), "secret =" + secret, Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_userid:
                String userId = AuthenticationManager.getInstance().getUserId();
                Toast.makeText(getActivity(), "userId =" + userId, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void exitLogic() {
        Log.d(TAG,"exitLogic");

    }
}
