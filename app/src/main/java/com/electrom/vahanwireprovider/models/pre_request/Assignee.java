
package com.electrom.vahanwireprovider.models.pre_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Assignee {

    @SerializedName("driver")
    @Expose
    private String driver;
    @SerializedName("mechanic")
    @Expose
    private String mechanic;
    @SerializedName("tow")
    @Expose
    private String tow;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getMechanic() {
        return mechanic;
    }

    public void setMechanic(String mechanic) {
        this.mechanic = mechanic;
    }

    public String getTow() {
        return tow;
    }

    public void setTow(String tow) {
        this.tow = tow;
    }

}
