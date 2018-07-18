package com.jinxin.cloudmusic.bean.MusicBean;

import com.jinxin.cloudmusic.bean.BaseModel;

/**
 * Created by ZJ on 2017/3/1 0001.
 * 榜单里
 */
public class Billboard extends BaseModel
{
    private String billboard_type;

    private String billboard_no;

    private String update_date;

    private String billboard_songnum;

    private int havemore;

    private String name;

    private String comment;

    private String pic_s640;

    private String pic_s444;

    private String pic_s260;

    private String pic_s210;

    private String web_url;

    public void setBillboard_type(String billboard_type){
        this.billboard_type = billboard_type;
    }
    public String getBillboard_type(){
        return this.billboard_type;
    }
    public void setBillboard_no(String billboard_no){
        this.billboard_no = billboard_no;
    }
    public String getBillboard_no(){
        return this.billboard_no;
    }
    public void setUpdate_date(String update_date){
        this.update_date = update_date;
    }
    public String getUpdate_date(){
        return this.update_date;
    }
    public void setBillboard_songnum(String billboard_songnum){
        this.billboard_songnum = billboard_songnum;
    }
    public String getBillboard_songnum(){
        return this.billboard_songnum;
    }
    public void setHavemore(int havemore){
        this.havemore = havemore;
    }
    public int getHavemore(){
        return this.havemore;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    public String getComment(){
        return this.comment;
    }
    public void setPic_s640(String pic_s640){
        this.pic_s640 = pic_s640;
    }
    public String getPic_s640(){
        return this.pic_s640;
    }
    public void setPic_s444(String pic_s444){
        this.pic_s444 = pic_s444;
    }
    public String getPic_s444(){
        return this.pic_s444;
    }
    public void setPic_s260(String pic_s260){
        this.pic_s260 = pic_s260;
    }
    public String getPic_s260(){
        return this.pic_s260;
    }
    public void setPic_s210(String pic_s210){
        this.pic_s210 = pic_s210;
    }
    public String getPic_s210(){
        return this.pic_s210;
    }
    public void setWeb_url(String web_url){
        this.web_url = web_url;
    }
    public String getWeb_url(){
        return this.web_url;
    }

    @Override
    public String toString() {
        return "Billboard{" +
                "billboard_type='" + billboard_type + '\'' +
                ", billboard_no='" + billboard_no + '\'' +
                ", update_date='" + update_date + '\'' +
                ", billboard_songnum='" + billboard_songnum + '\'' +
                ", havemore=" + havemore +
                ", name='" + name + '\'' +
                ", comment='" + comment + '\'' +
                ", pic_s640='" + pic_s640 + '\'' +
                ", pic_s444='" + pic_s444 + '\'' +
                ", pic_s260='" + pic_s260 + '\'' +
                ", pic_s210='" + pic_s210 + '\'' +
                ", web_url='" + web_url + '\'' +
                '}';
    }
}