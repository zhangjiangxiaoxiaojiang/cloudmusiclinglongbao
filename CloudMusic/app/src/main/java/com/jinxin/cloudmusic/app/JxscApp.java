package com.jinxin.cloudmusic.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import com.jinxin.cloudmusic.MainActivity;
import com.jinxin.cloudmusic.broad.BroadPushServerService;
import com.jinxin.cloudmusic.jpush.MessagePushThread;
import com.jinxin.cloudmusic.util.L;
import com.tencent.bugly.crashreport.CrashReport;


/**
 * Created by XTER on 2016/9/19.
 * 用静态内部类建单例，保证线程安全
 */
public class JxscApp extends Application {
	/**
	 * 是否要往大网关推送(尚未获取网关IP)
	 */
	public static  boolean IpFlag=true;
	public static boolean booleanJudge=false;//false:在线 true：本地（是否通过在线链接播放）
	public static boolean booSearchOrInternet=false;
	public static boolean firstLogin=false;//一次denglu
	public static boolean searchBack=false;//搜索界面的返回键
	public static boolean mainBack=false;//云音乐主页面的返回键，true:二级页面返回到一级页面 false:云音乐activity跳转到设备控制activity
	public static int bMusicListDetail=-1;//false:0 true:1 （榜单二级界面）
	public static int iMusicListDetail=-1;//false:0 true:1 （榜单二级界面）
	//public static boolean hidePlayinterface=false;//是否隐藏播放界面
	public static int hidePlayinterface=-1;//是否隐藏播放界面(默认：-1 false:0 ture:1)
	public static boolean hideMusicSetting=false;//是否隐藏登录界面
	public static int playMode=0;//0：循环播放（默认） 1：随机播放 2：单曲循环
	public static int lyricSum=-1;//歌词是否重新绘制
	public static boolean bPalySong=false;//否：false 是：true（是否是直接语音播放音乐）
	public static boolean bSpeechSearchInterface=false;//关闭：false 打开：true （关闭手动点出来的搜索界面）
	public static boolean bChangeOther=false;//关闭：false 打开：true （判断是否退出切换到其他app）
	public static int mediaPlayCurret=0;//退出去是拿到当前的进度值
	public static String account;
	public static String secretKey;
	public static boolean bEqualizer=false;//均衡器界面  隐藏：true 不隐藏：false

	//解决主页加载重复数据的问题
	public static boolean mainPage=false;//是否重新加载主页数据
	public static int mainCount=0;

	private static JxscApp instance;
	private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
	public WindowManager.LayoutParams getWindowParams() {
		return windowParams;
	}
	public static JxscApp getInstance(){
		return instance;
	}

	@Override
	public void onCreate() {
		// 应用程序入口处调用,避免手机内存过小,杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用“,”分隔。
		// 设置你申请的应用appid
		// 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
		super.onCreate();
		instance = this;
		L.DEBUG = true;
		//大网关广播推送，接收端服务
		Intent PushService = new Intent(this, BroadPushServerService.class);
		startService(PushService);
		new Thread(new MessagePushThread()).start();//开启推送

		//接入bugly，并且初始化
		CrashReport.initCrashReport(getApplicationContext(), "f3a81390fa", false);

	}

	public static Context getContext() {
		return instance.getApplicationContext();
	}

}
