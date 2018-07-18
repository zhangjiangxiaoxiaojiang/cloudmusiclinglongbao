package com.jinxin.cloudmusic.net.base;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;
import com.jinxin.cloudmusic.util.L;

/**
 * Created by Administrator on 2017/2/28 0028.
 */
public class FastMusicRequest extends JsonRequest<String> {
    private final Response.Listener<String> mListener;


    public FastMusicRequest(int method, String url, String requestBody, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
        mListener = listener;
        L.d(url + "?" + requestBody);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        return null;
    }
}
