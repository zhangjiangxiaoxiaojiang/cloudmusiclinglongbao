package com.jinxin.cloudmusic.util.versionupdate;


import java.io.Serializable;

/**
 * Created by ZJ on 2017/8/25 0025.
 * 版本信息(使用者根据自己的需要创建相应的实体类)
 */
public class VersionInfo implements Serializable {
    private int id;
    private int appType;
    private String appVersion;
    private long publishTime;
    private int publishUser;
    private long downloadTimes;
    private int status;
    private String comments;
    private String oldName;
    private String newName;
    private String appPath;
    private long appSize;

    public VersionInfo() {
    }

    public VersionInfo(int id, int appType, String appVersion, long publishTime, int publishUser, long downloadTimes, int status, String comments, String oldName, String newName, String appPath, long appSize) {
        this.id = id;
        this.appType = appType;
        this.appVersion = appVersion;
        this.publishTime = publishTime;
        this.publishUser = publishUser;
        this.downloadTimes = downloadTimes;
        this.status = status;
        this.comments = comments;
        this.oldName = oldName;
        this.newName = newName;
        this.appPath = appPath;
        this.appSize = appSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(int publishUser) {
        this.publishUser = publishUser;
    }

    public long getDownloadTimes() {
        return downloadTimes;
    }

    public void setDownloadTimes(long downloadTimes) {
        this.downloadTimes = downloadTimes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getAppPath() {
        return appPath;
    }

    public void setAppPath(String appPath) {
        this.appPath = appPath;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }
}
