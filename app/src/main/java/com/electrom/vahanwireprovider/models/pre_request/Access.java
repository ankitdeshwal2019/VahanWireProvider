
package com.electrom.vahanwireprovider.models.pre_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Access {

    @SerializedName("login_pin")
    @Expose
    private String loginPin;
    @SerializedName("otp")
    @Expose
    private String otp;

    public String getLoginPin() {
        return loginPin;
    }

    public void setLoginPin(String loginPin) {
        this.loginPin = loginPin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
