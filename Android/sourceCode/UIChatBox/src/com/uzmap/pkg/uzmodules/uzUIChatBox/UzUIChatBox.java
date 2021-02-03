package com.uzmap.pkg.uzmodules.uzUIChatBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.uzmap.pkg.uzcore.UZCoreUtil;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import com.uzmap.pkg.uzcore.UZWebView;
import com.uzmap.pkg.uzcore.uzmodule.UZModule;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import com.uzmap.pkg.uzkit.UZUtility;
import com.uzmap.pkg.uzmodules.uzUIChatBox.CenterExtraPagerAdapter.PageData;
import com.uzmap.pkg.uzmodules.uzUIChatBox.GridAdapter.KeyClickListener;
import com.uzmap.pkg.uzmodules.uzUIChatBox.ViewBackground.BackgroundType;

public class UzUIChatBox extends UZModule implements OnClickListener, TextWatcher, AnimationListener, KeyClickListener, OnPageChangeListener {
	
	private static final int NO_OF_EMOTICONS_PER_PAGE = 28;
	private UZModuleContext mModuleContext;
	private UZModuleContext mToggleKeyboardCallBack;
	private UZModuleContext mChangeCallBack;
	private UZModuleContext mPressCallBack;
	private UZModuleContext mPressCancelCallBack;
	private UZModuleContext mMoveOutCallBack;
	private UZModuleContext mMoveOutCancelCallBack;
	private UZModuleContext mMoveInCallBack;
	private UZModuleContext mShowRecordCallBack;
	private UZModuleContext mShowEmotionCallBack;
	private UZModuleContext mShowExtrasCallBack;
	private UZModuleContext mValueChangeCallBack;
	private JsParamsUtil mJsParamsUtil;
	private View mSpaceView;
	// 聊天框的布局;
	private ChatBoxLayout mChatBoxLayout;

	private InputLinearLayout mEditLayout;
	private RelativeLayout mTableLayout;
	private ChatBoxEditText mEditText;
	private Button mRecordBtn;
	private ImageView mSpeechBtn;
	private ImageView mFaceBtn;
	private FrameLayout mSendLayout;
	private ImageView mExstraBtn;

	private Button mSendBtn;
	private ViewPager mFaceViewPager;
	private ViewPager mExtraViewPager;
	private IndictorView mIndictorView;
	private String mEmotionsPath;

	private Map<String, String> mEmotionMap;
	private Map<String, String> mInsertEmotionMap;
	private ArrayList<String> mEmotionsList;
	private ArrayList<ExpandData> mExtraParams;
	private BitmapUtils mBitmapUtils;

	private StateListDrawable mSpeechBtnDrawable;
	private StateListDrawable mFaceBtnDrawable;
	private StateListDrawable mSpeechKeyDrawable;
	private StateListDrawable mKeyboardBtnDrawable;

	private Drawable mRecordNoramlDrawable;
	private Drawable mRecordActiveDrawable;
	private Animation mSendBtnShowAnimation;
	private Animation mSendBtnHideAnimation;
	private CharSequence mTempMsg;
	private String mIndicatorTarget;
	private String mRecordNormalTitle;
	private String mRecordActiveTitle;
	private LayoutListener mLayoutListener;
	private boolean isOnlySendBtnExist;
	private boolean isShowAnimation;
	private boolean isKeyBoardVisible;
	private boolean isIndicatorVisible;
	private Handler mDelayedHandler = new Handler(Looper.getMainLooper());
	
	private Runnable mDelayedRunnable = new Runnable() {
		@Override
		public void run() {
			isCallBack = true;
			mTableLayout.setVisibility(View.VISIBLE);
		}
	};

	private RelativeLayout mRecordPanel;
	public static final String TAG = "Debug";

	public UzUIChatBox(UZWebView webView) {
		super(webView);
	}

	private boolean isClose = false;

	public void jsmethod_open(UZModuleContext moduleContext) {
		this.isClose = moduleContext.optBoolean("isClose");
		if (mChatBoxLayout == null) {
			initParams(moduleContext);
			createViews(moduleContext);
			// 初始化自定义表情；
			initEmotions();
			initExtras();
			initViews(moduleContext);
			initBorder(moduleContext, mEditLayout);
			// 设置编辑框的监听;
			setLayoutListener(mEditText);
		} else {
			mChatBoxLayout.setVisibility(View.VISIBLE);
		}

		mEditLayout.setPadding(mEditLayout.getPaddingLeft(),
				mJsParamsUtil.inputBoxTopMargin(moduleContext),
				mEditLayout.getPaddingRight(), mEditLayout.getPaddingBottom());
		int leftMargin = mJsParamsUtil.inputBarTextMarginLeft(moduleContext);
		mEditText.setPadding(leftMargin, ChatBoxEditText.DEFAULT_PADDING,
				ChatBoxEditText.DEFAULT_PADDING,
				ChatBoxEditText.DEFAULT_PADDING);

		if (moduleContext.optBoolean("disableSendMessage")) {
			mEditText.setEnabled(false);
			mRecordBtn.setEnabled(false);
			mSpeechBtn.setEnabled(false);
			mFaceBtn.setEnabled(false);
			mExstraBtn.setEnabled(false);
		}
		
		JSONObject stylesObj = mModuleContext.optJSONObject("styles");
		if(stylesObj != null){
			JSONObject inputBarObj = stylesObj.optJSONObject("inputBar");
			if(inputBarObj != null){
				String placeholderColor = inputBarObj.optString("placeholderColor");
				if(!TextUtils.isEmpty(placeholderColor)){
					mEditText.setHintTextColor(UZUtility.parseCssColor(placeholderColor));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void jsmethod_close(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			removeViewFromCurWindow(mChatBoxLayout);
			removeViewFromCurWindow(mSpaceView);
			mDelayedHandler.removeCallbacks(mDelayedRunnable);
			mDelayedHandler.removeCallbacks(mDelayedShowKeyBoardRunnable);
			if (mEditText != null) {
				mEditText.getViewTreeObserver().removeGlobalOnLayoutListener(
						mLayoutListener);
				mLayoutListener = null;
			}
			mChatBoxLayout = null;
			mToggleKeyboardCallBack = null;
			mChangeCallBack = null;
			mPressCallBack = null;
			mPressCancelCallBack = null;
			mMoveOutCallBack = null;
			mMoveOutCancelCallBack = null;
			mMoveInCallBack = null;
			mShowRecordCallBack = null;
			mShowEmotionCallBack = null;
			mShowExtrasCallBack = null;
			mValueChangeCallBack = null;
		}

		if (coverLayout != null) {
			removeViewFromCurWindow(coverLayout);
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onClean() {
		
		if (mChatBoxLayout != null) {
			removeViewFromCurWindow(mChatBoxLayout);
			removeViewFromCurWindow(mSpaceView);
			if(mDelayedHandler != null){
				mDelayedHandler.removeCallbacks(mDelayedRunnable);
				mDelayedHandler.removeCallbacks(mDelayedShowKeyBoardRunnable);
			}
			if (mEditText != null) {
				mEditText.getViewTreeObserver().removeGlobalOnLayoutListener(mLayoutListener);
				mLayoutListener = null;
			}
			mChatBoxLayout = null;
			mToggleKeyboardCallBack = null;
			mChangeCallBack = null;
			mPressCallBack = null;
			mPressCancelCallBack = null;
			mMoveOutCallBack = null;
			mMoveOutCancelCallBack = null;
			mMoveInCallBack = null;
			mShowRecordCallBack = null;
			mShowEmotionCallBack = null;
			mShowExtrasCallBack = null;
			mValueChangeCallBack = null;
		}

		if (coverLayout != null) {
			removeViewFromCurWindow(coverLayout);
		}
	}

	public void jsmethod_hide(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			mChatBoxLayout.setVisibility(View.GONE);
			mSpaceView.setVisibility(View.GONE);
			if (coverLayout != null) {
				coverLayout.setVisibility(View.GONE);
			}
		}
	}

	public void jsmethod_show(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			mChatBoxLayout.setVisibility(View.VISIBLE);
			mSpaceView.setVisibility(View.VISIBLE);
			if (coverLayout != null) {
				coverLayout.setVisibility(View.VISIBLE);
			}

		}
	}

	public void jsmethod_popupBoard(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			String target = moduleContext.optString("target", "emotion");
			if (target.equals("emotion")) {
				clickFaceBtnShowTable();
				setEmotionPageNums();
			} else {
				clickExtraBtnShowTable();
				setExtraPageNums();
			}
		}
	}

	public void jsmethod_closeBoard(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			mTableLayout.setVisibility(View.GONE);
		}
	}

	/***
	 * 键盘的弹出与显示;
	 * 
	 * @param moduleContext
	 */
	public void jsmethod_popupKeyboard(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			showKeybord();
		}
	}

