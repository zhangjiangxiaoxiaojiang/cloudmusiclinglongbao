package com.jinxin.cloudmusic.net.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeUtils {

	private final static ThreadLocal<SimpleDateFormat> dateFormater3 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyyMMddHHmmss");
		}
	};
	
	public static String getSystemCurrentTime() {
		Calendar rightNow = Calendar.getInstance();
		return dateFormater3.get().format(rightNow.getTime());
	}

}
