
package com.electrom.vahanwireprovider.models.update_profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrgDetails {

    @SerializedName("gst_number")
    @Expose
    private String gstNumber;
    @SerializedName("pan_number")
    @Expose
    private String panNumber;

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

}
