/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */

#import "UZUIChatBox.h"
#import "UZAppUtils.h"
#import "NSDictionaryUtils.h"
#import "JSON.h"
#import "UZUIChatBoxTextView.h"
#import "UZUIChatBoxBtnView.h"
#import "UZUIChatBoxAttachment.h"
#define TagBoardBG 99
#define TagCutLineDown 135
#define TagSpeechBtn 767
#define TagRecordBtn 1001
#define TagRecordTitle 1002
#define TagEmotionBtn 769
#define TagEmotionBoard 999
#define TagExtraBoard 1000
#define TagExtraBtn 998
#define TagRecordPanelBtn 1002
#define SCREEN_HEIGHTL [UIScreen mainScreen].bounds.size.height
#define SCREEN_WIDTHL [UIScreen mainScreen].bounds.size.width
#define KIsiPhoneX ((int)((SCREEN_HEIGHTL/SCREEN_WIDTHL)*100) == 216)?YES:NO
typedef enum {
    both = 0,
    emotionBoard,
    extrasBoard,
    bothNot
    
} PgControllShow;

typedef enum {
    touchIn = 0,
    touchMoveOut,
    touchMoveIn,
    touchCancel,
    touchMoveOutCancel,
} TouchType;

@interface UZUIChatBox ()
<UITextViewDelegate, ChatBtnViewDelegate, UIGestureRecognizerDelegate>
{
    NSInteger recordBtnId, openCbID, emotionBtnState,additionalBtnState ,recordBtnState;
    NSInteger recBtnPressIdcb, recBtnPressCancelIdcb, recBtnMoveoutIdcb, recBtnMoveinIdcb, recBtnMoveoutCancelIdcb,recordCanceledCbId;
    NSInteger inputBarMoveIdcb, inputBoxChangeIdcb, showRecordIdcb, showEmotionIdcb, showExtrasIdcb, valueChangedCbid;
    float _mainScreenWidth, _mainScreenHeight, _maxHeight,statusScreenHeight;
    
    UZUIChatBoxBtnView *_recordBtn;
    TouchType touchEvent;
    UZUIChatBoxTextView *_textView;
    UIView *_emotionView, *_extrasBoard, *_chatBgView, *_soundRecordingView;
    NSString *_pgColor, *_pgActiveColor, *_boardBgColor,*_boardColor, *_viewName, *_placeholderStr;
    NSTimer *_timer;
    PgControllShow showPgControll;
    NSString *normalTitle, *activeTitle;
    NSString *normalRecordImg, *activeRecordImg;
    UIView *btnSuperView;
    BOOL autoFocus, isKeyboardShow,isClose;
    NSString *sendBtnBgStr, *sendBtnAcStr, *sendBtnTitle, *sendBtnTitleColor;
    float sendBtnTilteSize;
    BOOL isShowSendBtn;
    //private
    float intervalePop;
    float topMarginH;

}

@property (nonatomic, strong) NSString *placeholderStr;
@property (nonatomic, strong) NSArray *sourceAry;
@property (nonatomic, strong) UZUIChatBoxTextView *textView;
@property (nonatomic, strong) UIView *chatBgView;
@property (nonatomic, strong) NSString *emotionNormalImg;
@property (nonatomic, strong) NSString *emotionHighImg;
@property (nonatomic, strong) NSString *keyNormalImg;
@property (nonatomic, strong) NSString *keyHighImg;
@property (nonatomic, strong) UIView *emotionView;
@property (nonatomic, strong) UIView *soundRecordingView;
@property (nonatomic, strong) UIView *extrasBoard;
@property (nonatomic, strong) UIView *bgDView;

@property (nonatomic, strong) UIPageControl *pageControl;
@property (nonatomic, strong) UIPageControl *pageControlExtra;
@property (nonatomic, assign) float currentInputfeildHeight;
@property (nonatomic, assign) float currentChatViewHeight;
@property (nonatomic, strong) NSDictionary *sendBtnInfo;
@property (nonatomic, strong) NSDictionary *recordBtnInfo;
@property (nonatomic, strong) UZUIChatBoxBtnView *recordBtn;
@property (nonatomic, strong) UZUIChatBoxBtnView *recordPanelBtn;

@property (nonatomic, assign) BOOL showFaceBtn;
@property (nonatomic, assign) CGFloat chatH;

@property( nonatomic,strong)NSString *emojPath;
@property (nonatomic,strong)NSString *realImgPath;
@property (nonatomic, strong)NSString *recordType;
@property (nonatomic, assign) BOOL isStatusBarNormal;

@end

@implementation UZUIChatBox
@synthesize textView = _textView;
@synthesize chatBgView = _chatBgView;
@synthesize sourceAry;
@synthesize keyHighImg, keyNormalImg, emotionHighImg, emotionNormalImg;
@synthesize emotionView = _emotionView, extrasBoard = _extrasBoard ,soundRecordingView = _soundRecordingView;
@synthesize pageControl, pageControlExtra;
@synthesize placeholderStr = _placeholderStr;
@synthesize currentChatViewHeight, currentInputfeildHeight, sendBtnInfo, recordBtnInfo;
@synthesize recordBtn = _recordBtn;

int getUIRowCountWith(float screenWidth ,float sideLength);

#pragma mark-
#pragma mark lifeCycle
#pragma mark-

- (void)dealloc {

    [self removeObserver:self forKeyPath:@"currentInputfeildHeight" context:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIKeyboardWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIApplicationWillChangeStatusBarFrameNotification object:nil];
    [[NSNotificationCenter defaultCenter]removeObserver:self name:UIApplicationDidChangeStatusBarFrameNotification object:nil];

    [self close:nil];
}

- (id)initWithUZWebView:(UZWebView *)webView_ {
    self = [super initWithUZWebView:webView_];
    if (self != nil) {
        [[ NSNotificationCenter defaultCenter ] addObserver : self selector : @selector (statusBarFrameWillChange:) name : UIApplicationWillChangeStatusBarFrameNotification object : nil ];
        [[ NSNotificationCenter defaultCenter ] addObserver : self selector : @selector (layoutControllerSubViews:) name : UIApplicationDidChangeStatusBarFrameNotification object : nil ];

        //增加监听，当键盘出现或改变时收出消息
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
        
        //增加监听，当键退出时收出消息
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];


        
        showPgControll = bothNot;
        _maxHeight = 90;
        recordBtnId = -1;
        touchEvent = -1;
        autoFocus = NO;
        recBtnPressIdcb =  recBtnPressCancelIdcb =  recBtnMoveoutIdcb =  recBtnMoveinIdcb =  recBtnMoveoutCancelIdcb = -1;
        inputBarMoveIdcb = inputBoxChangeIdcb = showRecordIdcb = showEmotionIdcb = showExtrasIdcb = valueChangedCbid = -1;
        self.currentInputfeildHeight = 0;
        isKeyboardShow = NO;
        [self addObserver:self forKeyPath:@"currentInputfeildHeight" options:NSKeyValueObservingOptionNew|NSKeyValueObservingOptionOld context:nil];
    }
    return self;
}

-(void)layoutControllerSubViews:(NSNotification *)notification{
  
    CGRect statusBarRect = [[UIApplication sharedApplication] statusBarFrame];
    if (statusBarRect.size.height == 40)
    {
        _mainScreenHeight = self.viewController.view.frame.size.height;
    }else{
        _mainScreenHeight = [UIScreen mainScreen].bounds.size.height;

    }

    
}

- (void)statusBarFrameWillChange:(NSNotification*)notification{
    CGRect statusBarRect = [[UIApplication sharedApplication] statusBarFrame];
    if (statusBarRect.size.height == 40)
    {
        _mainScreenHeight = self.viewController.view.frame.size.height;
    }else{
        _mainScreenHeight = [UIScreen mainScreen].bounds.size.height;

    }
}


- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context {
    float new = [[change valueForKey:@"new"] floatValue];
    float old = [[change valueForKey:@"old"] floatValue];
    if (new != old){
        NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithCapacity:2];
        if ([keyPath isEqualToString:@"currentInputfeildHeight"]) {
            if (inputBoxChangeIdcb >= 0) {
                [dict setObject:[NSNumber numberWithFloat:self.currentInputfeildHeight] forKey:@"inputBarHeight"];
                [dict setObject:[NSNumber numberWithFloat:self.currentChatViewHeight] forKey:@"panelHeight"];
                [self sendResultEventWithCallbackId:inputBoxChangeIdcb dataDict:dict errDict:nil doDelete:NO];
            }
            CGRect rect = btnSuperView.frame;
            rect.origin.y = _chatBgView.bounds.size.height - self.chatH;
            [UIView beginAnimations:nil context:nil];
            [UIView setAnimationDuration:0.3];
            btnSuperView.frame = rect;
            [UIView commitAnimations];
        }
        if ([keyPath isEqualToString:@"currentChatViewHeight"]) {
            if (inputBarMoveIdcb >= 0) {
                [dict setObject:[NSNumber numberWithFloat:self.currentInputfeildHeight] forKey:@"inputBarHeight"];
                [dict setObject:[NSNumber numberWithFloat:self.currentChatViewHeight] forKey:@"panelHeight"];
                [self sendResultEventWithCallbackId:inputBarMoveIdcb dataDict:dict errDict:nil doDelete:NO];
            }
        }
    }
}

#pragma mark-
#pragma mark interface
#pragma mark-

- (void)open:(NSDictionary *)paramDict_{
    intervalePop = [paramDict_ floatValueForKey:@"delay" defaultValue:intervalePop];
    if (_chatBgView) {
        [[_chatBgView superview] bringSubviewToFront:_chatBgView];
        _chatBgView.hidden = NO;
        [[_emotionView superview] bringSubviewToFront:_emotionView];
        _emotionView.hidden = NO;
        [[_extrasBoard superview] bringSubviewToFront:_extrasBoard];
        _extrasBoard.hidden = NO;
        [[_soundRecordingView superview] bringSubviewToFront:_soundRecordingView];
        _soundRecordingView.hidden = NO;
        return;
    }
    openCbID = [paramDict_ integerValueForKey:@"cbId" defaultValue:-1];
    isClose = [paramDict_ boolValueForKey:@"isClose" defaultValue:false];
    if ([paramDict_ objectForKey:@"maxRows"]) {
        NSInteger lineMaxNum = [paramDict_ integerValueForKey:@"maxRows" defaultValue:0];
        if (lineMaxNum > 0) {
            _maxHeight = lineMaxNum*20.0 + 12;
        }
    }
    isShowSendBtn = [paramDict_ boolValueForKey:@"isShowSendBtn" defaultValue:true];
    NSDictionary *texts = [paramDict_ dictValueForKey:@"texts" defaultValue:@{}];
    NSDictionary *sendBtnDict = [texts dictValueForKey:@"sendBtn" defaultValue:@{}];
    sendBtnTitle = [sendBtnDict stringValueForKey:@"title" defaultValue:@"发送"];
    NSDictionary *styles = [paramDict_ dictValueForKey:@"styles" defaultValue:@{}];
    NSDictionary *sendBtn  = [styles dictValueForKey:@"sendBtn" defaultValue:@{}];
    sendBtnBgStr = [sendBtn stringValueForKey:@"bg" defaultValue:@"#4cc518"];
    sendBtnAcStr = [sendBtn stringValueForKey:@"activeBg" defaultValue:@"#46a91e"];
    sendBtnTitleColor = [sendBtn stringValueForKey:@"titleColor" defaultValue:@"#ffffff"];
    sendBtnTilteSize = [sendBtn floatValueForKey:@"titleSize" defaultValue:13.0];
    _viewName = [paramDict_ stringValueForKey:@"fixedOn" defaultValue:nil];
    BOOL disableSendMessage = [paramDict_ boolValueForKey:@"disableSendMessage" defaultValue:false];
    
  
//    UIView *superView = [self getViewByName:_viewName];
//    superView.autoresizingMask = UIViewAutoresizingFlexibleTopMargin;
//
//    _mainScreenWidth = superView.frame.size.width;
//    _mainScreenHeight =superView.frame.size.height;
    
    CGSize windowSize = self.viewController.view.bounds.size;
//    _mainScreenHeight = windowSize.height;
//    _mainScreenWidth = windowSize.width;
 
    //输入框
    NSDictionary *inputBoxInfo = [styles dictValueForKey:@"inputBox" defaultValue:@{}];
    NSString *borderColors = [inputBoxInfo stringValueForKey:@"borderColor" defaultValue:@"#B3B3B3"];
    NSString *fileBgColors = [inputBoxInfo stringValueForKey:@"bgColor" defaultValue:@"#ffffff"];
    CGFloat borderCorner = [inputBoxInfo floatValueForKey:@"borderCorner" defaultValue:5];

    CGFloat topMargin = [inputBoxInfo floatValueForKey:@"topMargin" defaultValue:10];
    topMarginH = topMargin;
    //页面控制器配置
    NSDictionary *pageConInfo = [styles dictValueForKey:@"indicator" defaultValue:nil];
    if (pageConInfo) {
        NSString *targetPg = [pageConInfo stringValueForKey:@"target" defaultValue:@"both"];
        if ([targetPg isKindOfClass:[NSString class]] && targetPg.length>0) {
            if ([targetPg isEqualToString:@"emotionPanel"]) {
                showPgControll = emotionBoard;
            } else if ([targetPg isEqualToString:@"extrasPanel"]) {
                showPgControll = extrasBoard;
            } else {
                showPgControll = both;
            }
        } else {
            showPgControll = both;
        }
        _pgColor = [pageConInfo stringValueForKey:@"color" defaultValue:nil];
        if (![_pgColor isKindOfClass:[NSString class]] || _pgColor.length<=0) {
            _pgColor = @"#c4c4c4";
        }
        _pgActiveColor = [pageConInfo stringValueForKey:@"activeColor" defaultValue:nil];
        if (![_pgActiveColor isKindOfClass:[NSString class]] || _pgActiveColor.length<=0) {
            _pgActiveColor = @"#9e9e9e";
        }
    } else {
        showPgControll = bothNot;
        _pgColor = @"#c4c4c4";
        _pgActiveColor = @"#9e9e9e";
    }
    //遮罩层，捕获用户点击非模块视图区域的点击事件
    float orignaly;
    orignaly = 0;
  
    // 监听点击
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(handleSingleTap:)];
    singleTap.delegate = self;
    singleTap.delaysTouchesBegan = YES;
    singleTap.numberOfTapsRequired = 1;
    UIWebView *superWebView = (UIWebView *)self.uzWebView;
    [superWebView.scrollView addGestureRecognizer:singleTap];
    //输入框背景承载视图
    _chatBgView = [[UIView alloc]init];
    if (KIsiPhoneX) {
        self.chatH = 84+topMargin-10;
        if (self.chatH<84) {
            self.chatH = 84;
        }

    }else{
        self.chatH = 50+topMargin-10;
        if (self.chatH<50) {
            self.chatH = 50;
        }
    }
    CGRect tabBarFrame = CGRectMake(0, windowSize.height-self.chatH, windowSize.width, self.chatH);
    _chatBgView.frame = tabBarFrame;
  
    NSDictionary *inputBarStyle = [styles dictValueForKey:@"inputBar" defaultValue:@{}];
    NSDictionary *inputBoxStyle = [styles dictValueForKey:@"inputBox" defaultValue:@{}];
    NSString *tempBgColor = [inputBarStyle stringValueForKey:@"bgColor" defaultValue:@"#f2f2f2"];
    NSString *barBoardColor = [inputBarStyle stringValueForKey:@"borderColor" defaultValue:@"#d9d9d9"];
    NSString *textColor = [inputBarStyle stringValueForKey:@"textColor" defaultValue:@"#000"];
    CGFloat textSize = [inputBarStyle floatValueForKey:@"textSize" defaultValue:16];
    CGFloat textMarginLeft = [inputBarStyle floatValueForKey:@"textMarginLeft" defaultValue:1];

    _boardColor = [inputBoxStyle stringValueForKey:@"boardBgColor" defaultValue:@"#f2f2f2"];
    _boardBgColor = tempBgColor;
    _chatBgView.backgroundColor = [UZAppUtils colorFromNSString:_boardBgColor];
    _chatBgView.autoresizingMask = UIViewAutoresizingFlexibleTopMargin;
    [self addSubview:_chatBgView fixedOn:_viewName fixed:true];
    
    if (disableSendMessage) {
        _bgDView = [[UIView alloc]init];
        _bgDView.frame = _chatBgView.frame;
        _bgDView.backgroundColor = [UZAppUtils colorFromNSString:@"rgba(0,0,0,0.5)"];
//        [_chatBgView addSubview:bgDView];
        [self addSubview:_bgDView fixedOn:_viewName fixed:true];
        [_bgDView bringSubviewToFront:_chatBgView];
    }
    //
    windowSize = _chatBgView.superview.bounds.size;
    _mainScreenWidth = windowSize.width;
    _mainScreenHeight = windowSize.height;
    tabBarFrame = CGRectMake(0, windowSize.height-self.chatH, windowSize.width, self.chatH);
    _chatBgView.frame = tabBarFrame;
    self.currentInputfeildHeight = _chatBgView.frame.size.height;
    self.currentChatViewHeight = windowSize.height - self.currentInputfeildHeight - _chatBgView.frame.origin.y;
    [self view:_chatBgView preventSlidBackGesture:YES];
    _chatBgView.userInteractionEnabled = YES;
    

    //表情面板、附加功能面板底板
    UIView *bgTempView = [[UIView alloc]init];
    bgTempView.frame = CGRectMake(0, 0, windowSize.height, 456);
    bgTempView.backgroundColor = [UZAppUtils colorFromNSString:_boardBgColor];
    bgTempView.tag = TagBoardBG;
    [_chatBgView addSubview:bgTempView];
    //按钮的父view
    btnSuperView = [[UIView alloc]initWithFrame:_chatBgView.bounds];
    btnSuperView.backgroundColor = [UIColor clearColor];
    [_chatBgView addSubview:btnSuperView];
    //输入框上下分割线
