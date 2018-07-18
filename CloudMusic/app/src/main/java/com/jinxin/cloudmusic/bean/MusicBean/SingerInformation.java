package com.jinxin.cloudmusic.bean.MusicBean;

import com.jinxin.cloudmusic.bean.BaseModel;

/**
 * Created by ZJ on 2017/3/1 0001.
 * 歌手信息
 */
public class SingerInformation extends BaseModel{
    private String weight;

    private String nickname;

    private String ting_uid;

    private String stature;

    private String avatar_s500;

    private String source;

    private String url;

    private int collect_num;

    private int comment_num;

    private String area;

    private String avatar_mini;

    private String firstchar;

    private String avatar_s180;

    private String gender;

    private String avatar_small;

    private String albums_total;

    private String artist_id;

    private String constellation;

    private int is_collect;

    private String intro;

    private String aliasname;

    private String country;

    private int share_num;

    private String bloodtype;

    private String avatar_middle;

    private int mv_total;

    private String songs_total;

    private String birth;

    private String avatar_s1000;

    private String piao_id;

    private String listen_num;

    private String avatar_big;

    private String name;

    private String company;

    public void setWeight(String weight){
        this.weight = weight;
    }
    public String getWeight(){
        return this.weight;
    }
    public void setNickname(String nickname){
        this.nickname = nickname;
    }
    public String getNickname(){
        return this.nickname;
    }
    public void setTing_uid(String ting_uid){
        this.ting_uid = ting_uid;
    }
    public String getTing_uid(){
        return this.ting_uid;
    }
    public void setStature(String stature){
        this.stature = stature;
    }
    public String getStature(){
        return this.stature;
    }
    public void setAvatar_s500(String avatar_s500){
        this.avatar_s500 = avatar_s500;
    }
    public String getAvatar_s500(){
        return this.avatar_s500;
    }
    public void setSource(String source){
        this.source = source;
    }
    public String getSource(){
        return this.source;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }
    public void setCollect_num(int collect_num){
        this.collect_num = collect_num;
    }
    public int getCollect_num(){
        return this.collect_num;
    }
    public void setComment_num(int comment_num){
        this.comment_num = comment_num;
    }
    public int getComment_num(){
        return this.comment_num;
    }
    public void setArea(String area){
        this.area = area;
    }
    public String getArea(){
        return this.area;
    }
    public void setAvatar_mini(String avatar_mini){
        this.avatar_mini = avatar_mini;
    }
    public String getAvatar_mini(){
        return this.avatar_mini;
    }
    public void setFirstchar(String firstchar){
        this.firstchar = firstchar;
    }
    public String getFirstchar(){
        return this.firstchar;
    }
    public void setAvatar_s180(String avatar_s180){
        this.avatar_s180 = avatar_s180;
    }
    public String getAvatar_s180(){
        return this.avatar_s180;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    public String getGender(){
        return this.gender;
    }
    public void setAvatar_small(String avatar_small){
        this.avatar_small = avatar_small;
    }
    public String getAvatar_small(){
        return this.avatar_small;
    }
    public void setAlbums_total(String albums_total){
        this.albums_total = albums_total;
    }
    public String getAlbums_total(){
        return this.albums_total;
    }
    public void setArtist_id(String artist_id){
        this.artist_id = artist_id;
    }
    public String getArtist_id(){
        return this.artist_id;
    }
    public void setConstellation(String constellation){
        this.constellation = constellation;
    }
    public String getConstellation(){
        return this.constellation;
    }
    public void setIs_collect(int is_collect){
        this.is_collect = is_collect;
    }
    public int getIs_collect(){
        return this.is_collect;
    }
    public void setIntro(String intro){
        this.intro = intro;
    }
    public String getIntro(){
        return this.intro;
    }
    public void setAliasname(String aliasname){
        this.aliasname = aliasname;
    }
    public String getAliasname(){
        return this.aliasname;
    }
    public void setCountry(String country){
        this.country = country;
    }
    public String getCountry(){
        return this.country;
    }
    public void setShare_num(int share_num){
        this.share_num = share_num;
    }
    public int getShare_num(){
        return this.share_num;
    }
    public void setBloodtype(String bloodtype){
        this.bloodtype = bloodtype;
    }
    public String getBloodtype(){
        return this.bloodtype;
    }
    public void setAvatar_middle(String avatar_middle){
        this.avatar_middle = avatar_middle;
    }
    public String getAvatar_middle(){
        return this.avatar_middle;
    }
    public void setMv_total(int mv_total){
        this.mv_total = mv_total;
    }
    public int getMv_total(){
        return this.mv_total;
    }
    public void setSongs_total(String songs_total){
        this.songs_total = songs_total;
    }
    public String getSongs_total(){
        return this.songs_total;
    }
    public void setBirth(String birth){
        this.birth = birth;
    }
    public String getBirth(){
        return this.birth;
    }
    public void setAvatar_s1000(String avatar_s1000){
        this.avatar_s1000 = avatar_s1000;
    }
    public String getAvatar_s1000(){
        return this.avatar_s1000;
    }
    public void setPiao_id(String piao_id){
        this.piao_id = piao_id;
    }
    public String getPiao_id(){
        return this.piao_id;
    }
    public void setListen_num(String listen_num){
        this.listen_num = listen_num;
    }
    public String getListen_num(){
        return this.listen_num;
    }
    public void setAvatar_big(String avatar_big){
        this.avatar_big = avatar_big;
    }
    public String getAvatar_big(){
        return this.avatar_big;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setCompany(String company){
        this.company = company;
    }
    public String getCompany(){
        return this.company;
    }

    @Override
    public String toString() {
        return "SingerInformation{" +
                "weight='" + weight + '\'' +
                ", nickname='" + nickname + '\'' +
                ", ting_uid='" + ting_uid + '\'' +
                ", stature='" + stature + '\'' +
                ", avatar_s500='" + avatar_s500 + '\'' +
                ", source='" + source + '\'' +
                ", url='" + url + '\'' +
                ", collect_num=" + collect_num +
                ", comment_num=" + comment_num +
                ", area='" + area + '\'' +
                ", avatar_mini='" + avatar_mini + '\'' +
                ", firstchar='" + firstchar + '\'' +
                ", avatar_s180='" + avatar_s180 + '\'' +
                ", gender='" + gender + '\'' +
                ", avatar_small='" + avatar_small + '\'' +
                ", albums_total='" + albums_total + '\'' +
                ", artist_id='" + artist_id + '\'' +
                ", constellation='" + constellation + '\'' +
                ", is_collect=" + is_collect +
                ", intro='" + intro + '\'' +
                ", aliasname='" + aliasname + '\'' +
                ", country='" + country + '\'' +
                ", share_num=" + share_num +
                ", bloodtype='" + bloodtype + '\'' +
                ", avatar_middle='" + avatar_middle + '\'' +
                ", mv_total=" + mv_total +
                ", songs_total='" + songs_total + '\'' +
                ", birth='" + birth + '\'' +
                ", avatar_s1000='" + avatar_s1000 + '\'' +
                ", piao_id='" + piao_id + '\'' +
                ", listen_num='" + listen_num + '\'' +
                ", avatar_big='" + avatar_big + '\'' +
                ", name='" + name + '\'' +
                ", company='" + company + '\'' +
                '}';
    }
}
