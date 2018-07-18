package com.jinxin.cloudmusic.net.login;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.Header;
import com.jinxin.cloudmusic.bean.User;
import com.jinxin.cloudmusic.constant.NetConstant;
import com.jinxin.cloudmusic.net.base.BaseJsonRequest;
import com.jinxin.cloudmusic.net.base.BaseJsonRequestDemo;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;
import com.jinxin.cloudmusic.util.SysUtil;

/**
 * Created by Administrator on 2016/12/23.
 * 登录请求
 */
public class LoginRequest extends BaseJsonRequestDemo<User> {
	private String account;
	private String password;

	public LoginRequest(ICallback<User> callback, String account, String password) {
		super(callback);
		this.account = account;
		this.password = password;
	}

	@Override
	public Header createRequestHeader() {
		return new Header(NetConstant.BS_ACCOUNT_MANAGER,
				NetConstant.TRD_ACCOUNT_LOGIN, NetConstant.ACTION_REQUEST,
				SysUtil.getNow2(), NetConstant.TEST_TURE);
	}

	@Override
	public JSONObject createRequestBody() throws JSONException {
		JSONObject serviceContent = new JSONObject();
		serviceContent.put("account", account);
		serviceContent.put("password", password);
		return serviceContent;
	}

	@Override
	public void onResponse(JSONObject response) throws JSONException {
		Log.e("-----",response.toString());
		if (response != null && response.containsKey("response")) {
			L.d(response.toString());
			JSONObject jo = response.getJSONObject("response");
			if (jo.getString("rspCode").equalsIgnoreCase("0000")) {
				JSONObject jo1 = response.getJSONObject("serviceContent");
//				if(!TextUtils.isEmpty(jo1.toString())){
//					DBM.getCurrentOrm().save(new LocalJsonData("user",jo1.toString(),SysUtil.getNow2()));
//				}
				User user = JSON.parseObject(jo1.toString(), User.class);
				getCallback().onReceive(user);
			} else
				getCallback().onError(jo.getString("rspDesc"));
		}
	}

	@Override
	public void onError(String error) {
		L.e(error);
	}
}
