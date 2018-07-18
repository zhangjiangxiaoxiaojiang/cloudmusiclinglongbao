package com.jinxin.cloudmusic.bean.GroupBean;

import android.graphics.Bitmap;

/**
 * Created by ZJ on 2017/3/16 0016.
 */
public class MainData {
    private int prass;//当前int值
    private int sum;//总int值
    private String formatTimeCurentTime;//歌曲当前时间
    private String formatTimeTotalTime;//歌曲总时间
    private boolean playState;//播放状态
    private Play play;//当前播放音乐详情
    private Bitmap bm;//本地音乐的图片
    private String musicName;//本地音乐名字

    //操作在线的构造参数
    public MainData(Play play,int prass, boolean playState, String formatTimeTotalTime, String formatTimeCurentTime, int sum) {//用于播放在线音乐
        this.play=play;
        this.prass = prass;
        this.playState = playState;
        this.formatTimeTotalTime = formatTimeTotalTime;
        this.formatTimeCurentTime = formatTimeCurentTime;
        this.sum = sum;
    }

    //操作本地的构造参数
    public MainData(String musicName,Bitmap bm,int prass, boolean playState, String formatTimeTotalTime, String formatTimeCurentTime, int sum) {//用于播放本地音乐
        this.musicName=musicName;
        this.bm=bm;
        this.prass = prass;
        this.sum = sum;
        this.formatTimeCurentTime = formatTimeCurentTime;
        this.playState = playState;
        this.formatTimeTotalTime = formatTimeTotalTime;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }

    public int getPrass() {
        return prass;
    }

    public void setPrass(int prass) {
        this.prass = prass;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getFormatTimeCurentTime() {
        return formatTimeCurentTime;
    }

    public void setFormatTimeCurentTime(String formatTimeCurentTime) {
        this.formatTimeCurentTime = formatTimeCurentTime;
    }

    public String getFormatTimeTotalTime() {
        return formatTimeTotalTime;
    }

    public void setFormatTimeTotalTime(String formatTimeTotalTime) {
        this.formatTimeTotalTime = formatTimeTotalTime;
    }

    public boolean isPlayState() {
        return playState;
    }

    public void setPlayState(boolean playState) {
        this.playState = playState;
    }

    public Play getPlay() {
        return play;
    }

    public void setPlay(Play play) {
        this.play = play;
    }

    @Override
    public String toString() {
        return "MainData{" +
                "prass=" + prass +
                ", sum=" + sum +
                ", formatTimeCurentTime='" + formatTimeCurentTime + '\'' +
                ", formatTimeTotalTime='" + formatTimeTotalTime + '\'' +
                ", playState=" + playState +
                ", play=" + play +
                '}';
    }
}
