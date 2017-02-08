package com.rd.live.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


public class RdGiftPage extends GridView {

	public RdGiftPage(Context context) {
		super(context);
	}

	public RdGiftPage(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public RdGiftPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && null != iback) {
			iback.getSize(getWidth(), getHeight());
		}
	}

	private iGiftSizeCallBack iback;

	public void setGiftSizeListener(iGiftSizeCallBack iback) {
		this.iback = iback;
	}

	public interface iGiftSizeCallBack {

		public void getSize(int width, int height);
	}

}
