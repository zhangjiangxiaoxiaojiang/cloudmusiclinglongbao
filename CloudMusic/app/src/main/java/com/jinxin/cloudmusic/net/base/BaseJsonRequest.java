package com.jinxin.cloudmusic.net.base;



import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.Header;

/**
 * Created by XTER on 2016/9/22.
 * 基类
 */
public abstract class BaseJsonRequest<T> extends BaseRequest<T> {
	public BaseJsonRequest(ICallback<T> callback) {
		super(callback);
	}

	@Override
	public String getRequestText() throws JSONException {
		JSONObject jsonObj = createRequestBody();
		return "$data=" + jsonObj.toString();
	}
}
