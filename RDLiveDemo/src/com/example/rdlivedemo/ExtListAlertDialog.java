package com.example.rdlivedemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.rd.recorder.LiveConfig;

/**
 * 直播配置
 * @author JIAN
 * @date 2017-1-17 下午2:36:06
 */
public class ExtListAlertDialog extends Dialog {
	private IDialogListener listener;
	private RadioButton rbHD, rbSD, rbFront, rbRear, rbEnabled, rbUnEanble;

	public ExtListAlertDialog(Context context, IDialogListener mlistener) {
		super(context, R.style.dialog);
		listener = mlistener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.alert_live_config, null);
		setContentView(view);
		initViews();
		findViewById(R.id.btncancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ExtListAlertDialog.this.cancel();
						if (null != listener) {
							listener.onCancel(ExtListAlertDialog.this);
						}
					}
				});
		findViewById(R.id.btnsure).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						ExtListAlertDialog.this.cancel();
						onSaveConfig();
					}
				});

	};

	/**
	 * 确认直播配置
	 */
	private void onSaveConfig() {
		if (null != listener) {
			LiveConfig temp;
			if (rbHD.isChecked()) {// 获取选中的清晰度
				temp = onHD();
			} else {
				temp = onSD();
			}

			if (rbFront.isChecked()) {// 前置;
				temp.setFront(true);
			} else {
				temp.setFront(false);
			}
			if (rbEnabled.isChecked()) {// 美颜
				temp.enableBeautify(true);
			} else {
				temp.enableBeautify(false);
			}
			// 设置直播参数
			listener.onSure(ExtListAlertDialog.this, temp);
		}
	}

	/**
	 * 初始化直播参数的配置
	 */
	private void initViews() {
		// 清晰度
		rbHD = (RadioButton) findViewById(R.id.liveHD);
		rbSD = (RadioButton) findViewById(R.id.liveSD);
		rbHD.setChecked(true);
		// 摄像头
		rbFront = (RadioButton) findViewById(R.id.liveFront);
		rbRear = (RadioButton) findViewById(R.id.liveRear);
		rbFront.setChecked(true);
		// 是否美颜
		rbEnabled = (RadioButton) findViewById(R.id.rbliveBeautifyEnabled);
		rbUnEanble = (RadioButton) findViewById(R.id.rbliveBeautifyUnenabled);
		rbEnabled.setChecked(true);

	}

	/**
	 * 响应高清
	 */
	private LiveConfig onHD() {
		// 高清的推荐参数
		return new LiveConfig().setOutSize(360, 640).setLiveBit(800 * 1000)
				.setFrame(20);

	}

	/**
	 * 响应标清
	 * 
	 * @return
	 */
	private LiveConfig onSD() {
		// 标清的推荐参数
		return new LiveConfig().setOutSize(180, 320).setLiveBit(400 * 1000)
				.setFrame(15);

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	/**
	 * 当前dialog回调接口
	 * 
	 * @author JIAN
	 * 
	 */
	public static interface IDialogListener {
		/**
		 * 确定
		 * 
		 * @param dialog
		 * @param config
		 *            直播参数
		 */
		public void onSure(Dialog dialog, LiveConfig config);

		/**
		 * 取消
		 * 
		 * @param dialog
		 */
		public void onCancel(Dialog dialog);
	}

}
