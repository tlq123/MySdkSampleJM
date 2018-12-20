package com.turing.turingsdksample.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.turing.turingsdksample.R;
import com.turing.turingsdksample.callback.CategoryOperateCallback;


/**
 * @author：licheng@uzoo.com
 */

public class BottomLayout extends LinearLayout implements View.OnClickListener {


    private View bottomLayout;//底部整体布局
    private RelativeLayout rel_input, rel_asr;
    private EditText ed_input;
    private Button btn_select_asr, btn_start_asr;
    private Button btn_select_input, btn_send_tts;
    private CategoryOperateCallback callback;
    private Context context;


    public BottomLayout(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public BottomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView(context);
    }

    public BottomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(context);
    }

    private void initView(Context context) {
        bottomLayout = LayoutInflater.from(context).inflate(R.layout.base_bottom, null);
        //输入的整体
        rel_input = (RelativeLayout) bottomLayout.findViewById(R.id.rel_input);
        ed_input = (EditText) bottomLayout.findViewById(R.id.ed_input);
        btn_select_asr = (Button) bottomLayout.findViewById(R.id.btn_select_asr);
        btn_select_asr.setOnClickListener(this);
        btn_send_tts = (Button) bottomLayout.findViewById(R.id.btn_send);
        btn_send_tts.setOnClickListener(this);
        //asr的整体
        rel_asr = (RelativeLayout) bottomLayout.findViewById(R.id.rel_asr);
        btn_start_asr = (Button) bottomLayout.findViewById(R.id.btn_start_asr);
        btn_start_asr.setOnClickListener(this);
        btn_select_input = (Button) bottomLayout.findViewById(R.id.btn_select_input);
        btn_select_input.setOnClickListener(this);
        addView(bottomLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_asr://点击ASR
                startAsr();
                break;
            case R.id.btn_select_input://选择键盘，切换到输入框
                rel_input.setVisibility(VISIBLE);
                rel_asr.setVisibility(GONE);
                break;
            case R.id.btn_select_asr:
                hiddenKeyboard();
                rel_input.setVisibility(GONE);
                rel_asr.setVisibility(VISIBLE);
                break;
            case R.id.btn_send:
                onClickSendText();
                break;

        }
    }

    public void setCallback(CategoryOperateCallback callback) {
        this.callback = callback;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            if (this.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 开始asr
     **/
    private void startAsr() {
        if (callback != null) {
            if (context.getString(R.string.personvsrobot).equals(callback.getFragmentTag())) {
                //当时人机交互时
                callback.operateAll();
            } else if (context.getString(R.string.text_asr).equals(callback.getFragmentTag())) {
                //asr
                callback.operateOnlyASR();
            } else if (context.getString(R.string.duo_status_vs).equals(callback.getFragmentTag())) {
                callback.operateOnlyASR();
            }
        }
    }

    private void onClickSendText() {
        String str = ed_input.getText().toString();
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(context, R.string.warn_content_isnull, Toast.LENGTH_SHORT).show();
            return;
        }
        if (context.getString(R.string.text_tts).equals(callback.getFragmentTag())) {
            callback.operateOnlyTTS(str);
        } else if (context.getString(R.string.personvsrobot).equals(callback.getFragmentTag())) {
            callback.operateTextForTTS(str);
        } else if (context.getString(R.string.duo_status_vs).equals(callback.getFragmentTag())) {
            callback.operateTextForTTS(str);
        }
        hiddenKeyboard();
        clearInputDate();
    }


    /**
     * 当只有ASR时
     **/
    public void onlyASRUI() {
        rel_input.setVisibility(View.GONE);
        btn_select_input.setVisibility(View.GONE);
        rel_asr.setVisibility(VISIBLE);
    }

    /**
     * 当只有TTS时
     **/
    public void onlyTTSUI() {
        rel_asr.setVisibility(GONE);
        rel_input.setVisibility(VISIBLE);
        btn_select_asr.setVisibility(GONE);
    }

    private void hiddenKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(ed_input, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(ed_input.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 设置asr按钮的文本
     **/
    public void setTextBtnASR(String str) {
        btn_start_asr.setText(str);
    }

    /**
     * 获得asr按钮的文本
     **/
    public String getTextBtnASR() {
        return btn_start_asr.getText().toString();
    }

    /**
     * 清空edit里面的数据
     **/
    public void clearInputDate() {
        ed_input.setText("");
    }
}
