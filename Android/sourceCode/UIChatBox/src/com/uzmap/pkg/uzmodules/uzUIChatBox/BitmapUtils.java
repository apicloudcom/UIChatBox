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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;

import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzkit.data.UZWidgetInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

public class BitmapUtils {
	private UZWidgetInfo mWidgetInfo;
	private Context mContext;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(
			Color.TRANSPARENT);

	public BitmapUtils(UZWidgetInfo mWidgetInfo, Context mContext) {
		this.mWidgetInfo = mWidgetInfo;
		this.mContext = mContext;
	}

	public <T extends View> void display(T container, String uri) {
		if (container == null) {
			return;
		}
		container.clearAnimation();
		final BitmapLoadTask<T> loadTask = new BitmapLoadTask<T>(container, uri);
		AsyncDrawable<T> asyncDrawable = new AsyncDrawable<T>(
				TRANSPARENT_DRAWABLE, loadTask);
		((ImageView) container).setImageDrawable(asyncDrawable);
		loadTask.execute();
	}

	public class BitmapLoadTask<T extends View> extends
			AsyncTask<Void, Object, Bitmap> {
		private final String uri;
		private final WeakReference<T> containerReference;

		public BitmapLoadTask(T container, String uri) {
			if (container == null || uri == null) {
				throw new IllegalArgumentException("args may not be null");
			}
			this.containerReference = new WeakReference<T>(container);
			this.uri = uri;

		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bitmap = generateBitmap(uri);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			final T container = this.getTargetContainer();
			if (container != null) {
				if (bitmap != null) {
					((ImageView) container).setImageBitmap(bitmap);
				}
			}
		}

		public T getTargetContainer() {
			final T container = containerReference.get();
			final BitmapLoadTask<T> bitmapWorkerTask = getBitmapTaskFromContainer(container);

			if (this == bitmapWorkerTask) {
				return container;
			}

			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static <T extends View> BitmapLoadTask<T> getBitmapTaskFromContainer(
			T container) {
		if (container != null) {
			Drawable drawable = ((ImageView) container).getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable<T> asyncDrawable = (AsyncDrawable<T>) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	@SuppressLint("DefaultLocale")
	public Bitmap generateBitmap(String path) {
		String pathname = UZUtility.makeRealPath(path, mWidgetInfo);
		if (!isBlank(pathname)) {
			String sharePath;
			File file;
			try {
				if (pathname.contains("android_asset")) {
					int dotPosition = pathname.lastIndexOf('/');
					String ext = pathname.substring(dotPosition + 1,
							pathname.length()).toLowerCase();
					file = new File(mContext.getExternalCacheDir(), ext);
					sharePath = file.getAbsolutePath();
					InputStream input = UZUtility.guessInputStream(pathname);
					copy(input, file);
				} else if (pathname.contains("file://")) {
					sharePath = substringAfter(pathname, "file://");
				} else {
					sharePath = path;
				}
				InputStream input = UZUtility.guessInputStream(sharePath);
				return BitmapFactory.decodeStream(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String generatePath(String pathname) {
		String path = UZUtility.makeRealPath(pathname, mWidgetInfo);
		if (path != null) {
			String sharePath;
			if (path.contains("file://")) {
				sharePath = substringAfter(path, "file://");
			} else if (path.contains("android_asset")) {
				sharePath = path;
			} else {
				sharePath = path;
			}
			return sharePath;
		}
		return null;
	}

	private void copy(InputStream inputStream, File output) throws IOException {
		OutputStream outputStream = null;
		try {
			if (!output.exists()) {
				output.createNewFile();
			}
			outputStream = new FileOutputStream(output);
			int read = 0;
			byte[] bytes = new byte[1024];
			if (inputStream != null) {
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			}
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
			}
		}
	}

	public boolean isBlank(CharSequence cs) {
		int strLen;
		if ((cs == null) || ((strLen = cs.length()) == 0))
			return true;
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}

	public String substringAfter(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (separator == null) {
			return "";
		}
		int pos = str.indexOf(separator);
		if (pos == -1) {
			return "";
		}
		return str.substring(pos + separator.length());
	}

	public String toString(InputStream input, String encoding)
			throws IOException {
		StringWriter sw = new StringWriter();
		copy(input, sw, encoding);
		return sw.toString();
	}

	public void copy(InputStream input, Writer output, String encoding)
			throws IOException {
		if (encoding == null) {
			copy(input, output);
		} else {
			InputStreamReader in = new InputStreamReader(input, encoding);
			copy(in, output);
		}
	}

	public void copy(InputStream input, Writer output) throws IOException {
		InputStreamReader in = new InputStreamReader(input);
		copy(in, output);
	}

	public static int copy(Reader input, Writer output) throws IOException {
		long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
			return -1;
		}
		return (int) count;
	}

	public static long copyLarge(Reader input, Writer output)
			throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public StateListDrawable addStateDrawable(BitmapDrawable nomalDrawable,
			BitmapDrawable pressDrawable) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_pressed }, pressDrawable);
		sd.addState(new int[] {}, nomalDrawable);
		return sd;
	}

}
