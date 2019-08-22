//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;

public class InputFieldListener {
	private UZModuleContext mChangeHeight;
	private UZModuleContext mToggleKeyboard;
	private JSONObject mResult = new JSONObject();
	private View mMonitorView;
	private Rect mRect = new Rect();
	private int mOriginalChatViewH;
	private int mChatViewH;
	private int mTmpChatViewH;
	private int mInputFieldH;
	private Context mContext;

	public InputFieldListener(View mMonitorView, int originalChatViewH,
			Context context) {
		this.mMonitorView = mMonitorView;
		this.mOriginalChatViewH = originalChatViewH;
		this.mContext = context;
	}

	public void setChangeHeight(UZModuleContext changeHeight) {
		this.mChangeHeight = changeHeight;
	}

	public void setToggleKeyboard(UZModuleContext toggleKeyboard) {
		this.mToggleKeyboard = toggleKeyboard;
	}

	public void onReLayout(int inputFieldH) {
		if (mInputFieldH == 0) {
			mInputFieldH = inputFieldH;
		}
		mMonitorView.getWindowVisibleDisplayFrame(mRect);
		mTmpChatViewH = mOriginalChatViewH - mRect.bottom;
		if (mChatViewH == mTmpChatViewH && mInputFieldH == inputFieldH) {
			return;
		} else if (mChatViewH != mTmpChatViewH) {
			mChatViewH = mTmpChatViewH;
			inputFieldCallBack(mToggleKeyboard, mResult, mInputFieldH,
					mChatViewH);
		} else if (mInputFieldH != inputFieldH) {
			mInputFieldH = inputFieldH;
			inputFieldCallBack(mChangeHeight, mResult, mInputFieldH, mChatViewH);
		}
	}

	public void inputFieldCallBack(UZModuleContext moduleContext,
			JSONObject result, int inputFieldH, int chatViewH) {
		try {
			result.put("inputBarHeight", px2dip(inputFieldH));
			result.put("panelHeight", px2dip(chatViewH));
			if (moduleContext != null) {
				moduleContext.success(result, false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public int getmInputFieldH() {
		return mInputFieldH;
	}

	private int px2dip(float pxValue) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
