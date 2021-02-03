//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.lang.ref.WeakReference;

public class AsyncDrawable<T extends View> extends Drawable {

	private final WeakReference<BitmapUtils.BitmapLoadTask<T>> mBitmapLoadTaskReference;
	private final Drawable mBaseDrawable;

	public AsyncDrawable(Drawable drawable,
			BitmapUtils.BitmapLoadTask<T> bitmapWorkerTask) {
		if (drawable == null) {
			throw new IllegalArgumentException("drawable may not be null");
		}
		if (bitmapWorkerTask == null) {
			throw new IllegalArgumentException(
					"bitmapWorkerTask may not be null");
		}
		mBaseDrawable = drawable;
		mBitmapLoadTaskReference = new WeakReference<BitmapUtils.BitmapLoadTask<T>>(
				bitmapWorkerTask);
	}

	public BitmapUtils.BitmapLoadTask<T> getBitmapWorkerTask() {
		return mBitmapLoadTaskReference.get();
	}

	@Override
	public void draw(Canvas canvas) {
		mBaseDrawable.draw(canvas);
	}

	@Override
	public void setAlpha(int i) {
		mBaseDrawable.setAlpha(i);
	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {
		mBaseDrawable.setColorFilter(colorFilter);
	}

	@Override
	public int getOpacity() {
		return mBaseDrawable.getOpacity();
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		mBaseDrawable.setBounds(left, top, right, bottom);
	}

	@Override
	public void setBounds(Rect bounds) {
		mBaseDrawable.setBounds(bounds);
	}

	@Override
	public void setChangingConfigurations(int configs) {
		mBaseDrawable.setChangingConfigurations(configs);
	}

	@Override
	public int getChangingConfigurations() {
		return mBaseDrawable.getChangingConfigurations();
	}

	@Override
	public void setDither(boolean dither) {
		mBaseDrawable.setDither(dither);
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		mBaseDrawable.setFilterBitmap(filter);
	}

	@Override
	public void invalidateSelf() {
		mBaseDrawable.invalidateSelf();
	}

	@Override
	public void scheduleSelf(Runnable what, long when) {
		mBaseDrawable.scheduleSelf(what, when);
	}

	@Override
	public void unscheduleSelf(Runnable what) {
		mBaseDrawable.unscheduleSelf(what);
	}

	@Override
	public void setColorFilter(int color, PorterDuff.Mode mode) {
		mBaseDrawable.setColorFilter(color, mode);
	}

	@Override
	public void clearColorFilter() {
		mBaseDrawable.clearColorFilter();
	}

	@Override
	public boolean isStateful() {
		return mBaseDrawable.isStateful();
	}

	@Override
	public boolean setState(int[] stateSet) {
		return mBaseDrawable.setState(stateSet);
	}

	@Override
	public int[] getState() {
		return mBaseDrawable.getState();
	}

	@Override
	public Drawable getCurrent() {
		return mBaseDrawable.getCurrent();
	}

	@Override
	public boolean setVisible(boolean visible, boolean restart) {
		return mBaseDrawable.setVisible(visible, restart);
	}

	@Override
	public Region getTransparentRegion() {
		return mBaseDrawable.getTransparentRegion();
	}

	@Override
	public int getIntrinsicWidth() {
		return mBaseDrawable.getIntrinsicWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return mBaseDrawable.getIntrinsicHeight();
	}

	@Override
	public int getMinimumWidth() {
		return mBaseDrawable.getMinimumWidth();
	}

	@Override
	public int getMinimumHeight() {
		return mBaseDrawable.getMinimumHeight();
	}

	@Override
	public boolean getPadding(Rect padding) {
		return mBaseDrawable.getPadding(padding);
	}

	@Override
	public Drawable mutate() {
		return mBaseDrawable.mutate();
	}

	@Override
	public ConstantState getConstantState() {
		return mBaseDrawable.getConstantState();
	}

}