//    UIView *cutLineUp = [[UIView alloc]initWithFrame:CGRectMake(0, 0, _mainScreenWidth, 1)];
//    cutLineUp.backgroundColor = [UZAppUtils colorFromNSString:barBoardColor];
//    [_chatBgView addSubview:cutLineUp];
    //下分割线
    UIView *cutLineDown = [[UIView alloc]initWithFrame:CGRectMake(0, 50-1, windowSize.width, 1)];
    cutLineDown.backgroundColor = [UZAppUtils colorFromNSString:barBoardColor];
    cutLineDown.tag = TagCutLineDown;
    [_chatBgView addSubview:cutLineDown];
    
    NSDictionary *topDividerDict = [styles dictValueForKey:@"topDivider" defaultValue:@{}];
    NSString *topDividerColor = [topDividerDict stringValueForKey:@"color" defaultValue:@"#000"];
    CGFloat topDividerWidth = [topDividerDict floatValueForKey:@"width" defaultValue:0];
    if ([UZAppUtils isValidColor:topDividerColor]) {
        UILabel *lineLabel = [[UILabel alloc]initWithFrame:CGRectMake(0, -topDividerWidth, windowSize.width, topDividerWidth)];
        lineLabel.backgroundColor = [UZAppUtils colorFromNSString:topDividerColor];
        [_chatBgView addSubview:lineLabel];
    } else {
        CGRect bgrect = CGRectMake(0, -topDividerWidth, windowSize.width, topDividerWidth);
        UIImageView *bgImgView = [[UIImageView alloc]initWithFrame:bgrect];
        bgImgView.backgroundColor = [UIColor clearColor];
        bgImgView.image = [UIImage imageWithContentsOfFile:[UZAppUtils getPathWithUZSchemeURL:topDividerColor]];
        bgImgView.contentMode = UIViewContentModeScaleToFill;
        [_chatBgView addSubview:bgImgView];
        
    }
    //附加功能
    NSDictionary *extrasBtnStyle = [styles dictValueForKey:@"extrasBtn" defaultValue:nil];
    NSDictionary *extrasInfo = [paramDict_ dictValueForKey:@"extras" defaultValue:@{}];
    if (extrasBtnStyle) {
        NSString *normal = [extrasBtnStyle stringValueForKey:@"normalImg" defaultValue:nil];
        NSString *highlight = [extrasBtnStyle stringValueForKey:@"activeImg" defaultValue:nil];
        //附加功能按钮
        UIButton *extrasBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        extrasBtn.frame = CGRectMake(windowSize.width-9-29, topMargin, 30, 30);
        NSString *realaddNormal = [self getPathWithUZSchemeURL:normal];
        [extrasBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realaddNormal] forState:UIControlStateNormal];
        NSString *realaddHigh = [self getPathWithUZSchemeURL:highlight];
        [extrasBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realaddHigh] forState:UIControlStateHighlighted];
        [extrasBtn addTarget:self action:@selector(extrasBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        extrasBtn.tag = TagExtraBtn;
        [btnSuperView addSubview:extrasBtn];
        //绘制附加功能面板
        [self drawExtraBoard:extrasInfo];
    }
    //表情按钮的图片读取
    NSDictionary *emotionBtnInfo = [styles dictValueForKey:@"emotionBtn" defaultValue:@{}];
    if (emotionBtnInfo && ![emotionBtnInfo isEqualToDictionary:@{}]) {
        self.showFaceBtn = YES;
    }else{
        self.showFaceBtn = NO;
    }
    NSString *emotionImgDefault = [[NSBundle mainBundle]pathForResource:@"res_UIChatBox/face" ofType:@"png"];
    NSString *emotionimg1 =  [emotionBtnInfo stringValueForKey:@"normalImg" defaultValue:@""];
    if ([emotionimg1 isEqualToString:@""]) {
      self.emotionNormalImg = emotionImgDefault;
    }else{
        self.emotionNormalImg = [self getPathWithUZSchemeURL:emotionimg1];

    }
    NSString *emotionimg2 =  [emotionBtnInfo stringValueForKey:@"activeImg" defaultValue:nil];
    if ([emotionimg2 isKindOfClass:[NSString class]] && emotionimg2.length>0) {
        self.emotionHighImg = [self getPathWithUZSchemeURL:emotionimg2];
    }
    //键盘按钮的图片读取
    NSDictionary *keyboardBtnInfo = [styles dictValueForKey:@"keyboardBtn" defaultValue:@{}];
    if (keyboardBtnInfo) {
        NSString *keyImg = [keyboardBtnInfo stringValueForKey:@"normalImg" defaultValue:nil];
        if ([keyImg isKindOfClass:[NSString class]] && keyImg.length>0) {
            self.keyNormalImg = [self getPathWithUZSchemeURL:keyImg];
        } else {
            self.keyNormalImg = [[NSBundle mainBundle]pathForResource:@"res_UIChatBox/key" ofType:@"png"];
        }
        NSString *keyImg1 = [keyboardBtnInfo stringValueForKey:@"activeImg" defaultValue:nil];
        if ([keyImg1 isKindOfClass:[NSString class]] && keyImg1.length>0) {
            self.keyHighImg = [self getPathWithUZSchemeURL:keyImg1];
        }
    }
    //左边按钮设置
    BOOL showSpeechBtn;
    NSDictionary *speechBtnInfo = [styles dictValueForKey:@"speechBtn" defaultValue:nil];
    NSDictionary *recordPanelBtnInfo = [styles dictValueForKey:@"recordPanelBtn" defaultValue:nil];
    NSString *recordType = [paramDict_ stringValueForKey:@"recordType" defaultValue:@"pressRecord"];
    self.recordType = recordType;
    if ([speechBtnInfo isKindOfClass:[NSDictionary class]] && speechBtnInfo.count>0) {
        self.sendBtnInfo = speechBtnInfo;
        showSpeechBtn = YES;
        UIButton *speechBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        speechBtn.frame = CGRectMake(8, topMargin, 30, 30);
        speechBtn.tag = TagSpeechBtn;
        NSString *normalIcon = [self getPathWithUZSchemeURL:[speechBtnInfo objectForKey:@"normalImg"]];
        NSString *normalIconAC = [self getPathWithUZSchemeURL:[speechBtnInfo objectForKey:@"activeImg"]];
        [speechBtn setImage:[UIImage imageWithContentsOfFile:normalIcon] forState:UIControlStateNormal];
        [speechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateSelected];
        if ([recordType isEqualToString:@"pressRecord"]) {
            [speechBtn addTarget:self action:@selector(speechBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        }else{
            [speechBtn addTarget:self action:@selector(speechRecordBtnClick:) forControlEvents:UIControlEventTouchUpInside];
              //绘制附加功能面板
            [self drawRecordBoard:recordPanelBtnInfo];
        }
        [btnSuperView addSubview:speechBtn];
      
       
    } else {
        showSpeechBtn = NO;
    }


    float textX = 8;
    float textXW = windowSize.width-94;
    if (showSpeechBtn) {
        textX = 16 + 30;
        textXW = textXW - 30 - 8;
    }
    if (extrasBtnStyle == nil) {
        textXW += 40;
    }
    if (!self.showFaceBtn) {
        textXW += 40;
    }
    autoFocus = [paramDict_ boolValueForKey:@"autoFocus" defaultValue:NO];
//    _textView = [[UZUIChatBoxTextView alloc] initWithFrame:CGRectMake(textX, topMargin, textXW, 32) ];
    _textView = [[UZUIChatBoxTextView alloc]initWithFrame:CGRectMake(textX, topMargin, textXW, 32)  textMarginLeft:textMarginLeft];
    _textView.delegate = self;
    _textView.layer.cornerRadius = borderCorner;
    _textView.layer.borderColor = [UZAppUtils colorFromNSString:borderColors].CGColor;
    _textView.returnKeyType = UIReturnKeySend;
    _textView.layer.borderWidth = 1;
    _textView.textColor = [UZAppUtils colorFromNSString:textColor];
    _textView.font = [UIFont systemFontOfSize:textSize];
    _textView.keyboardType = UIKeyboardTypeDefault;
    _textView.backgroundColor = [UZAppUtils colorFromNSString:fileBgColors];
    _textView.bounces = NO;

//    self.viewController.automaticallyAdjustsScrollViewInsets = NO;
    _textView.textContainerInset = UIEdgeInsetsMake((32-textSize)/2, textMarginLeft, 0, 16);

    if (autoFocus) {
        [_textView becomeFirstResponder];
    }
    [_chatBgView addSubview:_textView];
    //添加录音按钮
    if (showSpeechBtn) {
        NSDictionary *recordTexts = [texts dictValueForKey:@"recordBtn" defaultValue:@{}];
        normalTitle = [recordTexts stringValueForKey:@"normalTitle" defaultValue:@"按住 说话"];
        activeTitle = [recordTexts stringValueForKey:@"activeTitle" defaultValue:@"松开 结束"];
        NSDictionary *recordInfo = [styles dictValueForKey:@"recordBtn" defaultValue:@{}];
        self.recordBtnInfo = recordInfo;
        NSString *recordNormal = [recordInfo stringValueForKey:@"normalBg" defaultValue:@"#c4c4c4"];
        NSString *recordTColor = [recordInfo stringValueForKey:@"color" defaultValue:@"#000000"];
        float recordTitleSize = [recordInfo floatValueForKey:@"size" defaultValue:14];
        _recordBtn = [[UZUIChatBoxBtnView alloc]initWithFrame:_textView.frame];
        _recordBtn.backgroundColor = [UIColor clearColor];
        _recordBtn.hidden = YES;
        _recordBtn.delegate = self;
        [_chatBgView addSubview:_recordBtn];
        if ([UZAppUtils isValidColor:recordNormal]) {
            UIView *recordbg = [[UIView alloc]initWithFrame:_recordBtn.bounds];
            recordbg.backgroundColor = [UZAppUtils colorFromNSString:recordNormal];
            recordbg.tag = TagRecordBtn;
            [_recordBtn addSubview:recordbg];
            recordbg.userInteractionEnabled = NO;
        } else {
            UIImageView *recordbg = [[UIImageView alloc]initWithFrame:_recordBtn.bounds];
            NSString *realimg = [self getPathWithUZSchemeURL:recordNormal];
            recordbg.image = [UIImage imageWithContentsOfFile:realimg];
            recordbg.tag = TagRecordBtn;
            [_recordBtn addSubview:recordbg];
            recordbg.userInteractionEnabled = NO;
        }
        //录音按钮标题
        UILabel *recordTitle = [[UILabel alloc]init];
        recordTitle.frame = _recordBtn.bounds;
        recordTitle.backgroundColor = [UIColor clearColor];
        recordTitle.textColor = [UZAppUtils colorFromNSString:recordTColor];
        recordTitle.textAlignment = NSTextAlignmentCenter;
        recordTitle.font = [UIFont systemFontOfSize:recordTitleSize];
        recordTitle.text = normalTitle;
        recordTitle.tag = TagRecordTitle;
        [_recordBtn addSubview:recordTitle];
    }
    //占位提示符
    self.placeholderStr = [paramDict_ stringValueForKey:@"placeholder" defaultValue:@""];
//    if ([_placeholderStr isKindOfClass:[NSString class]] && _placeholderStr.length>0) {
        _textView.placeholder.text = _placeholderStr;
        _textView.placeholderSize = textSize;
        [_textView.placeholder setFont:[UIFont systemFontOfSize:textSize]];
//    }
    //表情按钮
    if (self.showFaceBtn) {
        UIButton *emotionKeyBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        if (extrasBtnStyle) {
            emotionKeyBtn.frame = CGRectMake(windowSize.width-18-60,topMargin, 30, 30);
        } else {
            emotionKeyBtn.frame = CGRectMake(windowSize.width-9-29, topMargin, 30, 30);
        }
        emotionKeyBtn.tag = TagEmotionBtn;
        UIImage *emotionImg = [UIImage imageWithContentsOfFile:emotionNormalImg];
        UIImage *emotionImgHigh = [UIImage imageWithContentsOfFile:emotionHighImg];
        [emotionKeyBtn setBackgroundImage:emotionImg forState:UIControlStateNormal];
        [emotionKeyBtn setBackgroundImage:emotionImgHigh forState:UIControlStateHighlighted];
        [emotionKeyBtn addTarget:self action:@selector(emotionBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        [btnSuperView addSubview:emotionKeyBtn];
    }
    //滚动缩回键盘
    [self setWebViewScrollDelegate:self];
    //读取表情文件
    if (self.showFaceBtn) {
        NSString *sourcePath = [paramDict_ stringValueForKey:@"emotionPath" defaultValue:nil];
        if (![sourcePath isKindOfClass:[NSString class]] || sourcePath.length<=0) {
            return;
        }
        emotionBtnState = 0;
        [NSThread detachNewThreadSelector:@selector(loadEmotionSource:) toTarget:self withObject:sourcePath];
    }
   
    [self sendResultEventWithCallbackId:openCbID dataDict:@{@"eventType":@"show",@"inputBarHeight":[NSString stringWithFormat:@"%0.f",self.chatH]} errDict:nil doDelete:NO];
    if (disableSendMessage) {
        _textView.userInteractionEnabled = NO;
        btnSuperView.userInteractionEnabled = NO;
    }

}

- (void)close:(NSDictionary *)paramsDict_ {
    if (showRecordIdcb >= 0) {
        [self deleteCallback:showRecordIdcb];
    }
    if (showEmotionIdcb >= 0) {
        [self deleteCallback:showEmotionIdcb];
    }
    if (showExtrasIdcb >= 0) {
        [self deleteCallback:showExtrasIdcb];
    }
    if (valueChangedCbid) {
        [self deleteCallback:valueChangedCbid];
    }
    if (inputBarMoveIdcb >= 0) {
        [self deleteCallback:inputBarMoveIdcb];
    }
    if (inputBoxChangeIdcb >= 0) {
        [self deleteCallback:inputBoxChangeIdcb];
    }
    if (recBtnPressIdcb >= 0) {
        [self deleteCallback:recBtnPressIdcb];
    }
    if (recBtnPressCancelIdcb >= 0) {
        [self deleteCallback:recBtnPressCancelIdcb];
    }
    if (recBtnMoveoutIdcb >= 0) {
        [self deleteCallback:recBtnMoveoutIdcb];
    }
    if (recBtnMoveoutCancelIdcb >= 0) {
        [self deleteCallback:recBtnMoveoutCancelIdcb];
    }
    if (recBtnMoveinIdcb >= 0) {
        [self deleteCallback:recBtnMoveinIdcb];
    }
    if (_chatBgView) {
        [_chatBgView removeFromSuperview];
        self.chatBgView = nil;
    }
    if (_bgDView) {
        [_bgDView removeFromSuperview];
        self.bgDView = nil;
    }
    _maxHeight = 90;
    if (recordBtnId!=-1) {
        [self deleteCallback:recordBtnId];
        recordBtnId = -1;
    }
    if (_recordBtn) {
        [_recordBtn removeFromSuperview];
        self.recordBtn = nil;
    }
    if (inputBoxChangeIdcb >= 0) {
        [self deleteCallback:inputBoxChangeIdcb];
        inputBoxChangeIdcb = -1;
    }
    if (inputBarMoveIdcb >= 0) {
        [self deleteCallback:inputBarMoveIdcb];
        [self removeObserver:self forKeyPath:@"currentChatViewHeight" context:nil];
        inputBarMoveIdcb = -1;
    }
    if (sendBtnInfo) {
        self.sendBtnInfo = nil;
    }
    if (recordBtnInfo) {
        self.recordBtnInfo = nil;
    }
    if (_textView) {
        self.textView.delegate = nil;
        self.textView = nil;
    }
    if (openCbID != -1){
        [self deleteCallback:openCbID];
        openCbID = -1;
    }
    if (keyNormalImg) {
        self.keyNormalImg = nil;
    }
    if (keyHighImg) {
        self.keyHighImg = nil;
    }
    if (emotionNormalImg) {
        self.emotionNormalImg = nil;
    }
    if (emotionHighImg) {
        self.emotionHighImg = nil;
    }
    if (_extrasBoard) {
        [_extrasBoard removeFromSuperview];
        self.extrasBoard = nil;
    }
    if (_soundRecordingView) {
        [_soundRecordingView removeFromSuperview];
        self.soundRecordingView = nil;
    }
    if (_emotionView) {
        [_emotionView removeFromSuperview];
        self.emotionView = nil;
    }
    if (pageControl) {
        self.pageControl = nil;
    }
    if (pageControlExtra) {
        self.pageControlExtra = nil;
    }
    if (_placeholderStr) {
        self.placeholderStr = nil;
    }
}

- (void)show:(NSDictionary *)paramDict_ {
    if (_chatBgView) {
        _chatBgView.hidden = NO;
    }
    if (_emotionView) {
        _emotionView.hidden = NO;
    }
    if (_extrasBoard) {
        _extrasBoard.hidden = NO;
    }
    if (_soundRecordingView) {
        _soundRecordingView.hidden = NO;
    }
    if (_bgDView) {
        _bgDView.hidden = NO;
    }
}

- (void)hide:(NSDictionary *)paramDict_ {
    if (_chatBgView) {
        _chatBgView.hidden = YES;
    }
    if (_emotionView) {
        _emotionView.hidden = YES;
    }
    if (_extrasBoard) {
        _extrasBoard.hidden = YES;
    }
    if (_soundRecordingView) {
        _soundRecordingView.hidden = YES;
    }
    if (_bgDView) {
        _bgDView.hidden = YES;
    }
}

- (void)popupKeyboard:(NSDictionary *)parmasDict_ {
    if (_recordBtn && !_recordBtn.hidden) {
        return;
    }
    NSInteger cbid = [parmasDict_ integerValueForKey:@"cbId" defaultValue:-1];
    _timer = [NSTimer scheduledTimerWithTimeInterval:intervalePop target:self selector:@selector(hideKeyborad) userInfo:nil repeats:NO];
    if (cbid!=-1) {
        [self sendResultEventWithCallbackId:cbid dataDict:[NSDictionary dictionaryWithObject:[NSNumber numberWithBool:YES] forKey:@"status"] errDict:nil doDelete:YES];
    }
}

- (void)closeKeyboard:(NSDictionary *)parmasDict_ {
    NSInteger cbid = [parmasDict_ integerValueForKey:@"cbId" defaultValue:-1];
    [_textView resignFirstResponder];
    if (cbid!=-1) {
        [self sendResultEventWithCallbackId:cbid dataDict:[NSDictionary dictionaryWithObject:[NSNumber numberWithBool:YES] forKey:@"status"] errDict:nil doDelete:YES];
    }
}

- (void)popupBoard:(NSDictionary *)parmasDict_ {
    NSString *target = [parmasDict_ stringValueForKey:@"target" defaultValue:@"emotion"];
    if ([target isEqualToString:@"emotion"]) {
        UIButton *emotionBtn = (UIButton *)[btnSuperView viewWithTag:TagEmotionBtn];
        [self emotionBtnClick:emotionBtn];
    } else {
        [self extrasBtnClick:nil];
    }
}

- (void)closeBoard:(NSDictionary *)parmasDict_ {
    [self shrinkKeyboard];
}

- (void)value:(NSDictionary *)paramsDict_ {
    NSInteger cbid = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
    if ([paramsDict_ objectForKey:@"msg"]) {
        NSString *msgStr = [paramsDict_ stringValueForKey:@"msg" defaultValue:@""];
        _textView.text = msgStr;
        [self textViewDidChange:_textView];
        NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithCapacity:2];
        [dict setObject:msgStr forKey:@"msg"];
        [dict setObject:[NSNumber numberWithBool:YES] forKey:@"status"];
        [self sendResultEventWithCallbackId:cbid dataDict:dict errDict:nil doDelete:YES];
    } else {
        //NSString *msgStr = _textView.text;
        //回调给前端
        NSMutableString *strM = [NSMutableString string];
        //__block NSString *string ;
        [_textView.attributedText enumerateAttributesInRange:NSMakeRange(0, _textView.attributedText.length) options:NSAttributedStringEnumerationLongestEffectiveRangeNotRequired usingBlock:^(NSDictionary<NSAttributedStringKey,id> * _Nonnull attrs, NSRange range, BOOL * _Nonnull stop) {
            NSString *str = nil;
            UZUIChatBoxAttachment *attachment = attrs[@"NSAttachment"];
            if (attachment) { // 表情
                //            str = [attachment.emotionString substringFromIndex:attachment.emotionString.length];
                str = attachment.emotionString ;
                [strM appendString:str];
            }
            else { // 文字
                str = [_textView.attributedText.string substringWithRange:range];
                [strM appendString:str];
            }
            
        }];
        NSString *willSendText = strM;
  
        if (!willSendText) {
            willSendText = @"";
        }
        NSMutableDictionary *dict = [NSMutableDictionary dictionaryWithCapacity:2];
        [dict setObject:willSendText forKey:@"msg"];
        [dict setObject:[NSNumber numberWithBool:YES] forKey:@"status"];
        [self sendResultEventWithCallbackId:cbid dataDict:dict errDict:nil doDelete:YES];
    }
    
    
    
}

- (void)insertValue:(NSDictionary *)paramsDict_ {
    NSString *tempStr = _textView.text;
    NSString *msgStr = [paramsDict_ stringValueForKey:@"msg" defaultValue:@""];
    if (tempStr.length==0) {
        _textView.text = msgStr;
    }
    NSInteger index = [paramsDict_ integerValueForKey:@"index" defaultValue:tempStr.length];
    if (index<0) {
        index = 0;
    }
    if (index>tempStr.length) {
        index = tempStr.length;
    }
    NSString *str1 = [tempStr substringToIndex:index];
    NSString *str2 = [tempStr substringFromIndex:index];
    NSString *strL = [NSString stringWithFormat:@"%@%@%@",str1,msgStr,str2];
    _textView.text = strL;
    [self textViewDidChange:_textView];
}

- (void)addEventListener:(NSDictionary *)paramsDict_ {
    NSInteger targetCbid = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
    if (targetCbid < 0) {
        return;
    }
    NSString *target = [paramsDict_ stringValueForKey:@"target" defaultValue:nil];
    if (![target isKindOfClass:[NSString class]] || target.length<=0) {
        return;
    }
    NSString *name = [paramsDict_ stringValueForKey:@"name" defaultValue:nil];
    if (![name isKindOfClass:[NSString class]] || name.length<=0) {
        return;
    }
    if ([target isEqualToString:@"inputBar"]) {
        if ([name isEqualToString:@"showRecord"]) {
            if (showRecordIdcb >= 0) {
                [self deleteCallback:showRecordIdcb];
            }
            showRecordIdcb = targetCbid;
        } else if ([name isEqualToString:@"showEmotion"]) {
            if (showEmotionIdcb >= 0) {
                [self deleteCallback:showEmotionIdcb];
            }
            showEmotionIdcb = targetCbid;
        } else if ([name isEqualToString:@"showExtras"]) {
            if (showExtrasIdcb >= 0) {
                [self deleteCallback:showExtrasIdcb];
            }
            showExtrasIdcb = targetCbid;
        } else if ([name isEqualToString:@"move"]) {
            if (inputBarMoveIdcb >=0 ) {
                [self deleteCallback:inputBarMoveIdcb];
                [self removeObserver:self forKeyPath:@"currentChatViewHeight" context:nil];
            }
            inputBarMoveIdcb = targetCbid;
            [self addObserver:self forKeyPath:@"currentChatViewHeight" options:NSKeyValueObservingOptionNew|NSKeyValueObservingOptionOld context:nil];
        } else if ([name isEqualToString:@"change"]) {
            if (inputBoxChangeIdcb >= 0) {
                [self deleteCallback:inputBoxChangeIdcb];
            }
            inputBoxChangeIdcb = targetCbid;
        } else if ([name isEqualToString:@"valueChanged"]) {
            if (valueChangedCbid >= 0) {
                [self deleteCallback:valueChangedCbid];
            }
            valueChangedCbid = targetCbid;
        }
    } else if ([target isEqualToString:@"recordBtn"]) {
        if ([name isEqualToString:@"press"]) {
            if (recBtnPressIdcb >= 0) {
                [self deleteCallback:recBtnPressIdcb];
            }
            recBtnPressIdcb = targetCbid;
        } else if ([name isEqualToString:@"press_cancel"]) {
            if (recBtnPressCancelIdcb >= 0) {
                [self deleteCallback:recBtnPressCancelIdcb];
            }
            recBtnPressCancelIdcb = targetCbid;
        } else if ([name isEqualToString:@"move_out"]) {
            if (recBtnMoveoutIdcb >= 0) {
                [self deleteCallback:recBtnMoveoutIdcb];
            }
            recBtnMoveoutIdcb = targetCbid;
        } else if ([name isEqualToString:@"move_out_cancel"]) {
            if (recBtnMoveoutCancelIdcb >= 0) {
                [self deleteCallback:recBtnMoveoutCancelIdcb];
            }
            recBtnMoveoutCancelIdcb = targetCbid;
        } else if ([name isEqualToString:@"move_in"]) {
            if (recBtnMoveinIdcb >= 0) {
                [self deleteCallback:recBtnMoveinIdcb];
            }
            recBtnMoveinIdcb = targetCbid;
        }else if ([name isEqualToString:@"recordCanceled"]) {
            if (recordCanceledCbId >= 0) {
                [self deleteCallback:recordCanceledCbId];
            }
            recordCanceledCbId = targetCbid;
        }
    }
}

- (void)setRecordButtonListener:(NSDictionary *)paramsDict_ {
    if (recordBtnId!=-1) {
        [self deleteCallback:recordBtnId];
    }
    recordBtnId = [paramsDict_ integerValueForKey:@"cbId" defaultValue:-1];
}

- (void)setPlaceholder:(NSDictionary *)paramsDict_ {
    self.placeholderStr = [paramsDict_ stringValueForKey:@"placeholder" defaultValue:nil];
    if (_textView.text.length == 0) {
        _textView.placeholder.text = self.placeholderStr;
    }
}

- (void)reloadExtraBoard:(NSDictionary *)paramsDict_ {
    NSDictionary *extrasInfo = [paramsDict_ dictValueForKey:@"extras" defaultValue:@{}];
    if (extrasInfo.count == 0) {
        return;
    }
    //绘制附加功能面板
    NSArray *btnsAry = [extrasInfo arrayValueForKey:@"btns" defaultValue:nil];
    if (![btnsAry isKindOfClass:[NSArray class]] || btnsAry.count==0) {
        return;
    }
    //计算每行按钮个数
    int btnNum = getUIRowCountWith(_mainScreenWidth, 60.0);
    //计算有几屏幕显示
    float pageNumtemp = btnsAry.count/(2.0*btnNum);
    NSInteger pageNumAdd = btnsAry.count/(2*btnNum);
    if ((pageNumtemp - pageNumAdd) > 0) {
        pageNumAdd ++;
    }
    //计算按钮间隙
    float verInterval = (_mainScreenWidth - 60*btnNum)/(btnNum + 1);
    //添加页码控制器
    pageControlExtra.numberOfPages = pageNumAdd;
    pageControlExtra.currentPage = 0;
    if (showPgControll==both || showPgControll==emotionBoard) {
        if (pageNumAdd > 1) {
            self.pageControlExtra.center = CGPointMake(_mainScreenWidth/2.0, 216-20);
            [_extrasBoard addSubview:pageControlExtra];
        }
    }
    //添加滚动视图
    UIScrollView *addSource = [_extrasBoard viewWithTag:TagExtraBoard];
    [addSource setContentSize:CGSizeMake(_mainScreenWidth*pageNumAdd, 216)];
    //移除所有的
    NSArray *allSubview = [addSource subviews];
    for (int i=0; i<allSubview.count; i++) {
        id targetView = [allSubview objectAtIndex:i];
        if ([targetView isKindOfClass:[UIButton class]] || [targetView isKindOfClass:[UILabel class]]) {
            [targetView removeFromSuperview];
        }
    }
    NSString *titleColor = [extrasInfo stringValueForKey:@"titleColor" defaultValue:nil];
    float titleSize = [extrasInfo floatValueForKey:@"titleSize" defaultValue:10];
    if (titleSize == 0) {
        titleSize = 10;
    }
    //往滚动视图添加按钮
    for (int i=0; i<pageNumAdd; i++) {//页循环
        for (int j=0; j<2; j++) {//行循环
            for (int g=0; g<btnNum; g++) {//列循环
                int the = 2*btnNum*i+j*btnNum+g;
                if (the >= btnsAry.count) {
                    return;
                }
                float origY;
                if (j==0) { origY =15; }else{ origY =15+60+20+11; }
                NSDictionary *btnInfo = [btnsAry objectAtIndex:the];
                NSString *normalImg = [btnInfo stringValueForKey:@"normalImg" defaultValue:nil];
                NSString *highlightImg = [btnInfo stringValueForKey:@"activeImg" defaultValue:nil];
                NSString *title = [btnInfo stringValueForKey:@"title" defaultValue:nil];
                UIButton *detailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
                detailBtn.frame = CGRectMake(_mainScreenWidth*i+verInterval+(60+verInterval)*g, origY, 60, 60);
                if (normalImg) {
                    NSString *realNormPath = [self getPathWithUZSchemeURL:normalImg];
                    [detailBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realNormPath] forState:UIControlStateNormal];
                    NSString *realhighPath = [self getPathWithUZSchemeURL:highlightImg];
                    [detailBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realhighPath] forState:UIControlStateHighlighted];
                }else{
                    [detailBtn setBackgroundColor:[UIColor greenColor]];
                }
                [detailBtn addTarget:self action:@selector(extrasBoardClick:) forControlEvents:UIControlEventTouchUpInside];
                detailBtn.tag = the;
                [addSource addSubview:detailBtn];
                UILabel *titleLabel = [[UILabel alloc]init];
                titleLabel.backgroundColor = [UIColor clearColor];
                titleLabel.frame = CGRectMake(detailBtn.frame.origin.x, detailBtn.frame.origin.y+detailBtn.frame.size.height+5.0, 60, 20);
                titleLabel.text = title;
                titleLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
                titleLabel.font = [UIFont systemFontOfSize:titleSize];
                titleLabel.textAlignment = NSTextAlignmentCenter;
                [addSource addSubview:titleLabel];
            }
        }
    }
}

#pragma mark - helper -

#pragma mark 弹出各种面板事件
- (void)speechBtnClick:(UIButton *)btn {
    if (btn.selected) {
        //将左边按钮置为当前按钮高亮图标
        //隐藏录音按钮
        _recordBtn.hidden = YES;
        _textView.hidden = NO;
        [_textView becomeFirstResponder];
        [self textViewDidChange:_textView];
        
        // 键盘弹出时, 下分割线位置要重新放到输入框底下.
        [self alignBottomLineToChatInputField];
    } else {
        //下移输入框
        //[self keyboardWillHide:nil];
        [_textView resignFirstResponder];
//        CGRect inputRect = _chatBgView.frame;
        CGSize windowSize = _chatBgView.superview.bounds.size;
        CGRect tabBarFrame = CGRectMake(0, windowSize.height-_chatBgView.frame.size.height, windowSize.width, _chatBgView.frame.size.height);
        
        CGFloat currentChatH = _chatBgView.frame.size.height;
//        inputRect.origin.y = _mainScreenHeight-currentChatH;
        //下移表情面板
        CGRect emotionRect = _emotionView.frame;
        emotionRect.origin.y = windowSize.height;
        //下移添加面板
        CGRect addRect = _extrasBoard.frame;
        addRect.origin.y = windowSize.height;
        //下移录音面板
        CGRect recordRect = _soundRecordingView.frame;
        recordRect.origin.y = windowSize.height;
        //动画
        [UIView beginAnimations:nil context:nil];
        [UIView setAnimationDuration:0.3];
        _chatBgView.frame = tabBarFrame;
        _extrasBoard.frame = addRect;
        _emotionView.frame = emotionRect;
        _soundRecordingView.frame = recordRect;
        [UIView commitAnimations];
        self.currentInputfeildHeight = currentChatH;
        self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
        //将左边按钮置为键盘图标
        [btn setImage:[UIImage imageWithContentsOfFile:keyNormalImg] forState:UIControlStateNormal];
        [btn setImage:[UIImage imageWithContentsOfFile:keyHighImg] forState:UIControlStateHighlighted];
        //显示录音按钮
        _recordBtn.hidden = NO;
        _textView.hidden = YES;
        //将输入框大小打回原形
        CGRect textTemp = _textView.frame;
        textTemp.size.height =32;
        _textView.frame = textTemp;
        CGRect textBoardTemp = _chatBgView.frame;
        if(textBoardTemp.size.height>self.chatH){
            float changeY = textBoardTemp.size.height-self.chatH;
            textBoardTemp.origin.y += changeY;
            textBoardTemp.size.height -= changeY;
            _chatBgView.frame = textBoardTemp;
            self.currentInputfeildHeight = _chatBgView.frame.size.height;
            self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
        }
        //下分割线
        [self alignBottomLineToChatBackgroundBottom];
    }
    //将表情按钮置为表情状态
    UIButton *tempFceBtn = (UIButton*)[btnSuperView viewWithTag:TagEmotionBtn];
    [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionNormalImg] forState:UIControlStateNormal];
    [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionHighImg] forState:UIControlStateHighlighted];
    emotionBtnState = 0;
    if (showRecordIdcb >= 0 && !btn.selected) {
        [self sendResultEventWithCallbackId:showRecordIdcb dataDict:nil errDict:nil doDelete:NO];
    }
    //重设按钮点击状态
    btn.selected = !btn.selected;
}

