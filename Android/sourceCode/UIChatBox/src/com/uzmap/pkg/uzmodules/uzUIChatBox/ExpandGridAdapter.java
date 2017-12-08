//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import java.util.ArrayList;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandGridAdapter extends BaseAdapter {
	private ArrayList<ExpandData> mExpandData;
	private Context mContext;
	private JsParamsUtil mJsParamsUtil;
	private ViewPager mViewPager;
	private UzUIChatBox mModule;
	private int mTitleSize;
	private int mTitleColor;
	/****
	 * 每一个表情点击的适配器;
	 * @param module
	 * @param mExpandData
	 * @param mContext
	 * @param mViewPager
	 * @param mModuleContext
	 */
	public ExpandGridAdapter(UzUIChatBox module,
			ArrayList<ExpandData> mExpandData, Context mContext,
			ViewPager mViewPager, UZModuleContext mModuleContext) {
		this.mModule = module;
		this.mExpandData = mExpandData;
		this.mContext = mContext;
		this.mViewPager = mViewPager;
		mJsParamsUtil = JsParamsUtil.getInstance();
		mTitleSize = mJsParamsUtil.extrasTitleSize(mModuleContext);
		mTitleColor = mJsParamsUtil.extrasTitleColor(mModuleContext);
	}

	@Override
	public int getCount() {
		return mExpandData.size();
	}

	@Override
	public Object getItem(int position) {
		return mExpandData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			int layoutId = UZResourcesIDFinder
					.getResLayoutID("mo_uichatbox_expand_girdview_item");
			convertView = View.inflate(mContext, layoutId, null);
			int item_iconId = UZResourcesIDFinder.getResIdID("item_icon");
			viewHolder.imageView = (ImageView) convertView
					.findViewById(item_iconId);
			int item_textId = UZResourcesIDFinder.getResIdID("item_text");
			viewHolder.textView = (TextView) convertView
					.findViewById(item_textId);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ExpandData expandData = mExpandData.get(position);
		BitmapDrawable nomalDrawable = createDrawable(expandData.getNomal(),
				null);
		BitmapDrawable pressDrawable = createDrawable(expandData.getPress(),
				null);
		viewHolder.imageView.setBackgroundDrawable(createStateDrawable(
				nomalDrawable, pressDrawable));
		viewHolder.textView.setText(expandData.getTitle());
		viewHolder.textView.setTextSize(mTitleSize);
		viewHolder.textView.setTextColor(mTitleColor);
		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int index = mViewPager.getCurrentItem() * 8 + position;
				mModule.openCallBack("clickExtras", index);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView imageView;
		TextView textView;
	}

	private BitmapDrawable createDrawable(String imgPath,
			BitmapDrawable defaultValue) {
		String realPath = mModule.makeRealPath(imgPath);
		Bitmap bitmap = mJsParamsUtil.getBitmap(realPath);
		if (bitmap != null) {
			return new BitmapDrawable(mContext.getResources(), bitmap);
		}
		return defaultValue;
	}

	private StateListDrawable createStateDrawable(BitmapDrawable nomalDrawable,
			BitmapDrawable pressDrawable) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_pressed }, pressDrawable);
		sd.addState(new int[] {}, nomalDrawable);
		return sd;
	}
}
