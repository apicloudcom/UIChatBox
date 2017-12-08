//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;

import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;

public class JsParamsUtil {
	private static JsParamsUtil instance;

	public static JsParamsUtil getInstance() {
		if (instance == null) {
			instance = new JsParamsUtil();
		}
		return instance;
	}

	public JSONObject paramJSONObject(UZModuleContext moduleContext, String name) {
		if (!moduleContext.isNull(name)) {
			return moduleContext.optJSONObject(name);
		}
		return null;
	}
	/***
	 * 在样式里面取出我们想要的变量；
	 * @param moduleContext
	 * @param parentName
	 * @param name
	 * @return
	 */
	public JSONObject innerParamJSONObject(UZModuleContext moduleContext,
			String parentName, String name) {
		//解析出来第一层json数据；
		JSONObject jsonObject = paramJSONObject(moduleContext, parentName);
		if (jsonObject != null) {
			//解析第二层json数据
			JSONObject innerObject = jsonObject.optJSONObject(name);
		     //返回最低一层json；
			if (innerObject != null) {
				return innerObject;
			}
		}
		return null;
	}

	public String placeholder(UZModuleContext moduleContext) {
		return moduleContext.optString("placeholder");
	}

	public boolean autoFocus(UZModuleContext moduleContext) {
		return moduleContext.optBoolean("autoFocus", false);
	}

	public int insertIndex(UZModuleContext moduleContext, int defaultValue) {
		int index = moduleContext.optInt("index", defaultValue);
		if (index > defaultValue) {
			index = defaultValue;
		}
		return index;
	}

	public String insertMsg(UZModuleContext moduleContext) {
		return moduleContext.optString("msg");
	}

	public int maxRows(UZModuleContext moduleContext) {
		return moduleContext.optInt("maxRows", 4);
	}

	public String emotionPath(UZModuleContext moduleContext) {
		return moduleContext.optString("emotionPath");
	}

