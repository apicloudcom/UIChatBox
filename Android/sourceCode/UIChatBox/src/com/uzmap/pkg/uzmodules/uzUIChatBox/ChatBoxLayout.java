//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import android.content.Context;
import android.os.Handler;
import android.widget.LinearLayout;

/**
 * 线性布局的子类；
 * @author 邓宝成
 */

public class ChatBoxLayout extends LinearLayout {

	private UzUIChatBox mUiChatBox;
	private Handler mDelayedHandler = new Handler();
	private boolean isAutoFocus;

	public ChatBoxLayout(Context context) {
		super(context);
		setWillNotDraw(false);
	}

	public void setUiChatBox(UzUIChatBox uiChatBox) {
		this.mUiChatBox = uiChatBox;
	}

	public void setAutoFocus(boolean isAutoFocus) {
		this.isAutoFocus = isAutoFocus;
	}
	
	/**
	 * 我们要使用View.post()这个方法就必须等onAttachedToWindow ()这个方法执行过了才行
	 */
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mDelayedHandler.postDelayed(mDelayedShowKeyBoardRunnable, 300);
	}

	private Runnable mDelayedShowKeyBoardRunnable = new Runnable() {
		@Override
		public void run() {
			if (isAutoFocus) {
				mUiChatBox.showKeybord();
			}
			mUiChatBox.openCallBack("show", 0);
		}
	};
	
}
