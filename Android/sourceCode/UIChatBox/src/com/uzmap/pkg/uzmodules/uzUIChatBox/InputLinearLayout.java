//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import android.content.Context;
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

	@Override
	protected void onDraw(Canvas canvas) {
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		canvas.drawLine(0, 0, width, 0, mPaint);
		canvas.drawLine(0, height, width, height, mPaint);
		super.onDraw(canvas);
	}
}
