
package com.electrom.vahanwireprovider.models.pre_request;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("booking_id")
    @Expose
    private String bookingId;
    @SerializedName("otp_verify")
    @Expose
    private String otpVerify;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("issue")
    @Expose
    private String issue;
    @SerializedName("_user_location")
    @Expose
    private UserLocation userLocation;
    @SerializedName("_user_location_address")
    @Expose
    private String userLocationAddress;
    @SerializedName("_user_drop_location")
    @Expose
    private UserDropLocation userDropLocation;
    @SerializedName("_user_drop_location_address")
    @Expose
    private String userDropLocationAddress;
    @SerializedName("booking_type")
    @Expose
    private String bookingType;
    @SerializedName("request_time")
    @Expose
    private String requestTime;
    @SerializedName("enroute_status")
    @Expose
    private String enrouteStatus;
    @SerializedName("timer")
    @Expose
    private String timer;
    @SerializedName("booking_status")
    @Expose
    private BookingStatus bookingStatus;
    @SerializedName("request_type")
    @Expose
    private String requestType;
    @SerializedName("request_mode")
    @Expose
    private String requestMode;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("user_date")
    @Expose
    private String userDate;
    @SerializedName("servicecenter_time")
    @Expose
    private String servicecenterTime;
    @SerializedName("delivery_date")
    @Expose
    private List<Object> deliveryDate = null;
    @SerializedName("checklist")
    @Expose
    private List<Object> checklist = null;
    @SerializedName("requote_status")
    @Expose
    private String requoteStatus;
    @SerializedName("quotation")
    @Expose
    private List<Object> quotation = null;
    @SerializedName("quotation_discount")
    @Expose
    private List<Object> quotationDiscount = null;
    @SerializedName("mini_quotation")
    @Expose
    private List<Object> miniQuotation = null;
    @SerializedName("billing")
    @Expose
    private List<Object> billing = null;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;
    @SerializedName("assignee")
    @Expose
    private Assignee assignee;
    @SerializedName("mainprovider_booking_id")
    @Expose
    private String mainproviderBookingId;
    @SerializedName("user_details")
    @Expose
    private UserDetails userDetails;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getOtpVerify() {
        return otpVerify;
    }

    public void setOtpVerify(String otpVerify) {
        this.otpVerify = otpVerify;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public UserLocation getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public String getUserLocationAddress() {
        return userLocationAddress;
    }

    public void setUserLocationAddress(String userLocationAddress) {
        this.userLocationAddress = userLocationAddress;
    }

    public UserDropLocation getUserDropLocation() {
        return userDropLocation;
    }

    public void setUserDropLocation(UserDropLocation userDropLocation) {
        this.userDropLocation = userDropLocation;
    }

    public String getUserDropLocationAddress() {
        return userDropLocationAddress;
    }

    public void setUserDropLocationAddress(String userDropLocationAddress) {
        this.userDropLocationAddress = userDropLocationAddress;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getEnrouteStatus() {
        return enrouteStatus;
    }

    public void setEnrouteStatus(String enrouteStatus) {
        this.enrouteStatus = enrouteStatus;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestMode() {
        return requestMode;
    }

    public void setRequestMode(String requestMode) {
        this.requestMode = requestMode;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getServicecenterTime() {
        return servicecenterTime;
    }

    public void setServicecenterTime(String servicecenterTime) {
        this.servicecenterTime = servicecenterTime;
    }

    public List<Object> getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(List<Object> deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public List<Object> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<Object> checklist) {
        this.checklist = checklist;
    }

    public String getRequoteStatus() {
        return requoteStatus;
    }

    public void setRequoteStatus(String requoteStatus) {
        this.requoteStatus = requoteStatus;
    }

    public List<Object> getQuotation() {
        return quotation;
    }

    public void setQuotation(List<Object> quotation) {
        this.quotation = quotation;
    }

    public List<Object> getQuotationDiscount() {
        return quotationDiscount;
    }

    public void setQuotationDiscount(List<Object> quotationDiscount) {
        this.quotationDiscount = quotationDiscount;
    }

    public List<Object> getMiniQuotation() {
        return miniQuotation;
    }

    public void setMiniQuotation(List<Object> miniQuotation) {
        this.miniQuotation = miniQuotation;
    }

    public List<Object> getBilling() {
        return billing;
    }

    public void setBilling(List<Object> billing) {
        this.billing = billing;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Assignee getAssignee() {
        return assignee;
    }

    public void setAssignee(Assignee assignee) {
        this.assignee = assignee;
    }

    public String getMainproviderBookingId() {
        return mainproviderBookingId;
    }

    public void setMainproviderBookingId(String mainproviderBookingId) {
        this.mainproviderBookingId = mainproviderBookingId;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

}
