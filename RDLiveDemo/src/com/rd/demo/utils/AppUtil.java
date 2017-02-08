package com.rd.demo.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class AppUtil {

	/**
	 * 调转到应用权限设置
	 * 
	 * @param context
	 * @param packagename
	 */

	public static void gotoAppInfo(Context context, String packagename) {
		try {
			Uri packageURI = Uri.parse("package:" + packagename);
			Intent intent = new Intent(
					Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Support Android M
	 * @return
	 */
	public static boolean hasM() {
		return Build.VERSION.SDK_INT >= 23;
	}

	/**
	 * 将asset文件保存为指定文件
	 * 
	 * @param am
	 * @param strAssetFile
	 * @param strDstFile
	 * @throws IOException
	 */
	public static boolean assetRes2File(AssetManager am, String strAssetFile,
			String strDstFile) {
		OutputStream os = null;
		try {
			os = new FileOutputStream(strDstFile);
			byte[] pBuffer = new byte[1024];
			int nReadLen;
			if (null == am) {
				return false;
			}
			InputStream is = am.open(strAssetFile);
			while ((nReadLen = is.read(pBuffer)) != -1) {
				os.write(pBuffer, 0, nReadLen);
			}
			os.flush();
			os.close();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

}
