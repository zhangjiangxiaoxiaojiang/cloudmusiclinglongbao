package com.jinxin.cloudmusic.net.base;


import org.json.JSONObject;

public class LoginResponse extends AbstractResponse<String> {

	public LoginResponse(ICallback<String> callback) {
		super(callback);
	}

	@Override
	public void parseResponse(JSONObject obj) {
		String str = obj.toString();
		getCallback().onReceive(str);
	}

	@Override
	public void onError(String error) {

	}

	@Override
	public void onFail(String error) {
		//getCallback().onFail(error);
	}

}
