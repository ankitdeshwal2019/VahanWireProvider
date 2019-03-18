package com.electrom.vahanwireprovider.retrofit_lib;

import com.electrom.vahanwireprovider.models.amb_all_booking.ABookingStatus;
import com.electrom.vahanwireprovider.models.amb_book_list.AmbBookList;
import com.electrom.vahanwireprovider.models.amb_booking_his.BookingHistory;
import com.electrom.vahanwireprovider.models.ambulance_detail.AmbulanceDetail;
import com.electrom.vahanwireprovider.models.booking_details.BookingDetails;
import com.electrom.vahanwireprovider.models.booking_status.B_Status;
import com.electrom.vahanwireprovider.models.brands.Brand;
import com.electrom.vahanwireprovider.models.cancel_reason_mech.CancelReason;
import com.electrom.vahanwireprovider.models.cancel_request.CancelRequest;
import com.electrom.vahanwireprovider.models.city.City;
import com.electrom.vahanwireprovider.models.country.Country;
import com.electrom.vahanwireprovider.models.detail.Detail;
import com.electrom.vahanwireprovider.models.issues.Issue;
import com.electrom.vahanwireprovider.models.location_update.LocUpdate;
import com.electrom.vahanwireprovider.models.login.Login;
import com.electrom.vahanwireprovider.models.login_ambulance.LoginAmbulance;
import com.electrom.vahanwireprovider.models.mech_history.MechanicHistory;
import com.electrom.vahanwireprovider.models.mech_status.MechanicStatus;
import com.electrom.vahanwireprovider.models.mechanic.MechanicLogin;
import com.electrom.vahanwireprovider.models.mechanic_detail.MechanicDetail;
import com.electrom.vahanwireprovider.models.mechanic_registration.Mechanic;
import com.electrom.vahanwireprovider.models.payment.Payment;
import com.electrom.vahanwireprovider.models.pro_update_mech.ProfileUpdateMech;
import com.electrom.vahanwireprovider.models.request_accept.RequestAccept;
import com.electrom.vahanwireprovider.models.services.Service;
import com.electrom.vahanwireprovider.models.state.State;
import com.electrom.vahanwireprovider.models.status_user.BookingStatus;
import com.electrom.vahanwireprovider.models.status_user.StatusUser;
import com.electrom.vahanwireprovider.models.update_profile.Update;
import com.electrom.vahanwireprovider.utility.UrlConstants;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

public interface ApiInterface {
    // Customer Registration Process****
    @POST(UrlConstants.REGISTER_PROVIDER)
    @FormUrlEncoded
    Call<ResponseBody> registerProvider(@Field("mobile") String mobile,
                                        @Field("device_type") String device_type,
                                        @Field("notification_id") String notification_id);
    @POST(UrlConstants.MECHANIC_MOBILE_REGISTRATION)
    @FormUrlEncoded
    Call<Mechanic> registration_mechanic(@Field("device_id") String device_id,
                                         @Field("device_type") String device_type,
                                         @Field("notification_id") String notification_id,
                                         @Field("mobile") String mobile);

    @POST(UrlConstants.VERIFY_MOB_PROVIDER)
    @FormUrlEncoded
    Call<ResponseBody> verifyProvider(@Field("mobile") String mobile,
                                      @Field("otp") String otp);

    @POST(UrlConstants.MECHANIC_MOBILE_VERIFY)
    @FormUrlEncoded
    Call<ResponseBody> verifyProviderMecanic(@Field("mobile") String mobile,
                                      @Field("otp") String otp);

    @POST(UrlConstants.LOGIN)
    @FormUrlEncoded
    Call<Login> login(@Field("mobile") String mobile,
                      @Field("pin") String pin,
                      @Field("device_type") String device_type,
                      @Field("notification_id") String notification_id);

    @GET(UrlConstants.PROVIDER_DETAIL)
    Call<Detail> getUpdatedDetail(@QueryMap Map<String, String> params);

    @GET(UrlConstants.AMBULANCE_DETAIL)
    Call<AmbulanceDetail> getUpdatedDetailAmbulance(@QueryMap Map<String, String> params);

    @GET(UrlConstants.MECHANIC_BOOKING_HISTORY)
    Call<MechanicHistory> getBookingHistoryMech(@QueryMap Map<String, String> params);

    @GET(UrlConstants.AMBULANCE_BOOKING_HISTORY)
    Call<AmbBookList> getBookingHistoryAmbulance(@QueryMap Map<String, String> params);

    @GET(UrlConstants.PROVIDER_DETAIL_MECHANIC)
    Call<MechanicDetail> getUpdatedDetailMechanic(@QueryMap Map<String, String> params);

    @GET(UrlConstants.ALL_SERVICES)
    Call<Service> getAllServices(@QueryMap Map<String, String> params);


    @GET(UrlConstants.ALL_SERVICES_MECHANIC)
    Call<Service> getAllServicesMechanic(@QueryMap Map<String, String> params);