	public String recordBtnNormalTitle(UZModuleContext moduleContext) {
		String defaultValue = "按住 说话";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "texts",
				"recordBtn");
		if (jsonObject != null) {
			return jsonObject.optString("normalTitle", defaultValue);
		}
		return defaultValue;
	}

	public String recordBtnActiveTitle(UZModuleContext moduleContext) {
		String defaultValue = "松开 结束";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "texts",
				"recordBtn");
		if (jsonObject != null) {
			return jsonObject.optString("activeTitle", defaultValue);
		}
		return defaultValue;
	}

	public int inputBarBorderColor(UZModuleContext moduleContext) {
		String defaultValue = "#d9d9d9";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"inputBar");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("borderColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int inputBarBgColor(UZModuleContext moduleContext) {
		String defaultValue = "#f2f2f2";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"inputBar");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("bgColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int inputBoxBorderColor(UZModuleContext moduleContext) {
		String defaultValue = "#B3B3B3";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"inputBox");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("borderColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int inputBoxBgColor(UZModuleContext moduleContext) {
		String defaultValue = "#FFFFFF";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"inputBox");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("bgColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public String faceBtnNormalImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"emotionBtn");
		if (jsonObject != null) {
			return jsonObject.optString("normalImg");
		}
		return null;
	}

	public String faceBtnActiveImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"emotionBtn");
		if (jsonObject != null) {
			return jsonObject.optString("activeImg");
		}
		return null;
	}

	public String extrasBtnNormalImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"extrasBtn");
		if (jsonObject != null) {
			return jsonObject.optString("normalImg");
		}
		return null;
	}

	public String extrasBtnActiveImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"extrasBtn");
		if (jsonObject != null) {
			return jsonObject.optString("activeImg");
		}
		return null;
	}

	public String keyboardBtnNormalImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"keyboardBtn");
		if (jsonObject != null) {
			return jsonObject.optString("normalImg");
		}
		return null;
	}

	public String keyboardBtnActiveImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"keyboardBtn");
		if (jsonObject != null) {
			return jsonObject.optString("activeImg");
		}
		return null;
	}

	public String speechBtnNormalImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"speechBtn");
		if (jsonObject != null) {
			return jsonObject.optString("normalImg");
		}
		return null;
	}

	public String speechBtnActiveImg(UZModuleContext moduleContext) {
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"speechBtn");
		if (jsonObject != null) {
			return jsonObject.optString("activeImg");
		}
		return null;
	}

	public String recordBtnNormalImg(UZModuleContext moduleContext) {
		String defaultValue = "#c4c4c4";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"recordBtn");
		if (jsonObject != null) {
			return jsonObject.optString("normalBg", defaultValue);
		}
		return defaultValue;
	}

	public String recordBtnActiveImg(UZModuleContext moduleContext) {
		String defaultValue = "#999999";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"recordBtn");
		if (jsonObject != null) {
			return jsonObject.optString("activeBg", defaultValue);
		}
		return defaultValue;
	}

	public int recordBtnColor(UZModuleContext moduleContext) {
		String defaultValue = "#000000";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"recordBtn");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("color",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int recordBtnFontSize(UZModuleContext moduleContext) {
		int defaultValue = 14;
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"recordBtn");
		if (jsonObject != null) {
			return jsonObject.optInt("size", defaultValue);
		}
		return defaultValue;
	}

	public int indicatorColor(UZModuleContext moduleContext) {
		String defaultValue = "#c4c4c4";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("color",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int indicatorActiveColor(UZModuleContext moduleContext) {
		String defaultValue = "#9e9e9e";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("activeColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public String indicatorTarget(UZModuleContext moduleContext) {
		String defaultValue = "both";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"indicator");
		if (jsonObject != null) {
			return jsonObject.optString("target", defaultValue);
		}
		return defaultValue;
	}

	public int extrasTitleSize(UZModuleContext moduleContext) {
		int defaultValue = 10;
		JSONObject jsonObject = paramJSONObject(moduleContext, "extras");
		if (jsonObject != null) {
			return jsonObject.optInt("titleSize", defaultValue);
		}
		return defaultValue;
	}

	public int extrasTitleColor(UZModuleContext moduleContext) {
		String defaultValue = "#a3a3a3";
		JSONObject jsonObject = paramJSONObject(moduleContext, "extras");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("titleColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public String sendBtnTitle(UZModuleContext moduleContext) {
		String defaultValue = "发送";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "texts",
				"sendBtn");
		if (jsonObject != null) {
			return jsonObject.optString("title", defaultValue);
		}
		return defaultValue;
	}
	
	public int sendBtnTitleSize(UZModuleContext moduleContext) {
		int defaultValue = 13;
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"sendBtn");
		if (jsonObject != null) {
			return jsonObject.optInt("titleSize", defaultValue);
		}
		return defaultValue;
	}

	public String sendBtnBg(UZModuleContext moduleContext) {
		String defaultValue = "#4cc518";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"sendBtn");
		if (jsonObject != null) {
			return jsonObject.optString("bg", defaultValue);
		}
		return defaultValue;
	}

	public String sendBtnHighlightBg(UZModuleContext moduleContext) {
		String defaultValue = "#46a91e";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"sendBtn");
		if (jsonObject != null) {
			return jsonObject.optString("activeBg", defaultValue);
		}
		return defaultValue;
	}

	public int sendBtnTitleColor(UZModuleContext moduleContext) {
		String defaultValue = "#ffffff";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"sendBtn");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString("titleColor",
					defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public int sendBtnHighlightTitleColor(UZModuleContext moduleContext) {
		String defaultValue = "#ffffff";
		JSONObject jsonObject = innerParamJSONObject(moduleContext, "styles",
				"sendBtn");
		if (jsonObject != null) {
			return UZUtility.parseCssColor(jsonObject.optString(
					"highlightTitleColor", defaultValue));
		}
		return UZUtility.parseCssColor(defaultValue);
	}

	public String listenerTarget(UZModuleContext moduleContext) {
		return moduleContext.optString("target");
	}

	public String listenerName(UZModuleContext moduleContext) {
		return moduleContext.optString("name");
	}

	public ArrayList<ExpandData> extras(UZModuleContext moduleContext) {
		JSONObject jsonObject = paramJSONObject(moduleContext, "extras");
		if (jsonObject != null) {
			JSONArray jsonArray = jsonObject.optJSONArray("btns");
			if (jsonArray != null && jsonArray.length() > 0) {
				ArrayList<ExpandData> extras = new ArrayList<ExpandData>();
				for (int i = 0; i < jsonArray.length(); i++) {
					String title = jsonArray.optJSONObject(i)
							.optString("title");
					String normal = jsonArray.optJSONObject(i).optString(
							"normalImg");
					String press = jsonArray.optJSONObject(i).optString(
							"activeImg");
					extras.add(new ExpandData(normal, press, title));
				}
				return extras;
			}
		}
		return null;
	}

	public Bitmap getBitmap(String path) {
		Bitmap bitmap = null;
		InputStream input = null;
		try {
			input = UZUtility.guessInputStream(path);
			bitmap = BitmapFactory.decodeStream(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (input != null) {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public String parseJsonFile(String path) {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			StringBuffer sb = new StringBuffer();
			inputStream = UZUtility.guessInputStream(path);
			if (inputStream == null) {
				return null;
			}
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream));
			String temp = null;
			while (true) {
				temp = bufferedReader.readLine();
				if (temp == null)
					break;
				String tmp = new String(temp.getBytes(), "utf-8");
				sb.append(tmp);
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					bufferedReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public int getScreenWidth(Activity act) {
		if (act == null) {
			return 0;
		}
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.widthPixels;
	}

	public int getScreenHeigth(Activity act) {
		if (act == null) {
			return 0;
		}
		DisplayMetrics metric = new DisplayMetrics();
		act.getWindowManager().getDefaultDisplay().getMetrics(metric);
		return metric.heightPixels;
	}
}
