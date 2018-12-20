package com.turing.turingsdksample.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.turing.turingsdksample.R;


/**
 * 类名: ProgressDialog</br> 
 * 描述: </br>
 * 开发人员： longtaoge</br>
 * 创建时间： 2013-5-3 
 */ 

public class CirclePointProgressDialog extends Dialog {
	private Context context = null;
	private static CirclePointProgressDialog customProgressDialog = null;

	public CirclePointProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CirclePointProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static CirclePointProgressDialog createDialog(Context context) {
		customProgressDialog = new CirclePointProgressDialog(context,
				R.style.CustomProgressDialog);
		customProgressDialog.setContentView(R.layout.dialog_cricle_progress);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		customProgressDialog.setCanceledOnTouchOutside(false);
		return customProgressDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}

		ImageView imageView = (ImageView) customProgressDialog
				.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView
				.getBackground();
		animationDrawable.start();
	}



	/**
	 * 
	 * [Summary] setMessage 提示内容
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public CirclePointProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);

		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}

		return customProgressDialog;
	}
	
}
