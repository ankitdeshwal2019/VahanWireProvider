
package com.electrom.vahanwireprovider.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NavigatedUser {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("device_type")
    @Expose
    private String deviceType;
    @SerializedName("nav_date")
    @Expose
    private String navDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getNavDate() {
        return navDate;
    }

    public void setNavDate(String navDate) {
        this.navDate = navDate;
    }

}
