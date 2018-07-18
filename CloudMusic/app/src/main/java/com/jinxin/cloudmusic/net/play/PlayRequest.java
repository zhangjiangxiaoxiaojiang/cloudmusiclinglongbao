package com.jinxin.cloudmusic.net.play;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.GroupBean.Play;
import com.jinxin.cloudmusic.bean.MusicBean.Bitrate;
import com.jinxin.cloudmusic.bean.MusicBean.SongInfo;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.BaseRequest;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;

/**
 * Created by ZJ on 2017/3/2 0002.
 * 播放
 * ?format=json&callback=?&from=webapp_music&method=baidu.ting.song.play&songid=523150864
 */
public class PlayRequest extends BaseJsonRequest<Play> {
    private String method;
    private String songid;
    public PlayRequest(ICallback<Play> callback,String method,String songid) {
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
        sb.append("&").append("method=baidu.ting.song.play");
        sb.append("&").append("songid=").append(songid);
        return sb.toString();
    }*/

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        if (response!=null&&response.containsKey("songinfo")&&response.containsKey("bitrate")){
            //解析歌曲信息
            JSONObject JoSongInfo=response.getJSONObject("songinfo");
            SongInfo songInfo= JSON.parseObject(JoSongInfo.toString(),SongInfo.class);

            //解析比特率
            JSONObject JoBitrate=response.getJSONObject("bitrate");
            Bitrate bitrate=JSON.parseObject(JoBitrate.toString(), Bitrate.class);

            Play play=new Play();
            play.setSongInfo(songInfo);
            play.setBitrate(bitrate);
            getCallback().onReceive(play);
        }
    }

    @Override
    public void onError(String error) {
        getCallback().onError(error);
        L.e(error);
    }
}
