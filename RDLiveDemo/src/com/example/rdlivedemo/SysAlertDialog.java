package com.example.rdlivedemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/**
 * 显示系统级提示框
 * 
 * @author abreal
 * 
 */
public class SysAlertDialog {

 
	private static ExtProgressDialog m_dlgLoading;

	/**
	 * 创建并显示提示框
	 * 
	 * @param context
	 * @param strMessage
	 */
	public static ExtProgressDialog showLoadingDialog(Context context,
			String strMessage) {
		return showLoadingDialog(context, strMessage, true,
				new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						cancelLoadingDialog();
					}
				});
	}

	/**
	 * 创建并显示提示框
	 * 
	 * @param context
	 * @param strMessage
	 */
	public static ExtProgressDialog showLoadingDialog(Context context,
			String strMessage, boolean cancelable, OnCancelListener listener) {
		if (m_dlgLoading == null) {
			m_dlgLoading = new ExtProgressDialog(context);
			m_dlgLoading.setMessage(strMessage);
			m_dlgLoading.setCanceledOnTouchOutside(false);
			m_dlgLoading.setCancelable(cancelable);
			m_dlgLoading.setOnCancelListener(listener);
		}
		try {
			if (null != m_dlgLoading) {
				m_dlgLoading.show();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return m_dlgLoading;
	}

	/**
	 * 取消加载中对话框
	 */
	public static synchronized void cancelLoadingDialog() {
		if (m_dlgLoading != null) {
			try {
				m_dlgLoading.cancel();
			} catch (Exception ex) {
			}
			m_dlgLoading = null;
		}
	}

	 
}
