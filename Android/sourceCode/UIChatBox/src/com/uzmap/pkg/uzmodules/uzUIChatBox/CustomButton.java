package com.uzmap.pkg.uzmodules.uzUIChatBox;

import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzmodules.uzUIChatBox.ViewBackground.BackgroundType;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomButton extends Button {

	private int mId;
	private int mX;
	private int mY;
	private int mWidth;
	private int mHeight;
	private String mFixedOn;
	private boolean mFixed;

	private Paint mPaint;
	private Bitmap mNormalBgBitmap;
	private Bitmap mHighlightBgBitmap;
	private boolean isPressed;
	private ViewBackground mNormalBg;
	private Rect mNormalRect;
	private ViewBackground mHighlightBg;
	private Rect mHighlightRect;
	private RectF mBgRect;
	private int mCorner;
	private int mNormalBgColor;
	private int mHighlightBgColor;

	private String mNoramlTitle;
	private String mHighLightTitle;
	private int mNoramlTitleColor;
	private int mHighLightTitleColor;

	public CustomButton(Context context) {
		super(context);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.FILL);
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(ViewBackground normalBg, ViewBackground highlightBg,
			int w, int h, int corner) {
		mNormalBg = normalBg;
		mHighlightBg = highlightBg;
		w = UZUtility.dipToPix(w);
		h = UZUtility.dipToPix(h);
		mCorner = corner;
		if (normalBg.getBgType() == BackgroundType.IMG) {
			mNormalBgBitmap = getCornerBitmap(normalBg.getBgBitmap(), w, h,
					corner);
			mNormalRect = new Rect(0, 0, mNormalBgBitmap.getWidth(),
					mNormalBgBitmap.getHeight());
		} else {
			mNormalBgColor = normalBg.getBgColor();
		}

		if (highlightBg.getBgType() == BackgroundType.IMG) {
			mHighlightBgBitmap = getCornerBitmap(highlightBg.getBgBitmap(), w,
					h, corner);
			mHighlightRect = new Rect(0, 0, mHighlightBgBitmap.getWidth(),
					mHighlightBgBitmap.getHeight());
		} else {
			mHighlightBgColor = highlightBg.getBgColor();
		}
		mBgRect = new RectF(0, 0, w, h);
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (!isPressed) {
			if (mNormalBgBitmap != null) {
				canvas.drawBitmap(mNormalBgBitmap, mNormalRect, mNormalRect,
						null);
			}else{
				mPaint.setColor(mNormalBgColor);
				canvas.drawRoundRect(mBgRect, mCorner, mCorner, mPaint);
			}
		} else {
			if(mHighlightBgBitmap!=null){
				canvas.drawBitmap(mHighlightBgBitmap, mHighlightRect,
						mHighlightRect, null);
			}else{
				mPaint.setColor(mHighlightBgColor);
				canvas.drawRoundRect(mBgRect, mCorner, mCorner, mPaint);
			}
		}
		super.onDraw(canvas);
	}

	private Bitmap getCornerBitmap(Bitmap res, int w, int h, int corner) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvasTmp = new Canvas(target);
		canvasTmp.drawRoundRect(new RectF(0, 0, w, h), corner, corner, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvasTmp.drawBitmap(res,
				new Rect(0, 0, res.getWidth(), res.getHeight()), new Rect(0, 0,
						w, h), paint);
		return target;
	}

	public int getmX() {
		return mX;
	}

	public void setmX(int mX) {
		this.mX = mX;
	}

	public int getmY() {
		return mY;
	}

	public void setmY(int mY) {
		this.mY = mY;
	}

	public int getmWidth() {
		return mWidth;
	}

	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
	}

	public int getmHeight() {
		return mHeight;
	}

	public void setmHeight(int mHeight) {
		this.mHeight = mHeight;
	}

	public String getmFixedOn() {
		return mFixedOn;
	}

	public void setmFixedOn(String mFixedOn) {
		this.mFixedOn = mFixedOn;
	}

	public boolean ismFixed() {
		return mFixed;
	}

	public void setmFixed(boolean mFixed) {
		this.mFixed = mFixed;
	}

	public int getmCorner() {
		return mCorner;
	}

	public void setmCorner(int mCorner) {
		this.mCorner = mCorner;
	}

	public ViewBackground getmNormalBg() {
		return mNormalBg;
	}

	public void setmNormalBg(ViewBackground mNormalBg) {
		this.mNormalBg = mNormalBg;
	}

	public ViewBackground getmHighlightBg() {
		return mHighlightBg;
	}

	public void setmHighlightBg(ViewBackground mHighlightBg) {
		this.mHighlightBg = mHighlightBg;
	}

	public String getmNoramlTitle() {
		return mNoramlTitle;
	}

	public void setmNoramlTitle(String mNoramlTitle) {
		this.mNoramlTitle = mNoramlTitle;
	}

	public String getmHighLightTitle() {
		return mHighLightTitle;
	}

	public void setmHighLightTitle(String mHighLightTitle) {
		this.mHighLightTitle = mHighLightTitle;
	}

	public int getmNoramlTitleColor() {
		return mNoramlTitleColor;
	}

	public void setmNoramlTitleColor(int mNoramlTitleColor) {
		this.mNoramlTitleColor = mNoramlTitleColor;
	}

	public int getmHighLightTitleColor() {
		return mHighLightTitleColor;
	}

	public void setmHighLightTitleColor(int mHighLightTitleColor) {
		this.mHighLightTitleColor = mHighLightTitleColor;
	}
}
