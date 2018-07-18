package com.jinxin.cloudmusic.net.SongerInfo;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.MusicBean.SingerInformation;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.BaseRequest;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;

/**
 * Created by ZJ on 2017/3/2 0002.
 * 获取歌手信息
 * ?format=json&callback=?&from=webapp_music&method=baidu.ting.artist.getInfo&tinguid=1100
 */
public class SongerInfoRequest extends BaseJsonRequest<SingerInformation> {
    private String method;
    private String tinguid;

    public SongerInfoRequest(ICallback<SingerInformation> callback,String method, String tinguid) {
        super(callback);
        this.method=method;
        this.tinguid = tinguid;
    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        serviceContent.put("method",method);
        serviceContent.put("tinguid", tinguid);
        return serviceContent;
    }
    /*@Override
    public String getRequestText() {
        StringBuilder sb = new StringBuilder();
        sb.append("?format=json");
        sb.append("&").append("callback=?");
        sb.append("&").append("from=webapp_music");
        sb.append("&").append("method=baidu.ting.artist.getInfo");
        sb.append("&").append("tinguid=").append(tinguid);
        return sb.toString();
    }*/

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        if (response != null && response.containsKey("intro") && response.containsKey("name")) {
            String intro = response.getString("intro");
            String name = response.getString("name");
            SingerInformation singerInformation = new SingerInformation();
            singerInformation.setIntro(intro);
            singerInformation.setName(name);
            getCallback().onReceive(singerInformation);
        }
    }

    @Override
    public void onError(String error) {
        getCallback().onError(error);
        L.e(error);
    }
}
