package com.jinxin.cloudmusic.net.power;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.AcountProduct;
import com.jinxin.cloudmusic.bean.CustomerProduct;
import com.jinxin.cloudmusic.bean.Header;
import com.jinxin.cloudmusic.bean.MusicBean.Result;
import com.jinxin.cloudmusic.bean.MusicBean.ResultList;
import com.jinxin.cloudmusic.bean.User;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.BaseJsonRequestDemo;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.net.config.DateTimeUtils;
import com.jinxin.cloudmusic.net.config.NetConfigs;
import com.jinxin.cloudmusic.util.L;

import java.util.List;


/**
 * Created by ZJ on 2017/3/2 0002.
 * 推荐列表
 * ?format=json&callback=?&from=webapp_music&method=baidu.ting.song.getRecommandSongList&song_id=290008&num=10
 */
public class PowerRequest extends BaseJsonRequestDemo<AcountProduct> {
    private String account;
    private String secretKey;
    private String code;
    /*public PowerRequest(ICallback<AcountProduct> callback) {
        super(callback);
    }*/
    /*private String bipCode;
    private String actionCode;*/
    /*public PowerRequest(ICallback<ResultList> callback, String bipCode, String actionCode) {
        super(callback);
        *//*this.bipCode=bipCode;
        this.actionCode=actionCode;*//*
    }*/

    public PowerRequest(ICallback<AcountProduct> callback, String account, String secretKey) {
        super(callback);
        this.account = account;
        this.secretKey = secretKey;
    }

    public PowerRequest(ICallback<AcountProduct> callback, String account, String secretKey,String code) {
        super(callback);
        this.account = account;
        this.secretKey = secretKey;
        this.code=code;
    }

    @Override
    public Header createRequestHeader() {

        return new Header(NetConfigs.BS_USER_MANAGER,
                NetConfigs.TRD_CUSTOMER_PATTERN_LIST, NetConfigs.ACTION_REQUEST,
                DateTimeUtils.getSystemCurrentTime(), NetConfigs.TEST_TRUE);

    }

    @Override
    public JSONObject createRequestBody() throws JSONException {
        /*JSONObject serviceContent = new JSONObject();
        serviceContent.put("method", method);
        serviceContent.put("song_id", song_id);
        serviceContent.put("num", num);
        return serviceContent;*/
        //*String _secretKey = SharedDB.loadStrFromDB(SharedDB.ORDINARY_CONSTANTS, NetConfigs.KEY_SECRETKEY,"");
        //String _account = CommUtil.getCurrentLoginAccount();
        //String _updateTime = SharedDB.loadStrFromDB(_account, NetConfigs.KEY_CUSTOMER_PRODUCT_LIST,NetConfigs.DEFAULT_UPDATE_TIME);*//*
        JSONObject serviceContent = new JSONObject();
        if (null==code){//默认传值
            serviceContent.put("code", "072");
        }else {//主动传值
            serviceContent.put("code", code);
        }
        serviceContent.put("account",account);
        serviceContent.put("secretKey",secretKey);
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
        if (response != null && response.containsKey("response")) {
            L.d(response.toString());
            JSONObject jo = response.getJSONObject("response");
            if (jo.getString("rspCode").equalsIgnoreCase("0000")) {
                JSONArray jo1 = response.getJSONArray("serviceContent");
                List<AcountProduct> acountProductList = JSON.parseArray(jo1.toString(), AcountProduct.class);
                getCallback().onReceive(acountProductList);
            } else
                getCallback().onError(jo.getString("rspDesc"));
        }
    }

    @Override
    public void onError(String error) {
        getCallback().onError(error);
        L.e(error);
    }
}
