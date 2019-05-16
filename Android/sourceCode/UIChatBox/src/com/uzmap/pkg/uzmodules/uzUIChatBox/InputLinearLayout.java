/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */

//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import com.uzmap.pkg.uzkit.UZUtility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class InputLinearLayout extends LinearLayout {
	private Paint mPaint;

	public InputLinearLayout(Context context) {
		super(context);
		init();
		setPadding(0, UZUtility.dipToPix(5), 0, UZUtility.dipToPix(5));
	}

	public InputLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(4);
		mPaint.setColor(Constans.INPUT_LAYOUT_BORDER_COLOR);
	}

	private Bitmap topBorderBmp;
	private int topBorderColor = 0xFF000000;
	private int topBorderHeight = UZUtility.dipToPix(0);
	private Paint mTopBorderPaint = new Paint();

	public void initPaint() {
		mTopBorderPaint.setStyle(Style.FILL);
	}

	public void setTopBorderBitmap(Bitmap bmp) {
		this.topBorderBmp = bmp;
	}

	public void setTopBorderHeight(int height) {
		this.topBorderHeight = height;
	}

	public void setTopBorderColor(int color) {
		this.topBorderColor = color;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();

		if (topBorderBmp != null) {
			Bitmap bmp = Bitmap.createScaledBitmap(topBorderBmp, getWidth(),
					topBorderHeight, true);
			canvas.drawBitmap(bmp, 0, 0, mTopBorderPaint);
		} else {
			mTopBorderPaint.setColor(topBorderColor);
			mTopBorderPaint.setStrokeWidth(topBorderHeight);
			canvas.drawLine(0, 0, getWidth(), 0, mTopBorderPaint);
		}

		canvas.drawLine(0, height, width, height, mPaint);
		super.onDraw(canvas);
	}
}
