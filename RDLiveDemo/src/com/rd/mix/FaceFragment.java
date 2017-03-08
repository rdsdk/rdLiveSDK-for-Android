package com.rd.mix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.rdlivedemo.R;
import com.rd.base.BaseFragment;
import com.rd.demo.utils.PathUtils;
import com.rd.flash.FlashItem;
import com.rd.imenu.IMenu;
import com.rd.lib.utils.ThreadPoolUtils;
import com.rd.live.RDLiveSDK;
import com.rd.live.ui.RdGridViewBase;
import com.rd.live.ui.RdGridViewBase.IOnItemClickListener;

/**
 * 人脸面具
 * 
 * @author JIAN
 * 
 */
public class FaceFragment extends BaseFragment {
	private View mrootView;
	private RdGridViewBase flashGridview;

	private final int MSG_INIT_UI = 20000;
	private IMenu listener;

	/**
	 * 人脸面具
	 */
	public FaceFragment(IMenu _listener) {
		super();
		listener = _listener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		logI("onCreate", this.toString());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		logI("onCreateView", this.toString());
		mrootView = inflater.inflate(R.layout.anim_layout, null);
		initView();
		return mrootView;
	}

	/**
	 * 关闭人脸面具菜单
	 */
	private void onFaceFinish() {
		if (null != listener) {
			listener.onMenuVisible(true);
		}
	}

	private void initView() {
		mrootView.findViewById(R.id.animlayout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						onFaceFinish();
					}
				});

		flashGridview = (RdGridViewBase) mrootView
				.findViewById(R.id.flash_rdgridview);
		flashGridview.setIOnItemClicklistener(new IOnItemClickListener() {

			@Override
			public void onItemClick(FlashItem info) {
				if (info.getFlashName().equals("none")) {
					RDLiveSDK.enableFaceFlash(false);
					onFaceFinish();
				} else {
					if (!RDLiveSDK.isFaceFlashEnabled()) {
						RDLiveSDK.enableFaceFlash(true);// 开启人脸遮罩功能
					}
					RDLiveSDK.reloadFaceFlash(info.getSdPath());
				}
			}
		});
		initData();

	}

	/**
	 * 准备数据
	 */
	private void initData() {
		getFlashInfos();
	}

	public static final String ASSET = "Asset";

	// 获取人脸面具
	private void getFlashInfos() {
		flashs.clear();
		String[] flashs = null;
		try {
			AssetManager ast = getActivity().getAssets();
			flashs = ast.list("frame_shot");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		final String[] tempFlash = flashs;

		// 前提:appIml.initAssetRes2File(context)已经成功导出到sd卡
		final String assetPath = PathUtils.createDir(ASSET);

		if (null != tempFlash && tempFlash.length > 0) {
			ThreadPoolUtils.executeEx(new Runnable() {

				@Override
				public void run() {

					int len = tempFlash.length;
					File temp;
					for (int i = 0; i < len; i++) {
						String fname = tempFlash[i];
						if (fname.contains(".zip")) {
							temp = new File(assetPath, fname.substring(0,
									fname.lastIndexOf(".")));
							if (null != temp && temp.isDirectory()) {
								FaceFragment.this.flashs
										.add(new FlashItem(temp));
							}
						}
					}
					mhandler.obtainMessage(MSG_INIT_UI).sendToTarget();

				}
			});
		}
	}

	private ArrayList<FlashItem> flashs = new ArrayList<FlashItem>();

	private Handler mhandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case MSG_INIT_UI:
				flashGridview.initData(flashs);
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 释放
	 */
	private void recycle() {
		mhandler.removeMessages(MSG_INIT_UI);
		flashs.clear();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		logI("onDestroyView", TAG);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		recycle();
		logI("ondestory", this.toString());
	}

	/**
	 * 响应返回键
	 * 
	 * @return
	 */
	@Override
	public int onBackPressed() {
		logI("onBackPressed", TAG);
		return super.onBackPressed();
	}

	private void logI(String tag, String msg) {
		Log.i(tag, msg);
	}

}
