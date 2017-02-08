/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rd.live.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * A view that contains camera zoom control and its layout.
 */
class ZoomControlBar extends ZoomControl {
	@SuppressWarnings("unused")
	private static final String TAG = "ZoomControlBar";
	private static final int THRESHOLD_FIRST_MOVE = 20; // pixels
	// Space between indicator icon and the zoom-in/out icon.
	private static final int ICON_SPACING = 24;

	private View mBar;
	private boolean mStartChanging;
	private int mSliderPosition = 0;
	private int mSliderLength;
	private int mWidth;
	private int mIconWidth;
	private int mTotalIconWidth;
	private int mPaddingLeft = 10, mPaddingRight = 20;

	public ZoomControlBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBar = new View(context);
		getMetrics(context);
		addView(mBar);
		mPaddingLeft = dpToPixel(5);
		mPaddingRight = dpToPixel(10);

	}

	private DisplayMetrics getMetrics(Context context) {
		if (null == metrics) {
			metrics = new DisplayMetrics();
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);

			wm.getDefaultDisplay().getMetrics(metrics);
		}
		return metrics;
	}

	private float sPixelDensity = 1;
	private DisplayMetrics metrics;

	private int dpToPixel(float dp) {
		return Math.round(sPixelDensity * dp);
	}

	@Override
	public void setActivated(boolean activated) {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			super.setActivated(activated);
			mBar.setActivated(activated);
		}
	}

	private int getSliderPosition(int x) {
		// Calculate the absolute offset of the slider in the zoom control bar.
		// For left-hand users, as the device is rotated for 180 degree for
		// landscape mode, the zoom-in bottom should be on the top, so the
		// position should be reversed.
		int pos; // the relative position in the zoom slider bar
		if (mOrientation == 90) {
			pos = mWidth - mTotalIconWidth - x;
		} else {
			pos = x - mTotalIconWidth;
		}
		if (pos < 0)
			pos = 0;
		if (pos > mSliderLength)
			pos = mSliderLength;
		return pos;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mWidth = w;
		mIconWidth = mZoomIn.getMeasuredWidth();
		mTotalIconWidth = mIconWidth + ICON_SPACING;
		mSliderLength = mWidth - (2 * mTotalIconWidth);
		// mSliderLength -= (mPaddingLeft + mPaddingRight);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (!isEnabled() || (mWidth == 0))
			return false;
		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			setActivated(false);
			closeZoomControl();
			break;

		case MotionEvent.ACTION_DOWN:
			setActivated(true);
			mStartChanging = false;
		case MotionEvent.ACTION_MOVE:
			int pos = getSliderPosition((int) event.getX());
			if (!mStartChanging) {
				// Make sure the movement is large enough before we start
				// changing the zoom.
				int delta = mSliderPosition - pos;
				if ((delta > THRESHOLD_FIRST_MOVE)
						|| (delta < -THRESHOLD_FIRST_MOVE)) {
					mStartChanging = true;
				}
			}
			if (mStartChanging) {
				performZoom(1.0d * pos / mSliderLength);
				mSliderPosition = pos;
			}
			requestLayout();
		}
		return true;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (mZoomMax == 0)
			return;
		int height = bottom - top;
		mBar.layout(mTotalIconWidth, 0, mWidth - mTotalIconWidth, height);
		// For left-hand users, as the device is rotated for 180 degree,
		// the zoom-in button should be on the top.
		int pos; // slider position
		int sliderPosition;
		if (mSliderPosition != -1) { // -1 means invalid
			sliderPosition = mSliderPosition;
		} else {
			sliderPosition = (int) ((double) mSliderLength * mZoomIndex / mZoomMax);
		}
		if (mOrientation == 90) {
			mZoomIn.layout(mPaddingLeft, 0, mIconWidth + mPaddingLeft, height);
			mZoomOut.layout(mWidth - mIconWidth - mPaddingRight, 0, mWidth,
					height);
			pos = mBar.getRight() - sliderPosition;
		} else {
			mZoomOut.layout(mPaddingLeft, 0, mIconWidth + mPaddingLeft, height);
			mZoomIn.layout(mWidth - mIconWidth - mPaddingRight, 0, mWidth,
					height);
			pos = mBar.getLeft() + sliderPosition;
		}
		int sliderWidth = mZoomSlider.getMeasuredWidth();
		mZoomSlider.layout((pos - sliderWidth / 2), 0, (pos + sliderWidth / 2),
				height);
	}

	@Override
	public void setZoomIndex(int index) {
		super.setZoomIndex(index);
		mSliderPosition = -1; // -1 means invalid
		requestLayout();
	}

	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	// {
	// if (!(this instanceof RelativeLayout))
	// throw new UnsupportedOperationException(" this must be RelativeLayout");
	// int i = Utils.measureHeightByBackground(this);
	// // Log.d(TAG, "i="+Integer.toString(i));
	// super.onMeasure(widthMeasureSpec, i);
	// }
}