-(void)speechRecordBtnClick:(UIButton*)sender{
    //将左边按钮重置
       if (recordBtnState == 0) {
        UIButton *tempSpeechBtn = (UIButton *)[btnSuperView viewWithTag:TagSpeechBtn];
       // NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
        NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
        [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateNormal];
        //[tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateHighlighted];
        tempSpeechBtn.selected = YES;
        //隐藏录音按钮
        _recordBtn.hidden = YES;
        _textView.hidden = NO;
        //关闭键盘
        [_textView resignFirstResponder];
           CGSize windowSize = _chatBgView.superview.bounds.size;
//           CGRect tabBarFrame = CGRectMake(0, windowSize.height-_chatBgView.frame.size.height, windowSize.width, _chatBgView.frame.size.height);
        //关闭表情面板
        CGRect  emojiRect = _emotionView.frame;
        emojiRect.origin.y = windowSize.height;
        _emotionView.frame = emojiRect;
        //关闭附加面板
        CGRect  extrasRect = _extrasBoard.frame;
        extrasRect.origin.y = windowSize.height;
        _extrasBoard.frame = extrasRect;
        //弹出添加板
        CGRect motionRect = _soundRecordingView.frame;
        motionRect.origin.y = windowSize.height-216;
        [self.viewController.view bringSubviewToFront:_soundRecordingView];
        //输入框移动
        CGRect inputRect = _chatBgView.frame;
           if (topMarginH<10) {
               topMarginH= 10;
           }
        inputRect.origin.y = motionRect.origin.y-50-(topMarginH-10);
        //动画
        [UIView beginAnimations:nil context:NULL];
        [UIView setAnimationBeginsFromCurrentState:YES];
        [UIView setAnimationDuration:0.3];
        [_chatBgView setFrame:inputRect];
        [_soundRecordingView setFrame:motionRect];
        [UIView commitAnimations];
        self.currentInputfeildHeight = _chatBgView.frame.size.height;
        self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
        //将按钮置为表情状态
        UIButton *tempFceBtn = (UIButton*)[btnSuperView viewWithTag:TagEmotionBtn];
        [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionNormalImg] forState:UIControlStateNormal];
        [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionHighImg] forState:UIControlStateHighlighted];
        emotionBtnState = 0;
        additionalBtnState = 0;
        recordBtnState = 1;
           
           if (showRecordIdcb >= 0) {
               [self sendResultEventWithCallbackId:showRecordIdcb dataDict:nil errDict:nil doDelete:NO];
           }

    }else{
        UIButton *tempSpeechBtn = (UIButton *)[btnSuperView viewWithTag:TagSpeechBtn];
         NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
       // NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
        [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIcon] forState:UIControlStateNormal];
        //[tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateHighlighted];
        tempSpeechBtn.selected = NO;
        recordBtnState=0;
        [_textView becomeFirstResponder];
        //[self shrinkKeyboard];
    }

}

