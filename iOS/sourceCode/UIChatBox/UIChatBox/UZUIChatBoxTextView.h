/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */

#import <UIKit/UIKit.h>

@interface UZUIChatBoxTextView : UITextView
- (id)initWithFrame:(CGRect)frame textMarginLeft:(CGFloat)marginLeft;
@property (nonatomic, strong) UILabel *placeholder;

@property (nonatomic, assign) CGFloat placeholderSize;
@property (nonatomic, assign) CGFloat textMarginLeft;

@end
