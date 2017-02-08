package com.rd.live.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.rd.recorder.ICameraZoomHandler;

public class GlTouchView extends FrameLayout {

	public GlTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_flignerDetector = new GestureDetector(context,
				new pressGestureListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (null != m_flignerDetector) {
			m_flignerDetector.onTouchEvent(event);
		}
		if (null != m_hlrZoom) {
			m_hlrZoom.onTouch(event);
		}
		return true;
	}

	/**
	 * 手势listener
	 * 
	 * @author abreal
	 * 
	 */
	private class pressGestureListener extends SimpleOnGestureListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.view.GestureDetector.SimpleOnGestureListener#onLongPress(
		 * android.view.MotionEvent)
		 */
		@Override
		public void onLongPress(MotionEvent e) {
			// Log.d(TAG, "onLongPress");
			super.onLongPress(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (m_ccvlListener != null) {
				if (e1.getX() < e2.getX()) { // 向右fling
					m_ccvlListener.onSwitchFilterToRight();
				} else { // 向左fling
					m_ccvlListener.onSwitchFilterToLeft();
				}
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp
		 * (android.view.MotionEvent)
		 */
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// CameraCoderView.this.cameraAutoFocus(e.getX(), e.getY());
			if (m_ccvlListener != null) {
				m_ccvlListener.onSingleTapUp(e);
			}
			return super.onSingleTapUp(e);
		}
	}

	private GestureDetector m_flignerDetector;

	public void setViewHandler(CameraCoderViewListener ccvl) {
		m_ccvlListener = ccvl;
	}

	protected CameraCoderViewListener m_ccvlListener;
	private ICameraZoomHandler m_hlrZoom;

	/**
	 * 切换摄像头特效Listener
	 * 
	 * @author abreal
	 * 
	 */
	public interface CameraCoderViewListener {
		/**
		 * 向左切换
		 */
		void onSwitchFilterToLeft();

		/**
		 * 向右切换
		 */
		void onSwitchFilterToRight();

		void onSingleTapUp(MotionEvent e);

	}

	public void setZoomHandler(ICameraZoomHandler hlrZoom) {
		m_hlrZoom = hlrZoom;
	}
}