- (void)emotionBtnClick:(UIButton *)btn {
    //将左边按钮重置
    UIButton *tempSpeechBtn = (UIButton*)[btnSuperView viewWithTag:TagSpeechBtn];
    NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
    NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIcon] forState:UIControlStateNormal];
    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateSelected];
    tempSpeechBtn.selected = NO;
    CGSize windowSize = _chatBgView.superview.bounds.size;
    //           CGRect tabBarFrame = CGRectMake(0, windowSize.height-_chatBgView.frame.size.height, windowSize.width, _chatBgView.frame.size.height);    //隐藏录音按钮
    _recordBtn.hidden = YES;
    _textView.hidden = NO;
    if (emotionBtnState == 0) {//表情状态
        //关闭键盘
        //[self keyboardWillHide:nil];
        [_textView resignFirstResponder];
        //关闭添加面板
        CGRect  emojiRect = _extrasBoard.frame;
        emojiRect.origin.y = windowSize.height;
        _extrasBoard.frame = emojiRect;
        //关闭录音面板
        CGRect  recordRect = _soundRecordingView.frame;
        recordRect.origin.y = windowSize.height;
        _soundRecordingView.frame = recordRect;
        //弹出表情面板
        CGRect motionRect = _emotionView.frame;
        if (KIsiPhoneX) {
            motionRect.origin.y = windowSize.height-246;

        }else{
            motionRect.origin.y = windowSize.height-216;

        }
        [self.viewController.view bringSubviewToFront:_emotionView];
        //输入框移动
        CGRect inputRect = _chatBgView.frame;
        
        NSLog(@"--------%lf",inputRect.size.height);
        CGFloat currentChatH;
        if (KIsiPhoneX) {
            currentChatH =  _chatBgView.frame.size.height-34;
        }else{
            currentChatH = _chatBgView.frame.size.height;
        }
        //inputRect.origin.y = motionRect.origin.y-self.currentInputfeildHeight;
        inputRect.origin.y = motionRect.origin.y-currentChatH;
        //动画
        [UIView beginAnimations:nil context:NULL];
        [UIView setAnimationBeginsFromCurrentState:YES];
        [UIView setAnimationDuration:0.3];
        [_chatBgView setFrame:inputRect];
        [_emotionView setFrame:motionRect];
        [UIView commitAnimations];
        self.currentInputfeildHeight = _chatBgView.frame.size.height;
        self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
        //将按钮状态值置为1-----键盘状态
        emotionBtnState =1;
        additionalBtnState = 0;
        //表情按钮
        CGRect newBtnRect = btn.frame;
        [btn removeFromSuperview];
        UIButton *emotionKeyBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        emotionKeyBtn.frame = newBtnRect;
        emotionKeyBtn.tag = TagEmotionBtn;
        [emotionKeyBtn setImage:[UIImage imageWithContentsOfFile:keyNormalImg] forState:UIControlStateNormal];
        [emotionKeyBtn setImage:[UIImage imageWithContentsOfFile:keyHighImg] forState:UIControlStateHighlighted];
        [emotionKeyBtn addTarget:self action:@selector(emotionBtnClick:) forControlEvents:UIControlEventTouchUpInside];
        [btnSuperView addSubview:emotionKeyBtn];
    } else {
        emotionBtnState = 0;
        //[self shrinkKeyboard];
        //打开键盘
        [_textView becomeFirstResponder];
        //关闭表情面板
        CGRect  emojiRect = _emotionView.frame;
        emojiRect.origin.y = windowSize.height;
        _emotionView.frame = emojiRect;
        //关闭添加面板
        CGRect  addRect =_extrasBoard.frame;
        addRect.origin.y = windowSize.height;
        _extrasBoard.frame = addRect;
        //关闭录音面板
        CGRect  recordRect = _soundRecordingView.frame;
        recordRect.origin.y = windowSize.height;
        _soundRecordingView.frame = recordRect;
        //将按钮置为表情状态
        [btn setImage:[UIImage imageWithContentsOfFile:emotionNormalImg] forState:UIControlStateNormal];
        [btn setImage:[UIImage imageWithContentsOfFile:emotionHighImg] forState:UIControlStateHighlighted];
    }
    
    [self alignBottomLineToChatInputField];
    
    if (showEmotionIdcb >= 0 && emotionBtnState != 0) {
        [self sendResultEventWithCallbackId:showEmotionIdcb dataDict:nil errDict:nil doDelete:NO];
    }
}


