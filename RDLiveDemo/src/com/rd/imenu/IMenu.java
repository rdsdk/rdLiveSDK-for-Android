package com.rd.imenu;

/**
 * 菜单控制栏回调
 * 
 * @author JIAN
 * 
 */
public interface IMenu {

	/**
	 * 是否显示菜单控制栏
	 * 
	 * @param isVisible
	 *            true,显示;flase ,不显示
	 */
	public void onMenuVisible(boolean isVisible);

}
