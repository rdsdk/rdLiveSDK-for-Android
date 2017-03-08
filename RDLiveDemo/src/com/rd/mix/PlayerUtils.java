package com.rd.mix;

import java.io.IOException;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rd.demo.utils.DateTimeUtils;
import com.rd.recorder.AudioPlayer;
import com.rd.recorder.AudioPlayer.OnCompletionListener;
import com.rd.recorder.AudioPlayer.OnErrorListener;
import com.rd.recorder.AudioPlayer.OnInfoListener;
import com.rd.recorder.AudioPlayer.OnPreparedListener;

public class PlayerUtils {

	private static AudioPlayer player;// 混音播放器

	private static PlayerUtils instance;

	public static PlayerUtils getInstance() {
		if (null == instance) {
			instance = new PlayerUtils();
		}
		return instance;
	}

	public interface IMuisic {
		public void onMusicPrepared(int duration);
	}

	/**
	 * 初始化音乐
	 * 
	 * @param info
	 *            mix对象
	 */
	public void initPlayer(final Context context, final MixInfo info,
			final IMuisic iListener) {
		if (null != player) {
			player.stop();
			player.release();
		}
		lastProgress = DEFAULT_PROGRESS;
		player = new AudioPlayer();
		try {
			player.setDataSource(info.getPath());
			player.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(AudioPlayer mp) {
					info.setDuration(DateTimeUtils.millsTommS(mp.getDuration()));
					iListener.onMusicPrepared(mp.getDuration());
					start();// 初始化完成就播放
				}
			});
			player.setOnInfoListener(new OnInfoListener() {

				@Override
				public boolean onInfo(AudioPlayer mp, int what, int extra) {
					return false;
				}
			});
			player.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(AudioPlayer mp) {
					player.seekTo(0);
					start();// 无限循环播放
				}
			});
			player.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(AudioPlayer mp, int what, int extra) {
					Log.e("playerUtils", "onerror.." + what + "..." + extra);
					Toast.makeText(context, "不支持该音乐", Toast.LENGTH_SHORT)
							.show();
					return false;
				}
			});
			player.prepareAsync();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 开始播放音乐
	 */
	private void start() {
		if (null != player && !player.isPlaying()) {
			player.start();
		}
	}

	private final int DEFAULT_PROGRESS = -1;
	private long lastProgress = DEFAULT_PROGRESS;

	/**
	 * 暂停混音
	 */
	public void onPasue() {
		if (null != player) {
			lastProgress = player.getCurrentPosition();
			if (player.isPlaying()) {
				player.pause();
			}
		} else {
			lastProgress = DEFAULT_PROGRESS;
		}
	}

	/**
	 * 恢复播放
	 */
	public void onResume() {
		if (null != player && lastProgress != DEFAULT_PROGRESS) {
			player.seekTo((int) lastProgress);
			lastProgress = DEFAULT_PROGRESS;
			player.start();
		} else {
			lastProgress = DEFAULT_PROGRESS;
		}
	}

	/**
	 * 销毁混音播放器
	 */
	public void release() {
		if (null != player) {
			player.setOnPreparedListener(null);
			player.setOnCompletionListener(null);
			player.setOnInfoListener(null);
			player.stop();
			player.release();
			player = null;
		}
		lastProgress = DEFAULT_PROGRESS;
		instance = null;
		System.gc();
	}

	private boolean isOsdEd = false;

	/**
	 * 全局变量是否打开水印
	 * 
	 * @return
	 */
	public boolean isOsdEd() {
		return isOsdEd;
	}

	/**
	 * 设置是否打开osd
	 * 
	 * @param isOsded
	 */
	public void setOsd(boolean isOsded) {
		isOsdEd = isOsded;
	}

}