/**
 把底部线,放到输入框底部.
 */
- (void)alignBottomLineToChatInputField {
    // 键盘弹出时, 下分割线位置要重新放到输入框底下.
    UIView *line = [_chatBgView viewWithTag:TagCutLineDown];
    CGRect lineRect = line.frame;
    lineRect.origin.y = _chatBgView.frame.size.height-1;
    line.frame = lineRect;
}

/**
 把底部线,放到靠近底层视图的最底部.常在键盘收起时使用.
 */
- (void)alignBottomLineToChatBackgroundBottom {
    UIView *line = [_chatBgView viewWithTag:TagCutLineDown];
    CGRect lineRect = line.frame;
    lineRect.origin.y = _chatBgView.frame.size.height-1;
    line.frame = lineRect;
}

//*附加按钮**/
- (void)extrasBtnClick:(UIButton *)btns {
    //将左边按钮重置
    CGSize windowSize = _chatBgView.superview.bounds.size;

    if (additionalBtnState == 0) {
        UIButton *tempSpeechBtn = (UIButton *)[btnSuperView viewWithTag:TagSpeechBtn];
        NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
        NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
        [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIcon] forState:UIControlStateNormal];
        [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateSelected];
        tempSpeechBtn.selected = NO;
        //隐藏录音按钮
        _recordBtn.hidden = YES;
        _textView.hidden = NO;
        //关闭键盘
        //[self keyboardWillHide:nil];
        [_textView resignFirstResponder];
        //关闭表情面板
        CGRect  emojiRect = _emotionView.frame;
        emojiRect.origin.y = windowSize.height;
        _emotionView.frame = emojiRect;
        //关闭录音面板
        CGRect  recordRect = _soundRecordingView.frame;
        recordRect.origin.y = windowSize.height;
        _soundRecordingView.frame = recordRect;
        //弹出添加板
        CGRect motionRect = _extrasBoard.frame;
        if (KIsiPhoneX) {
            motionRect.origin.y = windowSize.height-246;
            
        }else{
            motionRect.origin.y = windowSize.height-216;
            
        }
        [self.viewController.view bringSubviewToFront:_extrasBoard];
        //输入框移动
        CGRect inputRect = _chatBgView.frame;
        //    inputRect.origin.y = motionRect.origin.y-inputRect.size.height;
        if (topMarginH<10) {
            topMarginH= 10;
        }
        CGFloat currentChatH;
        if (KIsiPhoneX) {
            currentChatH =  _chatBgView.frame.size.height-34;
        }else{
            currentChatH = _chatBgView.frame.size.height;
        }
        //inputRect.origin.y = motionRect.origin.y-50-(topMarginH-10)-34;
        inputRect.origin.y = motionRect.origin.y-currentChatH;
        //动画
        [UIView beginAnimations:nil context:NULL];
        [UIView setAnimationBeginsFromCurrentState:YES];
        [UIView setAnimationDuration:0.3];
        [_chatBgView setFrame:inputRect];
        [_extrasBoard setFrame:motionRect];
        [UIView commitAnimations];
        self.currentInputfeildHeight = _chatBgView.frame.size.height;
        self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
        //将按钮置为表情状态
        UIButton *tempFceBtn = (UIButton*)[btnSuperView viewWithTag:TagEmotionBtn];
        [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionNormalImg] forState:UIControlStateNormal];
        [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionHighImg] forState:UIControlStateHighlighted];
        emotionBtnState = 0;
        additionalBtnState = 1;
        
        if (showExtrasIdcb >= 0) {
            [self sendResultEventWithCallbackId:showExtrasIdcb dataDict:nil errDict:nil doDelete:NO];
        }
    }else{
        additionalBtnState =0;
        //[self shrinkKeyboard];
        [_textView becomeFirstResponder];
        
    }
    
    [self alignBottomLineToChatInputField];
//    UIButton *tempSpeechBtn = (UIButton *)[btnSuperView viewWithTag:TagSpeechBtn];
//    NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
//    NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
//    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIcon] forState:UIControlStateNormal];
//    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateHighlighted];
//    tempSpeechBtn.selected = NO;
//    //隐藏录音按钮
//    _recordBtn.hidden = YES;
//    _textView.hidden = NO;
//    //关闭键盘
//    //[self keyboardWillHide:nil];
//    [_textView resignFirstResponder];
//    //关闭表情面板
//    CGRect  emojiRect = _emotionView.frame;
//    emojiRect.origin.y = _mainScreenHeight;
//    _emotionView.frame = emojiRect;
//    //弹出添加板
//    CGRect motionRect = _extrasBoard.frame;
//    motionRect.origin.y = _mainScreenHeight-216;
//    [self.viewController.view bringSubviewToFront:_extrasBoard];
//    //输入框移动
//    CGRect inputRect = _chatBgView.frame;
////    inputRect.origin.y = motionRect.origin.y-inputRect.size.height;
//    inputRect.origin.y = motionRect.origin.y-50;
//    //动画
//    [UIView beginAnimations:nil context:NULL];
//    [UIView setAnimationBeginsFromCurrentState:YES];
//    [UIView setAnimationDuration:0.3];
//    [_chatBgView setFrame:inputRect];
//    [_extrasBoard setFrame:motionRect];
//    [UIView commitAnimations];
//    self.currentInputfeildHeight = _chatBgView.frame.size.height;
//    self.currentChatViewHeight = _mainScreenHeight-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
//    //将按钮置为表情状态
//    UIButton *tempFceBtn = (UIButton*)[btnSuperView viewWithTag:TagEmotionBtn];
//    [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionNormalImg] forState:UIControlStateNormal];
//    [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionHighImg] forState:UIControlStateHighlighted];
//    emotionBtnState = 0;
//
//    if (showExtrasIdcb >= 0) {
//        [self sendResultEventWithCallbackId:showExtrasIdcb dataDict:nil errDict:nil doDelete:NO];
//    }
}

#pragma mark 缩回输入框事件
- (void)handleSingleTap:(UITapGestureRecognizer *)sender {
    
    if (sender.numberOfTapsRequired == 1) {//关闭输入框
        if (!isClose) {
            additionalBtnState = 0;
            CGSize windowSize = _chatBgView.superview.frame.size;
            CGRect tabBarFrame = CGRectMake(0, windowSize.height-_chatBgView.frame.size.height, windowSize.width, _chatBgView.frame.size.height);
            //下移输入框
            [_textView resignFirstResponder];
//            CGRect inputRect = _chatBgView.frame;
//            inputRect.origin.y = _mainScreenHeight-_chatBgView.frame.size.height;
            //下移表情面板
            CGRect emotionRect = _emotionView.frame;
            emotionRect.origin.y = windowSize.height;
            //下移添加面板
            CGRect addRect = _extrasBoard.frame;
            addRect.origin.y = windowSize.height;
            //下移录音面板
            CGRect recordRect = _soundRecordingView.frame;
            recordRect.origin.y = windowSize.height;
            //动画
            [UIView beginAnimations:nil context:nil];
            [UIView setAnimationDuration:0.3];
     

            _chatBgView.frame = tabBarFrame;
           //_chatBgView.frame = inputRect;
            _extrasBoard.frame = addRect;
            _emotionView.frame = emotionRect;
            _soundRecordingView.frame = recordRect;
            [UIView commitAnimations];
            self.currentInputfeildHeight = _chatBgView.frame.size.height;
            self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
            //将左边按钮重置
//            UIButton *tempSpeechBtn = (UIButton*)[btnSuperView viewWithTag:TagSpeechBtn];
//            NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
//            NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
            
            UIButton *tempFceBtn = (UIButton*)[btnSuperView viewWithTag:TagEmotionBtn];
            [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionNormalImg] forState:UIControlStateNormal];
            [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionHighImg] forState:UIControlStateHighlighted];

            [self alignBottomLineToChatBackgroundBottom];
        }else{
            [self close:nil];
            [self closeKeyboard:nil];
        }

    }
}

#pragma mark 加载表情数据
- (void)loadEmotionSource:(NSString *)path {
    NSArray *array = [path componentsSeparatedByString:@"/"];
    NSString *lastStr = [array lastObject];
    NSString *supStr = [self getPathWithUZSchemeURL:path];
    NSString *realPath = [NSString stringWithFormat:@"%@/%@.json",supStr,lastStr];
    @autoreleasepool{
        BOOL success = YES;
        NSError *err = nil;
        NSString *content =[NSString stringWithContentsOfFile:realPath encoding:NSUTF8StringEncoding error:&err];
        if (err){
            NSLog(@"Turbo_UIChatBox_loadData_err=%@",[err localizedDescription]);
        }
        if (content){
            NSArray *sourceDict = [content JSONValue];
            if (sourceDict){
                self.sourceAry = sourceDict;
            }else{
                success = NO;
            }
        }else{
            success = NO;
        }
        if (success) {
            [self performSelectorOnMainThread:@selector(drawEmotionBoard:) withObject:path waitUntilDone:NO];
        }
    }
}

