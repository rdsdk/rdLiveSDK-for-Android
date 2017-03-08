package com.example.rdlivedemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.rd.demo.utils.PathUtils;
import com.rd.imenu.IMenu;
import com.rd.imenu.IMenuListener;
import com.rd.live.RDLiveSDK;
import com.rd.mix.FaceFragment;
import com.rd.mix.MusicFragment;
import com.rd.mix.PlayerUtils;

/**
 * 直播高级功能演示 (静音、自定义推流、低配美颜、截图)
 * 
 * @author JIAN
 * @date 2017-1-15 上午11:21:53
 */
class MenuHanlder {

	private View rootView;
	private TextView cb_Mute;
	private View mshot;
	private static final String TAG = "MenuHanlder";
	private TextView cbMixMusic, btnFace;
	private FaceFragment faceFragment;
	private View menuLayout;// 控制按钮
	private BeautifyHandler beautifyHandler;
	private TextView cbOsd;// 水印
	private Context mcontext;

	private int tvColor_n, tvColor_ed;

	/**
	 * 直播高级功能演示 (静音、自定义推流、低配美颜、截图 )
	 * 
	 * @param menuView
	 */
	public MenuHanlder(LiveActivity parentActivity, View menuView) {
		this.parent = parentActivity;
		rootView = menuView;
		mcontext = rootView.getContext();
		Resources res = mcontext.getResources();
		tvColor_n = res.getColor(R.color.white);
		tvColor_ed = res.getColor(R.color.main_color);

		initViews();
		beautifyHandler = new BeautifyHandler(
				rootView.findViewById(R.id.beautify_level_layout),
				new IMenuListener() {
					@Override
					public void onMenuGone() {
						menuLayout.setVisibility(View.VISIBLE);
					}
				});

	}

	/**
	 * 显示调整美颜等级
	 * 
	 * @param enable
	 */
	public void onBeautifyShow(boolean enable) {
		menuLayout.setVisibility(View.GONE);
		if (null != beautifyHandler) {
			beautifyHandler.onBeautifyShow(enable);
		}
	}

	/**
	 * 是否显示
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return rootView.getVisibility() == View.VISIBLE;
	}

	/**
	 * 返回
	 * 
	 * @return 是否可以返回上一级
	 */
	public boolean onBackPressed() {
		if (null != mixFragment && mixFragment.isVisible()) {
			onMixGone();
			return false;
		}
		if (null != faceFragment && faceFragment.isVisible()) {// 人脸
			onFaceGone();
			return false;
		}
		return true;
	}

	private void onFaceGone() {
		parent.getFragmentManager().beginTransaction().remove(faceFragment)
				.commitAllowingStateLoss();
		menuLayout.setVisibility(View.VISIBLE);
	}

	private void onMixGone() {
		parent.getFragmentManager().beginTransaction().remove(mixFragment)
				.commitAllowingStateLoss();
		menuLayout.setVisibility(View.VISIBLE);
	}

	// 定义水印状态(默认未打开水印),静音状态(默认关闭)
	private boolean isOsded = false, isMuted = false;

