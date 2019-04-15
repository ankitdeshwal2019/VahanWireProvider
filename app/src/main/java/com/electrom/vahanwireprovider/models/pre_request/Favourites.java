
package com.electrom.vahanwireprovider.models.pre_request;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Favourites {

    @SerializedName("mechanic_mainprovider")
    @Expose
    private List<Object> mechanicMainprovider = null;
    @SerializedName("mechanic_provider")
    @Expose
    private List<Object> mechanicProvider = null;

    public List<Object> getMechanicMainprovider() {
        return mechanicMainprovider;
    }

    public void setMechanicMainprovider(List<Object> mechanicMainprovider) {
        this.mechanicMainprovider = mechanicMainprovider;
    }

    public List<Object> getMechanicProvider() {
        return mechanicProvider;
    }

    public void setMechanicProvider(List<Object> mechanicProvider) {
        this.mechanicProvider = mechanicProvider;
    }

}
