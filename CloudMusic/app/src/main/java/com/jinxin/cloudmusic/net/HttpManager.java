package com.jinxin.cloudmusic.net;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.constant.NetConstant;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.event.PosEvent;
import com.jinxin.cloudmusic.net.base.CustomJsonObjectRequest;
import com.jinxin.cloudmusic.net.base.FastJsonRequest;
import com.jinxin.cloudmusic.net.base.IRequest;
import com.jinxin.cloudmusic.net.base.IResponse;
import com.jinxin.cloudmusic.net.config.NetConfigs;
import com.jinxin.cloudmusic.net.login.LoginRequest;
import com.jinxin.cloudmusic.net.musiclist.BaseMusicPowerRequest;
import com.jinxin.cloudmusic.net.musiclist.BaseMusicRequest;
import com.jinxin.cloudmusic.util.L;
import com.jinxin.cloudmusic.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

/**
 * Created by XTER on 2016/9/19.
 * 网络管理
 */
public class HttpManager {
	private static HttpManager httpManager;
	private RequestQueue requestQueue;

	private HttpManager() {
		requestQueue = getRequestQueue();
	}

	private RequestQueue getRequestQueue() {
		if (requestQueue == null) {
			requestQueue = Volley.newRequestQueue(JxscApp.getContext());
		}
		return requestQueue;
	}

	public static synchronized HttpManager getInstance() {
		if (httpManager == null) {
			httpManager = new HttpManager();
		}
		return httpManager;
	}

	/*public void addRequest(LoginRequest request) {
		request.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(request);
	}*/

	public void addRequestMusic(final IRequest request) {
		BaseMusicRequest baseMusicRequest=new BaseMusicRequest(0, NetConstant.HTTP_ACCESSPATH+request.getRequestText().replace("\\\\","\\"),null , new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				request.onResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				request.onError(error.getMessage());
			}
		});
		String a=request.getRequestText();
		baseMusicRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(baseMusicRequest);
	}

	public void addRequestMusicPower(final IRequest request) {
		BaseMusicPowerRequest baseMusicPowerRequest=new BaseMusicPowerRequest(0, NetConstant.HTTP_MUSIC+request.getRequestText(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				request.onResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				request.onError(error.getMessage());
				Log.e("-----hhhasas","进入请求");
			}
		});
		String a=request.getRequestText();
		baseMusicPowerRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(baseMusicPowerRequest);
	}

	//mac登录
	public void addRequestMacMusicPower(final IRequest request) {
		BaseMusicPowerRequest baseMusicPowerRequest=new BaseMusicPowerRequest(0, NetConstant.HTTP_MACLOGIN+request.getRequestText(), null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Log.e("----mac登录",NetConstant.HTTP_MACLOGIN+request.getRequestText());
				request.onResponse(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				request.onError(error.getMessage());
				Log.e("-----hhhasas","进入请求");
			}
		});
		String a=request.getRequestText();
		baseMusicPowerRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(baseMusicPowerRequest);
	}


	public void addRequest(String url, final IRequest request) {
		FastJsonRequest fastRequest = new FastJsonRequest(url, request.getRequestText(), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (response != null) {
					request.onResponse(response);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				request.onError(error.getMessage());
			}
		});
		fastRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(fastRequest);
	}

	public void addRequest(int method,String url, final IRequest request) {
		FastJsonRequest fastRequest = new FastJsonRequest(method,url, request.getRequestText(), new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				if (response != null) {
					request.onResponse(response);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				request.onError(error.getMessage());
			}
		});
		fastRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(fastRequest);
	}

	public void addRequest(final IRequest request, final IResponse parser) {
		CustomJsonObjectRequest jsonObjectRequest = new CustomJsonObjectRequest(NetConfigs.HTTP_ACCESSPATH, request.getRequestText(),
                new Response.Listener<org.json.JSONObject>() {
                    @Override
                    public void onResponse(org.json.JSONObject response) {
                        if (response != null && parser != null) {
                            try {
                                org.json.JSONObject res = response.getJSONObject("response");
                                String resultCode = res.getString("rspCode");
                                String resultInfo = res.getString("rspDesc");
                                if (NetConfigs.CONNECTION_SUCCESS.equals(resultCode)) {//成功获取数据
                                    parser.parseResponse(response);
                                }else{//权限认证失败、获取数据失败
                                    parser.onFail(resultInfo);
                                    ToastUtil.showShort(JxscApp.getContext(), resultInfo.toString());
                                    //JxshApplication.getInstance().goHome(false);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            L.e(response.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(parser != null) {
                    parser.onError(error.getMessage());
                    ToastUtil.showShort(JxscApp.getContext(),error.getMessage());
                }
                //Log.e(TAG, error.getMessage(), error);
            }
        });
		jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		requestQueue.add(jsonObjectRequest);

	}

}