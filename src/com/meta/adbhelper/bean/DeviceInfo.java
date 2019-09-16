package com.meta.adbhelper.bean;

import java.io.Serializable;
import java.util.Date;

public class DeviceInfo implements Serializable {
    private Integer id;

    private String deviceId;

    private String deviceName;

    private Date timestamp;

    private String deviceModel;

    private String deviceBrand;

    private String systemVersion;

    private Integer systemApi;

    private String rom;

    private Integer deviceStatus;

    private String deviceFlag;

    private Integer currentTaskId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName == null ? null : deviceName.trim();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel == null ? null : deviceModel.trim();
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand == null ? null : deviceBrand.trim();
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public void setSystemVersion(String systemVersion) {
        this.systemVersion = systemVersion == null ? null : systemVersion.trim();
    }

    public Integer getSystemApi() {
        return systemApi;
    }

    public void setSystemApi(Integer systemApi) {
        this.systemApi = systemApi;
    }

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom == null ? null : rom.trim();
    }

    public Integer getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(Integer deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceFlag() {
        return deviceFlag;
    }

    public void setDeviceFlag(String deviceFlag) {
        this.deviceFlag = deviceFlag == null ? null : deviceFlag.trim();
    }

    public Integer getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(Integer currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DeviceInfo other = (DeviceInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDeviceId() == null ? other.getDeviceId() == null : this.getDeviceId().equals(other.getDeviceId()))
                && (this.getDeviceName() == null ? other.getDeviceName() == null : this.getDeviceName().equals(other.getDeviceName()))
                && (this.getTimestamp() == null ? other.getTimestamp() == null : this.getTimestamp().equals(other.getTimestamp()))
                && (this.getDeviceModel() == null ? other.getDeviceModel() == null : this.getDeviceModel().equals(other.getDeviceModel()))
                && (this.getDeviceBrand() == null ? other.getDeviceBrand() == null : this.getDeviceBrand().equals(other.getDeviceBrand()))
                && (this.getSystemVersion() == null ? other.getSystemVersion() == null : this.getSystemVersion().equals(other.getSystemVersion()))
                && (this.getSystemApi() == null ? other.getSystemApi() == null : this.getSystemApi().equals(other.getSystemApi()))
                && (this.getRom() == null ? other.getRom() == null : this.getRom().equals(other.getRom()))
                && (this.getDeviceStatus() == null ? other.getDeviceStatus() == null : this.getDeviceStatus().equals(other.getDeviceStatus()))
                && (this.getDeviceFlag() == null ? other.getDeviceFlag() == null : this.getDeviceFlag().equals(other.getDeviceFlag()))
                && (this.getCurrentTaskId() == null ? other.getCurrentTaskId() == null : this.getCurrentTaskId().equals(other.getCurrentTaskId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDeviceId() == null) ? 0 : getDeviceId().hashCode());
        result = prime * result + ((getDeviceName() == null) ? 0 : getDeviceName().hashCode());
        result = prime * result + ((getTimestamp() == null) ? 0 : getTimestamp().hashCode());
        result = prime * result + ((getDeviceModel() == null) ? 0 : getDeviceModel().hashCode());
        result = prime * result + ((getDeviceBrand() == null) ? 0 : getDeviceBrand().hashCode());
        result = prime * result + ((getSystemVersion() == null) ? 0 : getSystemVersion().hashCode());
        result = prime * result + ((getSystemApi() == null) ? 0 : getSystemApi().hashCode());
        result = prime * result + ((getRom() == null) ? 0 : getRom().hashCode());
        result = prime * result + ((getDeviceStatus() == null) ? 0 : getDeviceStatus().hashCode());
        result = prime * result + ((getDeviceFlag() == null) ? 0 : getDeviceFlag().hashCode());
        result = prime * result + ((getCurrentTaskId() == null) ? 0 : getCurrentTaskId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash=").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deviceId=").append(deviceId);
        sb.append(", deviceName=").append(deviceName);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", deviceModel=").append(deviceModel);
        sb.append(", deviceBrand=").append(deviceBrand);
        sb.append(", systemVersion=").append(systemVersion);
        sb.append(", systemApi=").append(systemApi);
        sb.append(", rom=").append(rom);
        sb.append(", deviceStatus=").append(deviceStatus);
        sb.append(", deviceFlag=").append(deviceFlag);
        sb.append(", currentTaskId=").append(currentTaskId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}