#pragma mark 绘制各种面板
- (void)drawEmotionBoard:(NSString *)path {//绘制表情面板
    CGSize windowSize = _chatBgView.superview.bounds.size;

    _emotionView = [[UIView alloc]init];
    if (KIsiPhoneX) {
        _emotionView.frame = CGRectMake(0, windowSize.height,windowSize.width , 246);

    }else{
        _emotionView.frame = CGRectMake(0, windowSize.height,windowSize.width , 216);

    }
    _emotionView.backgroundColor = [UZAppUtils colorFromNSString:_boardColor];
    [self addSubview:_emotionView fixedOn:_viewName fixed:YES];
    //计算每行按钮个数
    int btnNum = getUIRowCountWith(windowSize.width, 30.0);
    //计算有几屏幕显示
    float pageNumtemp = self.sourceAry.count/(btnNum*4.0);
    NSInteger pageNumEmo = self.sourceAry.count/(btnNum*4);
    if ((pageNumtemp-pageNumEmo) > 0) {
        pageNumEmo++;
    }
    //计算按钮间隙
    float verInterval = (windowSize.width - 30*btnNum)/(btnNum + 1);
    UIScrollView *emotionSource = [[UIScrollView alloc]initWithFrame:_emotionView.bounds];
    emotionSource.backgroundColor = [UIColor clearColor];
    emotionSource.bounces = NO;
    emotionSource.scrollsToTop = NO;
    emotionSource.delegate = self;
    emotionSource.pagingEnabled = YES;
    emotionSource.showsVerticalScrollIndicator = NO;
    emotionSource.showsHorizontalScrollIndicator = NO;
    emotionSource.tag = TagEmotionBoard;
    [_emotionView addSubview:emotionSource];
    [emotionSource setContentSize:CGSizeMake(windowSize.width*pageNumEmo, 216)];
    //添加页面控制器
    
    self.pageControl = [[UIPageControl alloc]init];
    if (KIsiPhoneX) {
        self.pageControl.frame =CGRectMake((windowSize.width-126)/2,216-30,126,20);
    }else{
        if (isShowSendBtn) {
            self.pageControl.frame =CGRectMake((windowSize.width-60-verInterval)/2-63,216-30,126,20);

        }else{
            self.pageControl.frame =CGRectMake((windowSize.width-126)/2,216-30,126,20);
        }
    }
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 6.0) {
        [pageControl setCurrentPageIndicatorTintColor:[UZAppUtils colorFromNSString:_pgActiveColor]];
        [pageControl setPageIndicatorTintColor:[UZAppUtils colorFromNSString:_pgColor]];
    }
    pageControl.numberOfPages = pageNumEmo;
    pageControl.currentPage = 0;
    [pageControl addTarget:self action:@selector(turnPage) forControlEvents:UIControlEventValueChanged];
    if (showPgControll==both || showPgControll==emotionBoard) {
        if (pageNumEmo > 1) {
            [_emotionView addSubview:pageControl];
        }
    }
    //添加发送按钮
    UIButton *sendBtn = [UIButton buttonWithType:UIButtonTypeCustom];
    sendBtn.frame = CGRectMake(windowSize.width-60-verInterval, 216-40-5, 60, 40);
    UIImage *bgImage;
    if ([UZAppUtils isValidColor:sendBtnBgStr]) {
        bgImage = [self getImageFromColor:[UZAppUtils colorFromNSString:sendBtnBgStr] withSize:sendBtn.bounds.size];
    } else {
        NSString *realPath = [self getPathWithUZSchemeURL:sendBtnBgStr];
        bgImage = [UIImage imageWithContentsOfFile:realPath];
    }
    [sendBtn setBackgroundImage:bgImage forState:UIControlStateNormal];
    UIImage *bgACImgage;
    if ([UZAppUtils isValidColor:sendBtnAcStr]) {
        bgACImgage = [self getImageFromColor:[UZAppUtils colorFromNSString:sendBtnAcStr] withSize:sendBtn.bounds.size];
    } else {
        NSString *realPath = [self getPathWithUZSchemeURL:sendBtnAcStr];
        bgACImgage = [UIImage imageWithContentsOfFile:realPath];
    }
    [sendBtn setBackgroundImage:bgACImgage forState:UIControlStateHighlighted];
    //title
    UILabel *sendbtnLabel = [[UILabel alloc]init];
    sendbtnLabel.backgroundColor = [UIColor clearColor];
    float y = (sendBtn.bounds.size.height - sendBtnTilteSize - 2)/2.0;
    if (y < 0) {
        y = 0;
    }
    sendbtnLabel.frame = CGRectMake(0, y, sendBtn.bounds.size.width, sendBtnTilteSize+2);

    sendbtnLabel.text = sendBtnTitle;
    sendbtnLabel.textColor = [UZAppUtils colorFromNSString:sendBtnTitleColor];
    sendbtnLabel.font = [UIFont systemFontOfSize:sendBtnTilteSize];
    sendbtnLabel.textAlignment = NSTextAlignmentCenter;
    [sendBtn addSubview:sendbtnLabel];
    [sendBtn addTarget:self action:@selector(send:) forControlEvents:UIControlEventTouchUpInside];
    [_emotionView addSubview:sendBtn];
    
    if (isShowSendBtn) {
        sendBtn.hidden = NO;
    }else{
        sendBtn.hidden = YES;
    }


    for (int i=0; i<pageNumEmo; i++) {//页数循环
        for (int j=0; j<4; j++) {//行循环
            for (int g=0; g<btnNum; g++) {//列循环
                int the = (btnNum*4)*i + btnNum*j + g - i;
                if (the>=self.sourceAry.count) {
                    //添加消除按钮
                    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
                    cancelBtn.frame = CGRectMake(windowSize.width-(30+verInterval)+(windowSize.width*i), 139, 30, 30);
                    NSString *img = [NSString stringWithFormat:@"%@/delete.png",path];
                    NSString *realImg = [self getPathWithUZSchemeURL:img];
                    [cancelBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realImg] forState:UIControlStateNormal];
                    [cancelBtn addTarget:self action:@selector(cancel:) forControlEvents:UIControlEventTouchUpInside];
                    [emotionSource addSubview:cancelBtn];
                   // [self sendResultEventWithCallbackId:openCbID dataDict:[NSDictionary dictionaryWithObject:@"show" forKey:@"eventType"] errDict:nil doDelete:NO];
                    return;
                }
                if (j==3 && g==btnNum-1) {
                    //添加消除按钮
                    UIButton *cancelBtn = [UIButton buttonWithType:UIButtonTypeCustom];
                    cancelBtn.frame = CGRectMake(windowSize.width-(30+verInterval)+(windowSize.width*i), 139, 30, 30);
                    NSString *img = [NSString stringWithFormat:@"%@/delete.png",path];
                    NSString *realImg = [self getPathWithUZSchemeURL:img];
                    [cancelBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realImg] forState:UIControlStateNormal];
                    [cancelBtn addTarget:self action:@selector(cancel:) forControlEvents:UIControlEventTouchUpInside];
                    [emotionSource addSubview:cancelBtn];
                } else {
                    NSDictionary *emojiInfo = [self.sourceAry objectAtIndex:the];
                    NSString *emojiPath = [emojiInfo objectForKey:@"name"];
                    self.realImgPath = path;
                    NSString *widgetPath = [NSString stringWithFormat:@"%@/%@.png",path,emojiPath];
                    NSString *emojiRealPath = [NSString stringWithFormat:@"%@",[self getPathWithUZSchemeURL:widgetPath]];
                    UIButton *emoji = [UIButton buttonWithType:UIButtonTypeCustom];
                    emoji.frame = CGRectMake((windowSize.width*i)+verInterval+(30+verInterval)*g, 16+(30+11)*j, 32, 32);
                    self.emojPath = emojiRealPath;
                    [emoji setBackgroundImage:[UIImage imageWithContentsOfFile:emojiRealPath] forState:UIControlStateNormal];
                    emoji.tag = the+1;
                    [emoji addTarget:self action:@selector(emotionBoardClick:) forControlEvents:UIControlEventTouchUpInside];
                    [emotionSource addSubview:emoji];
                }
            }
        }
    }
}

- (void)drawRecordBoard:(NSDictionary *)soundRecordInfo {
    CGSize windowSize = _chatBgView.superview.bounds.size;

    _soundRecordingView = [[UIView alloc]init];
    _soundRecordingView.frame = CGRectMake(0, windowSize.height,windowSize.width , 216);
    _soundRecordingView.backgroundColor = [UZAppUtils colorFromNSString:_boardColor];
    [self addSubview:_soundRecordingView fixedOn:_viewName fixed:YES];
    //往滚动视图添加按钮
    NSString *normalImg = [soundRecordInfo stringValueForKey:@"normalImg" defaultValue:nil];
    NSString *highlightImg = [soundRecordInfo stringValueForKey:@"activeImg" defaultValue:nil];
    CGFloat w = [soundRecordInfo floatValueForKey:@"width" defaultValue:100];
    CGFloat h = [soundRecordInfo floatValueForKey:@"height" defaultValue:100];
    
    self.recordPanelBtn = [[UZUIChatBoxBtnView alloc]initWithFrame:CGRectMake(0, 0, w, h)];
    self.recordPanelBtn.center = CGPointMake(_soundRecordingView.frame.size.width/2, _soundRecordingView.frame.size.height/2);
    self.recordPanelBtn.backgroundColor = [UIColor clearColor];
    self.recordPanelBtn.delegate = self;
    [_soundRecordingView addSubview:self.recordPanelBtn];

//    UIImageView *recordbg = [[UIImageView alloc]initWithFrame:recordPanel.bounds];
//    //recordbg.center = CGPointMake(_soundRecordingView.frame.size.width/2, _soundRecordingView.frame.size.height/2);
//    NSString *realimg = [self getPathWithUZSchemeURL:normalImg];
//    recordbg.image = [UIImage imageWithContentsOfFile:realimg];
//    recordbg.tag = TagRecordBtn;
//    [recordPanel addSubview:recordbg];
//    recordbg.userInteractionEnabled = NO;
    //录音按钮标题
    
    
    UIImageView *recordPanelbg = [[UIImageView alloc]initWithFrame: self.recordPanelBtn.bounds ];
    recordPanelbg.tag = TagRecordPanelBtn;
    if (normalImg) {
        normalRecordImg = [self getPathWithUZSchemeURL:normalImg];
        activeRecordImg = [self getPathWithUZSchemeURL:highlightImg];
        recordPanelbg.image = [UIImage imageWithContentsOfFile:normalRecordImg];
    }
    [self.recordPanelBtn addSubview:recordPanelbg];
    recordPanelbg.userInteractionEnabled = NO;

}

- (void)drawExtraBoard:(NSDictionary *)extrasInfo {
    CGSize windowSize = _chatBgView.superview.bounds.size;
    NSArray *btnsAry = [extrasInfo arrayValueForKey:@"btns" defaultValue:nil];
    BOOL isAdaptScreenSize = [extrasInfo boolValueForKey:@"isAdaptScreenSize" defaultValue:true];
    BOOL isCenterDisplay = [extrasInfo boolValueForKey:@"isCenterDisplay" defaultValue:false];
    if (![btnsAry isKindOfClass:[NSArray class]] || btnsAry.count==0) {
        return;
    }
    _extrasBoard = [[UIView alloc]init];
    _extrasBoard.frame = CGRectMake(0, windowSize.height,windowSize.width , 216);
    _extrasBoard.backgroundColor = [UZAppUtils colorFromNSString:_boardColor];
    [self addSubview:_extrasBoard fixedOn:_viewName fixed:YES];
    //计算每行按钮个数
    int btnNum = 0;  float pageNumtemp; NSInteger pageNumAdd;  float verInterval;
    if (!isCenterDisplay) {
        if (isAdaptScreenSize) {
            btnNum = getUIRowCountWith(windowSize.width, 60);
        }else{
            btnNum = 4;
        }
        //计算有几屏幕显示
        pageNumtemp = btnsAry.count/(2.0*btnNum);
        pageNumAdd = btnsAry.count/(2*btnNum);
        if ((pageNumtemp - pageNumAdd) > 0) {
            pageNumAdd ++;
        }
        //计算按钮间隙
       verInterval = (windowSize.width - 60*btnNum)/(btnNum + 1);
    }else{
        btnNum = 2;
        //计算有几屏幕显示
        pageNumtemp = btnsAry.count/(btnNum);
        pageNumAdd = btnsAry.count/(btnNum);
        if ((pageNumtemp - pageNumAdd) > 0) {
            pageNumAdd ++;
        }
        //计算按钮间隙
        verInterval = (windowSize.width - 80*btnNum)/(btnNum + 1);
    }
   
    

    //添加页码控制器
    self.pageControlExtra = [[UIPageControl alloc]initWithFrame:CGRectMake(0,216-20,126,20)];
    if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 6.0) {
        [pageControlExtra setCurrentPageIndicatorTintColor:[UZAppUtils colorFromNSString:_pgActiveColor]];
        [pageControlExtra setPageIndicatorTintColor:[UZAppUtils colorFromNSString:_pgColor]];
    }
    pageControlExtra.numberOfPages = pageNumAdd;
    pageControlExtra.currentPage = 0;
    [pageControlExtra addTarget:self action:@selector(turnPageAdd) forControlEvents:UIControlEventValueChanged];
    if (showPgControll==both || showPgControll==emotionBoard) {
        if (pageNumAdd > 1) {
            self.pageControlExtra.center = CGPointMake(windowSize.width/2.0, 216-20);
            [_extrasBoard addSubview:pageControlExtra];
        }
    }
    //添加滚动视图
    UIScrollView *addSource = [[UIScrollView alloc]initWithFrame:_extrasBoard.bounds];
    addSource.backgroundColor = [UIColor clearColor];
    addSource.bounces = NO;
    addSource.scrollsToTop = NO;
    addSource.delegate = self;
    addSource.pagingEnabled = YES;
    addSource.showsVerticalScrollIndicator = NO;
    addSource.showsHorizontalScrollIndicator = NO;
    addSource.tag = TagExtraBoard;
    [_extrasBoard addSubview:addSource];
    [addSource setContentSize:CGSizeMake(_mainScreenWidth*pageNumAdd, 216)];
    
    NSString *titleColor = [extrasInfo stringValueForKey:@"titleColor" defaultValue:nil];
    if (![titleColor isKindOfClass:[NSString class]] || titleColor.length<=0) {
        titleColor = @"#A3A3A3";
    }
    float titleSize = [extrasInfo floatValueForKey:@"titleSize" defaultValue:10];
    if (titleSize==0) {
        titleSize =10;
    }
    //往滚动视图添加按钮
    if (isCenterDisplay) {
        
        for (int i=0; i<pageNumAdd; i++) {//页循环
            for (int j=0; j<1; j++) {//行循环
                for (int g=0; g<2; g++) {//列循环
                    int the = btnNum*i+j*btnNum+g;
                    if (the >= btnsAry.count) {
                        return;
                    }
                    float origY =(216-85)/2 ;
                    NSDictionary *btnInfo = [btnsAry objectAtIndex:the];
                    NSString *normalImg = [btnInfo stringValueForKey:@"normalImg" defaultValue:nil];
                    NSString *highlightImg = [btnInfo stringValueForKey:@"activeImg" defaultValue:nil];
                    NSString *title = [btnInfo stringValueForKey:@"title" defaultValue:nil];
                    UIView *backView = [[UIView alloc]initWithFrame:CGRectMake(_mainScreenWidth*i+verInterval+(80+verInterval)*g, origY, 85, 85)];
                    backView.backgroundColor = [UIColor clearColor];
                    [addSource addSubview:backView];
                    
                    UIButton *detailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
                    detailBtn.frame = CGRectMake(12.5, 0, 60, 60);
                    if (normalImg) {
                        NSString *realNormPath = [self getPathWithUZSchemeURL:normalImg];
                        [detailBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realNormPath] forState:UIControlStateNormal];
                        NSString *realhighPath = [self getPathWithUZSchemeURL:highlightImg];
                        [detailBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realhighPath] forState:UIControlStateHighlighted];
                    }else{
                        [detailBtn setBackgroundColor:[UIColor greenColor]];
                    }
                    [detailBtn addTarget:self action:@selector(extrasBoardClick:) forControlEvents:UIControlEventTouchUpInside];
                    detailBtn.tag = the;
                    [backView addSubview:detailBtn];
                    UILabel *titleLabel = [[UILabel alloc]init];
                    titleLabel.backgroundColor = [UIColor clearColor];
                    titleLabel.frame = CGRectMake(12.5, 65, 60, 20);
                    titleLabel.text = title;
                    titleLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
                    titleLabel.font = [UIFont systemFontOfSize:titleSize];
                    titleLabel.textAlignment = NSTextAlignmentCenter;
                    [backView addSubview:titleLabel];
                }
            }
        }
    }else{
        for (int i=0; i<pageNumAdd; i++) {//页循环
            for (int j=0; j<2; j++) {//行循环
                for (int g=0; g<btnNum; g++) {//列循环
                    int the = 2*btnNum*i+j*btnNum+g;
                    if (the >= btnsAry.count) {
                        return;
                    }
                    float origY;
                    if (j==0) { origY =15; }else{ origY =15+60+20+11; }
                    NSDictionary *btnInfo = [btnsAry objectAtIndex:the];
                    NSString *normalImg = [btnInfo stringValueForKey:@"normalImg" defaultValue:nil];
                    NSString *highlightImg = [btnInfo stringValueForKey:@"activeImg" defaultValue:nil];
                    NSString *title = [btnInfo stringValueForKey:@"title" defaultValue:nil];
                    UIButton *detailBtn = [UIButton buttonWithType:UIButtonTypeCustom];
                    detailBtn.frame = CGRectMake(_mainScreenWidth*i+verInterval+(60+verInterval)*g, origY, 60, 60);
                    if (normalImg) {
                        NSString *realNormPath = [self getPathWithUZSchemeURL:normalImg];
                        [detailBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realNormPath] forState:UIControlStateNormal];
                        NSString *realhighPath = [self getPathWithUZSchemeURL:highlightImg];
                        [detailBtn setBackgroundImage:[UIImage imageWithContentsOfFile:realhighPath] forState:UIControlStateHighlighted];
                    }else{
                        [detailBtn setBackgroundColor:[UIColor greenColor]];
                    }
                    [detailBtn addTarget:self action:@selector(extrasBoardClick:) forControlEvents:UIControlEventTouchUpInside];
                    detailBtn.tag = the;
                    [addSource addSubview:detailBtn];
                    UILabel *titleLabel = [[UILabel alloc]init];
                    titleLabel.backgroundColor = [UIColor clearColor];
                    titleLabel.frame = CGRectMake(detailBtn.frame.origin.x, detailBtn.frame.origin.y+detailBtn.frame.size.height+5.0, 60, 20);
                    titleLabel.text = title;
                    titleLabel.textColor = [UZAppUtils colorFromNSString:titleColor];
                    titleLabel.font = [UIFont systemFontOfSize:titleSize];
                    titleLabel.textAlignment = NSTextAlignmentCenter;
                    [addSource addSubview:titleLabel];
                }
            }
        }
    }
    
}

