package com.rd.demo.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.annotation.SuppressLint;
import android.text.TextUtils;

public class FileUtils {

	private FileUtils() {
	}

	/**
	 * 判断文件是否存在
	 * 
	 * @return
	 */
	public static boolean isExist(String path) {
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file.exists()) {
				if (file.length() > 0) {
					return true;
				} else {
					file.delete();
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * 验证sd卡字幕特效是否存在
	 * 
	 * @param dirpath
	 * @return
	 */
	public static boolean checkDirExit(String dirpath) {

		if (!TextUtils.isEmpty(dirpath)) {

			File f = new File(dirpath);
			if (f.isDirectory()) {

				File[] fl = f.listFiles();

				if (fl.length > 1) { // 至少有个配置文件和一张图片

					return true;

				}

			}

		}

		return false;

	}

	/**
	 * 获取mv的json字符串
	 * 
	 * @param path
	 * @return
	 */
	public static String readTxtFile(String path) {
		try {
			if (TextUtils.isEmpty(path)) {
				return null;
			}
			File file = new File(path);
			if (!file.exists() || file.length() == 0) {
				return null;
			}
			StringBuffer m_sbReponseContent = new StringBuffer();
			String strReponseContent = null;

			InputStream is = null;
			InputStreamReader isr = null;
			java.io.BufferedReader br = null;

			is = new FileInputStream(file);
			isr = new InputStreamReader(is, "UTF-8");
			br = new java.io.BufferedReader(isr);

			String tempbf;
			try {
				while ((tempbf = br.readLine()) != null) {
					m_sbReponseContent.append(tempbf);
					m_sbReponseContent.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				is.close();
				isr.close();
				br.close();
			}
			strReponseContent = m_sbReponseContent.toString().trim();
			return strReponseContent;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static File buildFile(String fileName, boolean isDirectory) {
		File target = new File(fileName);
		if (isDirectory) {
			target.mkdirs();
		} else {
			if (!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
				File fNoMedia = new File(target.getParentFile(), ".nomedia");
				if (!fNoMedia.exists()) {
					try {
						fNoMedia.createNewFile();
					} catch (IOException e) {
					}
				}
				fNoMedia = null;
				target = new File(target.getAbsolutePath());
			}
		}
		return target;
	}

	@SuppressLint("NewApi")
	public static String unzip(String zipFilePath, String targetDir)
			throws IOException {
		OutputStream os = null;
		InputStream is = null;
		ZipFile zipFile = null;
		String directoryPath = "", item = "";
		String mTargetDir = null;
		try {
			zipFile = new ZipFile(zipFilePath);

			if (TextUtils.isEmpty(targetDir)) {
				directoryPath = zipFilePath.substring(0,
						zipFilePath.lastIndexOf("."));
			} else {
				directoryPath = targetDir;
			}
			Enumeration entryEnum = zipFile.entries();
			if (null != entryEnum) {
				ZipEntry zipEntry = null;

				boolean isfirst = true;
				while (entryEnum.hasMoreElements()) {
					zipEntry = (ZipEntry) entryEnum.nextElement();
					item = directoryPath;
					if (zipEntry.isDirectory()) {
						if (directoryPath.endsWith("/")) {
							item = directoryPath + zipEntry.getName();
						} else {
							item = directoryPath + File.separator
									+ zipEntry.getName();
						}

						if (isfirst) {
							mTargetDir = item;
							isfirst = false;

						}
						continue;
					}
					if (zipEntry.getSize() > 0) {
						// 文件
						File targetFile = buildFile(item + File.separator
								+ zipEntry.getName(), false);
						os = new BufferedOutputStream(new FileOutputStream(
								targetFile));
						is = zipFile.getInputStream(zipEntry);
						byte[] buffer = new byte[4096];
						int readLen = 0;
						while ((readLen = is.read(buffer, 0, 4096)) >= 0) {
							os.write(buffer, 0, readLen);
						}

						os.flush();
						os.close();
					} else {
						// 空目录
						buildFile(item + File.separator + zipEntry.getName(),
								true);
					}
				}
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (null != zipFile) {
				zipFile = null;
			}
			if (null != is) {
				is.close();
			}
			if (null != os) {
				os.close();
			}
		}

		if (!TextUtils.isEmpty(mTargetDir)) {

			return mTargetDir;

		}

		return directoryPath;
	}

	// 递归删除指定路径下的所有文件
	public static void deleteAll(File file) {

		if (file.isFile() || file.list().length == 0) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (File f : files) {
				deleteAll(f);// 递归删除每一个文件
				f.delete();// 删除该文件夹
			}
		}
	}

}
