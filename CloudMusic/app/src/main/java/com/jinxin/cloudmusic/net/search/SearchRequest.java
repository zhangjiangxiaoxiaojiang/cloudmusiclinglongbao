package com.jinxin.cloudmusic.net.search;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.GroupBean.Search;
import com.jinxin.cloudmusic.bean.MusicBean.Album;
import com.jinxin.cloudmusic.bean.MusicBean.Artist;
import com.jinxin.cloudmusic.bean.MusicBean.Song;
import com.jinxin.cloudmusic.constant.NetConstant;
import com.jinxin.cloudmusic.event.PosEvent;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


/**
 * Created by ZJ on 2017/3/2 0002.
 * 搜索
 * ?format=json&callback=?&from=webapp_music&method=baidu.ting.search.catalogSug&query=卢冠廷
 */
public class SearchRequest extends BaseJsonRequest<Search> {
    List<Album> albumList;//根据歌名请求时没有返回这个属性
    List<Artist> artistList;//时候歌曲歌名搜索没这个返回值

    private String method;
    private String query;

    public SearchRequest(ICallback<Search> callback, String method, String query) {
        super(callback);
        this.method = method;
        this.query = query;
    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        serviceContent.put("method", method);
        try {
            if (NetConstant.HTTP_ACCESSPATH.equals("http://192.168.60.102:8080/smarthome1/m-m/music3?")||NetConstant.HTTP_ACCESSPATH.equals("http://192.168.60.101:8080/smarthome1/m-m/music3?")) {
                serviceContent.put("query", URLEncoder.encode(query, "utf-8"));
            }//转换编码
            else {
                serviceContent.put("query", cnToUnicode(query));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return serviceContent;
    }

    private static String cnToUnicode(String cn) {
        char[] chars = cn.toCharArray();
        String returnStr = "";
        for (int i = 0; i < chars.length; i++) {
            returnStr += "\\u" + Integer.toString(chars[i], 16);
        }
        Log.e("search+",returnStr);
        return returnStr;
    }

   /* @Override
    public String getRequestText() {
        StringBuilder sb=new StringBuilder();
        sb.append("?format=json");
        sb.append("&").append("callback=?");
        sb.append("&").append("from=webapp_music");
        sb.append("&").append("method=baidu.ting.search.catalogSug");
        sb.append("&").append("query=").append(query);
        return sb.toString();
    }*/

    @Override
    public void onResponse(JSONObject response) throws JSONException {

        L.e(response.toString() + "返回结果");
        if (response != null && response.containsKey("song")/*&&response.containsKey("album")*/ /*&& response.containsKey("artist")*/) {
            //解析歌曲
            JSONArray jsonSong = response.getJSONArray("song");
            L.e(response.toString() + "返回结果");
            String a = jsonSong.toString();
            List<Song> songList = JSON.parseArray(jsonSong.toString(), Song.class);

            //解析专辑
            if (response.containsKey("album")) {
                JSONArray jsonAlbum = response.getJSONArray("album");
                albumList = JSON.parseArray(jsonAlbum.toString(), Album.class);
            }

            //解析歌手
            if (response.containsKey("artist")) {
                JSONArray jsonArtist = response.getJSONArray("artist");
                artistList = JSON.parseArray(jsonArtist.toString(), Artist.class);
            }

            Search search = new Search();
            search.setSongList(songList);
            if (response.containsKey("album")) {
                search.setAlbumList(albumList);
            }
            if (response.containsKey("artist")) {
                search.setArtistList(artistList);
            }
            getCallback().onReceive(search);
        }else {
            EventBus.getDefault().post(new PosEvent(512));
        }
    }

    @Override
    public void onError(String error) {
        getCallback().onError(error);
        L.e(error);
    }
}
