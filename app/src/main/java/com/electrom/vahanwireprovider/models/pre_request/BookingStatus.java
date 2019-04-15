
package com.electrom.vahanwireprovider.models.pre_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingStatus {

    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("mechanic")
    @Expose
    private Mechanic mechanic;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

}
