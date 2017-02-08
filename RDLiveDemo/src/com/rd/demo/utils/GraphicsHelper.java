package com.rd.demo.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 图像工具类
 * 
 * @author admin
 * 
 */
public class GraphicsHelper {
    /**
     * 创建无边框圆角图片
     * 
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap createRoundedCornerBitmap(Bitmap bitmap, int radius) {
	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		bitmap.getHeight(), Config.ARGB_8888);
	Canvas canvas = new Canvas(output);

	final int color = 0xff424242;
	final Paint paint = new Paint();
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	final RectF rectF = new RectF(rect);
	final float roundRadius = radius;

	paint.setAntiAlias(true);// 设置去锯齿
	paint.setColor(color);

	canvas.drawARGB(0, 0, 0, 0);
	canvas.drawRoundRect(rectF, roundRadius, roundRadius, paint);

	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	canvas.drawBitmap(bitmap, rect, rect, paint);
	return output;
    }

    /**
     * 创建带边框圆角图片
     * 
     * @param bitmap
     * @param radius
     * @param border
     * @param borderColor
     * @return
     */
    public static Bitmap createRoundedCornerBitmap(Bitmap bitmap, int radius,
	    int border, int borderColor) {
	Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
		bitmap.getHeight(), Config.ARGB_8888);
	Canvas canvas = new Canvas(output);

	final int color = 0xff424242;
	final Paint paint = new Paint();
	final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	final RectF rectF = new RectF(rect);
	final float roundRadius = radius;

	paint.setAntiAlias(true);// 设置去锯齿
	paint.setColor(color);

	canvas.drawARGB(0, 0, 0, 0);
	canvas.drawRoundRect(rectF, roundRadius, roundRadius, paint);

	paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	canvas.drawBitmap(bitmap, rect, rect, paint);

