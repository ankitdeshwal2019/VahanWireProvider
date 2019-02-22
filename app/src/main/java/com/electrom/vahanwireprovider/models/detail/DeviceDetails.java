
package com.electrom.vahanwireprovider.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceDetails {

    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("notification_id")
    @Expose
    private String notificationId;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

}
