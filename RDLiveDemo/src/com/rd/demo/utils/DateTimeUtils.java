package com.rd.demo.utils;

import java.util.Formatter;
import java.util.Locale;

/**
 * 时间日期及格式化工具
 * 
 * @author abreal
 * 
 */
public class DateTimeUtils {

	private static final StringBuilder m_sbFormator = new StringBuilder();
	private static final Formatter m_formatter = new Formatter(m_sbFormator,
			Locale.getDefault());

	/**
	 * 毫秒数转换为时间格式化字符串支持毫秒
	 * 
	 * @param timeMs
	 * @return
	 */
	public static String stringForMillisecondTime(long timeMs,
			boolean isShowMillisecond, boolean alignment) {
		return stringForTime(timeMs, false, true, isShowMillisecond, alignment);
	}

	/**
	 * 毫秒数转换为时间格式化字符串 支持是否显示小时或毫秒
	 * 
	 * @param timeMs
	 * @param existsHours
	 * @param existsMillisecond
	 * @param alignment
	 *            是否需要统计显示宽度，如为true时，5:4.5就为05:04.5
	 * @return
	 */
	public static String stringForTime(long timeMs, boolean existsHours,
			boolean exitsMin, boolean existsMillisecond, boolean alignment) {
		boolean bNegative = timeMs < 0;// 是否为负数
		if (bNegative) {
			timeMs = -timeMs;
		}
		int totalSeconds = (int) (timeMs / 1000);// 总计时间
		int millisecond = (int) (timeMs % 1000) / 10;// 毫秒
		int seconds = totalSeconds % 60;// 秒
		int minutes = (totalSeconds / 60) % 60;// 分
		int hours = totalSeconds / 3600;// 时

		m_sbFormator.setLength(0);
		try {
			// 判断是否支持小时
			if (hours > 0 || existsHours) {
				return m_formatter.format(
						alignment ? "%s%02d:%02d:%02d" : "%s%d:%d:%d",
						bNegative ? "-" : "", hours, minutes, seconds)
						.toString();

			} else if (existsMillisecond) {

				if (exitsMin) {

					if (minutes > 0 || alignment) {
						return m_formatter.format(
								alignment ? "%s%02d:%02d.%d" : "%s%d:%d.%d",
								bNegative ? "- " : "", minutes, seconds,
								millisecond).toString();
					} else {
						return m_formatter.format(
								alignment ? "%s%02d.%d" : "%s%d.%d",
								bNegative ? "- " : "", seconds, millisecond)
								.toString();
					}
				} else {
					int sec = hours * 60 * 60 + minutes * 60 + seconds;
					return m_formatter.format("%d.%d", sec, millisecond)
							.toString();
				}
			} else {
				return m_formatter.format(
						alignment ? "%s%02d:%02d" : "%s%d:%d",
						bNegative ? "- " : "", minutes, seconds).toString();
			}
		} catch (Exception ex) {
			return "";
		}
	}

	/**
	 * 获取视频当前播放时间格式化后的字符串
	 * 
	 * @param duration
	 * @return   70:59
	 */
	public static String updateTime(int duration) {
		return DateTimeUtils.stringForTime(duration, false, true, false, true);
	}
}
