package com.jinxin.cloudmusic.net.base;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by Administrator on 2017/2/28 0028.
 */
public class BaseMusicRequest<T> implements IRequest {
    private ICallback<T> callback = null;

    public BaseMusicRequest(ICallback<T> callback) {
        this.callback = callback;
    }

    protected ICallback<T> getCallback() {
        return callback;
    }


    @Override
    public String getRequestText() throws JSONException {
        return null;
    }

    @Override
    public void onResponse(JSONObject response) throws JSONException {

    }

    @Override
    public void onError(String error) {

    }
}
