//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//

package com.uzmap.pkg.uzmodules.uzUIChatBox;

import com.uzmap.pkg.uzkit.UZUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.EditText;

public class ChatBoxEditText extends EditText {
	
	private Paint mPaint;
	
	private static int leftPadding = UZUtility.dipToPix(5);
	
	public static int DEFAULT_PADDING = UZUtility.dipToPix(5);

	public ChatBoxEditText(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setStrokeWidth(2);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(leftIcon != null){
			canvas.drawBitmap(leftIcon, leftPadding, (getHeight() - leftIcon.getHeight()) / 2, mPaint);
		}
	}
	
	private Bitmap leftIcon;
	public void setLeftIcon(Bitmap leftIcon){
		this.leftIcon = leftIcon;
		invalidate();
		setPadding(leftIcon.getWidth() + leftPadding * 2, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
	}
	
}
