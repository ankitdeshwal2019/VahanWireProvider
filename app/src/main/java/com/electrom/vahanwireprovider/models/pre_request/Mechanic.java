
package com.electrom.vahanwireprovider.models.pre_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mechanic {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("cancel_reason")
    @Expose
    private String cancelReason;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

}
