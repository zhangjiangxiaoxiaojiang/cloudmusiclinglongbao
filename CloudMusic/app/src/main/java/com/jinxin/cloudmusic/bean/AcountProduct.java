package com.jinxin.cloudmusic.bean;

import com.litesuits.orm.db.annotation.Table;

//当前账号下的设备明细
@Table("whId")
public class AcountProduct extends BaseModel {
    private int id;

    private String codeName;

    private String customerId;

    private String whId;

    private int refId;

    private String mac;

    private String code;

    private String orderId;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public String getCodeName() {
        return this.codeName;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setWhId(String whId) {
        this.whId = whId;
    }

    public String getWhId() {
        return this.whId;
    }

    public void setRefId(int refId) {
        this.refId = refId;
    }

    public int getRefId() {
        return this.refId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return this.orderId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public String toString() {
        return "AcountProduct{" +
                "id=" + id +
                ", codeName='" + codeName + '\'' +
                ", customerId='" + customerId + '\'' +
                ", whId='" + whId + '\'' +
                ", refId=" + refId +
                ", mac='" + mac + '\'' +
                ", code='" + code + '\'' +
                ", orderId='" + orderId + '\'' +
                '}';
    }
}
