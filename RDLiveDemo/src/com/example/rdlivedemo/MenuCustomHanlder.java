package com.example.rdlivedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.rd.demo.utils.DateTimeUtils;
import com.rd.live.RDLiveSDK;
import com.rd.recorder.ICustomData;

/**
 * 自定义推流
 * 
 * @author JIAN
 * @date 2017-1-15 下午12:14:16
 */
class MenuCustomHanlder {
	private LiveCustomData iCustomdata;
	private final int MSG_CUSTOMDATA_START = 12; // 开启自定义推流
	private final int MSG_CUSTOMDATA_CLOSE = 14;// 关闭自定义推流
	private Context mContext;
	private final int WIDTH = 360, HEIGHT = 640;

	/**
	 * 构造器
	 * 
	 * @param context
	 */
	public MenuCustomHanlder(Context context) {
		mContext = context;
		iCustomdata = new LiveCustomData();

	}

	/**
	 * 自定义回调
	 * 
	 * @return
	 */
	public LiveCustomData getCustomData() {
		return iCustomdata;
	}

	/**
	 * 自定推流的数据
	 * 
	 * @author JIAN
	 */
	private class LiveCustomData implements ICustomData {

		private Bitmap bmp;

		public void setBmp(Bitmap _bmp) {
			bmp = _bmp;
		}

		/**
		 * sdk 显示要自定义的图片:(bmp==null ，即关闭自定义);(bmp!=null ,即为自定义推流)
		 */
		@Override
		public Bitmap getBmp() {
			synchronized (this) {
				return bmp;
			}
		}

	}

	;

	private boolean isCustoming = false;// 判断是否已经开启自定义的状态

	/**
	 * 是否开启自定义推流
	 * 
	 * @param enableView
	 */
	public void onCustomData(ImageView enableView) {

		if (RDLiveSDK.enableCustomData()) {
			p.setColor(Color.BLACK);
			p.setAntiAlias(true);
			p.setTextSize(25f);
			if (!isCustoming) {// 开启自定义推流功能
				ctime = System.currentTimeMillis();
				isCustoming = true;
				enableView.setImageResource(R.drawable.custom_p);
				if (null != iCustomdata) {
					handler.obtainMessage(MSG_CUSTOMDATA_START).sendToTarget();
				}

			} else {// 停止自定义推流
				if (null != iCustomdata) {
					handler.obtainMessage(MSG_CUSTOMDATA_CLOSE).sendToTarget();
				}
				enableView.setImageResource(R.drawable.custom_n);
				isCustoming = false;
			}
		} else {// 4.3以下的设备不支持该功能
			autoToast("该设备暂不支持该功能!");
		}

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			handler.removeCallbacks(getDataRunnable);
			if (MSG_CUSTOMDATA_START == msg.what) {// 推送自定义的数据
				handler.postDelayed(getDataRunnable, 700);
			} else if (MSG_CUSTOMDATA_CLOSE == msg.what) {// 关闭自定义数据的推送
				iCustomdata.setBmp(null);
			}

		}

		;
	};
	// 绘制自定义推流画图的画笔
	private Paint p = new Paint();
	private Runnable getDataRunnable = new Runnable() {

		/**
		 * 构建一个bmp,用于自定义推流，(生成图片耗时，须开启线程处理)
		 */
		@Override
		public void run() {
			synchronized (iCustomdata) {
				int[] size = RDLiveSDK.getOutSize();
				Bitmap bmp;
				if (null != size) {
					bmp = Bitmap.createBitmap(size[0], size[1],
							Config.ARGB_8888);
				} else {
					bmp = Bitmap.createBitmap(
							(int) (WIDTH - (Math.random() * 100)), HEIGHT,
							Config.ARGB_8888);
				}
				Canvas cv = new Canvas(bmp);
				cv.drawColor(Color.RED);
				String text = updateTime((int) (System.currentTimeMillis() - ctime));
				cv.drawText(text, bmp.getWidth() / 2, bmp.getHeight() / 2, p);
				cv.save();

				iCustomdata.setBmp(bmp);
				handler.obtainMessage(MSG_CUSTOMDATA_START).sendToTarget();

			}

		}
	};

	private long ctime = 0;

	/**
	 * 获取视频当前播放时间格式化后的字符串
	 */
	private String updateTime(int t) {
		return DateTimeUtils.stringForTime(t, true, true, true, true);
	}

	private void autoToast(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}
}
