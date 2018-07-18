package com.jinxin.cloudmusic.constant;

/**
 * Created by XTER on 2016/9/20.
 * 本地常量
 */
public class LocalConstant {
	public static final String DB_DEFAULT_NAME = "jxsc.db";

	public static final String DB_GATEWAY_NAME = "gateway.db";

	/* 当前账户名 */
	public static final String KEY_ACCOUNT = "account";
	/* 子账户名 */
	public static final String KEY_SUB_ACCOUNT = "subAccount";
	/* 密码--已加密 */
	public static final String KEY_PASSWORD = "password";

	//目前买入的mac
	public static  final String mac="0000128581425294";
	//播放器唯一码
	public static final String playerUnique="000002";

	/**
	 * 设备类型代号
	 */
	public static final String FUN_TYPE = "fun_type";

	public static final int CMD_ZG_LOCK = 1;
	public static final int CMD_ZG_DEVICE = 0;

	//通信area字段
	public static final String AREA_ALL = "all";
	public static final String AREA_ALARM = "alarm";
	public static final String AREA_STATE = "state";

	//各数据表名
	public static final String CLOUD_SETTING = "cloudsetting";
	public static final String CUSTOMER = "customer";
	public static final String CUSTOMER_AREA = "customerarea";
	public static final String CUSTOMER_PATTERN = "customerpattern";
	public static final String CUSTOMER_PRODUCT = "customerproduct";
	public static final String FUN_DETAIL = "fundetail";
	public static final String FUN_DETAIL_CONFIG = "fundetailconfig";
	public static final String MUSIC = "music";
	public static final String PRODUCT_DOOR_CONTACT = "productdoorcontact";
	public static final String PRODUCT_FUN = "productfun";
	public static final String PRODUCT_PATTERN_OPERATION = "productpatternoperation";
	public static final String PRODUCT_REGISTER = "productregister";
	public static final String PRODUCT_STATE = "productstate";
	public static final String WH_RPODUCT_UNFRSRED = "whproductunfrared";
	public static final String USER = "user";

	//其他area字段
	public static final String UPDATE = "jxtouch_update";
	public static final String UPDATE_YES="jxtouch_update_yes";
	public static final String GATEWAY_INFO = "gateway_info";
	public static final String GATEWAY_LOG = "gateway_log";
	public static final String VOICE_UPLOAD = "voice_upload";
}
