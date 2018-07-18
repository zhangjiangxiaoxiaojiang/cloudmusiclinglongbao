package com.jinxin.cloudmusic.net.recommendlist;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.MusicBean.ResultList;
import com.jinxin.cloudmusic.bean.MusicBean.Result;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.BaseRequest;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;


/**
 * Created by ZJ on 2017/3/2 0002.
 * 推荐列表
 * ?format=json&callback=?&from=webapp_music&method=baidu.ting.song.getRecommandSongList&song_id=290008&num=10
 */
public class RecommendListRequest extends BaseJsonRequest<ResultList> {
    private String method;
    private String song_id;
    private int num;
    public RecommendListRequest(ICallback<ResultList> callback,String method,String song_id,int num) {
        super(callback);
        this.method=method;
        this.song_id=song_id;
        this.num=num;
    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        serviceContent.put("method", method);
        serviceContent.put("song_id", song_id);
        serviceContent.put("num", num);
        return serviceContent;
    }
    /*@Override
    public String getRequestText() {
        StringBuilder sb = new StringBuilder();
        sb.append("?format=json");
        sb.append("&").append("callback=?");
        sb.append("&").append("from=webapp_music");
        sb.append("&").append("method=baidu.ting.song.getRecommandSongList");
        sb.append("&").append("song_id=").append(song_id);
        sb.append("&").append("num=").append(num);
        return sb.toString();
    }*/

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        L.e(response.toString());
        if (response!=null&&response.containsKey("result")){
            JSONObject jsonObject=response.getJSONObject("result");
            Result result= JSON.parseObject(jsonObject.toString(), Result.class);
            JSONArray jsonArray=jsonObject.getJSONArray("list");
            java.util.List<ResultList> listList=JSON.parseArray(jsonArray.toString(),ResultList.class);
            getCallback().onReceive(listList);
        }
    }

    @Override
    public void onError(String error) {
        getCallback().onError(error);
        L.e(error);
    }
}
