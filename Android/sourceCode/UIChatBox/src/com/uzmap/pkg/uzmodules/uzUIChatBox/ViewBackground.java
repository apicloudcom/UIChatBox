/**
 * APICloud Modules
 * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
 * Licensed under the terms of the The MIT License (MIT).
 * Please see the license.html included with this distribution for details.
 */
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
