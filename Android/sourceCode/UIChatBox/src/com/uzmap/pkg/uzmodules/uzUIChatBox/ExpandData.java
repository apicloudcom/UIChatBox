//
//UZModule
//
//Modified by magic 15/9/14.
//Copyright (c) 2015年 APICloud. All rights reserved.
//
package com.uzmap.pkg.uzmodules.uzUIChatBox;

public class ExpandData {
	private String normal;
	private String press;
	private String title;

	public ExpandData(String normal, String press, String title) {
		this.normal = normal;
		this.press = press;
		this.title = title;
	}

	public String getNomal() {
		return normal;
	}

	public void setNomal(String normal) {
		this.normal = normal;
	}

	public String getPress() {
		return press;
	}

	public void setPress(String press) {
		this.press = press;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
