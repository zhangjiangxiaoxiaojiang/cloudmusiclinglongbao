package com.jinxin.cloudmusic.bean.MusicBean;

import com.jinxin.cloudmusic.bean.BaseModel;

/**
 * Created by ZJ on 2017/3/1 0001.
 * 专辑
 */
public class Album extends BaseModel
{
    private String albumname;

    private String weight;

    private String artistname;

    private String resource_type_ext;

    private String artistpic;

    private String albumid;

    public void setAlbumname(String albumname){
        this.albumname = albumname;
    }
    public String getAlbumname(){
        return this.albumname;
    }
    public void setWeight(String weight){
        this.weight = weight;
    }
    public String getWeight(){
        return this.weight;
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
    public void setArtistpic(String artistpic){
        this.artistpic = artistpic;
    }
    public String getArtistpic(){
        return this.artistpic;
    }
    public void setAlbumid(String albumid){
        this.albumid = albumid;
    }
    public String getAlbumid(){
        return this.albumid;
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumname='" + albumname + '\'' +
                ", weight='" + weight + '\'' +
                ", artistname='" + artistname + '\'' +
                ", resource_type_ext='" + resource_type_ext + '\'' +
                ", artistpic='" + artistpic + '\'' +
                ", albumid='" + albumid + '\'' +
                '}';
    }
}