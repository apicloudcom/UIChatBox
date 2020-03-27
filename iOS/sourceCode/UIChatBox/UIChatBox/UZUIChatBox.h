/**
  * APICloud Modules
  * Copyright (c) 2014-2015 by APICloud, Inc. All Rights Reserved.
  * Licensed under the terms of the The MIT License (MIT).
  * Please see the license.html included with this distribution for details.
  */

#import "UZModule.h"

@protocol UZWebBrowserProtocol <NSObject>

@property (nonatomic, readonly) UIScrollView *scrollView;
@property (nonatomic) BOOL scalesPageToFit;

- (void)loadRequest:(NSURLRequest *)request;
- (void)loadHTMLString:(NSString *)string baseURL:(nullable NSURL *)baseURL;

@end

@interface UZUIChatBox : UZModule
@end
