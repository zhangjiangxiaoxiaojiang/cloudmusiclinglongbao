package com.jinxin.cloudmusic.net.singersonglist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.MusicBean.SongList;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.ICallback;

import java.util.List;


/**
 * Created by ZJ on 2017/3/2 0002.
 * 歌手歌曲列表
 * ?format=json&callback=?&from=webapp_music&method=baidu.ting.artist.getSongList&tinguid=1100&limits=6
 */
public class SingerSongListRequest extends BaseJsonRequest<SongList> {
    private String method;
    private String tinguid;
    private int limits;
    public SingerSongListRequest(ICallback<SongList> callback,String method,String tinguid,int limits) {
        super(callback);
        this.method=method;
        this.tinguid=tinguid;
        this.limits=limits;
    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        serviceContent.put("method", method);
        serviceContent.put("tinguid", tinguid);
        serviceContent.put("limits", limits);
        return serviceContent;
    }
    /*@Override
    public String getRequestText() {
        StringBuilder sb = new StringBuilder();
        sb.append("?format=json");
        sb.append("&").append("callback=?");
        sb.append("&").append("from=webapp_music");
        sb.append("&").append("method=baidu.ting.artist.getSongList");
        sb.append("&").append("tinguid=").append(tinguid);
        sb.append("&").append("limits=").append(limits);
        return sb.toString();
    }*/

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        if (response!=null&&response.containsKey("songlist")){
            //解析歌手歌曲列表
            JSONArray jaSongList=response.getJSONArray("songlist");
            List<SongList> songList= JSON.parseArray(jaSongList.toString(),SongList.class);
            getCallback().onReceive(songList);
        }
    }

    @Override
    public void onError(String error) {
        getCallback().onError(error);
    }
}
