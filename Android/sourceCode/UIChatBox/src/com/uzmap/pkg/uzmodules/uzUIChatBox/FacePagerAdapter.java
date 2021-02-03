//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import java.util.ArrayList;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzmodules.uzUIChatBox.GridAdapter.KeyClickListener;
import com.uzmap.pkg.uzmodules.uzUIChatBox.GridAdapter.ViewHolder;

public class FacePagerAdapter extends PagerAdapter {
	private static final int NO_OF_EMOTICONS_PER_PAGE = 28;
	private ArrayList<String> mEmotions;
	private Context mContext;
	private KeyClickListener mListener;
	private BitmapUtils mBitmapUtils;
	private int mBgColor;

	public FacePagerAdapter(Context mContext, ArrayList<String> mEmotions,
			BitmapUtils mBitmapUtils, KeyClickListener listener, int bgColor) {
		this.mEmotions = mEmotions;
		this.mContext = mContext;
		this.mBitmapUtils = mBitmapUtils;
		this.mListener = listener;
		// 传入的色值;
		this.mBgColor = bgColor;
	}

	@Override
	public int getCount() {
		return (int) Math.ceil((double) mEmotions.size()
				/ (double) NO_OF_EMOTICONS_PER_PAGE);
	}

	@Override
	public Object instantiateItem(View collection, int position) {

		int mo_chatbox_gridId = UZResourcesIDFinder.getResLayoutID("mo_uichatbox_grid");
		View layout = View.inflate(mContext, mo_chatbox_gridId, null);

		int initialPosition = position * NO_OF_EMOTICONS_PER_PAGE;
		ArrayList<String> emoticonsInAPage = new ArrayList<String>();

		for (int i = initialPosition; i < initialPosition
				+ NO_OF_EMOTICONS_PER_PAGE
				&& i < mEmotions.size(); i++) {
			emoticonsInAPage.add(mEmotions.get(i));
		}
		
		int emoticons_gridId = UZResourcesIDFinder.getResIdID("emoticons_grid");
		GridView grid = (GridView) layout.findViewById(emoticons_gridId);
		
		grid.requestDisallowInterceptTouchEvent(true);
		// 设置的面板背景值; FIXME
		grid.setBackgroundColor(mBgColor);
		grid.setPadding(0, UZUtility.dipToPix(20), 0, 0);
		GridAdapter adapter = new GridAdapter(mContext, mBitmapUtils,
				emoticonsInAPage, position, mListener);
		grid.setAdapter(adapter);
		((ViewPager) collection).addView(layout);
		
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ViewHolder viewHolder = (ViewHolder) arg1.getTag();
				viewHolder.imageView.performClick();
			}
		});
		return layout;
	}

	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
}