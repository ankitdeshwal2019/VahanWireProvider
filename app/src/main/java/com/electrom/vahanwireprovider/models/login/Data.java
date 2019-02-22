
package com.electrom.vahanwireprovider.models.login;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

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
    @SerializedName("registered_name")
    @Expose
    private String registeredName;
    @SerializedName("contact_person")
    @Expose
    private String contactPerson;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("mobile_pin")
    @Expose
    private String mobilePin;
    @SerializedName("isClosed")
    @Expose
    private Integer isClosed;
    @SerializedName("payment_methods")
    @Expose
    private List<PaymentMethod> paymentMethods = null;
    @SerializedName("services")
    @Expose
    private List<Service> services = null;
    @SerializedName("offers")
    @Expose
    private List<Offer> offers = null;
    @SerializedName("isPaid")
    @Expose
    private Integer isPaid;
    @SerializedName("paid_subscription")
    @Expose
    private List<PaidSubscription> paidSubscription = null;
    @SerializedName("isActive")
    @Expose
    private Integer isActive;
    @SerializedName("navigated_users")
    @Expose
    private List<NavigatedUser> navigatedUsers = null;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

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

    public String getRegisteredName() {
        return registeredName;
    }

    public void setRegisteredName(String registeredName) {
        this.registeredName = registeredName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getMobilePin() {
        return mobilePin;
    }

    public void setMobilePin(String mobilePin) {
        this.mobilePin = mobilePin;
    }

    public Integer getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Integer isClosed) {
        this.isClosed = isClosed;
    }

    public List<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
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

    public List<PaidSubscription> getPaidSubscription() {
        return paidSubscription;
    }

    public void setPaidSubscription(List<PaidSubscription> paidSubscription) {
        this.paidSubscription = paidSubscription;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public List<NavigatedUser> getNavigatedUsers() {
        return navigatedUsers;
    }

    public void setNavigatedUsers(List<NavigatedUser> navigatedUsers) {
        this.navigatedUsers = navigatedUsers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
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

}
