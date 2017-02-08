package com.rd.flash;

import java.io.File;

public class FlashItem {
	public FlashItem(File flashDir) {

		this.flashName = flashDir.getName();
		this.sdPath = flashDir.getAbsolutePath();
		this.bmpPath = sdPath + "/" + flashName + ".png";

	}

	/**
	 * 个例，第一个无
	 * 
	 * @param none
	 */
	public FlashItem(String none) {

		this.flashName = none;

	}

	public String getFlashName() {
		return flashName;
	}

	public String getSdPath() {
		return sdPath;
	}

	public String getBmpPath() {
		return bmpPath;
	}

	private String flashName, sdPath, bmpPath;
}
