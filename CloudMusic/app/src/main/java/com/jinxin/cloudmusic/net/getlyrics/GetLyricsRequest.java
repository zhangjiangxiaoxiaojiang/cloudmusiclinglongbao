package com.jinxin.cloudmusic.net.getlyrics;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.GroupBean.Lyrics;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.BaseRequest;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;

/**
 * Created by ZJ on 2017/3/2 0002.
 * 获取歌词
 * ?format=json&callback=?&from=webapp_music&method=baidu.ting.song.lry&songid=877578
 */
public class GetLyricsRequest extends BaseJsonRequest<Lyrics> {
    private String method;
    private String songid;

    public GetLyricsRequest(ICallback<Lyrics> callback,String method,String songid) {
        super(callback);
        this.method=method;
        this.songid=songid;
    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        serviceContent.put("method",method);
        serviceContent.put("songid", songid);
        return serviceContent;
    }
    /*@Override
    public String getRequestText() {
        StringBuilder sb=new StringBuilder();
        sb.append("?format=json");
        sb.append("&").append("callback=?");
        sb.append("&").append("from=webapp_music");
        sb.append("&").append("method=baidu.ting.song.lry");
        sb.append("&").append("songid=").append(songid);
        return sb.toString();
    }*/

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        if (response != null && response.containsKey("title") && response.containsKey("lrcContent")) {
            String title=response.getString("title");
            String lrcContent=response.getString("lrcContent");
            Lyrics lyrics=new Lyrics();
            lyrics.setTitle(title);
            lyrics.setLrcContent(lrcContent);
            getCallback().onReceive(lyrics);
        }
    }

    @Override
    public void onError(String error) {
        getCallback().onError(error);
        L.e(error);
    }
}
