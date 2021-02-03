# 概述

聊天盒子模块源码（内含iOS和android）

APICloud 的 UIChatBox 模块是一个聊天输入框模块，可通过此模块调用一个简单的聊天盒子功能，支持自定义标签面板，附加功能面板和录音按钮等相关功能。但是由于本模块 UI 布局界面为固定模式，不能满足日益增长的广大开发者对搜索模块样式的需求。因此，广大原生模块开发者，可以参考此模块的开发方式、接口定义等开发规范，或者基于此模块开发出更多符合产品设计的新 UI 布局的模块，希望此模块能起到抛砖引玉的作用。

# 模块接口
/*
Title: UIChatBox
Description: UIChatBox
*/

<p style="color: #ccc; margin-bottom: 30px;">来自于：APICloud 官方<a style="background-color: #95ba20; color:#fff; padding:4px 8px;border-radius:5px;margin-left:30px; margin-bottom:0px; font-size:12px;text-decoration:none;" target="_blank" href="//www.apicloud.com/mod_detail/UIChatBox">立即使用</a></p>

<div class="outline">

[open](#open)
[close](#close)
[show](#show)
[hide](#hide)
[popupBoard](#popupBoard)
[closeBoard](#closeBoard)
[popupKeyboard](#popupKeyboard)
[closeKeyboard](#closeKeyboard)
[value](#value)
[insertValue](#insertValue)
[addEventListener](#addEventListener)
[setPlaceholder](#setPlaceholder)
[reloadExtraBoard](#reloadExtraBoard)
[cancelRecord](#cancelRecord)

</div>

# 论坛示例

为帮助用户更好更快的使用模块，论坛维护了一个[示例](https://community.apicloud.com/bbs/thread-139014-1-1.html)，示例中包含示例代码、知识点讲解、注意事项等，供您参考。

# **模块概述**

UIChatBox 模块是一个聊天输入框模块，开发者可自定义该输入框的功能。通过 open 接口可在当前 window 底部打开一个输入框，该输入框的生命属于当前 window 所有。当输入框获取焦点后，会自动弹动到软键盘之上。开发者可通过监听输入框距离底部弹动的高度，来改变聊天对话界面的高度，从而实现类似 QQ 聊天页面的功能。**UIChatBox 模块是 chatBox 模块的优化版。**


本模块的主要功能有：

1，自定义表情集：open 接口的 emotionPath 参数

2，自定义输入框最大自适应高度：open 接口的 maxRows 参数

3，输入框占位提示文字：open 接口的 placeholder 参数

4，自定义是否显示附件功能按钮：

5，自定义显示录音按钮：

6，手动弹出、关闭软键盘功能

7，输入框插入、获取当前文本

8，动态刷新附加功能面板

功能详情参考接口参数。

模块预览图如下：

![UIChatBox](https://docs.apicloud.com/img/docImage/chatBox.jpg)

***本模块源码已开源，地址为：https://github.com/apicloudcom/UIChatBox***

## [实例widget下载地址](https://github.com/apicloudcom/UIChatBox-Example/archive/master.zip)

## 模块接口

<div id="open"></div>
# **open**

打开聊天输入框

open({parmas}, callback(ret))

## params

placeholder：

- 类型：字符串
- 描述：（可选项）输入框的占位提示文本

autoFocus：

- 类型：布尔
- 描述：（可选项）输入框是否自动获取焦点，并弹出键盘
- 默认值：false

maxRows：

- 类型：数字
- 描述：（可选项）输入框显示的最大行数（高度自适应）
- 默认值：4

emotionPath：

- 类型：字符串
- 描述：自定义表情文件夹（表情图片所在的文件夹，须同时包含一个与该文件夹同名的`.json`配置文件）的路径（本地路径，fs://、widget://）。**`.json`文件内的 name 值必须与表情文件夹内表情图片名对应。**另附：[表情文件夹资源示例](/res/emotion.zip)
- `.json`配置文件格式如下：

```json
[
    {'name': 'Expression_1', 'text': '[微笑]'},
    {'name': 'Expression_2', 'text': '[撇嘴]'}
]
```

- `.json`配置文件所在目录：

![emotionPath](/img/emotionPath.png)

texts：

- 类型：JSON 对象
- 描述：（可选项）聊天输入框模块可配置的文本
- 内部字段：

```js
{
    recordBtn: {                        //（可选项）JSON对象；录音按钮文字内容
        normalTitle: '按住 说话',        //（可选项）字符串类型；按钮常态的标题，默认：'按住 说话'
        activeTitle: '松开 结束'         //（可选项）字符串类型；按钮按下时的标题，默认：'松开 结束'
    },
    sendBtn: {                         //（可选项）JSON对象；发送按钮文字内容，在 iOS 平台上对键盘内按钮无效
        title: '发送'                   //（可选项）字符串类型；按钮常态的标题，默认：'发送'
    }
}
```

styles：

- 类型：JSON 对象
- 描述：模块各部分的样式集合
- 内部字段：

```js
{  
    topDivider:{                        //(可选项)顶部分割线配置
	width:3,                        //(可选项)顶部分割线的粗细，默认：0 
	color:'#000'                    //(可选项)顶部分割线的颜色；默认：#000 ;支持图片设置(fs:// & widget://)
    },
    inputBar:{                          //（可选项）JSON对象；输入区域（输入框及两侧按钮）整体样式      
        borderColor: '#d9d9d9',         //（可选项）字符串类型；输入框区域上下边框的颜色，支持 rgb，rgba，#；默认：'#d9d9d9'
        bgColor: '#f2f2f2',             //（可选项）字符串类型；输入框区域的整体背景色，支持 rgb，rgba，#；默认：'#f2f2f2'
	textColor:'#000',               //（可选项）字符串类型；输入文字的颜色；默认：#000，支持 rgb，rgba，#；默认：'#f2f2f2'
	placeholderColor:'',            //（可选项）字符串类型；提示文字的颜色；默认：系统默认，支持 rgb，rgba，#；默认：'#f2f2f2'
	textSize:16,                    //（可选项）数字类型；默认：16
	textMarginLeft:5                //（可选项）数字类型；光标距离左边框的距离，默认：5
    },
    inputBox: {                         //（可选项）JSON对象；输入框样式
        borderColor: '#B3B3B3',         //（可选项）字符串类型；输入框的边框颜色，支持 rgb，rgba，#；默认：'#B3B3B3'
        bgColor: '#f2f2f2',             //（可选项）字符串类型；输入框的背景色，支持 rgb，rgba，#；默认：'#f2f2f2'
        boardBgColor: '#f2f2f2',        //（可选项）字符串类型；面板的背景色(表情面板，附加面板)，支持 rgb，rgba，#；默认：'#f2f2f2'
        topMargin:10,                   //（可选项）数字类型；输入框距离顶部的边距；默认：10
	borderCorner:5,                 //(可选项)数字类型；默认：5
	leftIcon:{                      //(可选项)JSON对象，输入框左侧图标  本参数仅支持android
		size:20,                //(可选项)数字类型；icon的大小；默认：20
		path:'widget://res/left_icon.jpg'//字符串类型；icon的路径；支持widget:// fs://
	}
    },
    emotionBtn: {                       //（可选项）JSON对象；表情按钮样式；不传则不显示表情按钮
        normalImg: 'widget://'          //（可选项）字符串类型；表情按钮常态的背景图片（本地路径，fs://、widget://）；默认：笑脸小图标,
	},
    extrasBtn: {                        //（可选项）JSON对象；附加功能按钮样式，不传则不显示附加功能按钮
        normalImg: 'widget://'          //（可选项）字符串类型；附加功能按钮常态的背景图片（本地路径，fs://、widget://）
    },
    keyboardBtn: {                      //JSON对象；键盘按钮样式
        normalImg: 'widget://'          //字符串类型；键盘按钮常态的背景图片（本地路径，fs://、widget://）；默认：键盘小图标
    },
    speechBtn: {                        //（可选项）JSON对象；输入框左侧按钮样式，不传则不显示左边的语音按钮
        normalImg: 'widget://',         //字符串类型；左侧按钮常态的背景图片（本地路径，fs://、widget://）
        activeImg:''                    //字符串类型；左侧按钮按下的背景图片（本地路径，fs://、widget://）
    },
    recordBtn: {                        //JSON对象；“按住 录音”按钮的样式
        normalBg: '#c4c4c4',            //（可选项）字符串类型；按钮常态的背景，支持 rgb，rgba，#，图片路径（本地路径，fs://、widget://）；默认：'#c4c4c4'
        activeBg: '#999999',            //（可选项）字符串类型；按钮按下时的背景，支持 rgb，rgba，#，图片路径（本地路径，fs://、widget://）；默认：'#999999'；normalBg 和 activeBg 必须保持一致，同为颜色值，或同为图片路径
        color: '#000',                  //（可选项）字符串类型；按钮标题文字的颜色，支持 rgb，rgba，#，默认：'#000000'
        size: 14                        //（可选项）数字类型；按钮标题文字的大小，默认：14
   },
   recordPanelBtn:{                     //JSON对象；录音面板样式
     normalImg: '',        //（可选项）字符串类型；按钮常态的图片
     activeImg: ''         //（可选项）字符串类型；按钮按下时的图片
     width:100,           //（可选项）数字类型；按钮的宽度，默认：100
     height:100           //（可选项）数字类型；按钮的高度，默认：100
   
   },
    indicator: {                        //（可选项）JSON对象；表情和附加功能面板的小圆点指示器样式，若不传则不显示该指示器
        target: 'both',                 //（可选项）字符串类型；配置指示器的显示区域；默认：'both'
                                        //取值范围：
                                        //both（表情和附加功能面板皆显示）
                                        //emotionPanel（表情面板显示）
                                        //extrasPanel（附加功能面板显示）
        color: '#c4c4c4',               //（可选项）字符串类型；指示器颜色；支持 rgb、rgba、#；默认：'#c4c4c4'
        activeColor: '#9e9e9e'          //（可选项）字符串类型；当前指示器颜色；支持 rgb、rgba、#；默认：'#9e9e9e'
    },
    sendBtn: {                         //（可选项）JSON对象；发送按钮样式，本参数对 iOS 平台上的键盘内发送按钮无效
        bg: '#4cc518',                 //（可选项）字符串类型；发送按钮背景颜色，支持 rgb、rgba、#、img；默认：#4cc518
        titleColor: '#ffffff',          //（可选项）字符串类型；发送按钮标题颜色；默认：#ffffff
        activeBg: '#46a91e',            //（可选项）字符串类型；发送按钮背景颜色，支持 rgb、rgba、#、img；默认：无
        titleSize: 13                    //（可选项）数字类型；发送按钮标题字体大小；默认：13
    }
}
```
isShowSendBtn：

- 类型：布尔
- 描述：（可选项）表情键盘上的发送按钮是否显示 **注意:本参数对 iOS 平台上的键盘内发送按钮无效 ，该参数仅支持iOS**
- 默认：true


disableSendMessage:

- 类型：布尔类型
- 描述：（可选项）是否禁言
- 默认：false

extras：

- 类型：JSON 对象
- 描述：（可选项）点击附加功能按钮，打开的附加功能面板的按钮样式，配合 extrasBtn 一起使用，若 extrasBtn 参数内 normalImg 属性不传则此参数可不传

```js
{
    titleSize: 10,                  //（可选项）数字类型；标题文字大小，默认：10
    titleColor: '#a3a3a3',          //（可选项）字符串类型；标题文字颜色，支持 rgb、rgba、#；默认：'#a3a3a3'
    isAdaptScreenSize:true          // (可选项) 布尔类型；附加功能按钮的样式是否自适应屏幕大小布局，改参数为false时，则每一行固定显示四个,若为true，则根据屏幕大小自动适配 默认：true
    isCenterDisplay:false          // (可选项)布尔类型；附加功能按钮的样式是否居中显示，参数为false时，isAdaptScreenSize有效,若为true，则按钮居中显示； 默认：false
    btns: [{                        //数组类型；附加功能按钮的样式
        title: '图片',              //（可选项）字符串类型；附加功能按钮的标题内容                  
        normalImg: '',              //（可选项）字符串类型；按钮常态的背景图片（本地路径，fs://、widget://）
        activeImg: ''               //（可选项）字符串类型；按钮按下时的背景图片（本地路径，fs://、widget://）   
    }],
    isCustom:false ,                //是否自定义附加面板上按钮图标和文字间距及其左右边距；如果为true时，isAdaptScreenSize，isCenterDisplay两个参数无效，margin参数有效。为false时isAdaptScreenSize，isCenterDisplay两个参数有效，margin参数无效 ；（暂时只支持iOS）
    margin:{   //（暂时只支持iOS）
   
       horizontal ：20                //图标的水平间距
       vertical：20              //标题与图标的间距
       width:50,                 //图标的宽度（宽度高度一样）
       titleHeight：标题的高度
    },
    gridHorizontalPadding:20,    // 网格水平内边距；数字类型；默认：0 （暂仅支持Android）
    titleTopMargin:10            // 标题顶部边距（距离顶部图片）；数字类型；默认：0 （暂仅支持Android）  
}
```

recordType：

- 类型：字符串
- 描述：（可选项）点击左边录音按钮所展示的类型
- 默认：“按住 录音”按钮的样式
- 取值范围：
    - pressRecord  (“按住 录音”按钮的样式)
    - recordPanel  (录音面板)

isClose：

- 类型：布尔值
- 描述：（可选项）点击空白区域是否关闭整个模块(包括键盘)
- 默认：false

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    eventType: 'show',  //字符串类型；回调的事件类型，
                        //取值范围：
                        //show（该模块打开成功）
                        //send（用户点击发送按钮）
                        //clickExtras（用户点击附加功能面板内的按钮）
                        //clickRecord（用户点击录音功能面板内的按钮）
    index: 0,           //数字类型；当 eventType 为 clickExtras 时，此参数为用户点击附加功能按钮的索引，否则为 undefined
    msg: ''             //字符串类型；当 eventType 为 send 时，此参数返回输入框的内容，否则返回 undefined
    inputBarHeight:30   //数字类型；输入框及左右按钮整体区域的高度
}
```
## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.open({
	placeholder: '',
	maxRows: 4,
	emotionPath: 'widget://res/img/emotion',
	texts: {
		recordBtn: {
			normalTitle: '按住说话',
			activeTitle: '松开结束'
		},
		sendBtn: {
			title: 'send'
		}
	},
	styles: {
		inputBar: {
			borderColor: '#d9d9d9',
			bgColor: '#f2f2f2'
		},
		inputBox: {
			borderColor: '#B3B3B3',
			bgColor: '#FFFFFF'
		},
		emotionBtn: {
			normalImg: 'widget://res/img/chatBox_face1.png'
		},
		extrasBtn: {
			normalImg: 'widget://res/img/chatBox_add1.png'
		},
		keyboardBtn: {
			normalImg: 'widget://res/img/chatBox_key1.png'
		},
		speechBtn: {
			normalImg: 'widget://res/img/chatBox_key1.png'
		},
		recordBtn: {
			normalBg: '#c4c4c4',
			activeBg: '#999999',
			color: '#000',
			size: 14
		},
		indicator: {
			target: 'both',
			color: '#c4c4c4',
			activeColor: '#9e9e9e'
		},
		sendBtn: {
			titleColor: '#4cc518',
			bg: '#999999',
			activeBg: '#46a91e',
			titleSize: 14
		}
	},
	extras: {
		titleSize: 10,
		titleColor: '#a3a3a3',
		btns: [{
			title: '图片',
			normalImg: 'widget://res/img/chatBox_album1.png',
			activeImg: 'widget://res/img/chatBox_album2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}]
	}
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="close"></div>
# **close**

关闭聊天输入框

close()

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.close();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="show"></div>
# **show**

显示聊天输入框

show()

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.show();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="hide"></div>
# **hide**

隐藏聊天输入框

hide()

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.hide();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="popupKeyboard"></div>
# **popupKeyboard**

弹出键盘

popupKeyboard()

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.popupKeyboard();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="closeKeyboard"></div>
# **closeKeyboard**

收起键盘

closeKeyboard()

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.closeKeyboard();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="popupBoard"></div>
# **popupBoard**

弹出表情、附加功能面板

popupBoard({params})

## params

target:

- 类型：字符串
- 描述：操作的面板类型，取值范围如下：
	- emotion：表情面板
	- extras：附加功能面板
- 默认值：emotion
	

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.popupBoard({
	target: 'extras'
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="closeBoard"></div>
# **closeBoard**

收起表情、附加功能面板

closeBoard()

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.closeBoard();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="value"></div>
# **value**

获取或设置聊天输入框的内容

value({params}, callback(ret))

## params

msg：

- 类型：字符串
- 描述：（可选项）聊天输入框的内容，若不传则返回输入框的值

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    status: true,        //布尔型；true||false
    msg: ''              //字符串类型；输入框当前内容文本
}
```

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
//设置输入框的值
UIChatBox.value({
	msg: '设置输入框的值'
});

//获取输入框的值
UIChatBox.value(function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="insertValue"></div>
# **insertValue**

向聊天输入框的指定位置插入内容

insertValue({params})

## params

index：

- 类型：数字
- 描述：（可选项）待插入内容的起始位置。**注意：中文，全角符号均占一个字符长度；索引从0开始，0表示插入到最前面，1表示插入到第一个字符后面，2表示插入到第二个字符后面，以此类推。**
- 默认值：当前输入框的值的长度

msg：

- 类型：字符串
- 描述：（可选项）要插入的内容
- 默认值：''

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.insertValue({
	index: 10,
	msg: '这里是插入的字符串'
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="addEventListener"></div>
# **addEventListener**

事件监听

addEventListener({params}, callback(ret))

## params

target：

- 类型：字符串
- 描述：事件的目标对象
- 取值范围：
    * recordBtn（录音按钮，open 接口的 speechBtn 参数必须传值，否则事件监听无效）
    * inputBar（输入区域，输入框及两侧按钮区域）

name：

- 类型：字符串
- 描述：监听的事件类型
- 取值范围：
    - 适用于 recordBtn 对象
        - press（按下录音按钮）
        - press_cancel（松开录音按钮）
        - move_out（按下录音按钮后，从按钮移出）
        - move_out_cancel（按下录音按钮后，从按钮移出并松开按钮）
        - move_in（move_out 事件后，重新移入按钮区域）
	- recordCanceled 主动取消录音事件
    - 适用于 inputBar 对象
        - move（输入框所在区域弹动事件）
        - change（输入框所在区域高度改变）
        - showRecord（用户点击左侧语音按钮）
        - showEmotion（用户点击表情按钮）
        - showExtras（用户点击右侧附加功能按钮，如果 open 时传了 extras 参数才会有此回调）
        - valueChanged（输入框内容改变事件）

## callback(ret)

ret：

- 类型：JSON 对象
- 内部字段：

```js
{
    inputBarHeight: 60,    //数字类型；输入框及左右按钮整体区域的高度，仅当监听 inputBar 的 move 和 change 事件时本参数有值
    panelHeight: 300 ,     //数字类型；输入框下边缘距离屏幕底部的高度，仅当监听 inputBar 的 move 和 change 事件时本参数有值
    value: ''              //字符串类型；输入框当前内容，仅当 target 为 inputBar name 为 valueChanged 时有值
}
```

## 示例代码

```js
//监听 recordBtn 按钮
var UIChatBox = api.require('UIChatBox');
UIChatBox.addEventListener({
	target: 'recordBtn',
	name: 'press'
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});

//监听 inputBar 
var UIChatBox = api.require('UIChatBox');
UIChatBox.addEventListener({
	target: 'inputBar',
	name: 'move'
}, function(ret, err) {
	if (ret) {
		alert(JSON.stringify(ret));
	} else {
		alert(JSON.stringify(err));
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="setPlaceholder"></div>
# **setPlaceholder**

重设聊天输入框的占位提示文本

setPlaceholder({params})

## params

placeholder：

- 类型：字符串
- 描述：（可选项）占位提示文本，若不传或传空则表示清空占位提示内容

## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.setPlaceholder({
	placeholder: '修改了占位提示内容'
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本

<div id="reloadExtraBoard"></div>
# **reloadExtraBoard**

重新加载（刷新）附加功能面板，**open时必须添加附加功能按钮及其面板参数**

reloadExtraBoard({params})

## params

extras：

- 类型：JSON 对象
- 描述：（可选项）点击附加功能按钮，打开的附加功能面板的按钮样式，配合 extrasBtn 一起使用，若 extrasBtn 参数内 normalImg 属性不传则此参数可不传

```js
{
    titleSize: 10,                  //（可选项）数字类型；标题文字大小，默认：10
    titleColor: '#a3a3a3',          //（可选项）字符串类型；标题文字颜色，支持 rgb、rgba、#；默认：'#a3a3a3'
    btns: [{                        //数组类型；附加功能按钮的样式
        title: '图片',               //（可选项）字符串类型；附加功能按钮的标题内容                  
        normalImg: '',              //（可选项）字符串类型；按钮常态的背景图片（本地路径，fs://、widget://）
        activeImg: ''               //（可选项）字符串类型；按钮按下时的背景图片（本地路径，fs://、widget://）   
    }]
}
```
## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.reloadExtraBoard({
	extras: {
		titleSize: 10,
		titleColor: '#a3a3a3',
		btns: [{
			title: '图片',
			normalImg: 'widget://res/img/chatBox_album1.png',
			activeImg: 'widget://res/img/chatBox_album2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}, {
			title: '拍照',
			normalImg: 'widget://res/img/chatBox_cam1.png',
			activeImg: 'widget://res/img/chatBox_cam2.png'
		}]
	}
});
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本


<div id="cancelRecord"></div>
# **cancelRecord**

取消录音

cancelRecord()


## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.cancelRecord();
```

## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本



<div id="setInputBarBgColor"></div>
# **setInputBarBgColor**

这只面板的背景颜色

setInputBarBgColor({params})

## params

color：

- 类型：字符串类型
- 描述：面板背景色
- 默认值：'#f2f2f2'


## 示例代码

```js
var UIChatBox = api.require('UIChatBox');
UIChatBox.setInputBarBgColor({
	color: '',
});
```
## 可用性

iOS系统，Android系统

可提供的1.0.0及更高版本