package com.jinxin.cloudmusic.net.musiclist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.GroupBean.BillboardList;
import com.jinxin.cloudmusic.bean.MusicBean.Billboard;
import com.jinxin.cloudmusic.bean.MusicBean.SongList;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.BaseRequest;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;

import java.util.List;


/**
 * Created by XTER on 2017/2/28.
 * format=json&callback=?&from=webapp_music&method=baidu.ting.billboard.billList&type=1&size=10&offset=0
 */
public class MusicDemoRequest extends BaseJsonRequest<BillboardList> {
    private String method;
    private int type;
    private int size;
    private int offset;

    public MusicDemoRequest(ICallback<BillboardList> callback, String method, int type, int size, int offset) {
        super(callback);
        this.method = method;
        this.type = type;
        this.size = size;
        this.offset = offset;
    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        serviceContent.put("method", method);
        serviceContent.put("type", type);
        serviceContent.put("size", size);
        serviceContent.put("offset", offset);
        return serviceContent;
    }

	/*@Override
    public String getRequestText() {
		*//*StringBuilder sb = new StringBuilder();
		sb.append("?format=json");
		sb.append("&").append("callback=?");
		sb.append("&").append("from=webapp_music");
		sb.append("&").append("method=baidu.ting.billboard.billList");
		sb.append("&").append("type=").append(type);
		sb.append("&").append("size=").append(size);
		sb.append("&").append("offset=").append(offset);
		return sb.toString();*//*


	}*/

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        if (response != null && response.containsKey("song_list")) {
            L.e("hhhhhhh");
            //解析榜单
            JSONArray ja = response.getJSONArray("song_list");
            String a = ja.toString();
            List<SongList> songs = JSON.parseArray(ja.toString(), SongList.class);

            //解析广告单
            JSONObject jsBillboar = response.getJSONObject("billboard");
            String b = jsBillboar.toString();
            Billboard billboard = JSON.parseObject(jsBillboar.toString(), Billboard.class);

            BillboardList billboardList = new BillboardList();
            billboardList.setLists(songs);
            billboardList.setBillboard(billboard);
            getCallback().onReceive(billboardList);
        }
    }

    @Override
    public void onError(String error) {
        L.e(error);
        getCallback().onError(error);
    }
}
