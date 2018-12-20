package com.turing.turingsdksample.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.turing.turingsdksample.R;
import com.turing.turingsdksample.callback.PopWindownUICallback;
import com.turing.turingsdksample.callback.SelectFragmentCallback;


/**
 * @author：licheng@uzoo.com
 */

public class SelectPopWindow extends LinearLayout implements View.OnClickListener {

    private RelativeLayout rel_iot;
    private RelativeLayout rel_authority;

    public SelectPopWindow(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public SelectPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public SelectPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private RelativeLayout rel_person, rel_asr, rel_tts, rel_duo, rel_music,rel_semantic;
    private SelectFragmentCallback callback;
    private Context context;
    private PopWindownUICallback relUICallback;

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_item_select, null);
        rel_person = (RelativeLayout) view.findViewById(R.id.rel_person);
        rel_person.setOnClickListener(this);
        rel_asr = (RelativeLayout) view.findViewById(R.id.rel_asr);
        rel_asr.setOnClickListener(this);
        rel_tts = (RelativeLayout) view.findViewById(R.id.rel_tts);
        rel_tts.setOnClickListener(this);
        rel_duo = (RelativeLayout) view.findViewById(R.id.rel_duo);
        rel_music = (RelativeLayout) view.findViewById(R.id.rel_music);
        rel_music.setOnClickListener(this);
        rel_iot = (RelativeLayout) view.findViewById(R.id.rel_iot);
        rel_iot.setOnClickListener(this);
        rel_semantic= (RelativeLayout) view.findViewById(R.id.rel_semantic);
        rel_semantic.setOnClickListener(this);

        rel_authority = (RelativeLayout) view.findViewById(R.id.rel_authority);
        rel_authority.setOnClickListener(this);

        rel_duo.setVisibility(View.GONE);
        rel_duo.setOnClickListener(this);
        addView(view);
    }

    @Override
    public void onClick(View v) {
        rel_person.setSelected(false);
        rel_asr.setSelected(false);
        rel_tts.setSelected(false);
        rel_duo.setSelected(false);
        rel_music.setSelected(false);
        switch (v.getId()) {
            case R.id.rel_person:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.personvsrobot));
                }
                rel_person.setSelected(true);

                break;
            case R.id.rel_asr:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.text_asr));
                }
                rel_asr.setSelected(true);

                break;
            case R.id.rel_tts:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.text_tts));
                }
                rel_tts.setSelected(true);
                break;
            case R.id.rel_duo:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.duo_status_vs));
                }

                rel_duo.setSelected(true);
            case R.id.rel_music:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.music));
                }
                rel_music.setSelected(true);
                break;
            case R.id.rel_iot:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.iot));
                }
                rel_iot.setSelected(true);
                break;
            case R.id.rel_semantic:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.pop_semantic));
                }
                rel_semantic.setSelected(true);
                break;
            case R.id.rel_authority:
                if (callback != null) {
                    callback.onSelectFragment(context.getString(R.string.authority));
                }
                rel_semantic.setSelected(true);
                break;
            default:
                break;
        }
        onDemiss();
    }

    public void setCallback(SelectFragmentCallback callback) {
        this.callback = callback;
    }

    public void setRelUICallback(PopWindownUICallback relUICallback) {
        this.relUICallback = relUICallback;
    }

    private void onDemiss() {
        if (relUICallback != null) {
            relUICallback.onSelectDemiss();
        }
    }
}