	paint.setStyle(Paint.Style.STROKE);// 设置为空心
	paint.setColor(borderColor);
	paint.setStrokeWidth(border);
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	canvas.drawRoundRect(rectF, roundRadius, roundRadius, paint);
	return output;
    }

    /**
     * 绘制圆角图片
     * 
     * @param canvas
     * @param bitmap
     * @param radius
     */
    public static void drawRoundedCornerBitmap(Canvas canvas, int width,
	    int height, Bitmap bitmap, int radius) {
	bitmap = zoomBitmap(bitmap, width, height);
	bitmap = createRoundedCornerBitmap(bitmap, radius);
	Rect rect = new Rect(0, 0, width, height);
	canvas.drawBitmap(bitmap, rect, rect, new Paint());
    }

    /**
     * 绘制有边框圆角图片
     * 
     * @param canvas
     * @param width
     * @param height
     * @param bitmap
     * @param radius
     * @param borderWeight
     * @param borderColor
     */
    public static void drawRoundedCornerBitmap(Canvas canvas, int width,
	    int height, Bitmap bitmap, int radius, int borderWeight,
	    int borderColor) {
	bitmap = zoomBitmap(bitmap, width, height);
	bitmap = createRoundedCornerBitmap(bitmap, radius, borderWeight,
		borderColor);
	Rect rect = new Rect(0, 0, width, height);
	canvas.drawBitmap(bitmap, rect, rect, new Paint());
    }

    /**
     * 绘制有背景有边框圆角图片
     * 
     * @param canvas
     * @param width
     * @param height
     * @param bitmap
     * @param radius
     * @param borderWeight
     * @param borderColor
     * @param bgColor
     */
    public static void drawRoundedCornerBitmap(Canvas canvas, int width,
	    int height, Bitmap bitmap, int radius, int borderWeight,
	    int borderColor, int bgColor) {
	Bitmap zoomBitmap = zoomBitmap(bitmap, width, height);
	bitmap = createRoundedCornerBitmap(zoomBitmap, radius, borderWeight,
		borderColor);
	zoomBitmap.recycle();

	Rect rect = new Rect(0, 0, width, height);
	RectF rectF = new RectF(rect);
	float roundRadius = radius;

	Paint paint = new Paint();
	paint.setAntiAlias(true);// 设置去锯齿
	paint.setColor(bgColor);

	canvas.drawARGB(0, 0, 0, 0);
	canvas.drawRoundRect(rectF, roundRadius, roundRadius, paint);

	canvas.drawBitmap(bitmap, rect, rect, paint);
	bitmap.recycle();
    }

    /**
     * 获得带倒影的图片
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
	final int reflectionGap = 4;
	int width = bitmap.getWidth();
	int height = bitmap.getHeight();
	Matrix matrix = new Matrix();
	matrix.preScale(1, -1);
	Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
		width, height / 2, matrix, false);
	Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
		(height + height / 2), Config.ARGB_8888);
	Canvas canvas = new Canvas(bitmapWithReflection);
	canvas.drawBitmap(bitmap, 0, 0, null);
	Paint deafalutPaint = new Paint();
	canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
	canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
	Paint paint = new Paint();
	LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
		bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
		0x00ffffff, TileMode.CLAMP);
	paint.setShader(shader);
	// Set the Transfer mode to be porter duff and destination in
	paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
	// Draw a rectangle using the paint with our linear gradient
	canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
		+ reflectionGap, paint);
	return bitmapWithReflection;
    }

    /**
     * 将Drawable转化为Bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap getBitmap(Drawable drawable, int nWidth, int nHeight) {
	Bitmap bitmap = Bitmap.createBitmap(nWidth, nHeight,
		Config.ARGB_8888);
	Canvas canvas = new Canvas(bitmap);
	if (drawable instanceof BitmapDrawable) {
	    BitmapDrawable bd = (BitmapDrawable) drawable;
	    if (bd.getBitmap().isRecycled()) {
		bitmap.recycle();
		return null;
	    }
	}
	drawable.setBounds(0, 0, nWidth, nHeight);
	drawable.draw(canvas);
	return bitmap;
    }

    /**
     * 将Drawable转化为Bitmap
     * 
     * @param drawable
     * @return
     */
    public static Bitmap getBitmap(Drawable drawable) {
	int width = drawable.getIntrinsicWidth();
	int height = drawable.getIntrinsicHeight();
	if (width <= 0 || height <= 0) {
	    return null;
	}
	return getBitmap(drawable, width, height);
    }

    /**
     * 缩放图片
     * 
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
	int width = bitmap.getWidth();
	int height = bitmap.getHeight();
	Matrix matrix = new Matrix();
	float scaleWidht = ((float) w / width);
	float scaleHeight = ((float) h / height);
	matrix.postScale(scaleWidht, scaleHeight);
	Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
		matrix, true);
	return newbmp;
    }

    /** 水平方向模糊度 */
    private static float hRadius = 10;
    /** 竖直方向模糊度 */
    private static float vRadius = 10;
    /** 模糊迭代度 */
    private static int iterations = 7;

    public static Drawable BoxBlurFilter(Bitmap bmp, int type) {
	if (type == 0) {
	    bmp = ImageHorizontalCropWithRect(bmp);
	} else if (type == 1) {
	    bmp = ImageVerticalCropWithRect(bmp);
	}
	return BoxBlurFilter(bmp);
    }

    /**
     * 高斯模糊
     */
    public static Drawable BoxBlurFilter(Bitmap bmp) {
	int width = bmp.getWidth();
	int height = bmp.getHeight();
	int[] inPixels = new int[width * height];
	int[] outPixels = new int[width * height];
	Bitmap bitmap = Bitmap.createBitmap(width, height,
		Config.ARGB_8888);
	bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
	for (int i = 0; i < iterations; i++) {
	    blur(inPixels, outPixels, width, height, hRadius);
	    blur(outPixels, inPixels, height, width, vRadius);
	}
	blurFractional(inPixels, outPixels, width, height, hRadius);
	blurFractional(outPixels, inPixels, height, width, vRadius);
	bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
	@SuppressWarnings("deprecation")
	Drawable drawable = new BitmapDrawable(bitmap);
	bmp.recycle();
	return drawable;
    }

    /**
     * 按横竖视频裁切获取模糊的图片
     * 
     * @param drawable
     * @return
     */
    public static Drawable ImageCropWithRectAndBlurFilter(Bitmap bmp) {
	int w = bmp.getWidth(); // 得到图片的宽，高
	int h = bmp.getHeight();

	int nw, nh, retX, retY;
	if (w >= h) {
	    nw = w;
	    nh = (int) (nw / 1.8);
	    retX = 0;
	    retY = 0;
	} else {
	    nw = w / 2;
	    nh = w;
	    retX = w / 4;
	    retY = (h - w) / 2;
	}
	bmp = Bitmap.createBitmap(bmp, retX, retY, nw, nh, null, false);
	int width = bmp.getWidth(); // 得到图片的宽，高
	int height = bmp.getHeight();
	int[] inPixels = new int[width * height];
	int[] outPixels = new int[width * height];
	Bitmap bitmap = Bitmap.createBitmap(width, height,
		Config.ARGB_8888);
	bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
	for (int i = 0; i < 3; i++) {
	    blur(inPixels, outPixels, width, height, 5);
	    blur(outPixels, inPixels, height, width, 5);
	}
	blurFractional(inPixels, outPixels, width, height, 5);
	blurFractional(outPixels, inPixels, height, width, 5);
	bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
	@SuppressWarnings("deprecation")
	Drawable drawable = new BitmapDrawable(bitmap);
	bmp.recycle();
	return drawable;
    }

    /**
     * 按横着的长方形裁切图片
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap ImageHorizontalCropWithRect(Bitmap bitmap) {
	int w = bitmap.getWidth(); // 得到图片的宽，高
	int h = bitmap.getHeight();

	int nw, nh, retX, retY;
	if (w > h) {
	    nw = w;
	    nh = (int) (nw / 1.8);
	    retX = 0;
	    retY = 0;
	} else {
	    nw = w;
	    nh = (int) (nw / 1.8);
	    retX = 0;
	    retY = nh / 2;
	}
	Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
		false);
	return bmp;
    }

    /**
     * 按竖着的长方形裁切图片
     * 
     * @param bitmap
     * @return
     */
    public static Bitmap ImageVerticalCropWithRect(Bitmap bitmap) {
	int w = bitmap.getWidth(); // 得到图片的宽，高
	int h = bitmap.getHeight();

	int nw, nh, retX, retY;
	if (w > h) {
	    nw = h / 2;
	    nh = h;
	    retX = (w - nw) / 2;
	    retY = 0;
	} else {
	    nw = w / 2;
	    nh = w;
	    retX = w / 4;
	    retY = (h - w) / 2;
	}
	Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, nw, nh, null,
		false);
	return bmp;
    }

    public static void blur(int[] in, int[] out, int width, int height,
	    float radius) {
	int widthMinus1 = width - 1;
	int r = (int) radius;
	int tableSize = 2 * r + 1;
	int divide[] = new int[256 * tableSize];

	for (int i = 0; i < 256 * tableSize; i++)
	    divide[i] = i / tableSize;

	int inIndex = 0;

	for (int y = 0; y < height; y++) {
	    int outIndex = y;
	    int ta = 0, tr = 0, tg = 0, tb = 0;

	    for (int i = -r; i <= r; i++) {
		int rgb = in[inIndex + clamp(i, 0, width - 1)];
		ta += (rgb >> 24) & 0xff;
		tr += (rgb >> 16) & 0xff;
		tg += (rgb >> 8) & 0xff;
		tb += rgb & 0xff;
	    }

	    for (int x = 0; x < width; x++) {
		out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
			| (divide[tg] << 8) | divide[tb];

		int i1 = x + r + 1;
		if (i1 > widthMinus1)
		    i1 = widthMinus1;
		int i2 = x - r;
		if (i2 < 0)
		    i2 = 0;
		int rgb1 = in[inIndex + i1];
		int rgb2 = in[inIndex + i2];

		ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
		tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
		tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
		tb += (rgb1 & 0xff) - (rgb2 & 0xff);
		outIndex += height;
	    }
	    inIndex += width;
	}
    }

    public static void blurFractional(int[] in, int[] out, int width,
	    int height, float radius) {
	radius -= (int) radius;
	float f = 1.0f / (1 + 2 * radius);
	int inIndex = 0;

	for (int y = 0; y < height; y++) {
	    int outIndex = y;

	    out[outIndex] = in[0];
	    outIndex += height;
	    for (int x = 1; x < width - 1; x++) {
		int i = inIndex + x;
		int rgb1 = in[i - 1];
		int rgb2 = in[i];
		int rgb3 = in[i + 1];

		int a1 = (rgb1 >> 24) & 0xff;
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = rgb1 & 0xff;
		int a2 = (rgb2 >> 24) & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = rgb2 & 0xff;
		int a3 = (rgb3 >> 24) & 0xff;
		int r3 = (rgb3 >> 16) & 0xff;
		int g3 = (rgb3 >> 8) & 0xff;
		int b3 = rgb3 & 0xff;
		a1 = a2 + (int) ((a1 + a3) * radius);
		r1 = r2 + (int) ((r1 + r3) * radius);
		g1 = g2 + (int) ((g1 + g3) * radius);
		b1 = b2 + (int) ((b1 + b3) * radius);
		a1 *= f;
		r1 *= f;
		g1 *= f;
		b1 *= f;
		out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
		outIndex += height;
	    }
	    out[outIndex] = in[width - 1];
	    inIndex += width;
	}
    }

    public static int clamp(int x, int a, int b) {
	return (x < a) ? a : (x > b) ? b : x;
    }
}