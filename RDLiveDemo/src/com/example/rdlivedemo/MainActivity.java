package com.example.rdlivedemo;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rdlivedemo.ExtAlertDialog.IDialogListener;
import com.rd.demo.utils.AppUtil;
import com.rd.demo.utils.PermissionUtils;
import com.rd.demo.utils.PermissionUtils.IPermissionListener;
import com.rd.live.RDLiveSDK;
import com.rd.live.RDLiveSDK.ILivingCallBack;
import com.rd.recorder.LiveConfig;

/**
 * demo首页
 * 
 * @author jian
 */
public class MainActivity extends Activity {

	private TextView etUidOURL, tvTitle;
	private RadioButton rdServer, thirdServer;// 主单选
	private View livingUserView;// 正在直播的人数(只有锐动服务器此接口才有效)
	private TextView tvTextTitle;
	private View checkAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	private final int REQUSET_PERMISSION = 101;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (AppUtil.hasM()) {// android 6.0 设备检测权限
			checkPermission();
		} else {
			initView();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == REQUSET_PERMISSION) {
			doNext(requestCode, grantResults, permissions);
		}
	}

	@SuppressLint("NewApi")
	private void doNext(int requestCode, int[] grantResults,
			String[] permissions) {
		PermissionUtils.doNext(this, grantResults, permissions,
				new IPermissionListener() {

					@Override
					public void onPermission(int permissionResult) {
						if (permissionResult == PackageManager.PERMISSION_GRANTED) {
							initView();
							AppImp.init(getApplicationContext());
						} else if (permissionResult == PackageManager.PERMISSION_DENIED) {
							checkPermission();
						}

					}
				});

	}

	private void checkPermission() {
		PermissionUtils.checkPermission(
				MainActivity.this,
				REQUSET_PERMISSION,
				new IPermissionListener() {
					@Override
					public void onPermission(int permissionResult) {
						if (permissionResult == PackageManager.PERMISSION_GRANTED) {
							initView();
							AppImp.init(getApplicationContext());
						}
					}
				}, Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
	}

	/**
	 * 选择锐动服务器
	 */
	private void onCheckedRdServer() {
		tvTitle.setText(R.string.rdserver);
		tvTextTitle.setText("UID");
		etUidOURL.setText("123456");
		etUidOURL.setHint(R.string.uid_hint);
		livingUserView.setVisibility(View.GONE);
		livingUserView.setVisibility(View.GONE);
	}

	/**
	 * 选择第三方服务器
	 */
	private void onCheckedThirdServer() {
		tvTitle.setText(R.string.thirdserver);
		tvTextTitle.setText("Rtmp");
		etUidOURL.setText("rtmp://");
		etUidOURL.setHint(R.string.url_hint);
		livingUserView.setVisibility(View.GONE);
	}

	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		rdServer = (RadioButton) findViewById(R.id.rdServer);
		thirdServer = (RadioButton) findViewById(R.id.thirdServer);
		etUidOURL = (TextView) findViewById(R.id.etUidOUrl);
		tvTextTitle = (TextView) findViewById(R.id.tvTextTitle);
		rdServer.setOnClickListener(new OnClickListener() {// 响应选择锐动服务器

			@Override
			public void onClick(View v) {
				onCheckedRdServer();
			}
		});
		thirdServer.setOnClickListener(new OnClickListener() {// 响应选择三方服务器

					@Override
					public void onClick(View v) {
						onCheckedThirdServer();

					}
				});

		// 跳转到直播界面
		findViewById(R.id.mBtnStart).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String uid = etUidOURL.getText().toString();
				if (TextUtils.isEmpty(uid)) {
					if (rdServer.isChecked()) {
						onToast("请设置Uid");
					} else {
						onToast("请设置URL推流地址");
					}
				} else {
					onCheckLiveConfig(uid);

				}

			}
		});
		// 看直播(主播UID方式直播)
		findViewById(R.id.mBtnlook).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String uid = etUidOURL.getText().toString();
				if (rdServer.isChecked()) {
					onUidPlay(uid);
				} else {
					onURLPlay(uid);
				}

			}
		});

		// 检测当前appkey的直播权限
		checkAuth = findViewById(R.id.mBtnCheck);
		checkAuth.setVisibility(View.GONE);
		checkAuth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				int re = RDLiveSDK.getAuthType();
				String msg = "";
				if (re == RDLiveSDK.AT_INVALID) {
					msg = "Uid和Url直播方式都不可用";
				} else if (re == RDLiveSDK.AT_UID) {
					msg = "Uid直播可用";
				} else if (re == RDLiveSDK.AT_URL) {
					msg = "Url直播方式可用";
				} else if (re == RDLiveSDK.AT_URL_OR_UID) {
					msg = "Uid和Url两种直播方式都可用";
				} else {
					msg = "未知情况";
				}
				onToast(msg);

			}
		});

		// 获取当前appkey在线直播人数
		livingUserView = findViewById(R.id.mbtngetLivingUser);
		livingUserView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				RDLiveSDK.getLivingList(new ILivingCallBack() {

					@Override
					public void getLivingList(ArrayList<String> list) {
						if (null != list) {
							int len = list.size();
							for (int i = 0; i < len; i++) {
								Log.i("---" + i, "uid-->" + list.get(i));
							}
						}
						onToast((null == list) ? "没有在线主播" : ("在线人数" + list
								.size()));
					}
				});
			}
		});
		// // 直播参数配置
		// findViewById(R.id.mbtnLiveConfig).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// startActivity(new Intent(MainActivity.this,
		// LiveConfigActivity.class));
		//
		// }
		// });
		// 默认选择锐动服务器
		if (rdServer.isChecked()) {
			onCheckedRdServer();
		} else {
			onCheckedThirdServer();
		}
	}

	private void onToast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 响应UID方式看直播
	 * 
	 * @param uid
	 */
	private void onUidPlay(String uid) {
		if (TextUtils.isEmpty(uid)) {
			onToast("正在直播的Uid为NULL");
		} else {
			int aType = RDLiveSDK.getAuthType();
			if (aType == RDLiveSDK.AT_UID || aType == RDLiveSDK.AT_URL_OR_UID) {
				RDLiveSDK.getLivingUid(uid, new RDLiveSDK.ILivingListener() {

					@Override
					public void getLiving(HashMap<String, String> maps) {
						if (maps.containsKey("error")) {
							onToast(maps.get("error"));
						} else {
							String rtmp = maps.get("rtmp");
							String m3u8 = maps.get("m3u8");
							Log.d("getdata", "rtmp->" + rtmp);
							Log.d("getdata", "m3u8->" + m3u8);
							PlayActivity.gotoPlay(MainActivity.this, rtmp,
									maps.get("thumb"), maps.get("title"),
									maps.get("liveId"));
						}

					}
				});
			} else {
				onToast("Uid直播功能已过期！");
			}
		}
	}

	/**
	 * 响应看URL方式的直播
	 * 
	 * @param lrtmp
	 */
	private void onURLPlay(String lrtmp) {
		if (TextUtils.isEmpty(lrtmp)) {
			onToast("正在直播的流为NULL");
		} else {
			PlayActivity.gotoPlay(MainActivity.this, lrtmp, "", "未知", null);
		}
	}

	// 配置直播参数
	private void onCheckLiveConfig(final String uidORtmp) {
		Dialog d = new ExtAlertDialog(this, new IDialogListener() {

			@Override
			public void onSure(Dialog dialog, LiveConfig config) {

				LiveActivity.startSelf(MainActivity.this, uidORtmp, config);
			}

			@Override
			public void onCancel(Dialog dialog) {

			}
		});
		d.show();

	}

}
