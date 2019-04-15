
package com.electrom.vahanwireprovider.models.pre_request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PersonalDetails {

    @SerializedName("social_media")
    @Expose
    private SocialMedia socialMedia;
    @SerializedName("myimages")
    @Expose
    private Myimages myimages;
    @SerializedName("highest_qualification")
    @Expose
    private String highestQualification;
    @SerializedName("certification")
    @Expose
    private String certification;
    @SerializedName("total_exp")
    @Expose
    private String totalExp;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("marital_status")
    @Expose
    private String maritalStatus;
    @SerializedName("pan_number")
    @Expose
    private String panNumber;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("intro_video")
    @Expose
    private String introVideo;

    public SocialMedia getSocialMedia() {
        return socialMedia;
    }

    public void setSocialMedia(SocialMedia socialMedia) {
        this.socialMedia = socialMedia;
    }

    public Myimages getMyimages() {
        return myimages;
    }

    public void setMyimages(Myimages myimages) {
        this.myimages = myimages;
    }

    public String getHighestQualification() {
        return highestQualification;
    }

    public void setHighestQualification(String highestQualification) {
        this.highestQualification = highestQualification;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public String getTotalExp() {
        return totalExp;
    }

    public void setTotalExp(String totalExp) {
        this.totalExp = totalExp;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIntroVideo() {
        return introVideo;
    }

    public void setIntroVideo(String introVideo) {
        this.introVideo = introVideo;
    }

}