#pragma mark 面板内点击事件
- (void)extrasBoardClick:(UIButton *)btn{
    NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
    [sendDict setObject:[NSNumber numberWithBool:YES] forKey:@"click"];
    [sendDict setObject:[NSNumber numberWithInteger:btn.tag] forKey:@"index"];
    [sendDict setObject:@"clickExtras" forKey:@"eventType"];
    [self sendResultEventWithCallbackId:openCbID dataDict:sendDict errDict:nil doDelete:NO];
}

- (void)recordBoardClick:(UIButton *)btn{
    NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:2];
    //[sendDict setObject:[NSNumber numberWithBool:YES] forKey:@"click"];
    [sendDict setObject:@"clickRecord" forKey:@"eventType"];
    [self sendResultEventWithCallbackId:openCbID dataDict:sendDict errDict:nil doDelete:NO];
}


- (void)emotionBoardClick:(UIButton *)btn{
    NSRange range = [_textView selectedRange];
    NSInteger index = range.location;
    NSDictionary *emotionInfo = [self.sourceAry objectAtIndex:btn.tag-1];
    NSString *emojiPath = [emotionInfo objectForKey:@"name"];
    NSString *widgetPath = [NSString stringWithFormat:@"%@/%@.png",self.realImgPath,emojiPath];
    NSString *emojiRealPath = [NSString stringWithFormat:@"%@",[self getPathWithUZSchemeURL:widgetPath]];
    NSString *emotionStr = [emotionInfo stringValueForKey:@"text" defaultValue:@"[未知表情]"];
    NSMutableString *tempStr = [NSMutableString stringWithString:_textView.text];
    {
        //NSString *str1 = [tempStr substringToIndex:index];
        //NSString *str2 = [tempStr substringFromIndex:index];
        //NSString *strL = [NSString stringWithFormat:@"%@%@%@",str1,emotionStr,str2];
        UZUIChatBoxAttachment *attachment = [[UZUIChatBoxAttachment alloc] init];
       attachment.emotionString = emotionStr;
        attachment.image = [UIImage imageWithContentsOfFile:emojiRealPath];
        attachment.bounds = CGRectMake(0, -3, 18, 18);
        NSRange range = _textView.selectedRange;
        NSMutableAttributedString *textAttr = [[NSMutableAttributedString alloc] initWithAttributedString:_textView.attributedText];
        NSAttributedString *imageAttr = [NSMutableAttributedString attributedStringWithAttachment:attachment];
        [textAttr replaceCharactersInRange:_textView.selectedRange withAttributedString:imageAttr];

        [textAttr addAttributes:@{NSFontAttributeName : _textView.font} range:NSMakeRange(_textView.selectedRange.location, 1)];

        _textView.attributedText = textAttr;

        // 会在textView后面插入空的,触发textView文字改变
        [_textView insertText:@""];

        _textView.selectedRange = NSMakeRange(range.location + 1, 0);

        [self textViewDidChange:_textView];
    }
    range.location += 1;
    [_textView setSelectedRange:range];
}

#pragma mark 键盘监听
//当键盘出现或改变时调用
- (void)keyboardWillShow:(NSNotification *)aNotification{
    CGSize windowSize = _chatBgView.superview.bounds.size;

    if (![_textView isFirstResponder]) {
        return;
    }
    
    isKeyboardShow = YES;
    [self alignBottomLineToChatInputField];
    //将左边按钮重置
    UIButton *tempSpeechBtn = (UIButton *)[btnSuperView viewWithTag:TagSpeechBtn];
    NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
    NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIcon] forState:UIControlStateNormal];
    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateHighlighted];
    //将按钮置为表情状态
    UIButton *tempFceBtn = (UIButton *)[btnSuperView viewWithTag:TagEmotionBtn];
    [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionNormalImg] forState:UIControlStateNormal];
    [tempFceBtn setImage:[UIImage imageWithContentsOfFile:emotionHighImg] forState:UIControlStateHighlighted];
    emotionBtnState = 0;
    //关闭表情面板
    CGRect  emojiRect = _emotionView.frame;
    emojiRect.origin.y = windowSize.height;
    _emotionView.frame = emojiRect;
    //关闭添加面板
    CGRect  addRect =_extrasBoard.frame;
    addRect.origin.y = windowSize.height;
    _extrasBoard.frame = addRect;
    
    //关闭录音面板
    CGRect  addRecordRect =_soundRecordingView.frame;
    addRecordRect.origin.y = windowSize.height;
    _soundRecordingView.frame = addRecordRect;
    //获取键盘的高度
    NSDictionary *userInfo = [aNotification userInfo];
    NSValue *aValue = [userInfo objectForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect keyboardRect = [aValue CGRectValue];
    int height = keyboardRect.size.height;
    
    CGRect  tempFrame = _chatBgView.frame;
    CGFloat currentChatH;
    if (KIsiPhoneX) {
        currentChatH = _chatBgView.frame.size.height-34;
    }else{
        currentChatH = _chatBgView.frame.size.height;
    }
    tempFrame.origin.y = windowSize.height - height - currentChatH;
       //tempFrame.origin.y = _mainScreenHeight - height - 50;
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:NO];
    [UIView setAnimationDuration:0.3];
    [_chatBgView setFrame:tempFrame];

    [UIView commitAnimations];
    self.currentInputfeildHeight = currentChatH;
    self.currentChatViewHeight = windowSize.height - self.currentInputfeildHeight - _chatBgView.frame.origin.y;
    [self.viewController.view bringSubviewToFront:_chatBgView];
}

//当键退出时调用
- (void)keyboardWillHide:(NSNotification *)aNotification {
    if (!isKeyboardShow) {
        return;
    }
    additionalBtnState = 0;
    isKeyboardShow = NO;

//    CGRect  tempFrame = _chatBgView.frame;
//    tempFrame.origin.y = _mainScreenHeight-_chatBgView.frame.size.height;
    CGSize windowSize = _chatBgView.superview.frame.size;
    CGRect tempFrame = CGRectMake(0, windowSize.height-_chatBgView.frame.size.height, windowSize.width, _chatBgView.frame.size.height);
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationBeginsFromCurrentState:YES];
    [UIView setAnimationDuration:0.3];
    [_chatBgView setFrame:tempFrame];
    [UIView commitAnimations];
    self.currentInputfeildHeight = _chatBgView.frame.size.height;
    self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
}

- (void)shrinkKeyboard {
    //下移输入框
    [_textView resignFirstResponder];
//    CGRect inputRect = _chatBgView.frame;
//    inputRect.origin.y = _mainScreenHeight-_chatBgView.frame.size.height;
    CGSize windowSize = _chatBgView.superview.frame.size;
    CGRect inputRect = CGRectMake(0, windowSize.height-_chatBgView.frame.size.height, windowSize.width, _chatBgView.frame.size.height);
    
    //下移表情面板
    CGRect emotionRect = _emotionView.frame;
    emotionRect.origin.y = windowSize.height;
    //下移添加面板
    CGRect addRect = _extrasBoard.frame;
    addRect.origin.y = windowSize.height;
    //下移录音面板
    CGRect recordRect = _soundRecordingView.frame;
    recordRect.origin.y = windowSize.height;
    //动画
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationDuration:0.3];
    _chatBgView.frame = inputRect;
    _extrasBoard.frame = addRect;
    _emotionView.frame = emotionRect;
    _soundRecordingView.frame = recordRect;
    [UIView commitAnimations];
    self.currentInputfeildHeight = _chatBgView.frame.size.height;
    self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
    //将左边按钮重置
    UIButton *tempSpeechBtn = (UIButton*)[btnSuperView viewWithTag:TagSpeechBtn];
    NSString *normalIcon = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"normalImg" defaultValue:nil]];
    NSString *normalIconAC = [self getPathWithUZSchemeURL:[self.sendBtnInfo stringValueForKey:@"activeImg" defaultValue:nil]];
    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIcon] forState:UIControlStateNormal];
    [tempSpeechBtn setImage:[UIImage imageWithContentsOfFile:normalIconAC] forState:UIControlStateHighlighted];
    tempSpeechBtn.selected = NO;
}

#pragma mark 发送、取消函数
- (void)send:(id)btn {
    //将输入框大小打回原形
    CGRect textTemp = _textView.frame;
    textTemp.size.height = 32;
    _textView.frame = textTemp;
    CGRect textBoardTemp = _chatBgView.frame;
    if(textBoardTemp.size.height>self.chatH){
        float changeY = textBoardTemp.size.height-self.chatH;
        textBoardTemp.origin.y += changeY;
        textBoardTemp.size.height -= changeY;
        _chatBgView.frame = textBoardTemp;
        
        CGRect rect = btnSuperView.frame;
        rect.origin.y = _chatBgView.bounds.size.height - self.chatH;
        btnSuperView.frame = rect;
        CGSize windowSize = _chatBgView.superview.frame.size;
        self.currentInputfeildHeight = _chatBgView.frame.size.height;
        self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight-_chatBgView.frame.origin.y;
    }
    //下分割线
    UIView *line = [_chatBgView viewWithTag:TagCutLineDown];
    CGRect lineRect = line.frame;
    lineRect.origin.y = _chatBgView.frame.size.height - 1;
    line.frame = lineRect;
    //回调给前端
    NSMutableString *strM = [NSMutableString string];
    //__block NSString *string ;
    [_textView.attributedText enumerateAttributesInRange:NSMakeRange(0, _textView.attributedText.length) options:NSAttributedStringEnumerationLongestEffectiveRangeNotRequired usingBlock:^(NSDictionary<NSAttributedStringKey,id> * _Nonnull attrs, NSRange range, BOOL * _Nonnull stop) {
        NSString *str = nil;
        UZUIChatBoxAttachment *attachment = attrs[@"NSAttachment"];
        if (attachment) { // 表情
//            str = [attachment.emotionString substringFromIndex:attachment.emotionString.length];
            str = attachment.emotionString ;
           [strM appendString:str];
        }
        else { // 文字
            str = [_textView.attributedText.string substringWithRange:range];
            [strM appendString:str];
        }
        
    }];
    NSString *willSendText = strM;
   // NSString *willSendText = _textView.text;

    if (willSendText.length>0&&[willSendText isKindOfClass:[NSString class]]){
        NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:3];
        [sendDict setObject:[NSNumber numberWithBool:NO] forKey:@"click"];
        [sendDict setObject:willSendText forKey:@"msg"];
        [sendDict setObject:@"send" forKey:@"eventType"];
        [self sendResultEventWithCallbackId:openCbID dataDict:sendDict errDict:nil doDelete:NO];
        _textView.text = @"";
        [self textViewDidChange:_textView];
    } else {
        NSMutableDictionary *sendDict = [NSMutableDictionary dictionaryWithCapacity:3];
        [sendDict setObject:[NSNumber numberWithBool:NO] forKey:@"click"];
        [sendDict setObject:@"" forKey:@"msg"];
        [sendDict setObject:@"send" forKey:@"eventType"];
        [self sendResultEventWithCallbackId:openCbID dataDict:sendDict errDict:nil doDelete:NO];
    }
}

- (void)cancel:(UIButton *)btn {
    
      [_textView deleteBackward];
}

- (void)hideKeyborad {
    if (_timer) {
        [_timer invalidate];
        _timer = nil;
        [_textView becomeFirstResponder];
    }
}

int getUIRowCountWith(float screenWidth ,float sideLength)
{
    int row = 1;
    float interval =( screenWidth-(row*sideLength))/(row+1);
    while (interval>sideLength/3.0) {
        row++;
        interval =( screenWidth-(row*sideLength))/(row+1);
    }
    return row;
}

- (UIImage *)getImageFromColor:(UIColor *)color withSize:(CGSize)size {
    CGRect rect = CGRectMake(0, 0, size.width, size.height);
    UIGraphicsBeginImageContext(rect.size);
    CGContextRef context = UIGraphicsGetCurrentContext();
    CGContextSetFillColorWithColor(context, [color CGColor]);
    CGContextFillRect(context, rect);
    UIImage *img = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return img;
}

#pragma mark -
#pragma mark for delegate
#pragma mark -

#pragma mark UITextViewDelegate
- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text{
    if([text isEqualToString:@"\n"]){
        [self send:nil];
        return NO;
    }
    return YES;
}

