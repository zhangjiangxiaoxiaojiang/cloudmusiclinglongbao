package com.jinxin.cloudmusic.bean.MusicBean;

import java.util.List;
public class Result
{
    private List<ResultList> list;

    public void setList(List<ResultList> list){
        this.list = list;
    }
    public List<ResultList> getList(){
        return this.list;
    }

    @Override
    public String toString() {
        return "Result{" +
                "list=" + list +
                '}';
    }
}