    @GET(UrlConstants.ALL_ISSUES)
    Call<Issue> getAllIssueMechnic(@QueryMap Map<String, String> params);

    @GET(UrlConstants.ALL_PAYMENT_METHOD)
    Call<Payment> getAllPaymentMethod(@QueryMap Map<String, String> params);

    @GET(UrlConstants.ALL_PAYMENT_METHOD_MECH)
    Call<Payment> getAllPaymentMethodMech(@QueryMap Map<String, String> params);

    @POST(UrlConstants.RESISTRATION_UPDATE)
    @FormUrlEncoded
    Call<Update> registrationUpdate(@Field("mobile") String mobile,
                                    @Field("mobile_pin") String mobile_pin,
                                    @Field("registered_name") String registered_name,
                                    @Field("contact_person") String contact_person,
                                    @Field("phone") String phone,
                                    @Field("email") String email,
                                    @Field("gst_number") String gst_number,
                                    @Field("pan_number") String pan_number,
                                    @Field("device_type") String device_type,
                                    @Field("notification_id") String notification_id,
                                    @Field("password") String password,
                                    @Field("first_address") String first_address,
                                    @Field("city") String city,
                                    @Field("state") String state,
                                    @Field("country") String country,
                                    @Field("pincode") String pincode,
                                    @Field("latitude") String latitude,
                                    @Field("longitude") String longitude,
                                    @Field("paidsub_status") String paidsub_status,
                                    @Field("active_status") String active_status,
                                    @Field("sunday_status") String sunday_status,
                                    @Field("sunday_from") String sunday_from,
                                    @Field("sunday_to") String sunday_to,
                                    @Field("monday_status") String monday_status,
                                    @Field("monday_from") String monday_from,
                                    @Field("monday_to") String monday_to,
                                    @Field("tuesday_status") String tuesday_status,
                                    @Field("tuesday_from") String tuesday_from,
                                    @Field("tuesday_to") String tuesday_to,
                                    @Field("wednesday_status") String wednesday_status,
                                    @Field("wednesday_from") String wednesday_from,
                                    @Field("wednesday_to") String wednesday_to,
                                    @Field("thursday_status") String thursday_status,
                                    @Field("thursday_from") String thursday_from,
                                    @Field("thursday_to") String thursday_to,
                                    @Field("friday_status") String friday_status,
                                    @Field("friday_from") String friday_from,
                                    @Field("friday_to") String friday_to,
                                    @Field("saturday_status") String saturday_status,
                                    @Field("saturday_from") String saturday_from,
                                    @Field("saturday_to") String saturday_to);


    @POST(UrlConstants.OFFER_ADD)
    @FormUrlEncoded
    Call<ResponseBody> offerAdd(@Field("mobile") String mobile,
                                        @Field("title") String device_type,
                                        @Field("description") String notification_id,
                                        @Field("from") String from,
                                        @Field("to") String to);

    @POST(UrlConstants.OFFER_ADD_MECHANIC)
    @FormUrlEncoded
    Call<ResponseBody> offerAdd_mech(@Field("mobile") String mobile,
                                        @Field("title") String device_type,
                                        @Field("description") String notification_id,
                                        @Field("from") String from,
                                        @Field("to") String to);

    @POST(UrlConstants.PAYMENT_METHOD_UPDATE)
    @FormUrlEncoded
    Call<ResponseBody> payment_update_method(@Field("mobile") String mobile,
                                             @Field("method") String method);

    @POST(UrlConstants.PAYMENT_METHOD_UPDATE_MECH)
    @FormUrlEncoded
    Call<ResponseBody> payment_update_method_mech(@Field("mobile") String mobile,
                                             @Field("method") String method);

    @POST(UrlConstants.SERVICE_UPDATE)
    @FormUrlEncoded
    Call<ResponseBody> service_update(@Field("mobile") String mobile,
                                      @Field("service") String service);

    @POST(UrlConstants.SERVICE_UPDATE_MECHANIC)
    @FormUrlEncoded
    Call<ResponseBody> service_update_mech(@Field("mobile") String mobile,
                                      @Field("service") String service);


    @POST(UrlConstants.ISSUE_UPDATE_MECHANIC)
    @FormUrlEncoded
    Call<ResponseBody> issue_update_mech(@Field("mobile") String mobile,
                                      @Field("vehicle_issue") String issue);

    @POST(UrlConstants.BRAND_UPDATE_MECHANIC)
    @FormUrlEncoded
    Call<ResponseBody> brand_update_mech(@Field("mobile") String mobile,
                                      @Field("vehicle_brand") String vehicle_brand);

    @POST(UrlConstants.AMB_USER_STATUS)
    @FormUrlEncoded
    Call<StatusUser> update_status(@Field("id") String id,
                                   @Field("status") String status);

    @POST(UrlConstants.MECHANIC_STATUS)
    @FormUrlEncoded
    Call<MechanicStatus> update_status_mechanic(@Field("id") String id,
                                                @Field("status") String status);

