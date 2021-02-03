package com.uzmap.pkg.uzmodules.uzUIChatBox;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CenterExtraPagerAdapter extends PagerAdapter {

	private Context mContext;
	private ArrayList<PageData> pageDatas = new ArrayList<PageData>();
	private UZModuleContext uzContext;

	public CenterExtraPagerAdapter(UZModuleContext uzContext, 
			Context context,
			ArrayList<PageData> pageDatas) {
		
		this.mContext = context;
		this.pageDatas = pageDatas;
		this.uzContext = uzContext;
		
	}

	@Override
	public int getCount() {
		return pageDatas.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public Object instantiateItem(View collection, final int position) {
		int center_extra_layout_id = UZResourcesIDFinder
				.getResLayoutID("mo_uichatbox_center_extra_layout");
		LinearLayout layout = (LinearLayout) View.inflate(mContext,
				center_extra_layout_id, null);

		setData(layout, pageDatas.get(position), position);
		((ViewPager) collection).addView(layout);
		return layout;
	}
	
	@Override
	public void destroyItem(View collection, int position, Object view) {
		((ViewPager) collection).removeView((View) view);
	}

	public void callback(int index) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("eventType", "clickExtras");
			ret.put("index", index);
			uzContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void setData(LinearLayout layout, PageData data, final int position) {

		if(data.extraDatas.size() > 0){
				ExtraData leftData = data.extraDatas.get(0);
				int left_img_id = UZResourcesIDFinder.getResIdID("leftImg");
				ImageView leftItemImage = (ImageView) layout.findViewById(left_img_id);
				leftItemImage.setImageBitmap(UZUtility.getLocalImage(uzContext.makeRealPath(leftData.itemImageUrl)));
		
				int left_title_id = UZResourcesIDFinder.getResIdID("leftTxt");
				TextView leftTitleTxt = (TextView) layout.findViewById(left_title_id);
				leftTitleTxt.setText(leftData.itemText);
		
				leftItemImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						int index = UzUIChatBox.mCurrentPageIndex * 2;
						callback(index);
					}
				});
		}

		if(data.extraDatas.size() > 1){
			ExtraData rightData = data.extraDatas.get(1);
			int right_img_id = UZResourcesIDFinder.getResIdID("rightImg");
			ImageView rightItemImage = (ImageView) layout.findViewById(right_img_id);
			rightItemImage.setImageBitmap(UZUtility.getLocalImage(uzContext.makeRealPath(rightData.itemImageUrl)));

			int right_title_id = UZResourcesIDFinder.getResIdID("rightTxt");
			TextView rightTitleTxt = (TextView) layout.findViewById(right_title_id);
			rightTitleTxt.setText(rightData.itemText);
			
			rightItemImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					int index = UzUIChatBox.mCurrentPageIndex * 2;
					callback(index + 1);
				}
			});	
		}
		
	}
	
	public static class PageData {
		public PageData(){
			
		}
		public ArrayList<ExtraData> extraDatas = new ArrayList<ExtraData>();
	}
}
