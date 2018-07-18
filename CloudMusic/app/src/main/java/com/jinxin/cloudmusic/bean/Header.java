package com.jinxin.cloudmusic.bean;


import com.jinxin.cloudmusic.constant.NetConstant;

/**
 * Created by XTER on 2016/9/19.
 * 请求报文头
 */
public class Header extends Base{
	/** 发起方域代码 **/
	private String origDomain;

	/** 落地房域代码 **/
	private String homeDomain;

	/** 业务功能代码 **/
	private String bipCode;

	/** 业务功能版本代码**/
	private String bipVer;

	/** 交易代码 **/
	private String activityCode;

	/** 交易动作代码 **/
	private String actionCode;

	/** 处理时间 **/
	private String processTime;

	/** 测试标记 **/
	private String testFlag;

	public Header() {
	}

	public Header( String bipCode, String activityCode, String actionCode, String processTime, String testFlag) {
		this.origDomain = NetConstant.DM_SRC;
		this.homeDomain = NetConstant.DM_DST;
		this.bipCode = bipCode;
		this.bipVer = NetConstant.BIP_VER;
		this.activityCode = activityCode;
		this.actionCode = actionCode;
		this.processTime = processTime;
		this.testFlag = testFlag;
	}

	public String getOrigDomain() {
		return origDomain;
	}

	public void setOrigDomain(String origDomain) {
		this.origDomain = origDomain;
	}

	public String getHomeDomain() {
		return homeDomain;
	}

	public void setHomeDomain(String homeDomain) {
		this.homeDomain = homeDomain;
	}

	public String getBipCode() {
		return bipCode;
	}

	public void setBipCode(String bipCode) {
		this.bipCode = bipCode;
	}

	public String getBipVer() {
		return bipVer;
	}

	public void setBipVer(String bipVer) {
		this.bipVer = bipVer;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getProcessTime() {
		return processTime;
	}

	public void setProcessTime(String processTime) {
		this.processTime = processTime;
	}

	public String getTestFlag() {
		return testFlag;
	}

	public void setTestFlag(String testFlag) {
		this.testFlag = testFlag;
	}
}
