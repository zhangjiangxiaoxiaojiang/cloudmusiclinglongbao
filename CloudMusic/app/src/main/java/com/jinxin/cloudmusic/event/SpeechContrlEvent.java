package com.jinxin.cloudmusic.event;

/**
 * Created by ZJ on 2017/11/1 0001.
 * 语音控制事件
 */
public class SpeechContrlEvent {
    private int tag;
    private Object obj1;
    private Object obj2;
    private Object obj3;

    public SpeechContrlEvent(int tag, Object obj1) {
        this.tag = tag;
        this.obj1 = obj1;
    }

    public SpeechContrlEvent(int tag, Object obj1, Object obj2) {
        this.tag = tag;
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public SpeechContrlEvent(int tag, Object obj1, Object obj2, Object obj3) {
        this.tag = tag;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public Object getObj1() {
        return obj1;
    }

    public void setObj1(Object obj1) {
        this.obj1 = obj1;
    }

    public Object getObj2() {
        return obj2;
    }

    public void setObj2(Object obj2) {
        this.obj2 = obj2;
    }

    public Object getObj3() {
        return obj3;
    }

    public void setObj3(Object obj3) {
        this.obj3 = obj3;
    }

    @Override
    public String toString() {
        return "SpeechContrlEvent{" +
                "tag=" + tag +
                ", obj1=" + obj1 +
                ", obj2=" + obj2 +
                ", obj3=" + obj3 +
                '}';
    }
}
