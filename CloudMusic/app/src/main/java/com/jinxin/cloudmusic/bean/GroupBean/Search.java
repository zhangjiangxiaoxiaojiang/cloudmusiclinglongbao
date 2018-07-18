package com.jinxin.cloudmusic.bean.GroupBean;

import android.os.Parcel;
import android.os.Parcelable;

import com.jinxin.cloudmusic.bean.BaseModel;
import com.jinxin.cloudmusic.bean.MusicBean.Album;
import com.jinxin.cloudmusic.bean.MusicBean.Artist;
import com.jinxin.cloudmusic.bean.MusicBean.Song;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ZJ on 2017/3/2 0002.
 * 搜索
 */
public class Search extends BaseModel {
    private List<Song> songList;
    private List<Album> albumList;
    private List<Artist> artistList;

    public Search() {
    }

    public Search(List<Song> songList, List<Album> albumList, List<Artist> artistList) {
        this.songList = songList;
        this.albumList = albumList;
        this.artistList = artistList;
    }


    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }

    public List<Artist> getArtistList() {
        return artistList;
    }

    public void setArtistList(List<Artist> artistList) {
        this.artistList = artistList;
    }

    @Override
    public String toString() {
        return "Search{" +
                "songList=" + songList +
                ", albumList=" + albumList +
                ", artistList=" + artistList +
                '}';
    }


}
