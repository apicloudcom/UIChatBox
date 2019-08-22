//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import java.util.ArrayList;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter {

	private ArrayList<String> mPathList;
	private int mPageNum;
	private Context mContext;
	private KeyClickListener mListener;
	private BitmapUtils mBitmapUtils;

	public GridAdapter(Context context, BitmapUtils mBitmapUtils,
			ArrayList<String> mPathList, int mPageNum, KeyClickListener listener) {
		this.mContext = context;
		this.mPathList = mPathList;
		this.mBitmapUtils = mBitmapUtils;
		this.mPageNum = mPageNum;
		this.mListener = listener;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		View v = convertView;
		if (v == null) {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			int mo_chatbox_gridview_itemId = UZResourcesIDFinder
					.getResLayoutID("mo_uichatbox_gridview_item");
			v = inflater.inflate(mo_chatbox_gridview_itemId, null);
			int itemId = UZResourcesIDFinder.getResIdID("item");
			viewHolder.imageView = (ImageView) v.findViewById(itemId);
			v.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) v.getTag();
		}
		final String path = mPathList.get(position);
		mBitmapUtils.display(viewHolder.imageView, path);
		viewHolder.imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mListener.keyClickedIndex(path);
			}
		});

		return v;
	}

	@Override
	public int getCount() {
		return mPathList.size();
	}

	static class ViewHolder {
		ImageView imageView;
	}

	@Override
	public String getItem(int position) {
		return mPathList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getPageNumber() {
		return mPageNum;
	}

	public interface KeyClickListener {
		public void keyClickedIndex(String index);
	}
}
