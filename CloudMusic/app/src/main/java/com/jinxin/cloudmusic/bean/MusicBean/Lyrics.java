package com.jinxin.cloudmusic.bean.MusicBean;

import com.jinxin.cloudmusic.bean.BaseModel;
import com.litesuits.orm.db.annotation.Table;

/**
 * Created by ZJ on 2017/3/1 0001.
 * 获取歌词
 */
@Table("Lyrics")
public class Lyrics extends BaseModel{
    //原本为root类实意为歌词属性
    private String title;
    private String lrcContent;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setLrcContent(String lrcContent) {
        this.lrcContent = lrcContent;
    }

    public String getLrcContent() {
        return this.lrcContent;
    }

    @Override
    public String toString() {
        return "Lyrics{" +
                "title='" + title + '\'' +
                ", lrcContent='" + lrcContent + '\'' +
                '}';
    }
}
