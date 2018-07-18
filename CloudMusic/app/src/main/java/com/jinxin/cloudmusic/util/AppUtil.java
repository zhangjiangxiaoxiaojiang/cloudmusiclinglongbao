package com.jinxin.cloudmusic.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by XTER on 2016/9/20.
 * 应用相关
 */
public class AppUtil {
	
	/**
	 * 从包信息中获取版本号
	 *
	 * @param context 上下文
	 * @return version   版本代码
	 */
	private static int getVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	public static String getAssetWeatherIconPath(String weatherPath){
		if(weatherPath.contains("http://api.map.baidu.com/images")){
			return weatherPath.replace("http://api.map.baidu.com/images","file:///android_asset");
		}
		return weatherPath;
	}
}
