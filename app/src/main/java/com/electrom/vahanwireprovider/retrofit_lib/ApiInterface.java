package com.electrom.vahanwireprovider.retrofit_lib;

import com.electrom.vahanwireprovider.models.detail.Detail;
import com.electrom.vahanwireprovider.models.login.Login;
import com.electrom.vahanwireprovider.models.payment.Payment;
import com.electrom.vahanwireprovider.models.services.Service;
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

    @POST(UrlConstants.VERIFY_MOB_PROVIDER)
    @FormUrlEncoded
    Call<ResponseBody> verifyProvider(@Field("mobile") String mobile,
                                      @Field("otp") String otp);

    @POST(UrlConstants.LOGIN)
    @FormUrlEncoded
    Call<Login> login(@Field("mobile") String mobile,
                      @Field("pin") String pin);

    @GET(UrlConstants.PROVIDER_DETAIL)
    Call<Detail> getUpdatedDetail(@QueryMap Map<String, String> params);

    @GET(UrlConstants.ALL_SERVICES)
    Call<Service> getAllServices(@QueryMap Map<String, String> params);

    @GET(UrlConstants.ALL_PAYMENT_METHOD)
    Call<Payment> getAllPaymentMethod(@QueryMap Map<String, String> params);


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

    @POST(UrlConstants.PAYMENT_METHOD_UPDATE)
    @FormUrlEncoded
    Call<ResponseBody> payment_update_method(@Field("mobile") String mobile,
                                             @Field("method") String method);

    @POST(UrlConstants.SERVICE_UPDATE)
    @FormUrlEncoded
    Call<ResponseBody> service_update(@Field("mobile") String mobile,
                                      @Field("service") String service);

}
