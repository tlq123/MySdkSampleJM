package com.turing.turingsdksample.fragment;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.turing.turingsdksample.ui.CirclePointProgressDialog;


/**
 * @author：licheng@uzoo.com
 */

public abstract class BaseFragment extends Fragment {
    protected CirclePointProgressDialog cirCleDialog;

    /**
     * 设置dilog,cicle内容 类型1
     **/
    protected void setCircleDialogMessage(Context context, int conInt) {
        if (cirCleDialog == null) {
            cirCleDialog = CirclePointProgressDialog.createDialog(context);
        }
        cirCleDialog.setMessage(context.getResources().getString(conInt));
        cirCleDialog.show();
    }

    /**
     * 取消dialog,circle 类型1
     **/
    protected void hiddenCircleDialog() {
        if (cirCleDialog != null) {
            if (cirCleDialog.isShowing()) {
                cirCleDialog.dismiss();
            }
        }
    }

    /**
     * 弹出toast
     *
     * @param res 资源
     **/
    protected void toast(int res) {
        Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出toast
     *
     * @param str 文本
     **/
    protected void toast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onDetach() {
        super.onDetach();
            exitLogic();
    }

    public abstract  void exitLogic();
}
