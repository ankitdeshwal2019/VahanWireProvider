
package com.electrom.vahanwireprovider.models.version;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VersionData {

    @SerializedName("device")
    @Expose
    private String device;
    @SerializedName("latest_version")
    @Expose
    private String latestVersion;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
