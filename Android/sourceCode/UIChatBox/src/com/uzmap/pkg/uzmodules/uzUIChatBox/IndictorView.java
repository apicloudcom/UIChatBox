//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import com.uzmap.pkg.uzkit.UZUtility;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class IndictorView extends View {
	public static final int RADIUS = 3;
	private Paint mPaint;
	private float mRadius;
	private int mPointNums;
	private int mCurrentIndex;
	private int mNormalColor;
	private int mActiveColor;
	private int mStartX;

	public IndictorView(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mRadius = UZUtility.dipToPix(RADIUS);
	}

	public void initParams(int pointNums, int startX, int normalColor,
			int activeColor) {
		this.mStartX = startX;
		this.mPointNums = pointNums;
		this.mNormalColor = normalColor;
		this.mActiveColor = activeColor;
	}

	public void setCurrentIndex(int index) {
		mCurrentIndex = index;
		invalidate();
	}
	
	public void setPointNums(int pointNums){
		this.mPointNums = pointNums;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawPoints(canvas);
	}

	private void drawPoints(Canvas canvas) {
		for (int i = 0; i < mPointNums; i++) {
			if (i == mCurrentIndex) {
				mPaint.setColor(mActiveColor);
			} else {
				mPaint.setColor(mNormalColor);
			}
			canvas.drawCircle(mStartX + mRadius * 4 * i,
					UZUtility.dipToPix(10), mRadius, mPaint);
		}
	}

}
