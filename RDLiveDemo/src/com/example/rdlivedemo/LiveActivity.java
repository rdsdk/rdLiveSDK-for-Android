package com.example.rdlivedemo;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.rd.base.BaseActivity;
import com.rd.demo.utils.Utils;
import com.rd.live.RDLiveSDK;
import com.rd.live.ui.GlTouchView;
import com.rd.live.ui.LiveCameraZoomHandler;
import com.rd.mix.PlayerUtils;
import com.rd.recorder.IRecorderListener;
import com.rd.recorder.LiveConfig;
import com.rd.recorder.ResultConstants;

/**
 * 直播demo页
 * 
 * @author jian
 */
public class LiveActivity extends BaseActivity {
	private static final String PARAMUIDO_RTMP = "uidOrtmp";
	private static final String PARAMLIVE_CONFIG = "liveconfig";// 直播参数
	private static final String PARAMLIVE_TITLE = "livetitle";// 直播参数
	private ImageView m_btnChangeCamera, camare_flash;
	private Button m_btnLive;
	private GlTouchView touchview;
	private String uidORtmp;
	private LiveCameraZoomHandler m_hlrCameraZoom;
	private List<String> effects;// 支持的滤镜
	private View beautifybtn;// 美颜按钮

	private ImageView mCustom;
	private MenuCustomHanlder customHanlder;

	private String title;
	private OutOrientationHandler outTationHandler;