	/**
	 * 初始化高级功能的组件和事件
	 */
	private void initViews() {

		menuLayout = rootView.findViewById(R.id.liveMenuLayout);
		cbOsd = (TextView) menuLayout.findViewById(R.id.btnOsd);
		cbOsd.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {

				if (!isOsded) {
					// 开启水印
					RDLiveSDK.registerOSDBuilder(DemoOSDBuilder.class);
					isOsded = true;
					cbOsd.setText(R.string.osd_p);
					cbOsd.setCompoundDrawablesWithIntrinsicBounds(0,
							R.drawable.osd_ed, 0, 0);
					cbOsd.setTextColor(tvColor_ed);
				} else {
					// 关闭水印
					isOsded = false;
					RDLiveSDK.registerOSDBuilder(null);
					cbOsd.setText(R.string.osd_n);
					cbOsd.setCompoundDrawablesWithIntrinsicBounds(0,
							R.drawable.osd_n, 0, 0);
					cbOsd.setTextColor(tvColor_n);
				}
			}
		});
		cbMixMusic = (TextView) rootView.findViewById(R.id.btnMixMusic);
		cbMixMusic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (null != mixHandler) {
				// mixHandler.onCheckMixMusics(cbMixMusic);
				// menuLayout.setVisibility(View.GONE);
				// }
				menuLayout.setVisibility(View.GONE);
				onMixLayout();
			}
		});

		cb_Mute = (TextView) rootView.findViewById(R.id.cbLiveMute);
		cb_Mute.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View v) {
				try {
					if (isMuted) {// 关闭静音
						RDLiveSDK.setMute(false);
						isMuted = false;
						cb_Mute.setText(R.string.mute_n);
						cb_Mute.setCompoundDrawablesWithIntrinsicBounds(0,
								R.drawable.mute_n, 0, 0);
						cb_Mute.setTextColor(tvColor_n);
					} else {
						RDLiveSDK.setMute(true);
						isMuted = true;
						cb_Mute.setText(R.string.mute_ed);
						cb_Mute.setCompoundDrawablesWithIntrinsicBounds(0,
								R.drawable.mute_ed, 0, 0);
						cb_Mute.setTextColor(tvColor_ed);
					}

				} catch (Exception ex) {
					Log.e(TAG, "静音失败，" + ex.getMessage());
				}

				// RecorderManager.startLive(
				// Environment.getExternalStorageDirectory()
				// + "/zzout.mp4", "");
			}
		});
		mshot = rootView.findViewById(R.id.btnShot);
		mshot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String path = PathUtils.getTempPath() + "/Temp_"
						+ System.currentTimeMillis() + ".jpg";
				RDLiveSDK.screenshot(path, 360, 640);

				// RecorderManager.stopRecord();
			}
		});

		btnFace = (TextView) rootView.findViewById(R.id.btnFace);
		btnFace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				menuLayout.setVisibility(View.GONE);
				onFaceLayout();

			}
		});

	}

	/**
	 * 销毁混音播放器
	 */
	public void onDestory() {

		PlayerUtils.getInstance().release();

	}

	private LiveActivity parent;
	private MusicFragment mixFragment;

	/**
	 * 打开伴音项
	 */
	private void onMixLayout() {
		if (null == mixFragment) {
			mixFragment = new MusicFragment(new IMenu() {

				@Override
				public void onMenuVisible(boolean isVisible) {
					if (RDLiveSDK.isEnableMixAudio()) {
						cbMixMusic.setCompoundDrawablesWithIntrinsicBounds(0,
								R.drawable.mix_music_ed, 0, 0);
						cbMixMusic.setText(R.string.mix_music_ed);
						cbMixMusic.setTextColor(tvColor_ed);
					} else {
						RDLiveSDK.enableMixAudio(false);// 关闭混音
						PlayerUtils.getInstance().release();
						cbMixMusic.setCompoundDrawablesWithIntrinsicBounds(0,
								R.drawable.mix_none, 0, 0);
						cbMixMusic.setText(R.string.mix_music_n);
						cbMixMusic.setTextColor(tvColor_n);
					}
					onMixGone();
				}
			});
		}
		parent.getFragmentManager().beginTransaction()
				.replace(R.id.fragment, mixFragment).commitAllowingStateLoss();
	}

	/**
	 * 打开人脸项
	 */
	private void onFaceLayout() {
		if (null == faceFragment) {
			faceFragment = new FaceFragment(new IMenu() {

				@Override
				public void onMenuVisible(boolean isVisible) {
					onFaceGone();
				}
			});
		}
		parent.getFragmentManager().beginTransaction()
				.replace(R.id.fragment, faceFragment).commitAllowingStateLoss();
	}

	/**
	 * 摄像头打开成功之后才能调用
	 */
	public void onFaceMenuShow() {
		boolean re = RDLiveSDK.isSupportFaceFlash();
		if (null != btnFace) {
			if (re) {
				btnFace.setVisibility(View.VISIBLE);
			} else {
				btnFace.setVisibility(View.GONE);
			}
		}
	}

}
