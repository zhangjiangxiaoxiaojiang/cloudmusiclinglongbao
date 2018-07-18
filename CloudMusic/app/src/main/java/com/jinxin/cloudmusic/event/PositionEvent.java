package com.jinxin.cloudmusic.event;

/**
 * Created by ZJ on 2017/3/13 0013.
 * 上下首
 */
public class PositionEvent {
    private int p;

    public PositionEvent(int p) {
        this.p = p;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }
}
