package com.jinxin.cloudmusic.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.constant.LocalConstant;
import com.jinxin.cloudmusic.constant.NetConstant;
import com.jinxin.cloudmusic.db.SPM;
import com.jinxin.cloudmusic.event.UpdateDataEvent;
import com.jinxin.cloudmusic.util.SysUtil;
import com.jinxin.cloudmusic.util.ToastUtil;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;

public class UpdateDataService extends Service {

	public static final String TEST_IP = SPM.getStr(SPM.CONSTANT, NetConstant.KEY_GATEWAYIP, "");
	//public static final String TEST_IP = "192.168.30.120";
	public static final int PORT = 12305;//大网关建立链接

	/*public static final String TEST_IP = "www.beonehome.com";
	public static final int PORT = 6300;*/

	private IoConnector dataConnector;
	private static IoSession mSession;

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (dataConnector == null) {
					dataConnector = new NioSocketConnector();
				}

				dataConnector.getFilterChain().addFirst("reconnect", new IoFilterAdapter() {
					@Override
					public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
						ToastUtil.showLong(JxscApp.getContext(), "与大网关数据通道已断开");
						//EventBus.getDefault().post(new UpdateDataServiceDisconnectEvent());
						//跳转主界面
						/*Intent intent = new Intent();
						intent.setClass(UpdateDataService.this, LoginActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);*/
//						for (; ; ) {
//							try {
//								Thread.sleep(3000);
//								ConnectFuture future = dataConnector.connect();
//								future.awaitUninterruptibly();// 等待连接创建成功
//								mSession = future.getSession();// 获取会话
//								if (mSession.isConnected()) {
//									L.d("重连成功");
//									break;
//								}
//							} catch (Exception ex) {
//								L.w("重连服务器登录失败,3秒再连接一次:" + ex.getMessage());
//							}
//						}
					}
				});

				dataConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new UpdateDataCodecFactory()));
//				dataConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

				dataConnector.getSessionConfig().setReadBufferSize(1024 * 1024);

				dataConnector.setHandler(new UpdateDataHandler());

				dataConnector.setDefaultRemoteAddress(new InetSocketAddress(TEST_IP, PORT));

				ConnectFuture future = dataConnector.connect(new InetSocketAddress(
						TEST_IP, PORT));//与大网关建立链接

				future.awaitUninterruptibly();
				if (future.isConnected()){//可以判断mSession是否已经连接上
					mSession = future.getSession();
					ToastUtil.showShort(JxscApp.getContext(),"已建立session连接");
					EventBus.getDefault().post(new UpdateDataEvent());
					//updateData(LocalConstant.AREA_ALL);
				}
			}
		}).start();
		return 0;
	}

	/**
	 * 特殊请求--需自行封装数据
	 *
	 * @param jo 数据
	 */
	public synchronized static void updateSpecialData(JSONObject jo) {
		if (mSession != null && mSession.isConnected()) {
			mSession.write(jo);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dataConnector != null) {
			dataConnector.dispose();
		}
	}

	public static synchronized IoSession getSession() {
		return mSession;
	}

	public synchronized static void updateData(String area) {
		if (mSession != null && mSession.isConnected()) {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("area", area);
			jsonObj.put("time", SysUtil.getNow2());
			mSession.write(jsonObj);
		}
	}
}
