
package com.electrom.vahanwireprovider.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkingHours {

    @SerializedName("from")
    @Expose
    private String from;

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

    @SerializedName("to")
    @Expose
    private String to;



}
