package com.example.rdlivedemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.rd.recorder.OSDBuilder;

/**
 * 构造OSD 水印相关
 * 
 * @author abreal<br/>
 * 
 */
public class DemoOSDBuilder extends OSDBuilder {
	private StringBuilder m_sbBuilder = new StringBuilder();
	private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss S",
			Locale.getDefault());

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public DemoOSDBuilder(Context context) {
		super(context);
		setOSDGravity(Gravity.BOTTOM | Gravity.LEFT);// 指定OSD显示位置，默认显示在左下角
	}

	/**
	 * 返回OSD view
	 */
	@Override
	protected View getOSDView(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.osd_textview,
				null);
		onRefreshOSDView(view);
		return view;
	}

	private String buildOSDString(Date date) {

		m_sbBuilder.setLength(0);
		m_sbBuilder.append(DateFormat.format("yyyy年MM月dd日  ", date)).append(
				sdfTime.format(date));

		return m_sbBuilder.toString();
	}

	/**
	 * 刷新OSD
	 */
	@Override
	protected void onRefreshOSDView(View vOSD) {
		TextView tvOSD = (TextView) vOSD.findViewById(R.id.tvOSD);
		tvOSD.setText(buildOSDString(new Date()));
	}

	@Override
	protected void cleanUp() {

	}
}
