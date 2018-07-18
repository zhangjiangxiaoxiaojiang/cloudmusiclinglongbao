package com.jinxin.cloudmusic.event;

/**
 * Created by XTER on 2016/9/7.
 * 位置事件
 */
public class PosEvent {
	private int pos;

	public PosEvent(int pos) {
		this.pos = pos;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}
}