	@SuppressLint("NewApi")
	private void setRotationAnimation() {
		if (Utils.hasJellyBeanMR2()) {
			int rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_JUMPCUT;
			Window win = getWindow();
			WindowManager.LayoutParams winParams = win.getAttributes();
			winParams.rotationAnimation = rotationAnimation;
			win.setAttributes(winParams);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRotationAnimation();
		TAG = this.toString();
		isfirst = true;
		logI(TAG, "oncreate");
		// 获取传入的url或rtmp链接
		uidORtmp = getIntent().getStringExtra(PARAMUIDO_RTMP);
		LiveConfig config = (LiveConfig) getIntent().getSerializableExtra(
				PARAMLIVE_CONFIG);
		title = getIntent().getStringExtra(PARAMLIVE_TITLE);
		if (TextUtils.isEmpty(title)) {
			title = "直播";
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_preview_live);
		initViews();
		m_hlrCameraZoom = new LiveCameraZoomHandler(this, null);
		touchview.setZoomHandler(m_hlrCameraZoom);
		// 直播画面打开之前配置直播参数(可包含摄像头、输出尺寸、帧码率、是否美颜等)
		// 通过此方法设置摄像头方向、是否美颜只在onprepared(RelativeLayout,listener)之前调用有效
		// ,初始化之后设置方向使用RDLiveSDK.switchCamera()、RDLiveSDK.enableBeautify(bEnableBeautify)
		RDLiveSDK.setEncoderConfig(config);

		// 就绪摄像头布局
		RDLiveSDK.onPrepare(
				(RelativeLayout) findViewById(R.id.frameAspectRatioPreview),
				iListener);

		// 设置推流超时
		RDLiveSDK.setApiLiveSetRtmpUploadPacketTimeout(10);

		// 初始化高级功能界面按钮
		menuHanlder = new MenuHanlder(this, findViewById(R.id.menuLayout));
		customHanlder = new MenuCustomHanlder(this);
		orientationListener = new MyOrientationEventListener(this);

	}

	private MenuHanlder menuHanlder;

	private void initViews() {
		camare_flash = (ImageView) findViewById(R.id.btnCamareFlash);
		camare_flash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isopen = RDLiveSDK.getFlashMode();
				RDLiveSDK.setFlashMode(!isopen);
			}
		});
		beautifybtn = findViewById(R.id.btnCamareBeauty);
		beautifybtn.setVisibility(RDLiveSDK.isSupportBeautify() ? View.VISIBLE
				: View.GONE);
		beautifybtn.setSelected(RDLiveSDK.isBeautifyEnabled());

		beautifybtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isSupport = RDLiveSDK.isSupportBeautify();
				if (isSupport) {// 支持美颜
					RDLiveSDK.enableBeautify(!RDLiveSDK.isBeautifyEnabled());
					v.setSelected(RDLiveSDK.isBeautifyEnabled());
					if (RDLiveSDK.isBeautifyEnabled()) {
						onBeautifyEnabledLevel(true);
					} else {
						onBeautifyEnabledLevel(false);
					}
				} else {
					beautifybtn.setVisibility(View.GONE);
				}

			}
		});
		findViewById(R.id.living_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		touchview = (GlTouchView) findViewById(R.id.theGlTouchView);
		touchview.setViewHandler(coderListener);

		// 直播按钮 和按钮的显示文字
		m_btnLive = (Button) findViewById(R.id.btnStartLive);
		m_btnLive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int aType = RDLiveSDK.getAuthType();
				if (aType != RDLiveSDK.AT_INVALID) {
					onLiveButtonClick();
				} else {
					Log.e(TAG, "m_btnLive->Uid和Url直播方式均已过期!");
					autoToast("Uid和Url直播方式均已过期!");
				}

			}
		});

		// 切换摄像头
		m_btnChangeCamera = (ImageView) findViewById(R.id.btnChangeCamera);
		m_btnChangeCamera.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onSwitchCameraButtonClick();
			}
		});
		// 自定义图片推流
		mCustom = (ImageView) findViewById(R.id.btnCustom);
		mCustom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				customHanlder.onCustomData(mCustom);
			}
		});

		if (!RDLiveSDK.enableCustomData()) {
			mCustom.setVisibility(View.GONE);
		} else {
			mCustom.setVisibility(View.VISIBLE);
		}
		outTationHandler = new OutOrientationHandler(
				findViewById(R.id.out_orientation_layout_root));
	}

	private void onCheckCamareFlash() {
		camare_flash.setVisibility(RDLiveSDK.isFaceFront() ? View.GONE
				: View.VISIBLE);
	}

	private GlTouchView.CameraCoderViewListener coderListener = new GlTouchView.CameraCoderViewListener() {
		private int filterIndex = 0;

		@Override
		public void onSwitchFilterToRight() {
			if (null != effects && effects.size() > 0) {
				filterIndex++;
				if (filterIndex > effects.size() - 1) {
					filterIndex = 0;
				}
				// 设置滤镜
				RDLiveSDK.setColorEffect(effects.get(filterIndex));

			}
		}

		@Override
		public void onSwitchFilterToLeft() {
			if (null != effects && effects.size() > 0) {
				filterIndex--;
				if (filterIndex < 0) {
					filterIndex = effects.size() - 1;
				}
				// 设置滤镜
				RDLiveSDK.setColorEffect(effects.get(filterIndex));

			}

		}

		@Override
		public void onSingleTapUp(MotionEvent e) {
			RDLiveSDK.cameraFocus((int) e.getX(), (int) e.getY());
		}
	};

	@Override
	protected void onStart() {
		super.onStart();
		logI(this.toString(), "onstart--" + RDLiveSDK.isLiving()
				+ "--isLivingUI:" + isLivingUI);

		if (isfirst) {
			isfirst = false;
			mhandler.post(repushRunnable);
		} else {// 防止sharesdk中,在当前页分享到QQ
			// wechat,当前Activity会重新执行生命周期onStart()->onStop()
			// 耗时1500ms左右

			boolean isLivePaused = RDLiveSDK.isLivePaused(this);
			Log.e("onStart", "直播是否在暂停中：" + isLivePaused);

			if (isLivePaused) {// 强制改变UI为直播中的UI
				readLivingOrRecording(true);
			}
			onSureOrientation(lastOrientation);
			mhandler.postDelayed(repushRunnable, 1500);
		}

	}

	/**
	 * 该方法有缺陷，手机屏幕直接旋转180度，无法响应回调 建议使用 m_orientationListener
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e("onConfigurationChangedActivity", this.toString() + "---"
				+ newConfig.screenWidthDp + "*" + newConfig.screenHeightDp
				+ "---" + newConfig.orientation);
	}

	private boolean isLivingUI = false;

	private void onRePushImp() {
		/**
		 * 检测之前是否完全退出，若未完全退出，继续直播
		 */
		isLivingUI = RDLiveSDK.onRestoreInstanceState(this);
		Log.i(this.toString(), "onRePushImp--" + RDLiveSDK.isLiving()
				+ "--isLivingUI:" + isLivingUI);

		refreshButtonsStatus();
	}

	private boolean isLiving() {
		// 当程序从后台切回前台RDLiveSDK.isLiving()刚开始为false,需要辅助状态isLivingUI
		return isLivingUI || RDLiveSDK.isLiving();
	}

	/**
	 * 继续推流
	 */
	private Runnable repushRunnable = new Runnable() {

		@Override
		public void run() {
			if (isRunning) {
				onRePushImp();
			}
		}
	};

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

		}

		;
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		logI(TAG, "onDestroy");
		RDLiveSDK.onExit(this);
		super.onDestroy();
		doExitLiving = false;
		if (null != menuHanlder) {
			menuHanlder.onDestory();
		}
		if (null != outTationHandler) {
			outTationHandler.onDestory();
			outTationHandler = null;
		}
	}

	/**
	 * 本次直播彻底结束
	 */
	private void onLiveClose() {
		RDLiveSDK.stopPublish();
		onResetOrientation();
	}

	private boolean doExitLiving = false;

	/**
	 * 切换摄像头
	 */
	protected void onSwitchCameraButtonClick() {
		RDLiveSDK.switchCamera();
		camare_flash.postDelayed(new Runnable() {

			@Override
			public void run() {
				onCheckCamareFlash();
			}
		}, 500);

	}

	private void autoToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 开始直播
	 */
	private void onLiveButtonClick() {
		if (Utils.checkNetworkInfo(this) == Utils.UNCONNECTED) {
			autoToast("请打开网络连接!");
			return;
		}
		// 锁定方向
		onLockScreen();
		doLivePublish();
	}

	// 确认当前屏幕方向锁定屏幕
	private void onLockScreen() {
		int displayRotation = RDLiveSDK.getDisplayRotation(this);
		if (0 == displayRotation) {// 标准竖屏
			lastOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			setRequestedOrientation(lastOrientation);
		} else if (90 == displayRotation) {// 标准横屏
			lastOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			setRequestedOrientation(lastOrientation);
		} else if (180 == displayRotation) {// 反向竖屏
			lastOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
			setRequestedOrientation(lastOrientation);
		} else if (270 == displayRotation) {// 反向横屏
			lastOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
			setRequestedOrientation(lastOrientation);
		} else {
			lastOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
			setRequestedOrientation(lastOrientation);
		}
	}

	private int lastOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

	/**
	 * 开始直播前锁定方向，从后台切回前台还原锁定方向
	 * 
	 * @param mOrientation
	 */
	private void onSureOrientation(int mOrientation) {
		if (ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED != lastOrientation) {//
			setRequestedOrientation(lastOrientation);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	/**
	 * 定义推流超时的时间
	 */
	private final int RECONNECTION_TIMEOUT = 60000;

	private void doLivePublish() {
		if (!RDLiveSDK.isLiving()) {
			m_btnLive.setVisibility(View.GONE);
			try {
				RDLiveSDK.setReconnectionTimeOut(RECONNECTION_TIMEOUT);
				RDLiveSDK.startPublish(uidORtmp, title);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			autoToast("正在直播中...");
		}
	}

	@Override
	public void onBackPressed() {
		if (null != menuHanlder && (!menuHanlder.onBackPressed())) {
			return;
		}

		if (isLiving()) {
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("确定要停止直播吗?");
			ab.setItems(new String[] { "取消", "确认" },
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
							switch (which) {
							case 1:
								doExitLiving = true;
								onLiveClose();
								finish();
								break;

							default:
								break;
							}
						}
					});
			ab.setCancelable(false);
			ab.show();
		} else {
			super.onBackPressed();
		}
	}

	/**
	 * 刷新按钮状态
	 */
	private void refreshButtonsStatus() {
		readLivingOrRecording(isLiving());
	}

	/**
	 * 刷新按钮状态
	 */
	private void readLivingOrRecording(boolean isliving) {
		if (isRunning) {
			onCheckCamareFlash();
			if (!isliving) {// 未直播
				m_btnLive.setVisibility(View.VISIBLE);
				if (outTationHandler != null) {
					outTationHandler.checkGone();
				}
			} else {// 直播中
				m_btnLive.setVisibility(View.INVISIBLE);
			}
		}

	}

	/**
	 * 录制回调接口
	 */
	private IRecorderListener iListener = new IRecorderListener() {
		@Override
		public void onScreenShot(int nResult, String msg) {
			if (nResult == ResultConstants.SUCCESS) {
				Log.i(TAG, "onScreenShot:截图成功，保存路径为->" + msg);
			} else {
				Log.e(TAG, "onScreenShot: 截图失败,result:" + nResult + ",path:"
						+ msg);
			}
		}

		@Override
		public void onCamera(int nResult, String strResultInfo) {
			Log.e(TAG, "onCamera->" + nResult + "...." + strResultInfo);
		}

		@Override
		public void onPrepared(int nResult, String strResultInfo) {
			Log.d(TAG, "onPrepared->" + "初始化成功" + strResultInfo);
			RDLiveSDK.setCameraZoomHandler(m_hlrCameraZoom);
			// 设置自定义推流数据
			if (RDLiveSDK.enableCustomData() && null != customHanlder) {
				RDLiveSDK.setICustomData(customHanlder.getCustomData());
			}
			effects = RDLiveSDK.getSupportedColorEffects();
			if (null != effects) {
				// 前5项为基础滤镜
				// RecorderManager.BASE_FILTER_ID_NORMAL 原图
				// RecorderManager.BASE_FILTER_ID_GRAY 黑白
				// RecorderManager.BASE_FILTER_ID_SEPIA 怀旧
				// RecorderManager.BASE_FILTER_ID_COLD 冷色
				// RecorderManager.BASE_FILTER_ID_WARM 暖色
				for (int i = 0; i < effects.size(); i++) {
					Log.i("滤镜-" + i, effects.get(i));

				}
			}

			beautifybtn.setSelected(RDLiveSDK.isBeautifyEnabled());
			Log.d("onprepared",
					"是否前置:" + RDLiveSDK.isFaceFront() + "--是否静音:"
							+ RDLiveSDK.isMute() + "-美颜等级:"
							+ RDLiveSDK.getBeautifyLevel() + "-闪光灯-"
							+ RDLiveSDK.getFlashMode());
			camare_flash.setVisibility(RDLiveSDK.isFaceFront() ? View.GONE
					: View.VISIBLE);

			menuHanlder.onFaceMenuShow();
		}

		@Override
		public void onRecordBegin(int nResult, String strResultInfo) {
			Log.i(TAG, "liveActivity->onRecordBegin" + nResult + "-->"
					+ strResultInfo);
			refreshButtonsStatus();
			if (nResult >= ResultConstants.SUCCESS) {
				Log.d("onRecordBegin", "直播成功！");
			} else {
				isLivingUI = false;
				onLiveMsg(strResultInfo);
				Log.e("onRecordBegin", "直播失败," + "返回信息：" + strResultInfo
						+ "，返回值：" + nResult);
				onResetOrientation();
			}

		}

		@Override
		public void onRecordFailed(int nResult, String strResultInfo) {
			isLivingUI = false;
			Log.e("->onRecordFailed", nResult + "-:" + strResultInfo);
			refreshButtonsStatus();
			onResetOrientation();
			onLiveMsg(nResult + "-" + strResultInfo);
		}

		@Override
		public void onRecordEnd(int nResult, String strResultInfo) {
			isLivingUI = false;
			Log.i("onRecordEnd", nResult + "-->" + strResultInfo);
			if (nResult >= ResultConstants.SUCCESS) {
				Log.d("onRecordEnd", "直播已结束!");
			} else {
				onLiveMsg(strResultInfo);
				onResetOrientation();
				Log.e("onRecordEnd", "直播结束失败," + "\n返回信息：" + strResultInfo
						+ "，返回值：" + nResult);
			}

			refreshButtonsStatus();
		}

		@Override
		public void onGetRecordStatus(int nPosition, int nRecordFPS, int delayed) {
			if (delayed > 3000) {
				Log.i("onGetRecordStatus", "当前网络较慢,延迟" + (delayed / 1000) + "秒");
			}
		}

		/**
		 * 重连回调接口
		 */
		@Override
		public void onReconnection(int nResult, String msg) {
			isLivingUI = false;
			Log.i(TAG, "onReconnection->" + nResult + "--" + msg);
			if (nResult == ResultConstants.ERROR_RECONNECTION_TIMEOUT) {
				refreshButtonsStatus();
				onLiveMsg(msg);
				onResetOrientation();
			}
		}

		/**
		 * Android 6.0 请在调用onprepare前确保存储、相机、录音机权限均已授权成功;否则请打开权限
		 */
		@Override
		public void onPermissionFailed(int nResult, String strResultInfo) {
			Log.e(TAG, "onPermissionFailed->" + strResultInfo);
			if (nResult == ResultConstants.PERMISSION_FAILED) {
				Log.e(TAG, "onPermissionFailed->" + strResultInfo);
				finish();
			}
		}

		;

	};

	/**
	 * 直播彻底结束(RDLiveSDK.stopPublish()注意：切到后台时暂停回调函数onRecordEnd中不能处理)或推流失败、超时后，
	 * 恢复屏幕方向随系统旋转
	 */
	private void onResetOrientation() {
		lastOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}

	/**
	 * Dialog显示
	 * 
	 * @param msg
	 */
	private void onLiveMsg(String msg) {
		if (isRunning && !TextUtils.isEmpty(msg)) {
			SysAlertDialog.cancelLoadingDialog();
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle("直播");
			ab.setMessage(msg);
			ab.setPositiveButton("确认", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			});
			ab.show();

		}
	}

	/**
	 * 跳转到直播页
	 * 
	 * @param context
	 * @param uidORtmp
	 * @param config
	 * @param title
	 */
	public static void startSelf(Context context, String uidORtmp,
			LiveConfig config, String title) {
		if (!TextUtils.isEmpty(uidORtmp)) {
			RDLiveSDK.onInit(context);
			Intent intent = new Intent();
			intent.setClass(context, LiveActivity.class);
			intent.putExtra(PARAMUIDO_RTMP, uidORtmp);
			intent.putExtra(PARAMLIVE_CONFIG, config);
			intent.putExtra(PARAMLIVE_TITLE, title);

			context.startActivity(intent);
		} else {
			Toast.makeText(context, "参数不齐", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 程序即将切到后台，保存sdk内部推流状态
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		RDLiveSDK.onSaveInstanceState(this);
	}

	private boolean isfirst = false;

	@Override
	protected void onStop() {
		super.onStop();
		mhandler.removeCallbacks(repushRunnable);
		if (!doExitLiving) {
			// App切到后台,暂停推流
			RDLiveSDK.pausePublish();
		}
		PlayerUtils.getInstance().onPasue();
		Log.i("onstop->removeCallbacks", this.toString());
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("onrestart", this.toString());

	}

	/**
	 * 切回前台，方向监听置为可用
	 */
	@Override
	protected void onResume() {
		super.onResume();
		orientationListener.enable();
		PlayerUtils.getInstance().onResume();
		logI("onResume", this.toString());
	}

	/***
	 * 切到后台设置方向监听不可用
	 */
	@Override
	protected void onPause() {
		super.onPause();
		orientationListener.disable();
		logI("onPause", this.toString());
	}

	/**
	 * 响应开启美颜后设置美颜等级
	 */
	private void onBeautifyEnabledLevel(boolean enable) {
		if (null != menuHanlder) {
			menuHanlder.onBeautifyShow(enable);
		}

	}

	private MyOrientationEventListener orientationListener;

	private class MyOrientationEventListener extends OrientationEventListener {
		/**
		 * 当前方向度数
		 */
		int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;

		public MyOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {

			// 手机平放时，检测不到有效的角度
			if (orientation == ORIENTATION_UNKNOWN)
				return;
			mOrientation = Utils.roundOrientation(orientation, mOrientation);
			// 根据显示方向和当前手机方向，得出当前各个需要与方向适应的控件修正方向
			int displayOr = Utils.getDisplayRotation(LiveActivity.this);
			int orientationCompensation = (mOrientation + displayOr);
			if (mOrientationCompensation != orientationCompensation) {
				mOrientationCompensation = orientationCompensation;
				if (isLiving()) {// 直播开始后
					outTationHandler.setOrientation(mOrientationCompensation);
				}
			}
		}
	}

	/*
	 * 修正的方向度数(90的倍数）
	 */
	private int mOrientationCompensation = 0;

	private void logI(String tag, String msg) {
		Log.i(tag, msg);
	}

}