
package com.electrom.vahanwireprovider.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaidSubscription {

    @SerializedName("paid_status")
    @Expose
    private String paidStatus;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("to")
    @Expose
    private String to;

    public String getPaidStatus() {
        return paidStatus;
    }

    public void setPaidStatus(String paidStatus) {
        this.paidStatus = paidStatus;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

}
