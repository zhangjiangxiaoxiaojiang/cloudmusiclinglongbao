package com.jinxin.cloudmusic.bean;

import com.litesuits.orm.db.annotation.Table;

/**
 * Created by ZJ on 2017/4/18 0018.
 */
@Table("powerState")
public class PowerState extends BaseModel{
    private String whId;
    private String mac;//设备的mac
    private String powerName;
    private boolean powerState;

    public PowerState(String whId, String powerName, boolean powerState) {
        this.whId = whId;
        this.powerName = powerName;
        this.powerState = powerState;
    }

    public PowerState(String whId, String mac, String powerName, boolean powerState) {
        this.whId = whId;
        this.mac = mac;
        this.powerName = powerName;
        this.powerState = powerState;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getWhId() {
        return whId;
    }

    public void setWhId(String whId) {
        this.whId = whId;
    }

    public String getPowerName() {
        return powerName;
    }

    public void setPowerName(String powerName) {
        this.powerName = powerName;
    }

    public boolean isPowerState() {
        return powerState;
    }

    public void setPowerState(boolean powerState) {
        this.powerState = powerState;
    }

    /*@Override
    public String toString() {
        return "PowerState{" +
                "whId='" + whId + '\'' +
                ", powerName='" + powerName + '\'' +
                ", powerState=" + powerState +
                '}';
    }*/

    @Override
    public String toString() {
        return "PowerState{" +
                "whId='" + whId + '\'' +
                ", mac='" + mac + '\'' +
                ", powerName='" + powerName + '\'' +
                ", powerState=" + powerState +
                '}';
    }
}
