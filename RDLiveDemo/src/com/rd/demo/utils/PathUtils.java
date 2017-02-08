package com.rd.demo.utils;

import java.io.File;
import java.io.IOException;

/**
 * @author JIAN
 * @date 2017-1-20 下午2:27:43
 */
public class PathUtils {
	private static String m_sRootPath;//sd文件夹
	private static String m_sTempPath;

	/**
	 * 检查path，如不存在创建之<br>
	 * 并检查此路径是否存在文件.nomedia,如没有创建之
	 * 
	 * @param path
	 */
	private static void checkPath(File path) {
		File fNoMedia;
		if (!path.exists())
			path.mkdirs();
		fNoMedia = new File(path, ".nomedia");
		if (!fNoMedia.exists()) {
			try {
				fNoMedia.createNewFile();
			} catch (IOException e) {
			}
		}
		fNoMedia = null;
	}

	/**
	 * 解析文件存储路径
	 * 
	 * @param strRootPath
	 *            当前调用来自哪里
	 */
	public static void initialize(String strRootPath) {
		File path = new File(strRootPath);
		checkPath(path);
		m_sRootPath = path.toString();
		path = new File(path,"temp/");
		checkPath(path);
		m_sTempPath = path.toString();

	}

 

	/**
	 * 创建指定的文件夹
	 * 
	 * @param dir
	 * @return 生成的文件夹的完全路径
	 */
	public static String createDir(String dir) {
		File path = new File(m_sRootPath);
		checkPath(path);
		path = new File(m_sRootPath, dir + "/");
		checkPath(path);
		return path.toString();
	}

	/**
	 * 获取目录的临时文件
	 * @return  /sdcard/rootDir/temp/
     */
	public static String getTempPath(){
		return m_sTempPath;
	}

}
