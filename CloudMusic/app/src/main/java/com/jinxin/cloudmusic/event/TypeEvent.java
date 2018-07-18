package com.jinxin.cloudmusic.event;

/**
 * Created by ZJ on 2017/3/9 0009.
 * 传输类型事件
 */
public class TypeEvent {
    private int type;

    public TypeEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
