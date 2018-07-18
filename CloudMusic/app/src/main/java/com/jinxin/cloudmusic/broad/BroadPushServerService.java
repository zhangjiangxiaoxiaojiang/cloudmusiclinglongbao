package com.jinxin.cloudmusic.broad;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jinxin.cloudmusic.app.JxscApp;
import com.jinxin.cloudmusic.constant.NetConstant;
import com.jinxin.cloudmusic.db.SPM;
import com.jinxin.cloudmusic.event.DataEvent;
import com.jinxin.cloudmusic.util.L;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;


@SuppressLint("NewApi")
public class BroadPushServerService extends Service {

	private static final int ECHOMAX = 255; // 发送或接收的信息最大字节数

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		JxscApp.IpFlag=true;
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				int servPort = 11005;//接收端口号

				DatagramSocket socket;
				try {
					socket = new DatagramSocket(servPort);
					byte[] buffer = new byte[ECHOMAX];
					DatagramPacket packet = new DatagramPacket(buffer, ECHOMAX);
					while (true) { // 不断接收来自客户端的信息及作出相应的相应
						socket.receive(packet); // Receive packet from client
						L.i("地址： " + packet.getAddress().getHostAddress() + " 端口： " + packet.getPort());
						String data = new String(packet.getData(), 0, packet.getLength());
						data = data.substring(0, data.indexOf("\r\n"));
						L.i("接收数据：" + data);
							try {
							JSONObject jObj = JSON.parseObject(data);
							String gatewayIpInfo = jObj.getString("extra");
							String gatewayIp = gatewayIpInfo.split(",")[0];
							if (!TextUtils.isEmpty(gatewayIp)) {
								if (JxscApp.IpFlag) {
									//尚未获取网关时，先要启动更新数据服务
									//AppUtil.showNotification(getApplicationContext(), "大网关IP为：" + gatewayIp, true);
									L.e("大网关IP为：" + gatewayIp);
									SPM.saveStr(SPM.CONSTANT, NetConstant.KEY_GATEWAYIP, gatewayIp);
									JxscApp.IpFlag = false;
									EventBus.getDefault().post(new DataEvent(1101,""));
									//EventBus.getDefault().post(new LoginSuccessEvent());
								} else {
									//已获取网关后，可跳过启动数据更新服务直接开始更新数据
									//EventBus.getDefault().post(new UpdateDataEvent());
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

}
