package com.rd.live.ui;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.rd.recorder.ICameraZoomHandler;

/**
 * 处理摄像头变焦
 * 
 * @author abreal
 * 
 */
public class LiveCameraZoomHandler implements ICameraZoomHandler {

	private final String TAG = "LiveCameraZoomHandler";

	private int m_nZoomState = ZOOM_STOPPED;
	private boolean mSmoothZoomSupported = false, mIsZoomSupported = false;
	private int m_nZoomValue; // The current zoom value.
	private int m_nZoomMax;
	private int mTargetZoomValue;
	private ZoomControl m_ZoomControl; // 缩放组件
	private final ZoomListener m_ZoomListener = new ZoomListener();

	private boolean m_bPausing;
	private Camera m_mainCamera;
	private ScaleGestureDetector m_sgdScaleDetector;
	private boolean m_bHandleScale = false;
	@SuppressWarnings("unused")
	private boolean m_bRecording;

	@Override
	public Camera getMainCamera() {
		return m_mainCamera;
	}

	@Override
	public void setMainCamera(Camera m_mainCamera) {
		this.m_mainCamera = m_mainCamera;
	}

	public boolean isPausing() {
		return m_bPausing;
	}

	public void setPausing(boolean m_bPausing) {
		this.m_bPausing = m_bPausing;
	}

	public void setRecording(boolean bRecording) {
		m_bRecording = bRecording;
	}

 
	@Override
	public int getZoomState() {
		return m_nZoomState;
	}

 
	@Override
	public void setZoomState(int m_nZoomState) {
		this.m_nZoomState = m_nZoomState;
	}

 
	@Override
	public int getZoomValue() {
		return m_nZoomValue;
	}

	/**
	 * 构造函数
	 * 
	 * @param zoomCtrl
	 * @param gestureListener
	 */
	public LiveCameraZoomHandler(Context ctx, ZoomControl zoomCtrl) {
		m_ZoomControl = zoomCtrl;
		m_sgdScaleDetector = new ScaleGestureDetector(ctx,
				m_scaleGestureListener);
	}

 
	@Override
	public void initializeZoom() {
		if (null != m_mainCamera) {
			Parameters params = m_mainCamera.getParameters();
			mSmoothZoomSupported = params.isSmoothZoomSupported();
			mIsZoomSupported = params.isZoomSupported();

			m_nZoomMax = params.getMaxZoom();
			// Currently we use immediate zoom for fast zooming to get better UX
			// and
			// there is no plan to take advantage of the smooth zoom.
			if (null != m_ZoomControl && mIsZoomSupported) {
				m_ZoomControl.setZoomMax(m_nZoomMax);
				m_ZoomControl.setZoomIndex(params.getZoom());
				m_ZoomControl.setSmoothZoomSupported(mSmoothZoomSupported);
				m_ZoomControl.setOnZoomChangeListener(new ZoomChangeListener());
			}
			m_mainCamera.setZoomChangeListener(m_ZoomListener);
		}
	}

	/*
	 * 响应手势
	 */
	public boolean onTouch(MotionEvent event) {
		m_sgdScaleDetector.onTouchEvent(event);
		return m_bHandleScale;
	}

	private class ZoomChangeListener implements
			ZoomControl.OnZoomChangedListener {
		// only for immediate zoom
		@Override
		public void onZoomValueChanged(int index) {
			LiveCameraZoomHandler.this.onZoomValueChanged(index);
		}

		// only for smooth zoom
		@Override
		public void onZoomStateChanged(int state) {
			if (m_bPausing)
				return;

			if (state == ZoomControl.ZOOM_IN) {
				LiveCameraZoomHandler.this.onZoomValueChanged(m_nZoomMax);
			} else if (state == ZoomControl.ZOOM_OUT) {
				LiveCameraZoomHandler.this.onZoomValueChanged(0);
			} else {
				mTargetZoomValue = -1;
				if (m_nZoomState == ZOOM_START) {
					m_nZoomState = ZOOM_STOPPING;
					m_mainCamera.stopSmoothZoom();
				}
			}
		}
	}

	private final class ZoomListener implements
			android.hardware.Camera.OnZoomChangeListener {
		@Override
		public void onZoomChange(int value, boolean stopped,
				android.hardware.Camera camera) {
			m_nZoomValue = value;

			// Update the UI when we get zoom value.
			m_ZoomControl.setZoomIndex(value);

			setCameraZoom(value);

			if (stopped && m_nZoomState != ZOOM_STOPPED) {
				if (mTargetZoomValue != -1 && value != mTargetZoomValue) {
					m_mainCamera.startSmoothZoom(mTargetZoomValue);
					m_nZoomState = ZOOM_START;
				} else {
					m_nZoomState = ZOOM_STOPPED;
				}
			}
		}
	}

	private void onZoomValueChanged(int index) {
		// Not useful to change zoom value when the activity is paused.
		if (m_bPausing)
			return;

		if (mSmoothZoomSupported) {
			if (mTargetZoomValue != index && m_nZoomState != ZOOM_STOPPED) {
				mTargetZoomValue = index;
				if (m_nZoomState == ZOOM_START) {
					m_nZoomState = ZOOM_STOPPING;
					m_mainCamera.stopSmoothZoom();
				}
			} else if (m_nZoomState == ZOOM_STOPPED && m_nZoomValue != index) {
				mTargetZoomValue = index;
				m_mainCamera.startSmoothZoom(index);
				m_nZoomState = ZOOM_START;
			}
		} else {
			setCameraZoom(index);
		}
	}

	/**
	 * 设置摄像头变焦值
	 * 
	 * @param index
	 */
	private void setCameraZoom(int index) {
		try {
			Parameters param = m_mainCamera.getParameters();
			if (m_nZoomValue != index
					&& mIsZoomSupported
					&& (param.isZoomSupported() || param
							.isSmoothZoomSupported())) {
				m_nZoomValue = index;
				param.setZoom(m_nZoomValue);
				m_mainCamera.setParameters(param);
			}
		} catch (Exception ex) {

		}
	}

	private int m_nSetIndex;

	private ScaleGestureDetector.OnScaleGestureListener m_scaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			setCameraZoom(m_nSetIndex);
			m_bHandleScale = false;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			m_nSetIndex = m_nZoomValue;
			m_bHandleScale = true;
			return m_bHandleScale;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float scale = detector.getScaleFactor();
			// Log.e(TAG, String.format("onScale scale:%.2f",scale));
			if (Float.isNaN(scale) || Float.isInfinite(scale)) {
				return true;
			}
			int nStepValue = 0; // 步进修正
			int nOldSetIndex = m_nSetIndex;
			// int nZoomStateTmp = ZoomControl.ZOOM_STOP;

			m_nSetIndex = Math.round(m_nSetIndex * scale);
			if (scale != 1.0f) {
				if (scale > 1.0f) { // 放大
					nStepValue = 2;
					// nZoomStateTmp = ZoomControl.ZOOM_IN;
				} else {
					nStepValue = -2;
					// nZoomStateTmp = ZoomControl.ZOOM_OUT;
				}
			}

			m_nSetIndex += nStepValue;

			if (m_nSetIndex > m_nZoomMax) {
				m_nSetIndex = m_nZoomMax;
			} else if (m_nSetIndex < 1) {
				m_nSetIndex = 1;
			}
			if (Math.abs(nOldSetIndex - m_nSetIndex) > 10) {
				m_nSetIndex = nOldSetIndex;
			}
			setCameraZoom(m_nSetIndex);
			// Log.d(TAG, String.format("New value:%d,scale:%.3f", m_nSetIndex,
			// scale));
			return true;
		}
	};

}
