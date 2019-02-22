
package com.electrom.vahanwireprovider.models.detail;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("org_details")
    @Expose
    private OrgDetails orgDetails;
    @SerializedName("device_details")
    @Expose
    private DeviceDetails deviceDetails;
    @SerializedName("access")
    @Expose
    private Access access;
    @SerializedName("address")
    @Expose
    private Address address;
    @SerializedName("working_hours")
    @Expose
    private WorkingHours workingHours;
    @SerializedName("isClosed")
    @Expose
    private Integer isClosed;
    @SerializedName("payment_methods")
    @Expose
    private List<Object> paymentMethods = null;
    @SerializedName("services")
    @Expose
    private List<Object> services = null;
    @SerializedName("offers")
    @Expose
    private List<Offer> offers = null;
    @SerializedName("isPaid")
    @Expose
    private Integer isPaid;
    @SerializedName("paid_subscription")
    @Expose
    private List<Object> paidSubscription = null;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("navigated_users")
    @Expose
    private List<Object> navigatedUsers = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("contact_person")
    @Expose
    private String contactPerson;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("registered_name")
    @Expose
    private String registeredName;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("modified")
    @Expose
    private String modified;

    public OrgDetails getOrgDetails() {
        return orgDetails;
    }

    public void setOrgDetails(OrgDetails orgDetails) {
        this.orgDetails = orgDetails;
    }

    public DeviceDetails getDeviceDetails() {
        return deviceDetails;
    }

    public void setDeviceDetails(DeviceDetails deviceDetails) {
        this.deviceDetails = deviceDetails;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public Integer getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Integer isClosed) {
        this.isClosed = isClosed;
    }

    public List<Object> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<Object> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<Object> getServices() {
        return services;
    }

    public void setServices(List<Object> services) {
        this.services = services;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public Integer getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Integer isPaid) {
        this.isPaid = isPaid;
    }

    public List<Object> getPaidSubscription() {
        return paidSubscription;
    }

    public void setPaidSubscription(List<Object> paidSubscription) {
        this.paidSubscription = paidSubscription;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public List<Object> getNavigatedUsers() {
        return navigatedUsers;
    }

    public void setNavigatedUsers(List<Object> navigatedUsers) {
        this.navigatedUsers = navigatedUsers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

}
