package com.jinxin.cloudmusic.constant;

/**
 * Created by XTER on 2016/9/19.
 * 关于网络的常量
 */
public class NetConstant {
    public final static String HOST_INSPECT = "www.beonehome.com:6300";// 设备巡检暂时使用（云平台）数据分析服务器地址，后面由大网关代替

    public final static String HOST_ZEGBING = "www.beonehome.com:12307";// 设备巡检暂时使用（云平台）数据分析服务器地址，后面由大网关代替
    //	public static final String HTTP_ACCESSPATH = "http://192.168.60.226:9099/smartcity/mobile/service";
    //public static final String HTTP_ACCESSPATH = "http://tingapi.ting.baidu.com/v1/restserver/ting";
    //public static final String HTTP_ACCESSPATH = "http://192.168.60.101:8080/smarthome1/m-m/music3?";
    //public static final String HTTP_ACCESSPATH = "http://192.168.60.102:8080/smarthome1/m-m/music3?";
    public static final String HTTP_ACCESSPATH = "http://www.beonehome.com:6100/smarthome/m-m/music3?";//云平台
    //mac登录（旧）
    public static final String HTTP_MACLOGIN = "http://www.beonehome.com:6100/smarthome/customerProduct/loginWithMac?";//MAC登录

    //mac登录和版本更新（新）
    public static final String VERSION_UPDATE="http://www.beonehome.com:6100/smarthome/mobile/service";

    //	public static final String HTTP_ADVICE = "http://192.168.60.226:9099/smartcity/form/bigscreen/property/advice";
    public static final String HTTP_ADVICE = "http://www.beonehome.com:6100/smartcity/form/bigscreen/property/advice";


    //public static final String HTTP_MUSIC="http://192.168.60.148:8080/smarthomeOld";
    public static final String HTTP_MUSIC="http://www.beonehome.com:6100/smarthome/mobile/service?";

    public static final String BIP_VER = "1.0";
    public static final String TEST_TURE = "1";
    public static final String TEST_FALSE = "0";

    public static final String DM_SRC = "I001";
    public static final String DM_DST = "P000";

    public final static String ACTION_REQUEST = "0";
    public final static String ACTION_RESPONSE = "1";


    public static final String BS_ACCOUNT_MANAGER = "B000";
    public static final String BS_USER_MANAGER = "B001";
    public static final String TRD_ACCOUNT_LOGIN = "T000";
    public static final String TR_CODE_STATISTICS = "T101";

    //	00	操作成功
    //	01	网关处于离线
    //	02	处理超时
    //	03	服务端业务处理失败
    //	04	未知异常
    public static final String SERVER_ERROR_MSG_00 = "00";
    public static final String SERVER_ERROR_MSG_01 = "01";
    public static final String SERVER_ERROR_MSG_02 = "02";

    public static final int DEFAULT_CONNECT_TIME_OUT = 10000;
    public static final int DEFAULT_READ_TIME_OUT = 10000;

    public final static String KEY_GATEWAYIP = "gatewayIP"; //更改的大网关ip
    //public final static String KEY_GATEWAYIP = "192.168.30.132"; //更改的大网关ip
}