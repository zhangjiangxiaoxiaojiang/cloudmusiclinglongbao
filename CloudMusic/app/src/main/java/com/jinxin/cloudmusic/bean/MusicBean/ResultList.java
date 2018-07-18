package com.jinxin.cloudmusic.bean.MusicBean;

import com.jinxin.cloudmusic.bean.BaseModel;

/**
 * Created by ZJ on 2017/3/1 0001.
 * 推荐列表
 */
public class ResultList extends BaseModel
{
    private String artist_id;

    private String all_artist_id;

    private String all_artist_ting_uid;

    private String language;

    private String publishtime;

    private String album_no;

    private String toneid;

    private String all_rate;

    private String pic_small;

    private String pic_big;

    private String hot;

    private int has_mv_mobile;

    private String versions;

    private String bitrate_fee;

    private String song_id;

    private String title;

    private String ting_uid;

    private String author;

    private String album_id;

    private String album_title;

    private int is_first_publish;

    private int havehigh;

    private int charge;

    private int has_mv;

    private int learn;

    private String song_source;

    private String piao_id;

    private String korean_bb_song;

    private String resource_type_ext;

    private String mv_provider;

    public void setArtist_id(String artist_id){
        this.artist_id = artist_id;
    }
    public String getArtist_id(){
        return this.artist_id;
    }
    public void setAll_artist_id(String all_artist_id){
        this.all_artist_id = all_artist_id;
    }
    public String getAll_artist_id(){
        return this.all_artist_id;
    }
    public void setAll_artist_ting_uid(String all_artist_ting_uid){
        this.all_artist_ting_uid = all_artist_ting_uid;
    }
    public String getAll_artist_ting_uid(){
        return this.all_artist_ting_uid;
    }
    public void setLanguage(String language){
        this.language = language;
    }
    public String getLanguage(){
        return this.language;
    }
    public void setPublishtime(String publishtime){
        this.publishtime = publishtime;
    }
    public String getPublishtime(){
        return this.publishtime;
    }
    public void setAlbum_no(String album_no){
        this.album_no = album_no;
    }
    public String getAlbum_no(){
        return this.album_no;
    }
    public void setToneid(String toneid){
        this.toneid = toneid;
    }
    public String getToneid(){
        return this.toneid;
    }
    public void setAll_rate(String all_rate){
        this.all_rate = all_rate;
    }
    public String getAll_rate(){
        return this.all_rate;
    }
    public void setPic_small(String pic_small){
        this.pic_small = pic_small;
    }
    public String getPic_small(){
        return this.pic_small;
    }
    public void setPic_big(String pic_big){
        this.pic_big = pic_big;
    }
    public String getPic_big(){
        return this.pic_big;
    }
    public void setHot(String hot){
        this.hot = hot;
    }
    public String getHot(){
        return this.hot;
    }
    public void setHas_mv_mobile(int has_mv_mobile){
        this.has_mv_mobile = has_mv_mobile;
    }
    public int getHas_mv_mobile(){
        return this.has_mv_mobile;
    }
    public void setVersions(String versions){
        this.versions = versions;
    }
    public String getVersions(){
        return this.versions;
    }
    public void setBitrate_fee(String bitrate_fee){
        this.bitrate_fee = bitrate_fee;
    }
    public String getBitrate_fee(){
        return this.bitrate_fee;
    }
    public void setSong_id(String song_id){
        this.song_id = song_id;
    }
    public String getSong_id(){
        return this.song_id;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTing_uid(String ting_uid){
        this.ting_uid = ting_uid;
    }
    public String getTing_uid(){
        return this.ting_uid;
    }
    public void setAuthor(String author){
        this.author = author;
    }
    public String getAuthor(){
        return this.author;
    }
    public void setAlbum_id(String album_id){
        this.album_id = album_id;
    }
    public String getAlbum_id(){
        return this.album_id;
    }
    public void setAlbum_title(String album_title){
        this.album_title = album_title;
    }
    public String getAlbum_title(){
        return this.album_title;
    }
    public void setIs_first_publish(int is_first_publish){
        this.is_first_publish = is_first_publish;
    }
    public int getIs_first_publish(){
        return this.is_first_publish;
    }
    public void setHavehigh(int havehigh){
        this.havehigh = havehigh;
    }
    public int getHavehigh(){
        return this.havehigh;
    }
    public void setCharge(int charge){
        this.charge = charge;
    }
    public int getCharge(){
        return this.charge;
    }
    public void setHas_mv(int has_mv){
        this.has_mv = has_mv;
    }
    public int getHas_mv(){
        return this.has_mv;
    }
    public void setLearn(int learn){
        this.learn = learn;
    }
    public int getLearn(){
        return this.learn;
    }
    public void setSong_source(String song_source){
        this.song_source = song_source;
    }
    public String getSong_source(){
        return this.song_source;
    }
    public void setPiao_id(String piao_id){
        this.piao_id = piao_id;
    }
    public String getPiao_id(){
        return this.piao_id;
    }
    public void setKorean_bb_song(String korean_bb_song){
        this.korean_bb_song = korean_bb_song;
    }
    public String getKorean_bb_song(){
        return this.korean_bb_song;
    }
    public void setResource_type_ext(String resource_type_ext){
        this.resource_type_ext = resource_type_ext;
    }
    public String getResource_type_ext(){
        return this.resource_type_ext;
    }
    public void setMv_provider(String mv_provider){
        this.mv_provider = mv_provider;
    }
    public String getMv_provider(){
        return this.mv_provider;
    }

    @Override
    public String toString() {
        return "ResultList{" +
                "artist_id='" + artist_id + '\'' +
                ", all_artist_id='" + all_artist_id + '\'' +
                ", all_artist_ting_uid='" + all_artist_ting_uid + '\'' +
                ", language='" + language + '\'' +
                ", publishtime=" + publishtime +
                ", album_no='" + album_no + '\'' +
                ", toneid='" + toneid + '\'' +
                ", all_rate='" + all_rate + '\'' +
                ", pic_small='" + pic_small + '\'' +
                ", pic_big='" + pic_big + '\'' +
                ", hot='" + hot + '\'' +
                ", has_mv_mobile=" + has_mv_mobile +
                ", versions='" + versions + '\'' +
                ", bitrate_fee='" + bitrate_fee + '\'' +
                ", song_id='" + song_id + '\'' +
                ", title='" + title + '\'' +
                ", ting_uid='" + ting_uid + '\'' +
                ", author='" + author + '\'' +
                ", album_id='" + album_id + '\'' +
                ", album_title='" + album_title + '\'' +
                ", is_first_publish=" + is_first_publish +
                ", havehigh=" + havehigh +
                ", charge=" + charge +
                ", has_mv=" + has_mv +
                ", learn=" + learn +
                ", song_source='" + song_source + '\'' +
                ", piao_id='" + piao_id + '\'' +
                ", korean_bb_song='" + korean_bb_song + '\'' +
                ", resource_type_ext='" + resource_type_ext + '\'' +
                ", mv_provider='" + mv_provider + '\'' +
                '}';
    }
}
