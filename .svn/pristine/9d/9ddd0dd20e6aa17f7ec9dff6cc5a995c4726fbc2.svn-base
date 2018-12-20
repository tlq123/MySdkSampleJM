package com.turing.turingsdksample.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.turing.turingsdksample.R;


/**
 * @author：licheng@uzoo.com
 */

public class ParseLayout extends LinearLayout {
    public ParseLayout(Context context) {
        super(context);
        initView(context);
    }

    public ParseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ParseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private View view;
    private TextView tv_name, tv_content;

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.base_parse, null);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_content = (TextView) view.findViewById(R.id.tv_content);
        addView(view);
    }

    public void setCategory(String str) {
        tv_name.setText(str);
    }

    /**
     * 设置内容
     **/
    public void setTextContent(String str) {
        tv_content.setText(str);
    }
}
