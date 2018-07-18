package com.jinxin.cloudmusic.bean.MusicBean;

import java.util.List;
public class Root
{
    private List<Song> song;

    private List<Album> album;

    private String order;

    private int error_code;

    private List<Artist> artist;

    public void setSong(List<Song> song){
        this.song = song;
    }
    public List<Song> getSong(){
        return this.song;
    }
    public void setAlbum(List<Album> album){
        this.album = album;
    }
    public List<Album> getAlbum(){
        return this.album;
    }
    public void setOrder(String order){
        this.order = order;
    }
    public String getOrder(){
        return this.order;
    }
    public void setError_code(int error_code){
        this.error_code = error_code;
    }
    public int getError_code(){
        return this.error_code;
    }
    public void setArtist(List<Artist> artist){
        this.artist = artist;
    }
    public List<Artist> getArtist(){
        return this.artist;
    }

    @Override
    public String toString() {
        return "AcountProduct{" +
                "song=" + song +
                ", album=" + album +
                ", order='" + order + '\'' +
                ", error_code=" + error_code +
                ", artist=" + artist +
                '}';
    }
}
