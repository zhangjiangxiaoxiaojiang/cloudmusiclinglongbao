package com.jinxin.cloudmusic.net.base;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.Header;

/**
 * Created by XTER on 2016/9/22.
 * 基类
 */
public abstract class BaseJsonRequestDemo<T> extends BaseRequest<T> {
	public BaseJsonRequestDemo(ICallback<T> callback) {
		super(callback);
	}

	@Override
	public String getRequestText() throws JSONException {
		Header header = createRequestHeader();
		JSONObject serviceContent = createRequestBody();
		JSONObject jsonObj = new JSONObject();

		jsonObj.put("serviceContent", serviceContent);

		jsonObj.put("origDomain", header.getOrigDomain());
		jsonObj.put("homeDomain", header.getHomeDomain());
		jsonObj.put("bipCode", header.getBipCode());
		jsonObj.put("bipVer", header.getBipVer());
		jsonObj.put("activityCode", header.getActivityCode());
		jsonObj.put("processTime", header.getProcessTime());
		jsonObj.put("testFlag", header.getTestFlag());
		jsonObj.put("actionCode", header.getActionCode());
		jsonObj.put("response", null);

		return "$data=" + jsonObj.toString();
	}
}
