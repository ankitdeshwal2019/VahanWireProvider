
package com.electrom.vahanwireprovider.models.pre_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Documents {

    @SerializedName("health_insurance")
    @Expose
    private String healthInsurance;
    @SerializedName("driving_license")
    @Expose
    private String drivingLicense;
    @SerializedName("rc")
    @Expose
    private String rc;
    @SerializedName("pollution")
    @Expose
    private String pollution;
    @SerializedName("vahan_insurance")
    @Expose
    private String vahanInsurance;

    public String getHealthInsurance() {
        return healthInsurance;
    }

    public void setHealthInsurance(String healthInsurance) {
        this.healthInsurance = healthInsurance;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public String getPollution() {
        return pollution;
    }

    public void setPollution(String pollution) {
        this.pollution = pollution;
    }

    public String getVahanInsurance() {
        return vahanInsurance;
    }

    public void setVahanInsurance(String vahanInsurance) {
        this.vahanInsurance = vahanInsurance;
    }

}
