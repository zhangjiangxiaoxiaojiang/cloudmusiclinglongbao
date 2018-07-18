package com.jinxin.cloudmusic.event;

/**
 * Created by ZJ on 2017/3/8 0008.
 * 传输数据源
 */
public class DataEvent {
    private int datas;
    private Object obj;

    public DataEvent(int datas, Object obj) {
        this.datas = datas;
        this.obj = obj;
    }

    public int getDatas() {
        return datas;
    }

    public void setDatas(int datas) {
        this.datas = datas;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
