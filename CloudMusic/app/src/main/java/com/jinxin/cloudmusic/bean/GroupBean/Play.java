package com.jinxin.cloudmusic.bean.GroupBean;

import com.jinxin.cloudmusic.bean.BaseModel;
import com.jinxin.cloudmusic.bean.MusicBean.Bitrate;
import com.jinxin.cloudmusic.bean.MusicBean.SongInfo;

/**
 * Created by ZJ on 2017/3/2 0002.
 * 播放
 */
public class Play extends BaseModel{
    private SongInfo songInfo;
    private Bitrate bitrate;

    public Play() {
    }

    public Play(SongInfo songInfo, Bitrate bitrate) {
        this.songInfo = songInfo;
        this.bitrate = bitrate;
    }

    public SongInfo getSongInfo() {
        return songInfo;
    }

    public void setSongInfo(SongInfo songInfo) {
        this.songInfo = songInfo;
    }

    public Bitrate getBitrate() {
        return bitrate;
    }

    public void setBitrate(Bitrate bitrate) {
        this.bitrate = bitrate;
    }

    @Override
    public String toString() {
        return "Play{" +
                "songInfo=" + songInfo +
                ", bitrate=" + bitrate +
                '}';
    }
}
