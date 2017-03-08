package com.example.rdlivedemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rd.lib.ui.RotateRelativeLayout;
import com.rd.live.RDLiveSDK;

/**
 * 直播过程中，旋转屏幕方向的友情提示
 * 
 * @author JIAN
 * 
 */
public class OutOrientationHandler {

	private View root;
	private Context context;
	private RotateRelativeLayout rotateRelative;
	private ImageView outImg;
	private TextView outText1, outText2;

	public OutOrientationHandler(View _root) {
		root = _root;
		context = root.getContext();
		rotateRelative = (RotateRelativeLayout) root
				.findViewById(R.id.out_orientation_layout);
		outImg = (ImageView) root.findViewById(R.id.out_orientation_img);
		outText1 = (TextView) root.findViewById(R.id.out_orientation_text_1);
		outText2 = (TextView) root.findViewById(R.id.out_orientation_text_2);

	}

	public void checkGone() {
		if (root != null && root.getVisibility() == View.VISIBLE) {
			// Animation ani = AnimationUtils.loadAnimation(context,
			// android.R.anim.fade_out);
			// root.startAnimation(ani);
			root.setVisibility(View.GONE);
			outImg.setImageBitmap(null);
			outText1.setText("");
			outText2.setText("");
		}
	}

	private void checkIn(boolean isVer) {

		if (isVer) {
			outText1.setText(context.getString(R.string.out_vertical_1));
			outText2.setText(context.getString(R.string.out_vertical_2));
		} else {
			outText1.setText(context.getString(R.string.out_horizontal_1));
			outText2.setText(context.getString(R.string.out_horizontal_2));
		}
		if (root != null && root.getVisibility() != View.VISIBLE) {
			root.setVisibility(View.VISIBLE);
		}
	}

	private Bitmap tempbmp;

	public void setOrientation(int src) {

		int outOrientation = RDLiveSDK.getOutOrientation();// 直播输出的方向

		int screenOritentation = src % 360;

		Log.i("setOrientation", src + "---" + outOrientation + "--"
				+ screenOritentation);
		if (outOrientation != -1) {
			root.removeCallbacks(checkfalse);
			root.removeCallbacks(checktrue);
			checkGone();
			if (outOrientation == 90) {// 竖直输出
				if (screenOritentation == 0) {// 手机标准竖屏
					checkGone();
				} else if (screenOritentation == 270) {// 手机左横屏
					rotateRelative.setOrientation(screenOritentation);
					tempbmp = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.out_vertical);
					outImg.setImageBitmap(tempbmp);
					root.postDelayed(checktrue, 1000);
				} else if (screenOritentation == 90) {// 右横屏
					rotateRelative.setOrientation(screenOritentation);
					tempbmp = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.out_vertical);
					Bitmap temp = getbmp(tempbmp);
					tempbmp.recycle();
					tempbmp = null;
					outImg.setImageBitmap(temp);
					root.postDelayed(checktrue, 1000);
				} else {
					rotateRelative.setOrientation(screenOritentation);
					tempbmp = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.out_vertical);
					outImg.setImageBitmap(tempbmp);
					root.postDelayed(checktrue, 1000);
				}

			} else if (outOrientation == 0) {// 左横屏输出

				if (screenOritentation == 0) {
					checkGone();
					rotateRelative.setOrientation(screenOritentation);
				} else {
					rotateRelative.setOrientation(screenOritentation);
					tempbmp = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.out_horizontal);
					outImg.setImageBitmap(tempbmp);
					root.postDelayed(checkfalse, 1000);

				}

			} else if (outOrientation == 180) {// 右横屏输出

				if (screenOritentation == 0) {
					checkGone();
					rotateRelative.setOrientation(screenOritentation);
				} else if (screenOritentation == 270) {
					rotateRelative.setOrientation(screenOritentation);
					tempbmp = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.out_horizontal);
					Bitmap temp = getbmp(tempbmp);
					tempbmp.recycle();
					tempbmp = null;
					outImg.setImageBitmap(temp);
					root.postDelayed(checkfalse, 1000);
				} else {// 手机标准横屏
					rotateRelative.setOrientation(screenOritentation);
					tempbmp = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.out_horizontal);
					outImg.setImageBitmap(tempbmp);
					root.postDelayed(checkfalse, 1000);
				}

			}
		}

	}

	private Runnable checkfalse = new Runnable() {

		@Override
		public void run() {
			checkIn(false);
		}
	};
	private Runnable checktrue = new Runnable() {

		@Override
		public void run() {
			checkIn(true);
		}
	};

	public void onDestory() {
		if (null != tempbmp) {
			if (tempbmp.isRecycled()) {
				tempbmp.recycle();
			}
			tempbmp = null;
		}
		root.removeCallbacks(checkfalse);
		root.removeCallbacks(checktrue);
	}

	/**
	 * 左右反转
	 * 
	 * @param decodeResource
	 * @return
	 */
	private Bitmap getbmp(Bitmap decodeResource) {

		Paint p = new Paint();
		p.setAntiAlias(true);
		Bitmap b = Bitmap.createBitmap(decodeResource.getWidth(),
				decodeResource.getHeight(), Config.ARGB_8888);

		Canvas cv = new Canvas(b);

		Matrix m = new Matrix();
		// 反转一:
		m.postScale(-1f, 1f);// 左右反转
		m.postTranslate(decodeResource.getWidth(), 0);

		// 绘制反转方式二:
		cv.drawBitmap(decodeResource, m, p);
		cv.save();
		return b;

	}
}
