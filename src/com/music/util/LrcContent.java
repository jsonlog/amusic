package com.music.util;

import java.io.Serializable;

/**
 * 2013/6/1
 * @author playm
 * 歌词实体类
 */
public class LrcContent implements Serializable{//implements for java.lang.RuntimeException
	private String lrcStr;	//歌词内容
	private int lrcTime;	//歌词当前时间
	public String getLrcStr() {
		return lrcStr;
	}
	public void setLrcStr(String lrcStr) {
		this.lrcStr = lrcStr;
	}
	public int getLrcTime() {
		return lrcTime;
	}
	public void setLrcTime(int lrcTime) {
		this.lrcTime = lrcTime;
	}
}
