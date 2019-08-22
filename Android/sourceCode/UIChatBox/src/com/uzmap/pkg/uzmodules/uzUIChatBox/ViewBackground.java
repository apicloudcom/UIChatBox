package com.uzmap.pkg.uzmodules.uzUIChatBox;

import android.graphics.Bitmap;

public class ViewBackground {
	private BackgroundType mBgType;
	private Bitmap mBgBitmap;
	private int mBgColor;

	public BackgroundType getBgType() {
		return mBgType;
	}

	public void setBgType(BackgroundType bgType) {
		this.mBgType = bgType;
	}

	public Bitmap getBgBitmap() {
		return mBgBitmap;
	}

	public void setBgBitmap(Bitmap bitmap) {
		this.mBgBitmap = bitmap;
	}

	public int getBgColor() {
		return mBgColor;
	}

	public void setBgColor(int bgColor) {
		this.mBgColor = bgColor;
	}

	enum BackgroundType {
		IMG, COLOR
	}
}
