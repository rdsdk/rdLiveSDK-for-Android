package com.example.rdlivedemo;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.rd.demo.utils.DateTimeUtils;
import com.rd.live.ijk.RdLivePlayer;

/**
 * 播放页demo
 * 
 * @author jian
 */
public class PlayActivity extends Activity {

	private static String LIVE_URL_TAG = "liveUrl";
	private static String THUMB_IMG = "thumbimg";
	private static String TITLE = "title";
	private static String PLAY_TAG = "playTag";
	private static String LIVEID = "liveId";

	private final int PLAYER_TIMEOUT = 5000;// 超时 5秒
	private String liveUrl;
	private String title;
	private LiveType playTag;

	/**
	 * 创建枚举，类别是:正在直播、网络mp4地址
	 * 
	 * @author JIAN
	 */
	public static enum LiveType {
		rtmp, mp4;

		@Override
		public String toString() {
			switch (this) {
			case rtmp:
				return "rtmp";
			default:
				return "mp4";
			}
		}

		/**
		 * 解析平台名称
		 * 
		 * @param name
		 * @return
		 */
		public static LiveType valueBy(String name) {
			if (name.equalsIgnoreCase("rtmp")) {
				return rtmp;
			} else {
				return mp4;
			}
		}
	}

	private SeekBar videoProgressBar;
	private ImageView playBtn;
	private RdLivePlayer vvPlay;
	private TextView tvDuration;
	private String liveId = null;
	private CheckBox ckFullScreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_play);
		Intent intent = getIntent();
		liveUrl = intent.getStringExtra(LIVE_URL_TAG);
		title = intent.getStringExtra(TITLE);
		playTag = LiveType.valueBy(intent.getStringExtra(PLAY_TAG));
		if (TextUtils.isEmpty(liveUrl)) {
			finish();
			return;
		}
		if (playTag == LiveType.rtmp) {
			SysAlertDialog.showLoadingDialog(PlayActivity.this, "正在缓冲,请稍后...");
			liveId = intent.getStringExtra(LIVEID);
		}
		initView();
		initVedioView();

	}

	private View look_lived;
	private View flIndicator;

	private void initView() {
		ckFullScreen = (CheckBox) findViewById(R.id.ckFullScreen);
		ckFullScreen.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (null != vvPlay) {
					vvPlay.enableFullScreen(isChecked);
				}

			}
		});
		look_lived = findViewById(R.id.look_lived_layout);
		findViewById(R.id.look_living_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackPressed();
					}
				});
		tvDuration = (TextView) findViewById(R.id.tvEditorDuration);
		ImageView videoImage = (ImageView) findViewById(R.id.videoimgae);
		videoImage.setImageResource(R.drawable.waiting);
		TextView titleTV = (TextView) findViewById(R.id.titleTV);
		videoProgressBar = (SeekBar) findViewById(R.id.videoProgessbar);
		playBtn = (ImageView) findViewById(R.id.playBtn);

		vvPlay = (RdLivePlayer) findViewById(R.id.vv_play);
		vvPlay.setTimeOut(PLAYER_TIMEOUT);
		flIndicator = findViewById(R.id.fl_indicator);

		judgeLive(playTag);

		vvPlay.setMediaBufferingIndicator(flIndicator);
		vvPlay.setVideoPath(liveUrl, liveId);
		vvPlay.requestFocus();

		titleTV.setText(title);
		if (islived()) {
			videoProgressBar
					.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							onVideoStart();
						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							onVideoPause();

						}

						@Override
						public void onProgressChanged(SeekBar seekBar,
								int progress, boolean fromUser) {
							if (fromUser) {
								vvPlay.seekTo((progress * duration / 100));
							}
						}
					});
		}

		videoImage.setScaleType(ScaleType.CENTER_CROP); // 居中裁剪

	}

	/**
	 * 处理播放进度runnable
	 */
	private Runnable m_getPlayProgressRunnable = new Runnable() {

		@Override
		public void run() {
			int nPosition = vvPlay.getCurrentPosition();
			vvPlay.postDelayed(this, 280);
			videoProgressBar
					.setProgress((int) (nPosition / (duration + .0) * 100));
		}
	};

	/**
	 * 初始化部分组件
	 */
	private void initVedioView() {

		playBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (vvPlay.isPlaying()) {
					onVideoPause();
				} else {
					onVideoStart();
				}
			}
		});

		vvPlay.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(IMediaPlayer mp) {

				Log.i("onPrepared", "onPrepared: ");
				SysAlertDialog.cancelLoadingDialog();
				duration = (int) mp.getDuration();
				tvDuration.setText(DateTimeUtils.stringForMillisecondTime(
						duration, true, true));
				videoProgressBar.setMax(100);
			}
		});
		vvPlay.setOnInfoListener(new IMediaPlayer.OnInfoListener() {

			int last = 0;

			@Override
			public boolean onInfo(IMediaPlayer mp, int what, int extra) {
				Log.i("info....",
						what + "...." + extra + "--" + mp.getVideoWidth() + "*"
								+ mp.getVideoHeight());
				if (what == IMediaPlayer.ANCHOR_PASUING) {// 主播离开的特别标识
					SysAlertDialog.cancelLoadingDialog();
					if (null != flIndicator
							&& flIndicator.getVisibility() != View.VISIBLE) {
						flIndicator.setVisibility(View.VISIBLE);
					}

				} else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
					if (null != flIndicator
							&& flIndicator.getVisibility() == View.VISIBLE) {
						flIndicator.setVisibility(View.GONE);
					}
					if (last != what)
						SysAlertDialog.showLoadingDialog(PlayActivity.this,
								"正在缓冲,请稍后...").show();

				} else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
					if (null != flIndicator
							&& flIndicator.getVisibility() == View.VISIBLE) {
						flIndicator.setVisibility(View.GONE);
					}
					SysAlertDialog.cancelLoadingDialog();

				} else if (what == IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START) {// vvPlay.start()，开始音频回调,播放器接收到画面，可隐藏遮罩部分
					SysAlertDialog.cancelLoadingDialog();
					if (null != flIndicator
							&& flIndicator.getVisibility() == View.VISIBLE) {
						flIndicator.setVisibility(View.GONE);
					}
				} else {
					SysAlertDialog.cancelLoadingDialog();
					if (null != flIndicator
							&& flIndicator.getVisibility() == View.VISIBLE) {
						flIndicator.setVisibility(View.GONE);
					}
				}
				last = what;
				return false;
			}
		});
		vvPlay.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {

			@Override
			public void onCompletion(IMediaPlayer mp) {
				Log.d("onCompletion....", " ....." + mp.getDuration());
				if (vvPlay != null) {
					vvPlay.removeCallbacks(m_getPlayProgressRunnable);
					if (playTag != null && playTag == LiveType.mp4) {
						onComplete();
					} else {
						if (null != flIndicator
								&& flIndicator.getVisibility() == View.VISIBLE) {
							flIndicator.setVisibility(View.GONE);
						}
						try {
							onExitLiving("主播已退出房间");
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
		vvPlay.setOnErrorListener(new IMediaPlayer.OnErrorListener() {

			@Override
			public boolean onError(IMediaPlayer mp, int what, int extra) {
				Log.e(this.toString(), "setOnErrorListener...onError." + what
						+ "...." + extra);
				if (null != flIndicator
						&& flIndicator.getVisibility() == View.VISIBLE) {
					flIndicator.setVisibility(View.GONE);
				}
				if (what == -IMediaPlayer.ANCHOR_EXIT
						&& !(playTag == LiveType.mp4)) {
					onExitLiving("主播已退出房间");
				} else {
					onExitLiving("播放失败!");
				}
				return false;
			}

		});

		vvPlay.setKeepScreenOn(true);
		onVideoStart();
	}

	private int duration = 1;

	private void onExitLiving(String msg) {

		AlertDialog.Builder ab = new AlertDialog.Builder(this);
		ab.setTitle(msg);
		ab.setItems(new String[] { "退出" },
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						onBackPressed();
					}
				});
		ab.setCancelable(false);
		ab.show();

	}

	private void onComplete() {
		vvPlay.seekTo(0);
		vvPlay.pause();
		videoProgressBar.setProgress(0);
		playBtn.setImageResource(R.drawable.btn_play);

	}

	/*
	 * 判断是直播 还是 回放
	 */
	public void judgeLive(LiveType liveTag) {
		if (liveTag == LiveType.mp4) {
			look_lived.setVisibility(View.VISIBLE);
		} else {

			look_lived.setVisibility(View.GONE);
		}
	}

	private void onVideoStart() {
		vvPlay.start();
		playBtn.setImageResource(R.drawable.btn_pause);
		vvPlay.removeCallbacks(m_getPlayProgressRunnable);
		if (islived()) {
			vvPlay.post(m_getPlayProgressRunnable);
		}
	}

	/**
	 * 不是直播 true 不是，false 是直播
	 * 
	 * @return
	 */
	private boolean islived() {
		return (playTag != null && playTag == LiveType.mp4);
	}

	private void onVideoPause() {
		vvPlay.pause();
		playBtn.setImageResource(R.drawable.btn_play);
		vvPlay.removeCallbacks(m_getPlayProgressRunnable);
	}

	@Override
	public void onBackPressed() {
		if (null != vvPlay) {
			vvPlay.removeCallbacks(m_getPlayProgressRunnable);
			if (vvPlay.isPlaying()) {
				onVideoPause();
				vvPlay.stopPlayback();
			}
			vvPlay = null;
		}
		super.onBackPressed();

	}

	/**
	 * 跳转到播放界面
	 * 
	 * @param context
	 * @param rtmpOUrl
	 *            播放地址
	 * @param thumbImgUrl
	 *            视频缩率图
	 * @param title
	 *            视频标题
	 * @param liveId
	 *            直播id (看UID直播需要)
	 */
	public static void gotoPlay(Context context, String rtmpOUrl,
			String thumbImgUrl, String title, String liveId) {
		if (!TextUtils.isEmpty(rtmpOUrl)) {
			Intent intent = new Intent(context, PlayActivity.class);
			intent.putExtra(LIVE_URL_TAG, rtmpOUrl);
			intent.putExtra(THUMB_IMG, thumbImgUrl);
			intent.putExtra(TITLE, title);
			intent.putExtra(LIVEID, liveId);
			intent.putExtra(PLAY_TAG, (rtmpOUrl.startsWith("rtmp:") || rtmpOUrl
					.startsWith("rtsp:")) ? LiveType.rtmp.toString()
					: LiveType.mp4.toString());

			context.startActivity(intent);
		} else {
			Toast.makeText(context, "播放地址为空", Toast.LENGTH_SHORT).show();
		}

	}

}