    @POST(UrlConstants.PETROL_PUMP_UPLOAD)
    @FormUrlEncoded
    Call<StatusUser> petrol_pump_upload(@Field("id") String id,
                                        @Field("status") String status);

    @POST(UrlConstants.AMB_USER_LOGIN)
    @FormUrlEncoded
    Call<LoginAmbulance> login_ambulance(@Field("mobile") String mobile,
                                         @Field("pin") String pin,
                                         @Field("device_id") String device_id,
                                         @Field("device_type") String device_type,
                                         @Field("notification_id") String notification_id);


    @POST(UrlConstants.MECHANIC_REGISTRATION_UPDATE)
    @FormUrlEncoded
    Call<ProfileUpdateMech> registrationUpdateMechanic(
                                         @Field("mobile") String mobile,
                                         @Field("name") String name,
                                         @Field("mobile_pin") String mobile_pin,
                                         @Field("organisation_name") String organisation_name,
                                         @Field("latitude") String latitude,
                                         @Field("longitude") String longitude,
                                         @Field("email") String email,
                                         @Field("gst_number") String gst_number,
                                         @Field("pan_number") String pan_number,
                                         @Field("phone") String phone,
                                         @Field("first_address") String first_address,
                                         @Field("city") String city,
                                         @Field("state") String state,
                                         @Field("country") String country,
                                         @Field("pincode") String pincode);


    @POST(UrlConstants.MECHANIC_LOGIN)
    @FormUrlEncoded
    Call<MechanicLogin> login_mechanic(@Field("mobile") String mobile,
                                       @Field("pin") String pin,
                                       @Field("device_id") String device_id,
                                       @Field("device_type") String device_type,
                                       @Field("notification_id") String notification_id);

    @GET(UrlConstants.SELECT_COUNRTY)
    Call<Country> getCounrty();

    @GET(UrlConstants.CANCEL_REASON_MECHANIC)
    Call<CancelReason> cancelReason();

    @GET(UrlConstants.CANCEL_REASON_AMBULANCE)
    Call<CancelReason> cancelReasonAmbulance();

    @GET(UrlConstants.SELECT_STATE)
    Call<State> getState(@QueryMap Map<String, String> params);

    @GET(UrlConstants.SELECT_CITY)
    Call<City> getCity(@QueryMap Map<String, String> params);

    @GET(UrlConstants.MECHANIC_BOOKING_ACTIVE)
    Call<BookingDetails> getAllDetail(@QueryMap Map<String, String> params);

    @GET(UrlConstants.MECHANIC_BOOKING)
    Call<BookingDetails> getMechBooking(@QueryMap Map<String, String> params);

    @GET(UrlConstants.AMBLULANCE_BOOKING)
    Call<BookingHistory> getAmbulanceBooking(@QueryMap Map<String, String> params);

    @GET(UrlConstants.ALL_BRANDS)
    Call<Brand> getAllBrands(@QueryMap Map<String, String> params);

    @POST(UrlConstants.MECHANIC_REQUEST_ACCEPT)
    @FormUrlEncoded
    Call<RequestAccept> mech_req_accept(@Field("id") String id,
                                        @Field("booking_id") String booking_id);

    @POST(UrlConstants.REQUEST_AMBULANCE)
    @FormUrlEncoded
    Call<RequestAccept>  req_acceptAmbulance(@Field("id") String id,
                                        @Field("booking_id") String booking_id);

    @POST(UrlConstants.MECHANIC_REQUEST_CANCEL)
    @FormUrlEncoded
    Call<CancelRequest> mech_req_cancel(@Field("id") String id,
                                        @Field("booking_id") String booking_id,
                                        @Field("reason") String reason);

    @POST(UrlConstants.AMBULANCE_REQUEST_CANCEL)
    @FormUrlEncoded
    Call<CancelRequest> ambulance_req_cancel(@Field("id") String id,
                                        @Field("booking_id") String booking_id,
                                        @Field("reason") String reason);

    @POST(UrlConstants.MECHANIC_BOOKING_STATUS)
    @FormUrlEncoded
    Call<B_Status> mech_booking_status(@Field("id") String id,
                                       @Field("booking_id") String booking_id,
                                       @Field("enroute_status") String reason);

    @POST(UrlConstants.AMBULANCE_BOOKING_STATUS)
    @FormUrlEncoded
    Call<B_Status> ambulance_booking_status(@Field("id") String id,
                                       @Field("booking_id") String booking_id,
                                       @Field("enroute_status") String reason);


    @POST(UrlConstants.MECHANIC_UPDATE_LOCTION)
    @FormUrlEncoded
    Call<LocUpdate> update_loc(@Field("id") String id,
                               @Field("latitude") String latitude,
                               @Field("longitude") String longitude);

    @POST(UrlConstants.AMB_UPDATE_LOCTION)
    @FormUrlEncoded
    Call<LocUpdate> update_loc_amb(@Field("id") String id,
                               @Field("latitude") String latitude,
                               @Field("longitude") String longitude);

}
