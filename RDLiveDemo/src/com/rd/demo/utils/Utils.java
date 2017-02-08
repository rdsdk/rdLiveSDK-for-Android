package com.rd.demo.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.view.OrientationEventListener;
import android.view.Surface;


public class Utils {

	public static final int UNCONNECTED = 0;
	public static final int MOBILECONNECTED = 1;
	public static final int WIFICONNECTED = 2;
	public static final int UNKNOWCONNECTED = 3;

	/**
	 * 获取网络情况
	 * 
	 * @param c
	 * @return {@link Utils#UNCONNECTED} 无网络连接
	 *         {@link Utils#MOBILECONNECTED} 移动数据
	 *         {@link Utils#WIFICONNECTED} wifi
	 */
	public static int checkNetworkInfo(Context c) {
		ConnectivityManager manager = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return UNCONNECTED;
		}
		State mobile = NetworkInfo.State.UNKNOWN;
		State wifi = NetworkInfo.State.UNKNOWN;
		NetworkInfo ni = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (ni != null) {
			mobile = ni.getState();
		}
		ni = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (ni != null) {
			wifi = ni.getState();
		}
		if (wifi.equals(State.CONNECTED)) {
			return WIFICONNECTED;
		} else if (mobile.equals(State.CONNECTED)) {
			return MOBILECONNECTED;
		} else {
			return UNKNOWCONNECTED;
		}
	}

	public static boolean hasJellyBeanMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static int getDisplayRotation(Activity activity) {
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		return 0;
	}

	public static final int ORIENTATION_HYSTERESIS = 5;

	/**
	 * 根据需要设置的方向角度和当前(旧的)方向角度,计算出合适的新的角度
	 * 
	 * @param orientation
	 * @param orientationHistory
	 * @return
	 */
	public static int roundOrientation(int orientation, int orientationHistory) {
		boolean changeOrientation = false;
		if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
			changeOrientation = true;
		} else {
			int dist = Math.abs(orientation - orientationHistory);
			dist = Math.min(dist, 360 - dist);
			changeOrientation = (dist >= 45 + ORIENTATION_HYSTERESIS);
		}
		if (changeOrientation) {
			return ((orientation + 45) / 90 * 90) % 360;
		} else {
			return orientationHistory;
		}
	}
}
