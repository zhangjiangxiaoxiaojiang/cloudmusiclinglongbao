package com.jinxin.cloudmusic.bean.MusicBean;

import com.jinxin.cloudmusic.bean.BaseModel;

/**
 * Created by ZJ on 2017/3/1 0001.
 * 歌曲资料
 */
public class Song extends BaseModel
{
    private String bitrate_fee;

    private String weight;

    private String songname;

    private String songid;

    private String has_mv;

    private String yyr_artist;

    private String artistname;

    private String resource_type_ext;

    private String resource_provider;

    private String control;

    private String encrypted_songid;

    public void setBitrate_fee(String bitrate_fee){
        this.bitrate_fee = bitrate_fee;
    }
    public String getBitrate_fee(){
        return this.bitrate_fee;
    }
    public void setWeight(String weight){
        this.weight = weight;
    }
    public String getWeight(){
        return this.weight;
    }
    public void setSongname(String songname){
        this.songname = songname;
    }
    public String getSongname(){
        return this.songname;
    }
    public void setSongid(String songid){
        this.songid = songid;
    }
    public String getSongid(){
        return this.songid;
    }
    public void setHas_mv(String has_mv){
        this.has_mv = has_mv;
    }
    public String getHas_mv(){
        return this.has_mv;
    }
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
    public void setResource_type_ext(String resource_type_ext){
        this.resource_type_ext = resource_type_ext;
    }
    public String getResource_type_ext(){
        return this.resource_type_ext;
    }
    public void setResource_provider(String resource_provider){
        this.resource_provider = resource_provider;
    }
    public String getResource_provider(){
        return this.resource_provider;
    }
    public void setControl(String control){
        this.control = control;
    }
    public String getControl(){
        return this.control;
    }
    public void setEncrypted_songid(String encrypted_songid){
        this.encrypted_songid = encrypted_songid;
    }
    public String getEncrypted_songid(){
        return this.encrypted_songid;
    }

    @Override
    public String toString() {
        return "Song{" +
                "bitrate_fee='" + bitrate_fee + '\'' +
                ", weight='" + weight + '\'' +
                ", songname='" + songname + '\'' +
                ", songid='" + songid + '\'' +
                ", has_mv='" + has_mv + '\'' +
                ", yyr_artist='" + yyr_artist + '\'' +
                ", artistname='" + artistname + '\'' +
                ", resource_type_ext='" + resource_type_ext + '\'' +
                ", resource_provider='" + resource_provider + '\'' +
                ", control='" + control + '\'' +
                ", encrypted_songid='" + encrypted_songid + '\'' +
                '}';
    }
}