package com.jinxin.cloudmusic.net.player;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.bean.User;
import com.jinxin.cloudmusic.net.base.BaseJsonPlayerquest;
import com.jinxin.cloudmusic.net.base.ICallback;
import com.jinxin.cloudmusic.util.L;

/**
 * Created by ZJ on 2017/12/4 0004.
 * 播放器
 */
public class PlayerRequest extends BaseJsonPlayerquest<User> {
    private String mac;

    public PlayerRequest(ICallback<User> callback, String mac) {
        super(callback);
        this.mac = mac;
    }

    /*@Override
    public Header createRequestHeader() {
        return new Header(NetConstant.BS_ACCOUNT_MANAGER,
                NetConstant.TRD_ACCOUNT_LOGIN, NetConstant.ACTION_REQUEST,
                SysUtil.getNow2(), NetConstant.TEST_TURE);
    }*/

    @Override
    public JSONObject createRequestBody() throws JSONException {
        JSONObject serviceContent = new JSONObject();
        serviceContent.put("mac", mac);
        return serviceContent;
    }

    @Override
    public void onResponse(JSONObject response) throws JSONException {
        Log.e("-----", response.toString());
        if (response != null && response.containsKey("rspDesc")/*response.containsKey("response")*/) {
            L.d(response.toString());
            String jo = response.getString("rspDesc");
            if (response.getString("rspCode").equalsIgnoreCase("0000")) {

                //JSONObject jo1 = response.getJSONObject("rspDesc");
//				if(!TextUtils.isEmpty(jo1.toString())){
//					DBM.getCurrentOrm().save(new LocalJsonData("user",jo1.toString(),SysUtil.getNow2()));
//				}
                //拆分字符串
                //String strArr=jo.substring(2,jo.length()-2);
                User user = JSON.parseObject(jo.toString(), User.class);
                getCallback().onReceive(user);
            } else
                getCallback().onError(jo.toString());
        }
    }

    @Override
    public void onError(String error) {
        L.e(error);
    }
}
