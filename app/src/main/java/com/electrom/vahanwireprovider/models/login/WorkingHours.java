
package com.electrom.vahanwireprovider.models.login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WorkingHours {

    @SerializedName("sunday")
    @Expose
    private Sunday sunday;
    @SerializedName("monday")
    @Expose
    private Monday monday;
    @SerializedName("tuesday")
    @Expose
    private Tuesday tuesday;
    @SerializedName("wednesday")
    @Expose
    private Wednesday wednesday;
    @SerializedName("thursday")
    @Expose
    private Thursday thursday;
    @SerializedName("friday")
    @Expose
    private Friday friday;
    @SerializedName("saturday")
    @Expose
    private Saturday saturday;

    public Sunday getSunday() {
        return sunday;
    }

    public void setSunday(Sunday sunday) {
        this.sunday = sunday;
    }

    public Monday getMonday() {
        return monday;
    }

    public void setMonday(Monday monday) {
        this.monday = monday;
    }

    public Tuesday getTuesday() {
        return tuesday;
    }

    public void setTuesday(Tuesday tuesday) {
        this.tuesday = tuesday;
    }

    public Wednesday getWednesday() {
        return wednesday;
    }

    public void setWednesday(Wednesday wednesday) {
        this.wednesday = wednesday;
    }

    public Thursday getThursday() {
        return thursday;
    }

    public void setThursday(Thursday thursday) {
        this.thursday = thursday;
    }

    public Friday getFriday() {
        return friday;
    }

    public void setFriday(Friday friday) {
        this.friday = friday;
    }

    public Saturday getSaturday() {
        return saturday;
    }

    public void setSaturday(Saturday saturday) {
        this.saturday = saturday;
    }

}
