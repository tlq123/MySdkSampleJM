package com.turing.turingsdksample.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.turing.turingsdksample.R;
import com.turing.turingsdksample.callback.PopWindownUICallback;
import com.turing.turingsdksample.callback.SelectFragmentCallback;


/**
 * @author：licheng@uzoo.com
 */

public class HeadLayout extends RelativeLayout implements PopWindownUICallback, View.OnClickListener {
    public HeadLayout(Context context) {
        super(context);
        initView(context);
    }

    public HeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HeadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private View titleLayout;//头部布局
    private Button btn_left_return, btn_right_select;
    private TextView tv_title;
    private PopupWindow mPopupWindow;
    private SelectFragmentCallback callback;

    private void initView(final Context context) {
        titleLayout = LayoutInflater.from(context).inflate(R.layout.base_header, null);
        //左边的返回按钮
        btn_left_return = (Button) titleLayout.findViewById(R.id.btn_left_return);
        btn_left_return.setOnClickListener(this);
        //右边的选择按钮
        btn_right_select = (Button) titleLayout.findViewById(R.id.btn_right_select);
        btn_right_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = LayoutInflater.from(context).inflate(R.layout.pop_window, null);
                SelectPopWindow selectPopWindow = (SelectPopWindow) popupView.findViewById(R.id.select_window);
                selectPopWindow.setCallback(callback);
                selectPopWindow.setRelUICallback(HeadLayout.this);
                mPopupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
                mPopupWindow.setTouchable(true);
                mPopupWindow.setFocusable(true);
                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.shape_select_window));
                int mWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 450, getResources().getDisplayMetrics());
                mPopupWindow.showAsDropDown(titleLayout, mWidth, 0);
            }
        });
        //中间的名字
        tv_title = (TextView) titleLayout.findViewById(R.id.tv_title);
        addView(titleLayout);
    }

    public void setCallback(SelectFragmentCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onSelectDemiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        if (callback != null) {
            callback.onLeftBtn();
        }
    }
}
