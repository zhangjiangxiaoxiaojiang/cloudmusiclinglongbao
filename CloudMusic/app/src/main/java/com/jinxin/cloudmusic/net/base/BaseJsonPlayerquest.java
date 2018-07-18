package com.jinxin.cloudmusic.net.base;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by ZJ on 2017/12/4 0004.
 */
public abstract class BaseJsonPlayerquest<T> extends BaseRequest<T> {
    public BaseJsonPlayerquest(ICallback<T> callback) {
        super(callback);
    }

    @Override
    public String getRequestText() throws JSONException {
        JSONObject jsonObj = createRequestBody();
        return "mac=" + jsonObj.getString("mac");
    }


}
