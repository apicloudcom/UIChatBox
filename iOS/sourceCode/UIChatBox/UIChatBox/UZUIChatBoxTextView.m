/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */

#import "UZUIChatBoxTextView.h"

@implementation UZUIChatBoxTextView

@synthesize placeholder;

- (void)dealloc{
    if (placeholder) {
        [placeholder removeFromSuperview];
        self.placeholder = nil;
    }
}

- (id)initWithFrame:(CGRect)frame textMarginLeft:(CGFloat)marginLeft{
    self = [super initWithFrame:frame];
    if (self) {
        //标签
        UILabel *markLabel = [[UILabel alloc]init];
        markLabel.frame = CGRectMake(5+marginLeft+1, 1, frame.size.width-20, frame.size.height);
        markLabel.backgroundColor = [UIColor clearColor];
        markLabel.textColor = [UIColor grayColor];
        markLabel.textAlignment = NSTextAlignmentLeft;
        markLabel.userInteractionEnabled = NO;
        [self addSubview:markLabel];
        self.placeholder = markLabel;
    }
    return self;
}

-(CGRect)caretRectForPosition:(UITextPosition *)position{
    CGRect originalRect = [super caretRectForPosition:position];
    originalRect.size.height = _placeholderSize+4;
    return originalRect;
}

@end
