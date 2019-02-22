
package com.electrom.vahanwireprovider.models.detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Access {

    @SerializedName("otp")
    @Expose
    private Integer otp;

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

}
