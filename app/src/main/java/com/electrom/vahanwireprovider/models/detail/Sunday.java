
package com.electrom.vahanwireprovider.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sunday {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("from")
    @Expose
    private String from;
    @SerializedName("to")
    @Expose
    private String to;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