- (void)textViewDidChange:(UITextView *)textView {
    NSLog(@"%@",textView.text);
    CGSize windowSize = _chatBgView.superview.bounds.size;

    UZUIChatBoxTextView *tempView = (UZUIChatBoxTextView *)textView;
    if (textView.text.length != 0) {//开始输入则占位提示文字消失
        tempView.placeholder.text = nil;
    } else if (self.placeholderStr) {//显示占位提示文字
        tempView.placeholder.text = self.placeholderStr;
    }
    if([textView.text isEqualToString:@"\n"]){//点击了键盘上的发送按钮
        textView.text = @"";
        //点击了键盘上的发送按钮恢复占位提示文字
        tempView.placeholder.text = self.placeholderStr;
    }
    if (valueChangedCbid >= 0) {//输入框内的值有变化则回调给相应监听
        NSString *text = textView.text;
        if (text) {
            [self sendResultEventWithCallbackId:valueChangedCbid dataDict:[NSDictionary dictionaryWithObject:text forKey:@"value"] errDict:nil doDelete:NO];
        } else {
            [self sendResultEventWithCallbackId:valueChangedCbid dataDict:[NSDictionary dictionaryWithObject:@"" forKey:@"value"] errDict:nil doDelete:NO];
        }
    }
    
    NSMutableString *strM = [NSMutableString string];

    [textView.attributedText enumerateAttributesInRange:NSMakeRange(0, textView.attributedText.length) options:NSAttributedStringEnumerationLongestEffectiveRangeNotRequired usingBlock:^(NSDictionary<NSAttributedStringKey,id> * _Nonnull attrs, NSRange range, BOOL * _Nonnull stop) {
        NSString *str = nil;
        UZUIChatBoxAttachment *attachment = attrs[@"NSAttachment"];
        if (attachment) { // 表情
//            str = attachment.emotionString;
//              str = [attachment.emotionString substringFromIndex:attachment.emotionString.length-4];
            str = @"[1]";
            [strM appendString:str];
        } else { // 文字
            str = [textView.attributedText.string substringWithRange:range];
            [strM appendString:str];
        }

    }];
    
    //计算文本的高度
    float fPadding = 8.0; // 8.0px x 2 文字和左右边框的间隙大小
    CGSize constraint = CGSizeMake(textView.contentSize.width - fPadding, CGFLOAT_MAX);
//    CGSize sizeFrame = [textView.text sizeWithAttributes:]
    CGSize sizeFrame = [strM sizeWithFont:textView.font constrainedToSize:constraint lineBreakMode:NSLineBreakByWordWrapping];//计算当前文本的frame
    float height = sizeFrame.height + 8.0;// 加上文字和上下边框的间隙大小
    CGRect beforTextRect = textView.frame;
    BOOL isSmal = beforTextRect.size.height >= _maxHeight;//当前输入框高度小于最大值
    if (height>_maxHeight && isSmal) {//大于最大值且当前输入框小则不改变输入框大小
        if (height > 32) {//29.**;32 一行文字时的高度
            if (_maxHeight == 32) {
                //[textView setContentOffset:CGPointMake(0, height-textView.frame.size.height+10.0) animated:NO];
                return;
            }
            //[textView setContentOffset:CGPointMake(0, height-textView.frame.size.height+5.0) animated:NO];
        }//间隙正常
        return;
    }
    if (height > _maxHeight) {
        height = _maxHeight;
    }
    UIView *line = [_chatBgView viewWithTag:TagCutLineDown];//下分割线
    CGRect lineRect = line.frame;
    float changeHeight = 0;
    if (height > 32.0) {//输入框内文字大于一行时
        changeHeight = height - 32;//计算改变量
        //重新调整textView内容承载框的高度
        CGRect newRect = textView.frame;
        newRect.size.height = height;
        textView.frame = newRect;
        //重置textBar的大小和位置
        float x = _chatBgView.frame.origin.x;
        float y = _chatBgView.frame.origin.y;
        float w = _chatBgView.frame.size.width;
        float h = self.chatH + changeHeight;
        float changeY;
        if (h == _chatBgView.frame.size.height) {
         //保留使用
        } else if (h < _chatBgView.frame.size.height) {
             changeY = _chatBgView.frame.size.height - h;
             y = _chatBgView.frame.origin.y + changeY;
        } else {
            changeY = h - _chatBgView.frame.size.height;
             y = _chatBgView.frame.origin.y - changeY;
        }
        //animation
        [UIView animateWithDuration:0.4 animations:^{
            _chatBgView.frame = CGRectMake(x, y, w, h);
        } completion:^(BOOL finish){
            //调整内容文本上下间隙
//            CGSize textContentSize = textView.contentSize;
//            if (textContentSize.height > newRect.size.height) {
//                textContentSize.height = newRect.size.height;
//            }
//            textView.contentSize = textContentSize;
//            UIEdgeInsets textContentInset = textView.contentInset;
//            textContentInset.top = -4;
//            textView.contentInset = textContentInset;
//            CGPoint offset = textView.contentOffset;
//            offset.y = 4;
//            textView.contentOffset = offset;
            //NSLog(@"Animation---contentSize.height:%f",textView.contentSize.height);
            //NSLog(@"Animation---textView.contentInset.top:%f",textView.contentInset.top);
            //NSLog(@"Animation---contentOffset.y:%f",textView.contentOffset.y);
        }];
        self.currentInputfeildHeight = _chatBgView.frame.size.height;
        self.currentChatViewHeight = windowSize.height-self.currentInputfeildHeight - _chatBgView.frame.origin.y;
        
        //NSLog(@"contentSize.height:%f",textView.contentSize.height);
        //NSLog(@"textView.contentInset.top:%f",textView.contentInset.top);
        //NSLog(@"contentOffset.y:%f",textView.contentOffset.y);
    } else {//输入框内文字为一行时
        height = 32.0;
        //重新调整textView的高度
        CGRect newTextRect = textView.frame;
        newTextRect.size.height = height;
        //重置输入框背景主板的大小和位置
        float h = _chatBgView.frame.size.height;
        if (h > self.chatH) {
            float x = _chatBgView.frame.origin.x;
            float y = _chatBgView.frame.origin.y + (h - self.chatH);
            float w = _chatBgView.frame.size.width;
            h = self.chatH;
            [UIView beginAnimations:nil context:nil];
            [UIView setAnimationDuration:0.3];
            textView.frame = newTextRect;
            _chatBgView.frame = CGRectMake(x, y, w, h);
            [UIView commitAnimations];
            self.currentInputfeildHeight = _chatBgView.frame.size.height;
            self.currentChatViewHeight = windowSize.height - self.currentInputfeildHeight - _chatBgView.frame.origin.y;
        }
        //调整内容文本的上下间隙
        CGSize textContentSize = textView.contentSize;
        textContentSize.height =  textView.frame.size.height;
        textView.contentSize = textContentSize;
        UIEdgeInsets textContentInset = textView.contentInset;
        textContentInset.top = -1.5;
        textView.contentInset = textContentInset;
    }
    //下边框分割线
    lineRect.origin.y = _chatBgView.frame.size.height - 1;
    line.frame = lineRect;
}

#pragma mark-
#pragma mark scrollViewDelegate
#pragma mark-

- (void)scrollViewDidScroll:(UIScrollView *)sender{
    if (sender.tag == TagEmotionBoard) {
        CGFloat pagewidth = sender.frame.size.width;
        int page = floor(sender.contentOffset.x/pagewidth);
        pageControl.currentPage = page;
    } else if (sender.tag == TagExtraBoard) {
        CGFloat pagewidth = sender.frame.size.width;
        int page = floor(sender.contentOffset.x/pagewidth);
        pageControlExtra.currentPage = page;
    } 
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
    if (scrollView.tag != TagEmotionBoard && scrollView.tag != TagExtraBoard) {
        [self shrinkKeyboard];
    }
}

#pragma mark  面板页面控制器的方法
- (void)turnPage {
    //int page = pageControl.currentPage; // 获取当前的page
    //[self.scrollView scrollRectToVisible:CGRectMake(width*(page+1),0,width,scrollHeight) animated:YES]; // 触摸pagecontroller那个点点 往后翻一页 +1
}

- (void)turnPageAdd {
    //int page = pageControl.currentPage; // 获取当前的page
    //[self.scrollView scrollRectToVisible:CGRectMake(width*(page+1),0,width,scrollHeight) animated:YES]; // 触摸pagecontroller那个点点 往后翻一页 +1
}

- (BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldRecognizeSimultaneouslyWithGestureRecognizer:(UIGestureRecognizer *)otherGestureRecognizer {
    NSString *class1 = NSStringFromClass([gestureRecognizer class]);
    NSString *class2 = NSStringFromClass([otherGestureRecognizer class]);
    if ([class1 isEqual:class2]) {
        return YES;
    }
    return NO;
}

#pragma mark  - ButtonViewDelegate -

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
    [self changeRecordBgHighlight];
    if (recBtnPressIdcb >= 0) {
        [self sendResultEventWithCallbackId:recBtnPressIdcb dataDict:nil errDict:nil doDelete:NO];
    }
    touchEvent = touchIn;
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *t = [touches anyObject];
    if ([self.recordType isEqualToString:@"pressRecord"]) {
        CGPoint where = [t locationInView:_recordBtn];
        if (where.x<0 || where.x>_recordBtn.bounds.size.width || where.y<0 || where.y>_recordBtn.bounds.size.height) {
            [self changeRecordBgNormal];
            if (touchEvent != touchMoveOut) {
                if (recBtnMoveoutIdcb >= 0) {
                    [self sendResultEventWithCallbackId:recBtnMoveoutIdcb dataDict:nil errDict:nil doDelete:NO];
                }
            }
            touchEvent=touchMoveOut;
        } else {
            [self changeRecordBgHighlight];
            if (touchEvent==touchMoveOut) {
                if (recBtnMoveinIdcb >= 0) {
                    [self sendResultEventWithCallbackId:recBtnMoveinIdcb dataDict:nil errDict:nil doDelete:NO];
                }
            }
            touchEvent=touchMoveIn;
        }
    }else{
        CGPoint where = [t locationInView:_recordPanelBtn];
        if (where.x<0 || where.x>_recordPanelBtn.bounds.size.width || where.y<0 || where.y>_recordPanelBtn.bounds.size.height) {
            [self changeRecordBgNormal];
            if (touchEvent != touchMoveOut) {
                if (recBtnMoveoutIdcb >= 0) {
                    [self sendResultEventWithCallbackId:recBtnMoveoutIdcb dataDict:nil errDict:nil doDelete:NO];
                }
            }
            touchEvent=touchMoveOut;
        } else {
            [self changeRecordBgHighlight];
            if (touchEvent==touchMoveOut) {
                if (recBtnMoveinIdcb >= 0) {
                    [self sendResultEventWithCallbackId:recBtnMoveinIdcb dataDict:nil errDict:nil doDelete:NO];
                }
            }
            touchEvent=touchMoveIn;
        }
    }

}

- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
    UITouch *t = [touches anyObject];
    if ([self.recordType isEqualToString:@"pressRecord"]) {
        CGPoint where = [t locationInView:_recordBtn];
        [self changeRecordBgNormal];
        if (where.x<0 || where.x>_recordBtn.bounds.size.width || where.y<0 || where.y>_recordBtn.bounds.size.height) {
            if (recBtnMoveoutCancelIdcb >= 0) {
                [self sendResultEventWithCallbackId:recBtnMoveoutCancelIdcb dataDict:nil errDict:nil doDelete:NO];
            }
            touchEvent=touchMoveOutCancel;
        } else {
            if (recBtnPressCancelIdcb >= 0) {
                [self sendResultEventWithCallbackId:recBtnPressCancelIdcb dataDict:nil errDict:nil doDelete:NO];
            }
            touchEvent=touchCancel;
        }
    }else{
        CGPoint where = [t locationInView:_recordPanelBtn];
        [self changeRecordBgNormal];
        if (where.x<0 || where.x>_recordPanelBtn.bounds.size.width || where.y<0 || where.y>_recordPanelBtn.bounds.size.height) {
            if (recBtnMoveoutCancelIdcb >= 0) {
                [self sendResultEventWithCallbackId:recBtnMoveoutCancelIdcb dataDict:nil errDict:nil doDelete:NO];
            }
            touchEvent=touchMoveOutCancel;
        } else {
            if (recBtnPressCancelIdcb >= 0) {
                [self sendResultEventWithCallbackId:recBtnPressCancelIdcb dataDict:nil errDict:nil doDelete:NO];
            }
            touchEvent=touchCancel;
        }
    }
    
}

- (void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event{
    [self changeRecordBgNormal];
    if (recBtnPressCancelIdcb >= 0) {
        [self sendResultEventWithCallbackId:recBtnPressCancelIdcb dataDict:nil errDict:nil doDelete:NO];
    }
    touchEvent=touchCancel;
}

- (void)changeRecordBgNormal {
    if ([self.recordType isEqualToString:@"pressRecord"]) {
        id bgView = [_recordBtn viewWithTag:TagRecordBtn];
        NSString *recordNormal = [self.recordBtnInfo stringValueForKey:@"normalBg" defaultValue:@"#c4c4c4"];
        if ([bgView isKindOfClass:[UIImageView class]]) {
            UIImageView *tempBgView = (UIImageView*)bgView;
            tempBgView.image = [UIImage imageWithContentsOfFile:[self getPathWithUZSchemeURL:recordNormal]];
        } else {
            UIView *tempBgView = (UIView*)bgView;
            tempBgView.backgroundColor = [UZAppUtils colorFromNSString:recordNormal];
        }
    }else{
      
        id  recordPanelView = [_recordPanelBtn viewWithTag:TagRecordPanelBtn];
        if ([recordPanelView isKindOfClass:[UIImageView class]]) {
            UIImageView *tempRecordPanel = (UIImageView *)recordPanelView;
            tempRecordPanel.image = [UIImage imageWithContentsOfFile:normalRecordImg];
        }
    }
  
    //重置标题
    UILabel *tempLabel = (UILabel*)[_recordBtn viewWithTag:TagRecordTitle];
    if (tempLabel) {
        tempLabel.text = normalTitle;
    }
    
  
}

- (void)changeRecordBgHighlight {
    if ([self.recordType isEqualToString:@"pressRecord"]) {
        id bgView = [_recordBtn viewWithTag:TagRecordBtn];
        NSString *recordHighlight = [self.recordBtnInfo stringValueForKey:@"activeBg" defaultValue:@"#999999"];
        if ([bgView isKindOfClass:[UIImageView class]]) {
            UIImageView *tempBgView = (UIImageView *)bgView;
            tempBgView.image = [UIImage imageWithContentsOfFile:[self getPathWithUZSchemeURL:recordHighlight]];
        } else {
            UIView *tempBgView = (UIView *)bgView;
            tempBgView.backgroundColor = [UZAppUtils colorFromNSString:recordHighlight];
        }
    }else{
        id  recordPanelView = [_recordPanelBtn viewWithTag:TagRecordPanelBtn];
        if ([recordPanelView isKindOfClass:[UIImageView class]]) {
            UIImageView *tempRecordPanel = (UIImageView *)recordPanelView;
            tempRecordPanel.image = [UIImage imageWithContentsOfFile:activeRecordImg];
            
        }
        
    }
  
    //重置标题
    UILabel *tempLabel = (UILabel *)[_recordBtn viewWithTag:TagRecordTitle];
    if (tempLabel) {
        tempLabel.text = activeTitle;
    }
    


}



-(void)cancelRecord:(NSDictionary *)paramsDict_{
 
    [self sendResultEventWithCallbackId:recordCanceledCbId dataDict:nil errDict:nil doDelete:NO];

}

@end
