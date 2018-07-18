package com.jinxin.cloudmusic.bean.MusicBean;

import com.jinxin.cloudmusic.bean.BaseModel;

/**
 * Created by ZJ on 2017/3/1 0001.
 * 歌手信息
 */
public class Artist extends BaseModel
{
    private String yyr_artist;

    private String artistname;

    private String artistid;

    private String artistpic;

    private String weight;

    public void setYyr_artist(String yyr_artist){
        this.yyr_artist = yyr_artist;
    }
    public String getYyr_artist(){
        return this.yyr_artist;
    }
    public void setArtistname(String artistname){
        this.artistname = artistname;
    }
    public String getArtistname(){
        return this.artistname;
    }
    public void setArtistid(String artistid){
        this.artistid = artistid;
    }
    public String getArtistid(){
        return this.artistid;
    }
    public void setArtistpic(String artistpic){
        this.artistpic = artistpic;
    }
    public String getArtistpic(){
        return this.artistpic;
    }
    public void setWeight(String weight){
        this.weight = weight;
    }
    public String getWeight(){
        return this.weight;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "yyr_artist='" + yyr_artist + '\'' +
                ", artistname='" + artistname + '\'' +
                ", artistid='" + artistid + '\'' +
                ", artistpic='" + artistpic + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }
}