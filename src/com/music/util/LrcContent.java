package com.music.util;

import java.io.Serializable;

/**
 * 2013/6/1
 * @author playm
 * ���ʵ����
 */
public class LrcContent implements Serializable{//implements for java.lang.RuntimeException
	private String lrcStr;	//�������
	private int lrcTime;	//��ʵ�ǰʱ��
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
