package com.turing.turingsdksample.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.turing.turingsdksample.R;
import com.turing.turingsdksample.util.Logger;


/**
 * @Description:常用DIALOG
 */
public class CommonDialog extends Dialog {
    public static final String TAG = CommonDialog.class.getSimpleName();
    private TextView dialog_title_tv, dialog_content_tv;
    private EditText ed_content;
    private Button dialog_left_btn, dialog_right_btn;

    private Context mContext;

    public CommonDialog(Context context) {
        super(context, R.style.common_dialog);
        mContext = context;
//        initView();
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;

    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(Logger.TAG, "CommonDialog>>onCreate()");
        setContentView(R.layout.common_dialog_activity);
        initView();
    }

    private void initView() {
        dialog_title_tv = (TextView) findViewById(R.id.dialog_title_tv);
        dialog_content_tv = (TextView) findViewById(R.id.dialog_content_tv);
        ed_content = (EditText) findViewById(R.id.ed_name);
        dialog_left_btn = (Button) findViewById(R.id.dialog_left_btn);
        dialog_right_btn = (Button) findViewById(R.id.dialog_right_btn);
        dialog_left_btn.setOnClickListener(clickListener);
        dialog_right_btn.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CommonDialog.this.dismiss();
        }
    };

    public void setCommonTitle(String title) {
        dialog_title_tv.setText(title);
    }

    public void setContentHead(String content) {
        dialog_content_tv.setText(content);
    }

    /**
     * g获得编辑框的内容
     **/
    public String getEdContent() {
        return ed_content.getText().toString();
    }

    public void setLeftBtnText(String leftBtnText) {
        dialog_left_btn.setText(leftBtnText);
    }

    public void setRightBtnText(String rightBtnText) {
        dialog_right_btn.setText(rightBtnText);
    }

    public void setLeftBtnClick(View.OnClickListener clickListener) {
        if (clickListener == null) {
            return;
        }
        dialog_left_btn.setOnClickListener(clickListener);
    }

    public void setRightBtnClick(View.OnClickListener clickListener) {
        if (clickListener == null) {
            return;
        }
        dialog_right_btn.setOnClickListener(clickListener);
    }

}