	public void jsmethod_closeKeyboard(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					hideInputMethod(((Activity) context()).getCurrentFocus());
					mTableLayout.setVisibility(View.GONE);
					resetBtn();
				}
			}, 300);

		}
	}

	/***
	 * 获取或设置聊天输入框的内容
	 */
	public void jsmethod_value(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			String msg = mJsParamsUtil.insertMsg(moduleContext);
			if (moduleContext.isNull("msg")) {
				valueCallBack(moduleContext, getEditTextStr());
			} else if (TextUtils.isEmpty(msg)) {
				mEditText.setText("");
				valueCallBack(moduleContext, "");
			} else {
				final SpannableString insertMsg = parseMsg(msg);
				mEditText.setText(insertMsg);
				mEditText.post(new Runnable(){
					@Override
					public void run() {
						mEditText.setSelection(insertMsg.length());
					}
				});
				valueCallBack(moduleContext, getEditTextStr());
			}
		}
	}

	public void jsmethod_setPlaceholder(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			String hint = mJsParamsUtil.placeholder(moduleContext);
			mEditText.setHint(hint);
		}
	}

	public void jsmethod_insertValue(UZModuleContext moduleContext) {
		if (mChatBoxLayout != null) {
			String msg = mJsParamsUtil.insertMsg(moduleContext);
			int defaultIndex = mEditText.getText().length();
			int index = mJsParamsUtil.insertIndex(moduleContext, defaultIndex);
			SpannableString insertMsg = parseMsg(msg);
			mEditText.getText().insert(index, insertMsg);
		}
	}

	public void jsmethod_switchInputArea(UZModuleContext uzContext) {

	}

	public void jsmethod_setSwitchBtnIcon(UZModuleContext uzContext) {

	}

	/***
	 * 
	 * @param moduleContext
	 */
	private UZModuleContext mRecordCanceledContext;

	public void jsmethod_addEventListener(UZModuleContext moduleContext) {

		// 输入框的监听以及目标；
		String target = mJsParamsUtil.listenerTarget(moduleContext);
		String name = mJsParamsUtil.listenerName(moduleContext);
		if (target.equals("inputBar")) {
			if (name.equals("move")) {
				mToggleKeyboardCallBack = moduleContext;
			} else if (name.equals("change")) {
				mChangeCallBack = moduleContext;
			} else if (name.equals("showRecord")) {
				mShowRecordCallBack = moduleContext;
			} else if (name.equals("showEmotion")) {
				mShowEmotionCallBack = moduleContext;
			} else if (name.equals("showExtras")) {
				mShowExtrasCallBack = moduleContext;
			} else if (name.equals("valueChanged")) {
				mValueChangeCallBack = moduleContext;
			}

			// 语音处的操作；
		} else if (target.equals("recordBtn")) {
			// 按下录音；
			if (name.equals("press")) {
				mPressCallBack = moduleContext;
			} else if (name.equals("press_cancel")) {
				mPressCancelCallBack = moduleContext;
			} else if (name.equals("move_out")) {
				mMoveOutCallBack = moduleContext;
			} else if (name.equals("move_out_cancel")) {
				mMoveOutCancelCallBack = moduleContext;
			} else if (name.equals("move_in")) {
				mMoveInCallBack = moduleContext;
			} else if (name.equals("recordCanceled")) {
				mRecordCanceledContext = moduleContext;
			}
		}
	}

	public void jsmethod_reloadExtraBoard(UZModuleContext moduleContext) {
		mExtraParams = mJsParamsUtil.extras(moduleContext);
		if (mExtraParams != null) {
			setExtraPageNums();
			setExtraAdapter(moduleContext);
		}
	}

	private void initParams(UZModuleContext moduleContext) {
		mModuleContext = moduleContext;
		mBitmapUtils = new BitmapUtils(getWidgetInfo(), context());
		mJsParamsUtil = JsParamsUtil.getInstance();
		mEmotionMap = new HashMap<String, String>();
		mInsertEmotionMap = new HashMap<String, String>();
		mEmotionsList = new ArrayList<String>();
		mExtraParams = new ArrayList<ExpandData>();
		isOnlySendBtn();
		initConstansColors();
		initSendBtnAnimations();
	}

	private void isOnlySendBtn() {
		isOnlySendBtnExist = !isBtnShow("extrasBtn");
	}

	@SuppressLint("NewApi")
	private void createViews(final UZModuleContext moduleContext) {
		mSpaceView = new View(context());
		mChatBoxLayout = new ChatBoxLayout(context());

		mChatBoxLayout.setUiChatBox(this);
		mEditLayout = new InputLinearLayout(context());
		mTableLayout = new RelativeLayout(context());
		mEditText = new ChatBoxEditText(context());
		mRecordBtn = new Button(context());
		mSpeechBtn = new ImageView(context());
		mFaceBtn = new ImageView(context());
		mSendLayout = new FrameLayout(context());
		mExstraBtn = new ImageView(context());
		mFaceViewPager = new ViewPager(context());
		mExtraViewPager = new ViewPager(context());
		mIndictorView = new IndictorView(context());
		mRecordPanel = new RelativeLayout(context());

		mRecordPanel.setClickable(true);
		addRecordBtnToPanel(mRecordPanel);

	}

	public void callbackForBarHeight(UZModuleContext uzContext,
			int inputBarHeight) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("inputBarHeight", inputBarHeight);
			uzContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initIndictorView(int pointNums) {
		initIndictorLayout();
		initIndictorParams(pointNums);
	}

	private void initIndictorLayout() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				UZUtility.dipToPix(20));
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mIndictorView.setLayoutParams(params);
	}

	private void initIndictorParams(int pointNums) {
		int normalColor = mJsParamsUtil.indicatorColor(mModuleContext);
		int activeColor = mJsParamsUtil.indicatorActiveColor(mModuleContext);
		int w = (2 * pointNums - 1) * UZUtility.dipToPix(3) * 2;
		int screenWidth = mJsParamsUtil.getScreenWidth((Activity) context());
		int startX = (int) (screenWidth / 2.0 - w / 2.0);
		mIndictorView.initParams(pointNums, startX, normalColor, activeColor);
		mIndicatorTarget = mJsParamsUtil.indicatorTarget(mModuleContext);
		JSONObject jsonObject = mJsParamsUtil.innerParamJSONObject(
				mModuleContext, "styles", "indicator");
		isIndicatorVisible = (jsonObject == null ? false : true);
	}

	private void chargeIndictorVisible(String tableType) {
		if (isIndicatorVisible) {
			if (mIndicatorTarget.equals("both")
					|| tableType.equals(mIndicatorTarget)) {
				mIndictorView.setVisibility(View.VISIBLE);
			} else {
				mIndictorView.setVisibility(View.GONE);
			}
		} else {
			mIndictorView.setVisibility(View.GONE);
		}
	}

	// 初始化表情；
	private void initEmotions() {
		// 获取到自定义表情文件夹路径；由外部传入的；
		mEmotionsPath = mJsParamsUtil.emotionPath(mModuleContext);
		// widget://image/emotion
		/** 添加判断代码表示:没有传入传入emotionPath时直接退出初始化表情库的方法 **/
		int jsonNameIndex = mEmotionsPath.lastIndexOf('/') + 1;
		// 截取表情文件夹的名字；
		String jsonName = mEmotionsPath.substring(jsonNameIndex);
		// 真正的路径；
		String realPath = makeRealPath(mEmotionsPath + "/" + jsonName + ".json");
		String emotionsStr = mJsParamsUtil.parseJsonFile(realPath);
		parseEmotionJson(emotionsStr);
		initFaceViewPager();
	}

	/***
	 * 初始化附加功能处显示的适配器;
	 */
	private void initExtras() {
		mExtraParams = mJsParamsUtil.extras(mModuleContext);
		if (mExtraParams != null) {
			setExtraPageNums();
			setExtraAdapter(mModuleContext);
		}
	}

	/***
	 * 附加功能区的创建适配器;
	 * 
	 * @param moduleContext
	 */
	@SuppressWarnings("deprecation")
	private void setExtraAdapter(UZModuleContext moduleContext) {
		ExtraPagerAdapter expandMyPagerAdapter = createExtraAdapter(moduleContext);
		mExtraViewPager.setAdapter(expandMyPagerAdapter);
		mExtraViewPager.setOnPageChangeListener(this);
	}

	/***
	 * 设置页数;
	 */
	private void setExtraPageNums() {
		int size = mExtraParams.size();
		int pageSize;
		int pageNums;
		if(mIsCenterDisplay){
			pageSize = 2;
			pageNums = (size + pageSize - 1) / pageSize;
		} else {
			pageSize = 8;
			pageNums = (size + pageSize - 1) / pageSize;
		}
		mExtraViewPager.setOffscreenPageLimit(pageNums);
		initIndictorView(pageNums);
	}

	private ExtraPagerAdapter createExtraAdapter(UZModuleContext moduleContext) {
		// 附加功能区显示背景颜色;
		int bgColor = mJsParamsUtil.inputBoxBoardBgColor(mModuleContext);
		// int bgColor = mJsParamsUtil.inputBarBgColor(moduleContext);
		return new ExtraPagerAdapter(this, mExtraParams, context(),
				mExtraViewPager, moduleContext, bgColor);
	}

	/***
	 * 解析表情文件;
	 * 
	 * @param emotionsStr
	 */
	private void parseEmotionJson(String emotionsStr) {
		if (emotionsStr != null && !TextUtils.isEmpty(emotionsStr)) {
			try {
				JSONArray jsonArr = new JSONArray(emotionsStr);
				JSONObject jsonObject = null;
				for (int i = 0; i < jsonArr.length(); i++) {
					jsonObject = jsonArr.optJSONObject(i);
					String nameStr = jsonObject.optString("name");
					String name = mEmotionsPath + "/" + nameStr + ".png";
					String text = jsonObject.optString("text");
					mEmotionMap.put(name, text);
					// 表情的文本信息;
					mInsertEmotionMap.put(text, name);
					// 表情的名字;
					mEmotionsList.add(name);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void initFaceViewPager() {
		setEmotionPageNums();
		addDeleteEmotionBtn();
		setFaceAdapter();
	}

	@SuppressWarnings("deprecation")
	private void setFaceAdapter() {
		// 表情的适配器;
		FacePagerAdapter adapter = createFacePageAdapter();
		mFaceViewPager.setAdapter(adapter);
		mFaceViewPager.setOnPageChangeListener(this);
	}

	// 创建表情的适配器的方法;
	private FacePagerAdapter createFacePageAdapter() {
		// 获取传入表情面板的颜色值;

		int bgColor = mJsParamsUtil.inputBoxBoardBgColor(mModuleContext);
		return new FacePagerAdapter(context(), mEmotionsList, mBitmapUtils,
				this, bgColor);
	}

	private void addDeleteEmotionBtn() {
		String delete = mEmotionsPath + "/delete.png";
		int size = 0;
		for (int i = 1; i <= mFaceViewPager.getOffscreenPageLimit(); i++) {
			int deleteIndex = (i * NO_OF_EMOTICONS_PER_PAGE) - 1;
			size = mEmotionsList.size();
			if (deleteIndex > size - 1) {
				mEmotionsList.add(size, delete);
			} else {
				mEmotionsList.add(deleteIndex, delete);
			}
			size = mEmotionsList.size();
			if (deleteIndex == (size - 1)) {
				mEmotionsList.add(delete);
			}
		}
	}

	private void setEmotionPageNums() {
		int size = mEmotionsList.size();
		int pageSize = NO_OF_EMOTICONS_PER_PAGE;
		int pageNums = (size + pageSize - 1) / pageSize;
		mFaceViewPager.setOffscreenPageLimit(pageNums);
		initIndictorView(pageNums);
	}

	private void initViews(UZModuleContext uzContext) {
		initInputAreaView();
		initTableLayout(uzContext);
		initChatBoxLayout();
		insertSpaceView();
		insertCahtBoxLayout();
	}

	private void initInputAreaView() {
		// 左侧图标；
		initSpeechBtn();
		// 录音button
		initRecordBtn();
		// 输入框；
		initEditText();
		// 表情按钮图标；
		initFaceBtn();
		// 发送按钮图标；
		initSendBtn();
		// 额外按钮；
		initExtraBtn();
		// 发送的布局；
		initSendLayout();
		// 初始化编辑区的布局；
		initEditLayout();
	}

	private void initConstansColors() {
		int barBorderColor = mJsParamsUtil.inputBarBorderColor(mModuleContext);
		Constans.INPUT_LAYOUT_BORDER_COLOR = barBorderColor;
		int borderColor = mJsParamsUtil.inputBoxBorderColor(mModuleContext);
		Constans.INPUT_BOX_BORDER_COLOR = borderColor;
		int bgColor = mJsParamsUtil.inputBoxBgColor(mModuleContext);
		Constans.INPUT_BOX_BG_COLOR = bgColor;
		Constans.INPUT_BOX_CORNER = mJsParamsUtil
				.inputBoxBorderCorner(mModuleContext);
	}

	private void initSendBtnAnimations() {
		int showAnimaId = UZResourcesIDFinder.getResAnimID("unzoom_in");
		mSendBtnShowAnimation = AnimationUtils.loadAnimation(context(),
				showAnimaId);
		mSendBtnShowAnimation.setAnimationListener(this);
		int hideAnimaId = UZResourcesIDFinder.getResAnimID("unzoom_out");
		mSendBtnHideAnimation = AnimationUtils.loadAnimation(context(),
				hideAnimaId);
		mSendBtnHideAnimation.setAnimationListener(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void insertSpaceView() {
		mSpaceView.setBackgroundColor(Color.TRANSPARENT);
		mSpaceView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onSpaceViewClick();
				return false;
			}
		});

		String fixedOn = mModuleContext.optString("fixedOn");
		insertViewToCurWindow(mSpaceView, spaceViewLayout(), fixedOn);

	}

	private RelativeLayout.LayoutParams spaceViewLayout() {
		return new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
	}

	/***
	 * 这里是点击非键盘区域的事件监听;
	 */
	@SuppressWarnings("deprecation")
	private void onSpaceViewClick() {
		mTableLayout.setVisibility(View.GONE);
		mFaceBtn.setBackgroundDrawable(mFaceBtnDrawable);
		mExstraBtn.setBackgroundDrawable(bgDrawable);
		if (context() != null) {
			hideInputMethod(((Activity) context()).getCurrentFocus());
		}

		if (isClose) {
			jsmethod_close(mModuleContext);
		}
	}

	private LinearLayout coverLayout;

	private void insertCahtBoxLayout() {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mChatBoxLayout.setOrientation(LinearLayout.VERTICAL);
		final String fixedOn = mModuleContext.optString("fixedOn");

		insertViewToCurWindow(mChatBoxLayout, params, fixedOn);

		if (mModuleContext.optBoolean("disableSendMessage")) {
			mChatBoxLayout.post(new Runnable() {
				@Override
				public void run() {

					coverLayout = new LinearLayout(context());
					coverLayout.setBackgroundColor(0x88000000);

					RelativeLayout.LayoutParams coverParams = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							UZCoreUtil.pixToDip(mChatBoxLayout
									.getMeasuredHeight()));

					coverParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					insertViewToCurWindow(coverLayout, coverParams, fixedOn);
				}

			});
		}
	}

	private void autoFocus() {
		boolean isAutoFocus = mJsParamsUtil.autoFocus(mModuleContext);
		mChatBoxLayout.setAutoFocus(isAutoFocus);
	}

	private void initChatBoxLayout() {
		mChatBoxLayout.setWillNotDraw(false);
		mChatBoxLayout.addView(mEditLayout);
		mChatBoxLayout.addView(mTableLayout);
		autoFocus();
	}

	public void initBorder(UZModuleContext uzContext,
			InputLinearLayout inputLayout) {
		JSONObject stylesObj = uzContext.optJSONObject("styles");
		if (stylesObj != null) {
			JSONObject topDividerObj = stylesObj.optJSONObject("topDivider");
			if (topDividerObj != null) {
				String borderColor = topDividerObj.optString("color", "#000");
				int width = UZUtility.dipToPix(topDividerObj.optInt("width"));
				inputLayout.setTopBorderHeight(width);

				String realPath = uzContext.makeRealPath(borderColor);
				Bitmap borderBmp = UZUtility.getLocalImage(realPath);
				if (borderBmp != null) {
					inputLayout.setTopBorderBitmap(borderBmp);
				} else {
					inputLayout.setTopBorderColor(UZUtility
							.parseCssColor(borderColor));
				}
			}
		}
	}

	private void initEditLayout() {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		params.gravity = Gravity.CENTER_VERTICAL;
		mEditLayout.setOrientation(LinearLayout.HORIZONTAL);
		mEditLayout.setLayoutParams(params);
		mEditLayout.addView(mSpeechBtn);
		mEditLayout.addView(mRecordBtn);
		mEditLayout.addView(mEditText);
		mEditLayout.addView(mFaceBtn);
		mEditLayout.addView(mSendLayout);
		mEditLayout.setOnClickListener(this);
		initEditLayoutColors();
	}

	private void initEditLayoutColors() {
		int bgColor = mJsParamsUtil.inputBarBgColor(mModuleContext);
		mEditLayout.setBackgroundColor(bgColor);
		mEditText.setTextColor(mJsParamsUtil.inputTextColor(mModuleContext));
		mEditText.setTextSize(mJsParamsUtil.inputTextSize(mModuleContext));
	}

	private boolean mIsCenterDisplay = false;

	private void initTableLayout(UZModuleContext uzContext) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				UZUtility.dipToPix(50) * 4 + UZUtility.dipToPix(20));

		params.addRule(RelativeLayout.CENTER_VERTICAL);
		
		mTableLayout.setLayoutParams(params);
		mTableLayout.setVisibility(View.GONE);
		mTableLayout.addView(mFaceViewPager);
		mTableLayout.addView(mExtraViewPager);
		mTableLayout.addView(mIndictorView);

		// TODO start
		JSONObject extrasObj = uzContext.optJSONObject("extras");
		if (extrasObj != null && extrasObj.optBoolean("isCenterDisplay")) {
			mIsCenterDisplay = true;
			generateData();
			CenterExtraPagerAdapter centerExtraAdapter = new CenterExtraPagerAdapter(
					uzContext, context(), pageDatas);
			mExtraViewPager.setAdapter(centerExtraAdapter);
		}
		// TODO end

		int match_parent = RelativeLayout.LayoutParams.MATCH_PARENT;
		mRecordPanel.setLayoutParams(new RelativeLayout.LayoutParams(
				match_parent, match_parent));
		mTableLayout.addView(mRecordPanel);

		int bgColor = mJsParamsUtil.inputBarBgColor(mModuleContext);
		mTableLayout.setBackgroundColor(bgColor);

	}

	private ArrayList<PageData> pageDatas = new ArrayList<PageData>();

	public void generateData() {
		PageData pageData = null;
		for (int i = 0; i < mExtraParams.size(); i++) {

			String imgPath = mExtraParams.get(i).getNomal();
			if (TextUtils.isEmpty(imgPath)) {
				imgPath = mExtraParams.get(i).getPress();
			}

			if (i % 2 == 0) {
				pageData = new PageData();
				pageData.extraDatas.add(new ExtraData(imgPath, mExtraParams
						.get(i).getTitle()));
				pageDatas.add(pageData);
			} else {
				pageData.extraDatas.add(new ExtraData(imgPath, mExtraParams
						.get(i).getTitle()));
			}
		}
	}

	private void initSpeechBtn() {
		// 将dip单位的数值转为当前设备的绝对像素值.该函数会根据当前设备的屏幕密度进行换算
		LayoutParams params = new LayoutParams(
				UZUtility.dipToPix(Constans.BTN_SIZE),
				UZUtility.dipToPix(Constans.BTN_SIZE));
		params.gravity = Gravity.BOTTOM;
		int margin = UZUtility.dipToPix(Constans.INPUT_BOX_MARGIN);
		params.setMargins(margin, margin, margin, margin);
		// 设置imageview的位置；
		mSpeechBtn.setLayoutParams(params);

		initSpeechBtnVisible();
		initSpeechBtnBg();
		// 最左侧的切换图片；
		// 设置点击监听；
		mSpeechBtn.setOnClickListener(this);
	}

	private void initSpeechBtnVisible() {
		// 没有传入左侧图标图片时，隐藏imageview；
		if (!isBtnShow("speechBtn")) {

			mSpeechBtn.setVisibility(View.GONE);
		}

	}

	@SuppressWarnings("deprecation")
	private void initSpeechBtnBg() {
		// style /speechBtn 节点下的 normalImg 的url;
		String normalStr = mJsParamsUtil.speechBtnNormalImg(mModuleContext);
		// URL生成图片；
		BitmapDrawable normal = createDrawable(normalStr, null);

		// 按下后的图片url,目前接口中没有用到；
		String activeStr = mJsParamsUtil.speechBtnActiveImg(mModuleContext);

		// 按下URL生成图片；
		BitmapDrawable active = createDrawable(activeStr, normal);
		// 说明外部传入值了，然后显示控件；
		if (normal != null) {
			// 设置状态选择器；
			mSpeechBtnDrawable = createStateDrawable(normal, active);
			mSpeechBtn.setBackgroundDrawable(mSpeechBtnDrawable);
		}
	}

	private void initRecordBtn() {
		initRecordBtnLayout();
		initRecordBtnTextParams();
		initRecordBtnBg();
		setRecordBtnListener();
	}

	private void initRecordBtnLayout() {
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				UZUtility.dipToPix(Constans.BTN_SIZE), 1.0f);
		params.gravity = Gravity.CENTER_VERTICAL;
		int margin = UZUtility.dipToPix(Constans.INPUT_BOX_MARGIN);
		params.setMargins(margin, margin, margin, margin);
		mRecordBtn.setLayoutParams(params);
		mRecordBtn.setPadding(0, 0, 0, 0);
		mRecordBtn.setTransformationMethod(null);
		mRecordBtn.setVisibility(View.GONE);
	}

	private void initRecordBtnTextParams() {
		mRecordNormalTitle = mJsParamsUtil.recordBtnNormalTitle(mModuleContext);
		mRecordActiveTitle = mJsParamsUtil.recordBtnActiveTitle(mModuleContext);
		mRecordBtn.setText(mRecordNormalTitle);
		int color = mJsParamsUtil.recordBtnColor(mModuleContext);
		mRecordBtn.setTextColor(color);
		int size = mJsParamsUtil.recordBtnFontSize(mModuleContext);
		mRecordBtn.setTextSize(size);
	}

	@SuppressWarnings("deprecation")
	private void initRecordBtnBg() {
		String normalBg = mJsParamsUtil.recordBtnNormalImg(mModuleContext);
		String activeBg = mJsParamsUtil.recordBtnActiveImg(mModuleContext);
		if (isBitmap(normalBg)) {
			mRecordNoramlDrawable = new BitmapDrawable(
					context().getResources(),
					mBitmapUtils.generateBitmap(normalBg));
			mRecordActiveDrawable = new BitmapDrawable(
					context().getResources(),
					mBitmapUtils.generateBitmap(activeBg));
		} else {
			mRecordNoramlDrawable = new ColorDrawable(
					UZUtility.parseCssColor(normalBg));
			mRecordActiveDrawable = new ColorDrawable(
					UZUtility.parseCssColor(activeBg));
		}
		mRecordBtn.setBackgroundDrawable(mRecordNoramlDrawable);
	}

	private void setRecordBtnListener() {
		mRecordBtn.setOnTouchListener(new OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					onRecordEventDown();

					break;
				case MotionEvent.ACTION_MOVE:
					onRecordEventMove(event);
					break;
				case MotionEvent.ACTION_UP:
					onRecordEventUp(event);
					break;

				default:
					onRecordEventUp(event);
					break;
				}
				return true;
			}
		});
	}

	private boolean isInRecord;

	@SuppressWarnings("deprecation")
	private void onRecordEventDown() {
		recordEventBack("press");
		isInRecord = true;
		mRecordBtn.setText(mRecordActiveTitle);
		mRecordBtn.setBackgroundDrawable(mRecordActiveDrawable);
	}

	private void onRecordEventMove(MotionEvent event) {
		if (!isTouchInRecordBtn(event)) {
			if (isInRecord) {
				recordEventBack("move_out");
			}
			isInRecord = false;
		} else {
			if (!isInRecord) {
				recordEventBack("move_in");
				isInRecord = true;
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void onRecordEventUp(MotionEvent event) {
		if (isTouchInRecordBtn(event)) {
			recordEventBack("press_cancel");
		} else {
			recordEventBack("move_out_cancel");
		}
		mRecordBtn.setText(mRecordNormalTitle);
		mRecordBtn.setBackgroundDrawable(mRecordNoramlDrawable);
	}

	private void recordEventBack(String eventName) {
		if (eventName.equals("press")) {
			if (mPressCallBack != null) {
				JSONObject ret = new JSONObject();
				mPressCallBack.success(ret, false);
			}
		} else if (eventName.equals("press_cancel")) {
			if (mPressCancelCallBack != null) {
				JSONObject ret = new JSONObject();
				mPressCancelCallBack.success(ret, false);
			}
		} else if (eventName.equals("move_out")) {
			if (mMoveOutCallBack != null) {
				mMoveOutCallBack.success(null, false);
			}
		} else if (eventName.equals("move_out_cancel")) {
			if (mMoveOutCancelCallBack != null) {
				mMoveOutCancelCallBack.success(null, false);
			}
		} else if (eventName.equals("move_in")) {
			if (mMoveInCallBack != null) {
				mMoveInCallBack.success(null, false);
			}
		}
	}

	private boolean isTouchInRecordBtn(MotionEvent event) {
		float x = event.getRawX();
		float y = event.getRawY();

		int[] location = new int[2];
		mRecordBtn.getLocationOnScreen(location);
		int viewX = location[0];
		int viewY = location[1];

		float viewWidth = mRecordBtn.getWidth();
		float viewHeight = mRecordBtn.getHeight();

		if (x <= viewX + viewWidth && x >= viewX && y <= viewY + viewHeight
				&& y >= viewY) {
			return true;
		}
		return false;
	}

	private boolean isBitmap(String jsonStr) {
		if (!TextUtils.isEmpty(jsonStr)) {
			if (jsonStr.contains("://")) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private void initEditText() {
		initEditTextParams();
		initEditTextLayout();
		initEditColors();
		initEditListener();
	}

	private void initEditTextParams() {
		String placeholder = mJsParamsUtil.placeholder(mModuleContext);
		mEditText.setHint(placeholder);
		int maxRows = mJsParamsUtil.maxRows(mModuleContext);
		mEditText.setMaxLines(maxRows);
	}

	private void initEditTextLayout() {
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, 1.0f);
		params.gravity = Gravity.CENTER_VERTICAL;
		int margin = UZUtility.dipToPix(Constans.INPUT_BOX_MARGIN);
		params.setMargins(margin, margin, margin, margin);
		mEditText.setLayoutParams(params);
		mEditText.setGravity(Gravity.CENTER_VERTICAL);
		mEditText.setPadding(margin, margin, margin, margin);
	}

	@SuppressWarnings("deprecation")
	private void initEditColors() {
		setEditTextBg();

		String leftIconPath = mJsParamsUtil
				.inputBoxLeftIconPath(mModuleContext);
		int leftIconSize = mJsParamsUtil.inputBoxLeftIconSize(mModuleContext);
		Bitmap leftBmp = generateBmp(UZUtility.getLocalImage(leftIconPath),
				leftIconSize);
		if (leftBmp != null) {
			mEditText.setLeftIcon(leftBmp);
		}
	}

	private void setEditTextBg() {
		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setColor(Constans.INPUT_BOX_BG_COLOR);

		int borderCorner = mJsParamsUtil.inputBoxBorderCorner(mModuleContext);
		gradientDrawable.setCornerRadius(borderCorner);
		gradientDrawable.setStroke(UZUtility.dipToPix(1),Constans.INPUT_BOX_BORDER_COLOR);

		// ui
		mEditText.setBackgroundDrawable(gradientDrawable);
	}

	public Bitmap generateBmp(Bitmap sourceBmp, int size) {
		if (sourceBmp == null) {
			return null;
		}
		return Bitmap.createScaledBitmap(sourceBmp, size, size, true);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void initEditListener() {
		mEditText.addTextChangedListener(this);
		mEditText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onEditTextClick();
				return false;
			}
		});
	}

	private void initFaceBtn() {
		initFaceBtnLayout();
		initFaceBtnBg();
		// 右侧添加的表情；
		mFaceBtn.setOnClickListener(this);
	}

	/***
	 * 设置控件的位置；
	 */
	private void initFaceBtnLayout() {
		// 创建布局位置，将控件放入其中；
		LayoutParams params = new LayoutParams(
				UZUtility.dipToPix(Constans.BTN_SIZE),
				UZUtility.dipToPix(Constans.BTN_SIZE));
		params.gravity = Gravity.BOTTOM;
		int margin = UZUtility.dipToPix(Constans.INPUT_BOX_MARGIN);
		params.setMargins(margin, margin, margin, margin);
		mFaceBtn.setLayoutParams(params);
	}

	@SuppressWarnings("deprecation")
	private void initFaceBtnBg() {

		// xml自带的一个表情图标；获取到他的int值；
		// 默认的表情图标；
		int faceBtnImgId = UZResourcesIDFinder
				.getResDrawableID("mo_uichatbox_face_btn");
		// 将默认图片生成一张bitmap图片；
		BitmapDrawable defaultValue = createDrawable(faceBtnImgId);
		// 获取到传进来的显示表情样式图片url；
		String normalImgStr = mJsParamsUtil.faceBtnNormalImg(mModuleContext);
		// 说明没有传入值；隐藏图标；
		if (normalImgStr == null) {
			mFaceBtn.setVisibility(View.GONE);

			// 状态选择器添加默认的bitmap；
			mFaceBtnDrawable = createStateDrawable(defaultValue, defaultValue);
		} else {

			BitmapDrawable normal = createDrawable(normalImgStr, defaultValue);
			String activeImgStr = mJsParamsUtil
					.faceBtnActiveImg(mModuleContext);

			BitmapDrawable active = createDrawable(activeImgStr, normal);
			// 设置状态选择器
			mFaceBtnDrawable = createStateDrawable(normal, active);
		}
		mFaceBtn.setBackgroundDrawable(mFaceBtnDrawable);
		// 开启键盘面板的view
		initKeyboardDrawable();
	}

	/***
	 * 键盘按钮样式的数据；
	 */
	private void initKeyboardDrawable() {
		// 封装模块中自带的键盘图标；
		int keyboardBtnImgId = UZResourcesIDFinder
				.getResDrawableID("mo_uichatbox_keyboard_btn");
		// 转换成bitmap图片；
		BitmapDrawable defaultValue = createDrawable(keyboardBtnImgId);
		// js传入的数据；
		String normalStr = mJsParamsUtil.keyboardBtnNormalImg(mModuleContext);
		/**
		 * ---------------------------------------------------------------------
		 ***/
		if (normalStr == null) {
			mSpeechKeyDrawable = createStateDrawable(defaultValue, defaultValue);
			// 状态选择器都添加默认图片；
			mKeyboardBtnDrawable = createStateDrawable(defaultValue,
					defaultValue);

		} else {
			BitmapDrawable normal = createDrawable(normalStr, defaultValue);
			// js传入按下的图标；
			String activeStr = mJsParamsUtil
					.keyboardBtnActiveImg(mModuleContext);
			BitmapDrawable active = createDrawable(activeStr, normal);
			mKeyboardBtnDrawable = createStateDrawable(normal, active);
			mSpeechKeyDrawable = createStateDrawable(normal, normal);
		}

	}

	private void initExtraBtn() {
		initExtraBtnLayout();
		initExtraBtnVisible();
		initExtraBtnBg();
		mExstraBtn.setOnClickListener(this);
	}

	private void initExtraBtnLayout() {
		LayoutParams params = new LayoutParams(
				UZUtility.dipToPix(Constans.BTN_SIZE),
				UZUtility.dipToPix(Constans.BTN_SIZE));
		params.gravity = Gravity.BOTTOM;
		mExstraBtn.setLayoutParams(params);
	}

	private void initExtraBtnVisible() {
		if (!isBtnShow("extrasBtn")) {
			mExstraBtn.setVisibility(View.GONE);
			mSendBtn.setVisibility(View.VISIBLE);
		}
	}

	@SuppressWarnings("deprecation")
	private void initExtraBtnBg() {
		// 获取到额外接口传进来的站位图;
		String normalStr = mJsParamsUtil.extrasBtnNormalImg(mModuleContext);
		// 非点击状态下;
		BitmapDrawable normal = createDrawable(normalStr, null);
		String activeStr = mJsParamsUtil.extrasBtnActiveImg(mModuleContext);
		// 点击状态下;
		BitmapDrawable active = createDrawable(activeStr, normal);
		if (normal != null) {
			bgDrawable = createStateDrawable(normal, active);
			mExstraBtn.setBackgroundDrawable(bgDrawable);
		}
	}

	private void initSendBtn() {
		initSendBtnBg();
		initSendBtnLayout();
		mSendBtn.setOnClickListener(this);
	}

	private void initSendBtnLayout() {
		LayoutParams params = new LayoutParams(
				UZUtility.dipToPix(Constans.SEND_BTN_SIZE),
				UZUtility.dipToPix(Constans.BTN_SIZE));
		params.gravity = Gravity.BOTTOM;
		mSendBtn.setLayoutParams(params);
		mSendBtn.setVisibility(View.GONE);
	}

	private void initSendBtnBg() {
		initSendBtnStyles(mModuleContext);
	}

	private void initSendLayout() {
		LayoutParams params = new LayoutParams(
				UZUtility.dipToPix(Constans.SEND_BTN_SIZE),
				UZUtility.dipToPix(Constans.BTN_SIZE));
		params.gravity = Gravity.BOTTOM;
		int margin = UZUtility.dipToPix(Constans.INPUT_BOX_MARGIN);
		params.setMargins(margin, margin, margin, margin);
		mSendLayout.setLayoutParams(params);
		mSendLayout.addView(mExstraBtn);
		mSendLayout.addView(mSendBtn);
	}

	public void showKeybord() {

		new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				mEditText.setFocusable(true);
				mEditText.setFocusableInTouchMode(true);
				mEditText.requestFocus();
				InputMethodManager inputManager = (InputMethodManager) mEditText
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(mEditText, 0);
			}
		}, 100);
	}

	private void hideInputMethod(View view) {
		isShowExtra = false;
		if (context() != null && view != null) {
			InputMethodManager mInputMethodManager = (InputMethodManager) context()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (mInputMethodManager != null)
				mInputMethodManager.hideSoftInputFromWindow(
						view.getWindowToken(), 0);
		}
	}

	// 判断图片有没有传进来
	private boolean isBtnShow(String btnName) {

		JSONObject btnJson = mJsParamsUtil.innerParamJSONObject(mModuleContext,
				"styles", btnName);
		// 表示左侧的图片没有传入；
		if (btnJson == null) {
			return false;
		}
		return true;
	}

	// 创建一个bitmap图；

	private BitmapDrawable createDrawable(String imgPath,
			BitmapDrawable defaultValue) {
		String realPath = makeRealPath(imgPath);
		Bitmap bitmap = mJsParamsUtil.getBitmap(realPath);
		if (bitmap != null) {
			return new BitmapDrawable(context().getResources(), bitmap);
		}
		return defaultValue;
	}

	private BitmapDrawable createDrawable(int resId) {
		Resources resources = context().getResources();
		Drawable drawable = resources.getDrawable(resId);
		return (BitmapDrawable) drawable;
	}

	private StateListDrawable createStateDrawable(BitmapDrawable nomalDrawable,
			BitmapDrawable pressDrawable) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_pressed }, pressDrawable);
		sd.addState(new int[] {}, nomalDrawable);
		return sd;
	}

	/***
	 * 点击事件的处理方法；
	 */

	@Override
	public void onClick(View v) {
		if (v == mSpeechBtn) {

			// 最左侧的按钮
			onSpeechBtnClick();
			clickCallBack("showRecord");
		} else if (v == mFaceBtn) {
			isShowExtra = false;
			// 右侧表情按钮；
			onFaceBtnClick();
			clickCallBack("showEmotion");
			
			v.postDelayed(new Runnable(){
				@Override
				public void run() {
					checkViewConflict();
				}
			}, 300);
			
		} else if (v == mExstraBtn) {

			// 额外按钮的点击事件;
			onExtraBtnClick();
			clickCallBack("showExtras");
			// 点击发送的按钮;.
			
			v.postDelayed(new Runnable(){
				@Override
				public void run() {
					checkViewConflict();
				}
			}, 300);

		} else if (v == mSendBtn && mSendBtn instanceof Button) {
			onSendBtnClick();
		}
	}
	
	
	public void checkViewConflict(){
		if(mChatBoxLayout.getVisibility() == View.VISIBLE && isSoftShowing()){
			hideInputMethod(((Activity) context()).getCurrentFocus());
		}
	}
	
	private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = ((Activity)context()).getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        ((Activity)context()).getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return screenHeight - rect.bottom != 0;
    }

	/***
	 * 发送按钮的点击事件;
	 */
	private void onSendBtnClick() {

		openCallBack("send", 0);
		// 这里面应该添加判读控制面板是否可见以前来控制change 监听的控制面板的问题;
		mEditText.setText("");

	}

	/****
	 * 额外按钮的点击事件;
	 */

	private boolean isShowExtra = false;

	@SuppressWarnings("deprecation")
	private void onExtraBtnClick() {
		if (isShowExtra) {
			isShowExtra = false;
			clickExtraBtnChangeHide();
			return;
		}

		mFaceBtn.setBackgroundDrawable(mFaceBtnDrawable);

		// 判断发送的按钮是否可见;
		if (isViewVisible(mRecordBtn)) {
			// 可见隐藏掉发送按钮;
			mRecordBtn.setVisibility(View.GONE);
			// 设置最左侧键盘和语音按钮切换的图标;
			mSpeechBtn.setBackgroundDrawable(mSpeechBtnDrawable);
			// 显示输入框;
			mEditText.setVisibility(View.VISIBLE);

			// ======= debug ======
			clickExtraBtnShowTable();
			setExtraPageNums();
			// ======= debug ======
			isShowExtra = true;

		} else {
			// ====== debug ======
			clickExtraBtnShowTable();
			setExtraPageNums();
			isShowExtra = true;
			// ====== debug ======
		}
	}

	/***
	 * 隐藏额外面板不显示;
	 */
	@SuppressWarnings("deprecation")
	private void clickExtraBtnChangeHide() {

		mFaceBtn.setBackgroundDrawable(mFaceBtnDrawable);
		isKeyBoardVisible = true;
		mEditText.requestFocus();
		isCallBack = false;
		mTableLayout.setVisibility(View.GONE);
		mDelayedHandler.postDelayed(mDelayedShowKeyBoardRunnable, 300);

	}

	// 判断view是否可见；
	private boolean isViewVisible(View view) {
		if (view.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	private void showExtraTable() {
		isKeyBoardVisible = false;
		// mExstraBtn.setBackgroundDrawable(mKeyboardBtnDrawable);
		mExstraBtn.setBackgroundDrawable(bgDrawable);
		// 额外数据显示轮播器
		mExtraViewPager.setVisibility(View.VISIBLE);
		// 表情显示轮播器;
		mFaceViewPager.setVisibility(View.GONE);
		mRecordPanel.setVisibility(View.GONE);

		resetBtn();

		// 显示额外的panel;
		chargeIndictorVisible("extrasPanel");
		int pageNums = mExtraViewPager.getOffscreenPageLimit();
		if (pageNums <= 1) {
			mIndictorView.setVisibility(View.GONE);
		}
		mIndictorView.setPointNums(pageNums);
		mIndictorView.setCurrentIndex(mExtraViewPager.getCurrentItem());
	}

	@SuppressWarnings("deprecation")
	public void resetBtn() {
		String recordType = JsParamsUtil.getInstance().getRecordType(
				mModuleContext);
		if ("pressRecord".equals(recordType)) {
			return;
		}
		String normalImgStr = JsParamsUtil.getInstance().speechBtnNormalImg(
				mModuleContext);
		Bitmap normalImgBmp = UZUtility.getLocalImage(mModuleContext
				.makeRealPath(normalImgStr));
		mSpeechBtn.setBackgroundDrawable(new BitmapDrawable(normalImgBmp));
	}

	private void clickExtraBtnShowTable() {
		showExtraTable();
		isCallBack = false;
		hideInputMethod(((Activity) context()).getCurrentFocus());
		mDelayedHandler.postDelayed(mDelayedRunnable, 300);
	}

	@SuppressWarnings("deprecation")
	private void onEditTextClick() {
		isKeyBoardVisible = true;
		mEditText.requestFocus();
		mFaceBtn.setBackgroundDrawable(mFaceBtnDrawable);
		
		// FIXME: 123
		mTableLayout.setVisibility(View.GONE);
		resetBtn();
	}

	/**
	 * 当左侧图标被点击之后处理的触发事件；
	 */

	// TODO
	private void onSpeechBtnClick() {

		String recordType = JsParamsUtil.getInstance().getRecordType(
				mModuleContext);

		if ("recordPanel".equals(recordType)) {

			if (mSpeechBtn.isSelected()) {
				mSpeechBtn.setSelected(false);
				hideRecordBtn();
			} else {
				mSpeechBtn.setSelected(true);
				doRecordPanel();
			}
			return;
		}

		// 表示录音按钮view不可见；
		if (!isViewVisible(mRecordBtn)) {
			// 让其进行可见；
			showRecordBtn();
		} else {
			// 进行隐藏；
			hideRecordBtn();
		}

	}

	@SuppressWarnings("deprecation")
	private void doRecordPanel() {
		mTableLayout.setVisibility(View.VISIBLE);
		mRecordPanel.setVisibility(View.VISIBLE);
		hideInputMethod(mEditText);
		mFaceViewPager.setVisibility(View.GONE);
		mExtraViewPager.setVisibility(View.GONE);
		mIndictorView.setVisibility(View.GONE);

		String activeImgStr = JsParamsUtil.getInstance().speechBtnActiveImg(
				mModuleContext);
		Bitmap activeImgBmp = UZUtility.getLocalImage(mModuleContext
				.makeRealPath(activeImgStr));
		mSpeechBtn.setBackgroundDrawable(new BitmapDrawable(activeImgBmp));
	}

	private boolean _moveOut = false;

	@SuppressWarnings("deprecation")
	private void addRecordBtnToPanel(RelativeLayout container) {

		container.setBackgroundColor(Color.TRANSPARENT);
		final ImageView recordBtn = new ImageView(context());
		int btnW = JsParamsUtil.getInstance().getRecordWidth(mModuleContext);
		int btnH = JsParamsUtil.getInstance().getRecordHeight(mModuleContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				btnW, btnH);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		recordBtn.setLayoutParams(params);

		String normalBg = JsParamsUtil.getInstance().getRecordNormalImage(
				mModuleContext);
		String activeBg = JsParamsUtil.getInstance().getRecordActiveImg(
				mModuleContext);

		final Bitmap normalImgBmp = UZUtility.getLocalImage(mModuleContext
				.makeRealPath(normalBg));
		final Bitmap activeImgBmp = UZUtility.getLocalImage(mModuleContext
				.makeRealPath(activeBg));

		container.addView(recordBtn);
		recordBtn.setBackgroundDrawable(new BitmapDrawable(normalImgBmp));
		recordBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					_moveOut = false;
					if (mPressCallBack != null) {
						mPressCallBack.success(new JSONObject(), false);
					}

					recordBtn.setBackgroundDrawable(new BitmapDrawable(
							activeImgBmp));
					break;

				case MotionEvent.ACTION_MOVE:
					if (isOutterUp(arg1, arg0)) {
						if (mMoveOutCallBack != null) {
							if (_moveOut) {
								return true;
							}
							mMoveOutCallBack.success(new JSONObject(), false);
							_moveOut = true;
						}
					}

					if (isInner(arg1, arg0)) {
						if (!_moveOut) {
							return true;
						}
						mMoveInCallBack.success(new JSONObject(), false);
						_moveOut = false;
					}
					break;
				case MotionEvent.ACTION_UP:
					if (isOutterUp(arg1, arg0)) {
						arg1.setAction(MotionEvent.ACTION_CANCEL);
						return onTouch(arg0, arg1);
					}
					if (mPressCancelCallBack != null) {
						mPressCancelCallBack.success(new JSONObject(), false);
					}
					recordBtn.setBackgroundDrawable(new BitmapDrawable(
							normalImgBmp));
					break;
				case MotionEvent.ACTION_CANCEL:
					if (mMoveOutCancelCallBack != null) {
						mMoveOutCancelCallBack.success(new JSONObject(), false);
					}
					break;
				}
				return true;
			}

			private boolean isOutterUp(MotionEvent event, View v) {
				float touchX = event.getX();
				float touchY = event.getY();
				float maxX = v.getWidth();
				float maxY = v.getHeight();
				return touchX < 0 || touchX > maxX || touchY < 0
						|| touchY > maxY;
			}

			private boolean isInner(MotionEvent event, View v) {
				float touchX = event.getX();
				float touchY = event.getY();
				float maxX = v.getWidth();
				float maxY = v.getHeight();

				if (touchX > 0 && touchX < maxX && touchY > 0 && touchY < maxY) {
					return true;
				}
				return false;
			}
		});
	}

	public void callback(UZModuleContext uzContext, String eventType) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("eventType", eventType);
			uzContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public static StateListDrawable addStateDrawable(Bitmap nomalBmp,
			Bitmap pressBmp) {
		StateListDrawable sd = new StateListDrawable();
		sd.addState(new int[] { android.R.attr.state_pressed },
				new BitmapDrawable(pressBmp));
		sd.addState(new int[] { android.R.attr.state_focused },
				new BitmapDrawable(nomalBmp));
		sd.addState(new int[] {}, new BitmapDrawable(nomalBmp));
		return sd;
	}

	/***
	 * 表情图标点击操作;
	 */
	@SuppressWarnings("deprecation")
	private void onFaceBtnClick() {
		// 判断发送按钮图标是否可见;
		if (isViewVisible(mRecordBtn)) {
			// 隐藏发送显示
			mRecordBtn.setVisibility(View.GONE);
			// 设置最左侧显示的图标;
			mSpeechBtn.setBackgroundDrawable(mSpeechBtnDrawable);
			// 显示编辑框;
			mEditText.setVisibility(View.VISIBLE);
		}
		// 控制面板是否可见的标识;

		if (isKeyBoardVisible) {
			// 当可见之后;显示表情面板;
			clickFaceBtnShowTable();
			// indicate;
			setEmotionPageNums();
		} else {
			// 面板不可见时;
			if (mTableLayout.getVisibility() == View.GONE) {
				// 如果tab栏是隐藏的;
				clickFaceBtnShowTable();
				setEmotionPageNums();
			} else {
				// 表情可见面板;
				if (isViewVisible(mExtraViewPager)) {
					// 显示镖旗;
					clickFaceBtnChangeToFace();
					setEmotionPageNums();
				} else {
					// 隐藏表情面板;
					clickFaceBtnHideTable();
				}
			}
		}
	}

	private boolean isCallBack = true;

	private void clickFaceBtnShowTable() {
		clickFaceBtnChangeToFace();
		hideInputMethod(((Activity) context()).getCurrentFocus());
		isCallBack = false;
		mDelayedHandler.postDelayed(mDelayedRunnable, 300);
	}

	@SuppressWarnings("deprecation")
	private void clickFaceBtnChangeToFace() {
		isKeyBoardVisible = false;
		mFaceBtn.setBackgroundDrawable(mKeyboardBtnDrawable);
		mExtraViewPager.setVisibility(View.GONE);
		mRecordPanel.setVisibility(View.GONE);
		resetBtn();
		mFaceViewPager.setVisibility(View.VISIBLE);
		chargeIndictorVisible("emotionPanel");
		int pageNums = mFaceViewPager.getOffscreenPageLimit();
		if (pageNums <= 1) {
			mIndictorView.setVisibility(View.GONE);
		}
		mIndictorView.setPointNums(pageNums);
		mIndictorView.setCurrentIndex(mFaceViewPager.getCurrentItem());
	}

	@SuppressWarnings("deprecation")
	private void clickFaceBtnHideTable() {
		mFaceBtn.setBackgroundDrawable(mFaceBtnDrawable);
		isKeyBoardVisible = true;
		mEditText.requestFocus();
		isCallBack = false;
		mTableLayout.setVisibility(View.GONE);
		mDelayedHandler.postDelayed(mDelayedShowKeyBoardRunnable, 300);
	}

	private Runnable mDelayedShowKeyBoardRunnable = new Runnable() {
		@Override
		public void run() {
			isCallBack = true;
			showKeybord();
		}
	};

	private StateListDrawable bgDrawable;

	/***
	 * 显示语音的同时表切换图标缓过来；
	 */

	@SuppressWarnings("deprecation")
	private void showRecordBtn() {

		// 左侧点击后的切换图标；
		mSpeechBtn.setBackgroundDrawable(mSpeechKeyDrawable);
		// 测试代码；
		// mSpeechBtn.setBackgroundDrawable();

		mRecordBtn.setVisibility(View.VISIBLE);
		mEditText.setVisibility(View.GONE);
		mTableLayout.setVisibility(View.GONE);
		if (isBtnShow("extrasBtn")) {
			mSendBtn.setVisibility(View.GONE);
		}
		mFaceBtn.setBackgroundDrawable(mFaceBtnDrawable);
		hideInputMethod(((Activity) context()).getCurrentFocus());
	}

	@SuppressWarnings("deprecation")
	private void hideRecordBtn() {
		mSpeechBtn.setBackgroundDrawable(mSpeechBtnDrawable);
		mRecordBtn.setVisibility(View.GONE);
		mEditText.setVisibility(View.VISIBLE);
		mTableLayout.setVisibility(View.GONE);
		if (mEditText.getText().length() > 0) {
			mSendBtn.setVisibility(View.VISIBLE);
		}
		mEditText.requestFocus();
		showKeybord();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		showOrHideSendBtn();
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mTempMsg = s;
		showOrHideSendBtn();
	}

	@Override
	public void afterTextChanged(Editable s) {
		// 展示隐藏的发送控件；
		showOrHideSendBtn();
		// （输入框内容改变事件）的回调；
		valueChangeCallBack();
	}

	private void valueChangeCallBack() {
		if (mValueChangeCallBack != null) {
			JSONObject ret = new JSONObject();
			try {
				ret.put("value", getEditTextStr());
				mValueChangeCallBack.success(ret, false);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void showOrHideSendBtn() {
		if (mTempMsg == null) {
			return;
		}
		int length = mTempMsg.length();
		if (!isOnlySendBtnExist) {
			if (length > 0 && mSendBtn.getVisibility() == View.GONE) {
				sendBtnShowWithAnimation();
			} else if (length == 0 && mSendBtn.getVisibility() == View.VISIBLE) {
				sendBtnHideWithAnimation();
			}
		}
	}

	private void sendBtnShowWithAnimation() {
		isShowAnimation = true;
		mSendBtn.setVisibility(View.VISIBLE);
		mSendBtn.startAnimation(mSendBtnShowAnimation);
	}

	private void sendBtnHideWithAnimation() {
		isShowAnimation = false;
		mSendBtn.startAnimation(mSendBtnHideAnimation);
	}

	@Override
	public void onAnimationStart(Animation animation) {

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		if (!isShowAnimation) {
			mSendBtn.setVisibility(View.GONE);
		}
		mSendBtn.clearAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	public static int mCurrentPageIndex = 0;

	@Override
	public void onPageSelected(int position) {
		mIndictorView.setCurrentIndex(position);

		if (mIsCenterDisplay) {
			mCurrentPageIndex = position;
		}
	}

	@Override
	public void keyClickedIndex(String index) {
		if ((mEmotionsPath + "/delete.png").equals(index)) {
			deleteEmotion();
		} else {
			insertEmotion(index);
		}
	}

	private void deleteEmotion() {
		KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0,
				0, KeyEvent.KEYCODE_ENDCALL);
		mEditText.dispatchKeyEvent(event);
	}

	/***
	 * 表情库的显示大小出现了问题;禅道有问题关于这里; 是因为他是直接用的px 应该根据对应的分辨率转换成px
	 * 
	 * @param index
	 */
	private void insertEmotion(String index) {
		Bitmap bitmap = mJsParamsUtil.getBitmap(makeRealPath(index));
		/** 以下是修复禅道bug添加 ***/
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		// 将bitmap转换成Drawable
		@SuppressWarnings("deprecation")
		Drawable drawable = new BitmapDrawable(bitmap);
		// 设置图片的大小尺寸,转换成相应的dp 转换成实际的px值;
		int dipToPixheight = UZUtility.dipToPix(height / 2);
		int dipToPixwidth = UZUtility.dipToPix(width / 2);
		drawable.setBounds(0, 0, dipToPixwidth, dipToPixheight);
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
		// ImageSpan imageSpan = new ImageSpan(bitmap, ImageSpan.ALIGN_BOTTOM);
		/** 以上是修复禅道bug添加 ***/
		String text = mEmotionMap.get(index);
		if (text != null) {
			SpannableString spannableString = new SpannableString(text);
			spannableString.setSpan(imageSpan, 0, text.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			int cursorPosition = mEditText.getSelectionStart();
			mEditText.getText().insert(cursorPosition, spannableString);
		}
	}

	// 这里面有显示图片的地方;
	private SpannableString parseMsg(String msg) {
		Pattern p = Pattern.compile(".*?\\[(.*?)\\].*?");
		Matcher m = p.matcher(msg);
		List<String> emotionList = new ArrayList<String>();
		while (m.find()) {
			emotionList.add("[" + m.group(1) + "]");
		}
		SpannableString spannableString = new SpannableString(msg);
		for (int i = 0; i < emotionList.size(); i++) {
			String emotion = emotionList.get(i);
			int index = msg.indexOf(emotion);
			// 图片文本的描述信息;
			String source = mInsertEmotionMap.get(emotionList.get(i));
			// 生成额表情图片;
			Bitmap bitmap = mJsParamsUtil.getBitmap(makeRealPath(source));
			if (bitmap != null) {
				// 生成imageSpan
				ImageSpan imageSpan = new ImageSpan(context(), bitmap);
				spannableString.setSpan(imageSpan, index,
						index + emotion.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}

	public String getEditTextStr() {
		return mEditText.getText().toString();
	}

	public void openCallBack(String eventType, int index) {
		JSONObject ret = new JSONObject();
		try {
			if ("show".equals(eventType)) {
				ret.put("inputBarHeight",
						UZCoreUtil.pixToDip(mEditLayout.getMeasuredHeight()));
			}
			ret.put("eventType", eventType);
			if (eventType.equals("clickExtras")) {
				ret.put("index", index);
				ret.put("click", true);
			}
			if (eventType.equals("send")) {
				ret.put("msg", getEditTextStr());
			}
			mModuleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void clickCallBack(String eventType) {
		if (eventType.equals("showRecord")) {
			if (mShowRecordCallBack != null) {
				mShowRecordCallBack.success(null, false);
			}
		} else if (eventType.equals("showEmotion")) {
			if (mShowEmotionCallBack != null) {
				mShowEmotionCallBack.success(null, false);
			}
		} else if (eventType.equals("showExtras")) {
			if (mShowExtrasCallBack != null) {
				mShowExtrasCallBack.success(null, false);
			}
		}
	}

	private void setLayoutListener(View mParentLayout) {
		Rect r = new Rect();
		mParentLayout.getWindowVisibleDisplayFrame(r);
		int height = r.bottom;
		if (mLayoutListener == null) {
			mLayoutListener = new LayoutListener(mParentLayout, height);
			mParentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
					mLayoutListener);
		}
	}

	/***
	 * 监听键盘的变化;
	 * 
	 * @author baoch
	 * 
	 */
	private class LayoutListener implements
			ViewTreeObserver.OnGlobalLayoutListener {
		private View view;
		private boolean isTableVisible;
		private int inputBarHeight;
		private int height;

		public LayoutListener(View mParentLayout, int height) {
			this.view = mParentLayout;
			this.height = height;
			inputBarHeight = mEditLayout.getMeasuredHeight();
		}

		@Override
		public void onGlobalLayout() {
			if (inputBarHeight != mEditLayout.getMeasuredHeight()) {
				inputBarHeightCallBack(view);
			} else {
				if (height != initChatViewH(view)) {
					if (isTableVisible) {
						tableCallBack(isTableVisible);
						isTableVisible = isViewVisible(mTableLayout);
					} else {
						keyBoardCallBack(view);
						height = initChatViewH(view);
					}
				} else {
					tableCallBack(isTableVisible);
					isTableVisible = isViewVisible(mTableLayout);
				}
			}
			inputBarHeight = mEditLayout.getMeasuredHeight();
		}
	}

	/***
	 * 控制面板的高度;
	 * 
	 * @param view
	 */
	private void keyBoardCallBack(View view) {
		int height = initChatViewH(view);

		int inputBarHeight = UZCoreUtil.pixToDip(mEditLayout
				.getMeasuredHeight());
		int pixPanelHeight = mJsParamsUtil
				.getScreenHeigth((Activity) context()) - height;
		int dipPanelHeight = UZCoreUtil.pixToDip(pixPanelHeight);

		inputFieldCallBack(mToggleKeyboardCallBack, inputBarHeight,
				dipPanelHeight);
	}

	private void tableCallBack(boolean isTableVisible) {
		boolean isVisible = isViewVisible(mTableLayout);
		int inputBarHeight = UZCoreUtil.pixToDip(mEditLayout
				.getMeasuredHeight());
		if (isVisible != isTableVisible) {
			if (isVisible) {
				inputFieldCallBack(mToggleKeyboardCallBack, inputBarHeight, 220);
			} else {
				inputFieldCallBack(mToggleKeyboardCallBack, inputBarHeight, 0);
			}
		}
	}

	private int initChatViewH(View view) {
		Rect r = new Rect();
		view.getWindowVisibleDisplayFrame(r);
		return r.bottom;
	}

	public void inputFieldCallBack(UZModuleContext moduleContext,
			int inputFieldH, int chatViewH) {
		if (isCallBack) {
			JSONObject result = new JSONObject();
			try {
				result.put("inputBarHeight", inputFieldH);
				// if(chatViewH >= 0){
				if (chatViewH <= 0) {
					chatViewH = 0;
				}
				result.put("panelHeight", chatViewH);
				if (moduleContext != null) {
					moduleContext.success(result, false);
				}
				// }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 输入框高度变化的监听;
	 */
	public void inputBarHeightCallBack(View view) {
		int height = initChatViewH(view);
		int inputBarHeight = UZCoreUtil.pixToDip(mEditLayout
				.getMeasuredHeight());
		int pixPanelHeight = mJsParamsUtil
				.getScreenHeigth((Activity) context()) - height;
		int dipPanelHeight = UZCoreUtil.pixToDip(pixPanelHeight);

		// 这里是false
		boolean isVisible = isViewVisible(mTableLayout);
		// 控制面板的高度;
		// mKeyboardHeight;
		if (isVisible) {
			inputFieldCallBack(mChangeCallBack, inputBarHeight, 220);
		} else {
			inputFieldCallBack(mChangeCallBack, inputBarHeight, dipPanelHeight);
		}
	}

	private void valueCallBack(UZModuleContext moduleContext, String msg) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("status", true);
			if (msg != null) {
				ret.put("msg", msg);
			}
			moduleContext.success(ret, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initSendBtnStyles(UZModuleContext moduleContext) {
		ViewBackground btnNormalBg = new ViewBackground();
		ViewBackground btnHighlightBg = new ViewBackground();
		String btnNormalBgStr = null;
		btnNormalBgStr = mJsParamsUtil.sendBtnBg(moduleContext);
		btnNormalBg = getViewBackground(btnNormalBgStr);
		String btnHighlightBgStr = null;
		btnHighlightBgStr = mJsParamsUtil.sendBtnHighlightBg(moduleContext);
		if (btnHighlightBgStr.equals("")) {
			btnHighlightBgStr = btnNormalBgStr;
		}
		btnHighlightBg = getViewBackground(btnHighlightBgStr);

		int btnNoramlTitleColor = mJsParamsUtil
				.sendBtnTitleColor(moduleContext);
		mSendBtn = initBtn(Constans.SEND_BTN_SIZE, Constans.BTN_SIZE,
				mJsParamsUtil.sendBtnTitle(moduleContext),
				mJsParamsUtil.sendBtnTitleSize(moduleContext),
				btnNoramlTitleColor, btnNormalBg, btnHighlightBg, 10);
		mSendBtn.setPadding(0, 0, 0, 0);
		addClickListener((CustomButton) mSendBtn,
				mJsParamsUtil.sendBtnTitle(moduleContext),
				mJsParamsUtil.sendBtnHighlightTitleColor(moduleContext),
				mJsParamsUtil.sendBtnTitle(moduleContext), btnNoramlTitleColor,
				Constans.SEND_BTN_SIZE, Constans.BTN_SIZE, moduleContext);
	}

	private ViewBackground getViewBackground(String bgStr) {
		ViewBackground viewBackground = new ViewBackground();
		Bitmap bgBitmap = null;
		bgBitmap = mJsParamsUtil.getBitmap(makeRealPath(bgStr));
		if (bgBitmap == null) {
			int color;
			color = UZUtility.parseCssColor(bgStr);
			viewBackground.setBgColor(color);
			viewBackground.setBgType(BackgroundType.COLOR);
		} else {
			viewBackground.setBgBitmap(bgBitmap);
			viewBackground.setBgType(BackgroundType.IMG);
		}
		return viewBackground;
	}

	private CustomButton initBtn(int w, int h, String btnNoramlTitle,
			int btnTitleSize, int btnNoramlTitleColor,
			ViewBackground btnNormalBg, ViewBackground btnHighlightBg,
			int corner) {
		CustomButton btn = new CustomButton(context());
		btn.setText(btnNoramlTitle);
		btn.setTextSize(btnTitleSize);
		btn.setTextColor(btnNoramlTitleColor);
		btn.setBackgroundColor(Color.TRANSPARENT);
		btn.init(btnNormalBg, btnHighlightBg, w, h, corner);
		return btn;
	}

	@SuppressLint("ClickableViewAccessibility")
	private void addClickListener(final CustomButton btn,
			final String btnHighLightTitle, final int btnHighLightTitleColor,
			final String btnNoramlTitle, final int btnNoramlTitleColor,
			final int w, final int h, final UZModuleContext moduleContext) {
		btn.setmNoramlTitle(btnNoramlTitle);
		btn.setmNoramlTitleColor(btnNoramlTitleColor);
		btn.setmHighLightTitle(btnHighLightTitle);
		btn.setmHighLightTitleColor(btnHighLightTitleColor);
		btn.setOnTouchListener(new OnTouchListener() {

			boolean isMoveOut = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isMoveOut = false;
					onBtnClick(btn, true, btnHighLightTitle,
							btnNoramlTitleColor);
					break;
				case MotionEvent.ACTION_MOVE:
					if (!isInRange(event, w, h)) {
						isMoveOut = true;
						onBtnClick(btn, false, btnNoramlTitle,
								btnNoramlTitleColor);
					}
					break;
				case MotionEvent.ACTION_UP:
					if (isCanceled) {
						isCanceled = false;
						return true;
					}
					onBtnClick(btn, false, btnNoramlTitle, btnNoramlTitleColor);
					if (!isMoveOut) {
						onSendBtnClick();
					}
					break;
				}
				return true;
			}
		});
	}

	private void onBtnClick(CustomButton btn, boolean isPressed, String text,
			int textColor) {
		btn.setPressed(isPressed);
		btn.setText(text);
		btn.setTextColor(textColor);
		btn.invalidate();
	}

	private boolean isInRange(MotionEvent event, int w, int h) {
		float x = event.getX();
		float y = event.getY();
		if (x < 0 || y < 0 || x > UZUtility.dipToPix(w)
				|| y > UZUtility.dipToPix(h)) {
			return false;
		}
		return true;
	}

	private boolean isCanceled = false;

	public void jsmethod_cancelRecord(UZModuleContext uzContext) {

		if (mSendBtn != null && mModuleContext != null) {

			String normalTitle = mJsParamsUtil.sendBtnTitle(mModuleContext);
			int btnNoramlTitleColor = mJsParamsUtil
					.sendBtnTitleColor(mModuleContext);
			mSendBtn.setPressed(false);
			mSendBtn.setText(normalTitle);
			mSendBtn.setTextColor(btnNoramlTitleColor);
			mSendBtn.invalidate();
			isCanceled = true;

			if (mRecordCanceledContext != null) {
				JSONObject ret = new JSONObject();
				mRecordCanceledContext.success(ret, false);
			}
		}
	}
	
	public void jsmethod_setInputBarBgColor(UZModuleContext uzModuleContext){
		String color = uzModuleContext.optString("color","#f2f2f2");
		if (mEditLayout != null) {
			mEditLayout.setBackgroundColor(UZUtility.parseCssColor(color));
		}
	}

}
