package com.rd.demo.utils;

import java.util.ArrayList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

/**
 * android M请求权限
 * 
 * @author JIAN
 * 
 */
public class PermissionUtils {
	public static final int PERMISSION_MANAGER_FAILED = -2;// 已勾选不再请求权限项
	private static Dialog dialog;

	private static void showMessageOKCancel(Context context, String message,
			DialogInterface.OnClickListener okListener) {

		AlertDialog.Builder ab = new Builder(context);
		ab.setMessage(message);
		ab.setPositiveButton("去打开", okListener);
		ab.setCancelable(false);
		dialog = ab.show();
	}

	@SuppressLint("NewApi")
	public static void checkPermission(final Activity activity,
			int requestCode, final IPermissionListener listener,
			String... permissions) {
		if (null != dialog) {
			dialog.dismiss();
			dialog = null;
		}
		ArrayList<String> missions = new ArrayList<String>();

		if (null != permissions && permissions.length > 0) {
			for (int i = 0; i < permissions.length; i++) {

				if (activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
					missions.add(permissions[i]);
				}

			}
		}
		if (missions.size() > 0) {

			activity.requestPermissions(
					missions.toArray(new String[missions.size()]), requestCode);

		} else {
			if (null != listener) {
				listener.onPermission(PackageManager.PERMISSION_GRANTED);
			}
		}
	}

	static void getFailedPermission(ArrayList<String> needMission,
			String permission) {
		if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			needMission.add("存储");
		} else if (permission.equals(Manifest.permission.CAMERA)) {
			needMission.add("相机");
		} else if (permission.equals(Manifest.permission.RECORD_AUDIO)) {
			needMission.add("麦克风");
		} else if (permission.equals(Manifest.permission.READ_SMS)) {
			needMission.add("短信");
		} else if (permission.equals(Manifest.permission.READ_CONTACTS)) {
			needMission.add("联系人");
		}
	}

	static void onResultFailed(final Context activity,
			ArrayList<String> needMission, int permissionResult,
			final IPermissionListener listener) {
		if (null != needMission && needMission.size() > 0) {
			String rmission = needMission.get(0);
			for (int i = 1; i < needMission.size(); i++) {
				rmission = rmission + "、 " + needMission.get(i);
			}
			String message = "未取得您的 " + rmission
					+ " 使用权限,直播无法开启。请前往应用权限设置打开权限。";
			PermissionUtils.showMessageOKCancel(activity, message,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							AppUtil.gotoAppInfo(activity,
									activity.getPackageName());
							if (null != listener) {
								listener.onPermission(PERMISSION_MANAGER_FAILED);
							}

						}
					});
		} else {
			if (null != listener) {
				listener.onPermission(permissionResult);
			}
		}

	}

	@SuppressLint("NewApi")
	public static void doNext(Activity activity, int[] grantResults,
			String[] permissions, final IPermissionListener listener) {
		int[] oks = new int[permissions.length];
		for (int i = 0; i < grantResults.length; i++) {
			if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
				oks[i] = PackageManager.PERMISSION_GRANTED;
			} else {
				boolean re = activity
						.shouldShowRequestPermissionRationale(permissions[i]);

				if (!re) {
					// 已经勾选不再询问
					// Log.e("---检测权限是否已勾选不再询问", "无法继续请求" + permissions[i]);
					oks[i] = PERMISSION_MANAGER_FAILED;

				} else {
					oks[i] = PackageManager.PERMISSION_DENIED;
					// Log.e("---检测权限是否已勾选不再询问", "可以继续请求" + permissions[i]);
				}

			}
		}

		ArrayList<String> needMission = new ArrayList<String>();
		int target = PackageManager.PERMISSION_GRANTED;// 默认标记允许全部权限
		for (int i = 0; i < oks.length; i++) {

			if (oks[i] == PackageManager.PERMISSION_GRANTED) {
			} else if (oks[i] == PackageManager.PERMISSION_DENIED) {// 可以继续请求
				target = PackageManager.PERMISSION_DENIED;
			} else if (oks[i] == PERMISSION_MANAGER_FAILED) {// 已勾选不再询问,无法继续请求
				if (target != PackageManager.PERMISSION_DENIED) {
					target = PERMISSION_MANAGER_FAILED;
				}
				PermissionUtils
						.getFailedPermission(needMission, permissions[i]);
			}

		}

		if (target == PackageManager.PERMISSION_DENIED) { // 断是否有没有勾选不再询问的结果
			if (null != listener) {
				listener.onPermission(PackageManager.PERMISSION_DENIED);
			}
		} else {
			PermissionUtils.onResultFailed(activity, needMission, target,
					listener);
		}

	}

	/**
	 * android M请求权限
	 * 
	 * @author JIAN
	 * 
	 */
	public static interface IPermissionListener {
		/**
		 * 请求权限是否全部满足
		 * 
		 * @param permissionResult
		 *            0 全部权限都满足 ;-1 有些权限不满足,可以继续请求;-2 已勾选不再请求权限项,会自动跳转到设置权限界面
		 */
		public void onPermission(int permissionResult);
	}

}
