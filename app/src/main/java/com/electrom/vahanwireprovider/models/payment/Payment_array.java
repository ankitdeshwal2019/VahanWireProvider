
package com.electrom.vahanwireprovider.models.payment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment_array {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("checked_status")
    @Expose
    private Integer checkedStatus;

    boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getCheckedStatus() {
        return checkedStatus;
    }

    public void setCheckedStatus(Integer checkedStatus) {
        this.checkedStatus = checkedStatus;
    }

}
