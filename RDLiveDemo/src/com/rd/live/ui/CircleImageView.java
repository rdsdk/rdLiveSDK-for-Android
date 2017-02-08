package com.rd.live.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.rd.demo.utils.GraphicsHelper;

/**
 * CircleImageView
 */
public class CircleImageView extends ImageView {

    private int radius = 40;

    private int borderWeight = 3;

    private int bgColor = 0xff888888;
    private int borderColor = 0x00000000;

    // private int focusBorderColor = 0x88ff8800;
    // private int focusBgColor = 0x88ffaa88;

    private int drawBorderColor = borderColor;
    private int drawBgColor = bgColor;

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.getDrawable();
        if (drawable == null) {
            super.onDraw(canvas);
            return;
        }
        try {
            Bitmap bitmap = GraphicsHelper
                    .getBitmap(drawable);
            if (null != bitmap) {
                int w = this.getWidth();
                int h = this.getHeight();
                GraphicsHelper.drawRoundedCornerBitmap(
                        canvas, w, h, bitmap, w / 2, borderWeight,
                        drawBorderColor, drawBgColor);
                bitmap.recycle();
            }
        } catch (Exception e) {
        }
    }

    // @Override
    // public boolean onTouchEvent(MotionEvent event) {
    // switch (event.getAction()) {
    // case MotionEvent.ACTION_DOWN:
    // this.drawBorderColor = focusBorderColor;
    // this.drawBgColor = focusBgColor;
    // break;
    // case MotionEvent.ACTION_MOVE:
    // this.drawBorderColor = borderColor;
    // this.drawBgColor = bgColor;
    // break;
    // case MotionEvent.ACTION_UP:
    // this.drawBorderColor = borderColor;
    // this.drawBgColor = bgColor;
    // break;
    // }
    // postInvalidate();
    // return true;
    // }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBorderWeight() {
        return borderWeight;
    }

    public void setBorderWeight(int borderWeight) {
        this.borderWeight = borderWeight;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        this.drawBorderColor = borderColor;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        this.drawBgColor = bgColor;
    }

}