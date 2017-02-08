package com.example.rdlivedemo;

import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;

import com.rd.imenu.IMenuListener;
import com.rd.live.RDLiveSDK;

/**
 * Created by JIAN on 2017/1/19.
 */

public class BeautifyHandler {
	private View mRootView;
	private Context mContext;
	private IMenuListener listener;
	private SeekBar sbar;
	private TextView tvLevel;

	public BeautifyHandler(View beautyLyout, IMenuListener menuListener) {
		listener = menuListener;
		mRootView = beautyLyout;
		mContext = mRootView.getContext();
		initViews(mRootView);
	}

	private void initViews(View view) {

		sbar = (SeekBar) view.findViewById(R.id.liveLevelBar);
		tvLevel = (TextView) view.findViewById(R.id.tvBeautifyLevel);
		int mlevel = RDLiveSDK.getBeautifyLevel();
		sbar.setProgress(mlevel);// 当前被应用的美颜等级
		tvLevel.setText(mContext.getString(R.string.beautifyLevel, mlevel));

		sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mRootView.postDelayed(mPostRunnable, 3000);// 自动消失

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				mRootView.removeCallbacks(mPostRunnable);
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser) {
					int level = progress;// 美颜等级1-5
					tvLevel.setText(mContext.getString(R.string.beautifyLevel,
							level));
					RDLiveSDK.setBeautifyLevel(level);
				}

			}
		});
	}

	// 自动消失
	private Runnable mPostRunnable = new Runnable() {

		@Override
		public void run() {
			mRootView.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.alpha_out));
			mRootView.setVisibility(View.GONE);
			if (null != listener) {
				listener.onMenuGone();
			}
		}
	};

	/**
	 * 调整美颜等级和UI
	 * @param enable true打开美颜调整等级;flase 关闭美颜，关闭UI
     */
	public void onBeautifyShow(boolean enable) {
		if(enable) {//打开美颜
			mRootView.setVisibility(View.VISIBLE);
			mRootView.postDelayed(mPostRunnable, 3000);// 自动消失
		}else{//关闭美颜
			mRootView.removeCallbacks(mPostRunnable);
			mRootView.post(mPostRunnable);
		}

	}

}
