package com.rd.flash;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.rdlivedemo.R;
import com.rd.demo.utils.PathUtils;
import com.rd.imenu.IMenuListener;
import com.rd.lib.utils.ThreadPoolUtils;
import com.rd.live.ui.RdGridViewBase;
import com.rd.recorder.RecorderManager;

/**
 * 人脸面具
 * 
 * @author JIAN
 * 
 */
public class FlashHandler {

	public static final String ASSET = "Asset";
	private ArrayList<FlashItem> flashs = new ArrayList<FlashItem>();
	private IMenuListener listener;
	private final int MSG_INIT_UI = 20000;
	private TextView tvFace;
	private Context mcontext;
	private View mrootView;
	private RdGridViewBase flashGridview;
	private int tvColor_n, tvColor_ed;// 未选中，选中两种状态颜色

	private FlashHandler() {

	}

	public FlashHandler(Context context, View rootView, IMenuListener _listener) {
		mrootView = rootView;
		mcontext = mrootView.getContext();
		listener = _listener;
		Resources res = mcontext.getResources();
		tvColor_n = res.getColor(R.color.white);
		tvColor_ed = res.getColor(R.color.main_color);
		mrootView.findViewById(R.id.flash_view_top).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onFlashVisibleGone();
					}
				});
		// 关闭人脸控制面板
		mrootView.findViewById(R.id.shadow_flash).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onFlashVisibleGone();
					}
				});
		flashGridview = (RdGridViewBase) mrootView
				.findViewById(R.id.flash_rdgridview);
		flashGridview
				.setIOnItemClicklistener(new RdGridViewBase.IOnItemClickListener() {

					@Override
					public void onItemClick(FlashItem info) {
						if (info.getFlashName().equals("none")) {
							RecorderManager.enableFacing(false);
							tvFace.setText(R.string.face_n);
							tvFace.setCompoundDrawablesWithIntrinsicBounds(0,
									R.drawable.face_n, 0, 0);
							tvFace.setTextColor(tvColor_n);
							onFlashVisibleGone();
						} else {
							tvFace.setText(R.string.face_p);
							RecorderManager.reloadFlash(info.getSdPath());
							tvFace.setCompoundDrawablesWithIntrinsicBounds(0,
									R.drawable.face_ed, 0, 0);
							tvFace.setTextColor(tvColor_ed);
						}
					}
				});

		getFlashInfos();

	}

	// 获取人脸面具
	private void getFlashInfos() {
		flashs.clear();
		String[] flashs = null;
		try {
			AssetManager ast = mcontext.getAssets();
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
								FlashHandler.this.flashs
										.add(new FlashItem(temp));
							}
						}
					}
					mhandler.obtainMessage(MSG_INIT_UI).sendToTarget();

				}
			});
		}
	}

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
		}

		;
	};

	// 隐藏人脸面板
	public void onHide() {
		mrootView.setVisibility(View.GONE);
	}

	// 显示人脸面板
	public void onShow(TextView tvFace) {
		this.tvFace = tvFace;
		mrootView.setVisibility(View.VISIBLE);
	}

	/**
	 * 是否可以返回
	 * 
	 * @return
	 */
	public boolean onBackpressed() {
		if (mrootView.getVisibility() == View.VISIBLE) {
			mrootView.setVisibility(View.GONE);
			return false;
		}
		return true;
	}

	/**
	 * 响应关闭人脸控制面板
	 */
	private void onFlashVisibleGone() {
		onHide();
		if (null != listener) {
			listener.onMenuGone();
		}
	}

	public void recycle() {
		mhandler.removeMessages(MSG_INIT_UI);
		flashs.clear();
	}

}
