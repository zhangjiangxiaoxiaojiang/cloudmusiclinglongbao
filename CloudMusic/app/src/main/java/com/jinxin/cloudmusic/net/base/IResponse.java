package com.jinxin.cloudmusic.net.base;

import org.json.JSONObject;

/**
 * @author Huang
 * @date 2015-12-16 上午10:42:15
 * @version V1.0
 */
public interface IResponse {
	void parseResponse(JSONObject obj);
	void onFail(String error);
	void onError(String error);
}
