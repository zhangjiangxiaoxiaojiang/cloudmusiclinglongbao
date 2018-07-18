package com.jinxin.cloudmusic.bean.GroupBean;

import com.jinxin.cloudmusic.bean.BaseModel;

/**
 * Created by ZJ on 2017/3/2 0002.
 * 获取歌词
 */
public class Lyrics extends BaseModel{
    private String title;
    private String lrcContent;

    public Lyrics() {
    }

    public Lyrics(String title, String lrcContent) {
        this.title = title;
        this.lrcContent = lrcContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLrcContent() {
        return lrcContent;
    }

    public void setLrcContent(String lrcContent) {
        this.lrcContent = lrcContent;
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "title='" + title + '\'' +
                ", lrcContent='" + lrcContent + '\'' +
                '}';
    }
}
