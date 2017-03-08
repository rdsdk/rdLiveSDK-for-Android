package com.rd.mix;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.rdlivedemo.R;
import com.rd.base.BaseFragment;
import com.rd.demo.utils.AppUtil;
import com.rd.demo.utils.PathUtils;
import com.rd.imenu.IMenu;
import com.rd.mix.PlayerUtils.IMuisic;
import com.rd.recorder.RecorderManager;

public class MusicFragment extends BaseFragment {

	public MusicFragment(IMenu ilistener) {
		super();
		listener = ilistener;
	}

	private View mRootView;
	private Context mContext;
	private ListView mlistMusic;
	private MixAdapter mixAdapter;
	private IMenu listener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		logI("oncreate", this.toString());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		logI("onCreateView", this.toString());
		mRootView = inflater.inflate(R.layout.mix_music_layout, null);
		init();
		getMusic();
		return mRootView;
	}

	private void onCloseMix() {
		RecorderManager.enableMixAudio(false);// 关闭混音
		PlayerUtils.getInstance().release();
		onMixVisibleGone();
	}

	public void init() {
		mContext = mRootView.getContext();
		// 混音列表
		mlistMusic = (ListView) mRootView.findViewById(R.id.mix_listview);
		mRootView.findViewById(R.id.btnMixClose).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onCloseMix();
					}
				});
		mixAdapter = new MixAdapter(mContext);
		mlistMusic.setOnItemClickListener(listviewListener);
		mlistMusic.setAdapter(mixAdapter);
		// 关闭mix控制面板
		mRootView.findViewById(R.id.mixlayout).setOnClickListener(
				goneMixLayoutListener);
		mRootView.findViewById(R.id.shadow_music).setOnClickListener(
				goneMixLayoutListener);

	}

	// 关闭mix控制面板
	private OnClickListener goneMixLayoutListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			onMixVisibleGone();
		}
	};

	private OnItemClickListener listviewListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			String path = mixAdapter.getItem(position).getPath();
			if (!TextUtils.isEmpty(path)) {
				RecorderManager.enableMixAudio(true);// 开启混音 ，
				// 要混淆的音乐只能通过AudioPlayer播放
				PlayerUtils.getInstance().initPlayer(getActivity(),
						mixAdapter.getItem(position), new IMuisic() {

							@Override
							public void onMusicPrepared(int duration) {
								mixAdapter.notifyDataSetChanged();

							}
						});
				mixAdapter.checked(position);
				onMixVisibleGone();

			} else {
				onCloseMix();

			}

		}

	};

	/**
	 * 关闭混音控制面板
	 */
	private void onMixVisibleGone() {
		if (null != listener) {
			listener.onMenuVisible(true);
		}
		logI("onMixVisibleGone", this.toString());

	}

	/**
	 * 返回
	 */
	@Override
	public int onBackPressed() {
		logI("onBackPressed", TAG);
		return super.onBackPressed();
	}

	@Override
	public void onResume() {
		super.onResume();
		logI("onResume", this.toString());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		logI("onDestroyView", this.toString());
		mRootView = null;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		logI("onAttach", this.toString());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		logI("onDestroy", this.toString());
	}

	private void logI(String tag, String msg) {
		Log.i(tag, msg);
	}

	private final int CLEAR = 1, REFLESH = 2;

	private void getMusic() {
		exportMusic();
	}

	/**
	 * 导出内置音乐到sd
	 */
	public void exportMusic() {
		new AsyncTask<Integer, Integer, Integer>() {
			ArrayList<MixInfo> list = new ArrayList<MixInfo>();
			String mixRoot;

			@Override
			protected void onPreExecute() {
				mixRoot = PathUtils.createDir("mixs");
			}

			;

			@Override
			protected Integer doInBackground(Integer... params) {

				File f = new File(mixRoot, "01.mp3");
				if (!f.exists()) {
					// // 导出内置音乐
					AppUtil.assetRes2File(mContext.getAssets(), "mixs/01.mp3",
							f.getAbsolutePath());
				}
				list.add(new MixInfo("00:50", f.getAbsolutePath(), "街头"));

				f = new File(mixRoot, "02.mp3");
				if (!f.exists()) {
					// // 导出内置音乐
					AppUtil.assetRes2File(mContext.getAssets(), "mixs/02.mp3",
							f.getAbsolutePath());
				}
				list.add(new MixInfo("00:48", f.getAbsolutePath(), "旅行"));

				f = new File(mixRoot, "03.mp3");
				if (!f.exists()) {
					// // 导出内置音乐
					AppUtil.assetRes2File(mContext.getAssets(), "mixs/03.mp3",
							f.getAbsolutePath());
				}
				list.add(new MixInfo("00:49", f.getAbsolutePath(), "清新"));

				return 0;
			}

			@Override
			protected void onPostExecute(Integer result) {
				if (isRunning) {
					mixAdapter.update(list);
				}

			}

			;
		}.execute();
	}

}
