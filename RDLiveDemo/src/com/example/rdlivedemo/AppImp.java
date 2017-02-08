package com.example.rdlivedemo;

import java.io.File;
import java.io.IOException;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.rd.demo.utils.FileUtils;
import com.rd.demo.utils.PathUtils;
import com.rd.flash.FlashHandler;
import com.rd.lib.utils.CoreUtils;
import com.rd.lib.utils.ThreadPoolUtils;
import com.rd.live.RDLiveSDK;

public class AppImp extends Application {
	/**
	 * 锐动云控平台申请的appKey,Secret<br>
	 * 锐动云控平台地址：<a
	 * href="http://dianbook.17rd.com/business/app/myapp">http://dianbook
	 * .17rd.com/business/app/myapp</a><br>
	 */

	private final static String APPKEY = "631c39cb35047e92";
	private final static String APPSECRET = "be9d6d860c077aef2d12282e316b2618m8WwCu9819CSq8P5L1G4NB9mhdlgvmdOpv0r880CEXxZBTOHShUzT1XrS8bUSeEzNyxeDLfGDyTK7QivgkiUMIuIQ9LUmFzrIIGDJ80BtA4gJN+W3GPaKnceVWy0FLuXPZ7HlOz2xNQmUgC8okbdECAObY3bnjPUcPTVXRPmm9d/y9W7fCJJ8phEIxyIlcNrCBVWzaZ1WD6RgoH6Uk8uDdQB0LTL6+na25CxIIfwBVwR1Oeyyr33dMZTwG46XSJ/HDMyeveb3k+nXjUn9lnANUIrQSeWkzfwbRimAKT6UmJvYvHsw/AgLi8S7csApkC7e5/sEDxpM+ObsvWgXXCAR/qJ4Kwc5tqgXzgCFCWh2jOlUkTRDuq4bebd28a5UN2mgZtg77tid94O1NtXx6zRl2WRTZN8bhpC87dKxh8kRtIkI8oRSvv+dRxBDIWE88xjkkdX0PXZMRi+o/UtIhqGc3gbVT06pGa7FuJJvg+wPPA/UmDZIbiVs7tEqrbkYpGa";

	@Override
	public void onCreate() {
		super.onCreate();
		// 初始appkey和appsecret
		init(this);

	}

	static void init(Context context) {
		// 初始化sdk
		RDLiveSDK.init(context, APPKEY, APPSECRET);
		// 强制设置为debug模式
		RDLiveSDK.enableDebug(false);

		PathUtils.initialize(Environment.getExternalStorageDirectory() + "/"
				+ context.getApplicationInfo().packageName);

		// 加载内置资源
		initAssetRes2File(context);
	}

	/**
	 * 处理资源文件，耗时操作，
	 * 
	 * @param ctx
	 */
	private static void initAssetRes2File(final Context ctx) {

		ThreadPoolUtils.executeEx(new Runnable() {

			@Override
			public void run() {
				String assetDir = PathUtils.createDir(FlashHandler.ASSET);

				String flashDirName = "frame_shot";
				File flashRoot = new File(assetDir, flashDirName);
				if (!flashRoot.exists()) {
					flashRoot.mkdirs();
				}

				AssetManager ast = ctx.getAssets();
				try {
					String[] tlist = ast.list(flashDirName);
					int len = tlist.length;
					for (int i = 0; i < len; i++) {
						initItemAsset(ast, flashRoot, tlist[i], assetDir);
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

	}

	private static void initItemAsset(AssetManager ast, File flashRoot,
			String flashName, String unzipPath) {
		File fsdzip = new File(flashRoot, flashName);
		CoreUtils.assetRes2File(ast, flashRoot.getName() + "/" + flashName,
				fsdzip.getAbsolutePath());
		try {
			FileUtils.unzip(fsdzip.getAbsolutePath(), unzipPath);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 解压完成就删除
		fsdzip.delete();

	}

}
