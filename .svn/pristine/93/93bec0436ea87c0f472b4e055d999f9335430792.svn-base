package com.turing.turingsdksample.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.turing.semantic.SemanticManager;
import com.turing.semantic.entity.AppAndContactsBean;
import com.turing.semantic.listener.OnHttpRequestListener;
import com.turing.tts.TTSManager;
import com.turing.turingsdksample.R;
import com.turing.turingsdksample.adapter.ContentAdapter;
import com.turing.turingsdksample.bean.ContentBean;
import com.turing.turingsdksample.callback.MusicCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author：licheng@uzoo.com
 */

public class ContentLayout extends LinearLayout implements View.OnClickListener {
    public ContentLayout(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public ContentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private View view;
    private ListView lv;
    private Button btnStart, btnEnd, btn_music_start, btn_upload_deviceinfo;
    private TextView tvName;
    private ArrayList<ContentBean> listsData;
    private ContentAdapter contentAdapter;
    private Context context;
    private MusicCallback musicCallback;
    /**
     * 上传通讯录与APP列表回调
     */
    private OnHttpRequestListener mOnHttpRequestListener;

    private void initView() {
        initData();
        view = LayoutInflater.from(context).inflate(R.layout.base_content, null);
        tvName = (TextView) view.findViewById(R.id.tv_name);
        lv = (ListView) view.findViewById(R.id.lv);
        lv.setSelector(new ColorDrawable());
        lv.setAdapter(contentAdapter);
        btnStart = (Button) view.findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        btnEnd = (Button) view.findViewById(R.id.btn_end);
        btnEnd.setOnClickListener(this);
        btn_music_start = (Button) view.findViewById(R.id.btn_music_start);
        btn_music_start.setOnClickListener(this);
        btn_upload_deviceinfo = (Button) view.findViewById(R.id.btn_upload_deviceinfo);
        btn_upload_deviceinfo.setOnClickListener(this);
        addView(view);
    }

    private void initData() {
        listsData = new ArrayList<ContentBean>();
        contentAdapter = new ContentAdapter(context, listsData);
    }

    /**
     * 设置当前的类型
     **/
    public void setCategory(String str) {
        tvName.setText(str);
    }

    public ArrayList<ContentBean> getListsData() {
        return listsData;
    }

    /**
     * 开始按钮
     **/
    public void setStartViewShow(int visible) {
        btnStart.setVisibility(visible);
    }

    /**
     * 停止按钮的
     **/
    public void setEndViewShow(int visible) {
        btnEnd.setVisibility(visible);
    }

    /**
     * 设置开始按钮的
     **/
    public void setStartTextShow(String text) {
        btnStart.setText(text);
    }

    /**
     * 设置停止按钮的
     **/
    public void setEndTextShow(String text) {
        btnEnd.setText(text);
    }


    /**
     * 停止按钮的
     **/
    public void setMusicShow(int visible) {
//        btn_music_start.setVisibility(visible);
    }

    /**
     * 上传按钮
     * @param visible
     */
    public void setUploadViewShow(int visible){
        btn_upload_deviceinfo.setVisibility(visible);
    }

    /**
     * 设置不可按
     **/
    public void setMusicSelectable(boolean clickable) {
        btn_music_start.setClickable(clickable);
        btn_music_start.setSelected(clickable);
    }

    public void setMusicCallback(MusicCallback musicCallback) {
        this.musicCallback = musicCallback;
    }

    public void setOnHttpRequestListener(OnHttpRequestListener mOnHttpRequestListener) {
        this.mOnHttpRequestListener = mOnHttpRequestListener;
    }

    /**
     * 更新adapter
     **/
    public void notifyDataChanged() {
        contentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start:
                if (listsData != null && listsData.size() > 0) {
                    String str = listsData.get(0).getText();
                    if (!TextUtils.isEmpty(str)) {
                        TTSManager.getInstance().startTTS(str, null);
                    }
                }

                break;
            case R.id.btn_end:
                TTSManager.getInstance().stopTTS();
                break;
            case R.id.btn_music_start:
                if (musicCallback != null) {
//                    musicCallback.startMusic("");
                }
                break;
            case R.id.btn_upload_deviceinfo:
                AppAndContactsBean appAndContactsBean=new AppAndContactsBean();
                Map<String,String> appMap=new HashMap<>();
                //设备中的app名称与app包名放入Map中
                appMap.put("日历","com.android.calendar");
                appMap.put("微信","com.android.wechat");
                Map<String,String> contactMap=new HashMap<>();
                //设备中的通讯录中联系人名称与电话放入Map中
                contactMap.put("张三","123456789");
                contactMap.put("李四","987654321");
                appAndContactsBean.setAppsMap(appMap);
                appAndContactsBean.setContactMap(contactMap);
                SemanticManager.getInstance().uploadAppsAndContacts(appAndContactsBean,mOnHttpRequestListener);
                break;
            default:
                break;
        }
    }


}
