package com.electrom.vahanwireprovider.features;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.booking_detail_new.Billing;
import com.electrom.vahanwireprovider.models.booking_detail_new.BookingDetails;
import com.electrom.vahanwireprovider.models.booking_detail_new.Data;
import com.electrom.vahanwireprovider.models.booking_detail_new.Details;
import com.electrom.vahanwireprovider.models.booking_detail_new.UserDetails;
import com.electrom.vahanwireprovider.models.booking_status.B_Status;
import com.electrom.vahanwireprovider.models.cancel_reason_mech.CancelReason;
import com.electrom.vahanwireprovider.models.cancel_reason_mech.Datum;
import com.electrom.vahanwireprovider.models.cancel_request.CancelRequest;
import com.electrom.vahanwireprovider.models.city.City;
import com.electrom.vahanwireprovider.models.city.CityData;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.PicassoClient;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingStatusMechanic extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BookingStatusMechanic.class.getSimpleName();
    Context context = BookingStatusMechanic.this;
    SessionManager sessionManager;
    CustomTextView tvBookingMechanicName, tvBookingMechanicMobile, tvTitle;
    List<Datum> data;
    ArrayList<String> reason = new ArrayList();
    String cancelReason;

    LinearLayout llCointainerOnTheWay, llCointainerReachStart, llCointainerServiceDone,
            llCointainerCompalete, llDiration, llCallCointainer,llCointainerBilling;

    CircleImageView ivOnTheWay, ivReachStart, ivServiceDone, ivComplete, ivBilling;
    ImageView back;
    Double Latitude, Longitude;
    String contact;
    CustomTextView tvBookingMechanicDirection, tvCheckBill;
    CircleImageView ivMechanicProfile;
    Dialog cancelDiolog;
    CustomButton btnCancelMechanic;
    ImageView ivDir;
    String OTP;
    int routeCount = 0;
    String billing_url="";
    String billview="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_tracker);

        initView();

       getAllBookingDetail();
       getreason();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(context);
        llCointainerOnTheWay = findViewById(R.id.llCointainerOnTheWay);
        llCointainerReachStart = findViewById(R.id.llCointainerReachStart);
        llCointainerServiceDone= findViewById(R.id.llCointainerServiceDone);
        llCointainerCompalete = findViewById(R.id.llCointainerCompalete);
        llCallCointainer = findViewById(R.id.llCallCointainer);
        llCointainerBilling = findViewById(R.id.llCointainerBilling);
        tvBookingMechanicDirection = findViewById(R.id.tvBookingMechanicDirection);
        tvCheckBill = findViewById(R.id.tvCheckBill);
        llDiration = findViewById(R.id.llDiration);
        tvBookingMechanicName = findViewById(R.id.tvBookingMechanicName);
        tvBookingMechanicMobile = findViewById(R.id.tvBookingMechanicMobile);
        ivMechanicProfile = findViewById(R.id.ivMechanicProfile);
        btnCancelMechanic = findViewById(R.id.btnCancelMechanic);
        ivBilling = findViewById(R.id.ivBilling);
        tvTitle = findViewById(R.id.tvTitle);
        ivDir = findViewById(R.id.ivDir);
        back = findViewById(R.id.back);
        llCointainerOnTheWay.setOnClickListener(this);
        llCointainerReachStart.setOnClickListener(this);
        llCointainerServiceDone.setOnClickListener(this);
        llCointainerCompalete.setOnClickListener(this);
        llDiration.setOnClickListener(this);
        back.setOnClickListener(this);
        tvCheckBill.setOnClickListener(this);
        btnCancelMechanic.setOnClickListener(this);
        llCallCointainer.setOnClickListener(this);
        llCointainerBilling.setOnClickListener(this);

        ivOnTheWay = findViewById(R.id.ivOnTheWay);
        ivReachStart = findViewById(R.id.ivReachStart);
        ivServiceDone = findViewById(R.id.ivServiceDone);
        ivComplete = findViewById(R.id.ivComplete);
        llCointainerBilling = findViewById(R.id.llCointainerBilling);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.llCointainerOnTheWay:
                if(routeCount <  1)
                boobkingStatusOnTheWay();
                break;

            case R.id.llCointainerReachStart:
                if(routeCount == 1)
                verifyOtpPopup();
                break;

            case R.id.llCointainerServiceDone:
                if(routeCount == 2)
                bookingStatusServiceDone();
                break;

            case R.id.llCointainerBilling:
                if(routeCount == 3)
                    startActivity(new Intent(getApplicationContext(), WebViewMechBilling.class)
                    .putExtra("url", billing_url));
                Log.e(TAG, "onClick: " + billing_url );
                break;

            case R.id.llCointainerCompalete:
                if(routeCount == 4)
                boobkingStatusComplete();
                Log.e(TAG, "onClick: " + routeCount );
                break;

            case R.id.btnCancelMechanic:
                cancelPopup();
                break;

                case R.id.back:
                    Intent logout = new Intent(getApplicationContext(), BookingHistoryMechanic.class);
                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(logout);
                break;

                case R.id.tvCheckBill:
                    startActivity(new Intent(getApplicationContext(), BillingView.class)
                            .putExtra("url_view", billview));
                    Log.e(TAG, "onClick: " +routeCount + " :: url mini Quote : " +  billview);
                break;

                case R.id.llDiration:

                    String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", Latitude, Longitude, "");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);

                break;

                case R.id.llCallCointainer:

                    Dexter.withActivity(this)
                            .withPermission(Manifest.permission.CALL_PHONE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {

                                    Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
                                    startActivity(call);
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    showSettingsDialog();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                    token.continuePermissionRequest();
                                }
                            }).check();
                break;
        }
    }

    private void boobkingStatusOnTheWay()
    {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));
        Log.e(TAG, "booking id " + sessionManager.getString(SessionManager.BOOKING_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<B_Status> call = apiService.mech_booking_status(
                sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID), "1");

        call.enqueue(new Callback<B_Status>() {
            @Override
            public void onResponse(Call<B_Status> call, Response<B_Status> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    B_Status bookingStatus = response.body();

                    if(bookingStatus.getStatus().equals("200"))
                    {
                        routeCount = 1;
                        Log.d(TAG, "success " + bookingStatus.getMessage());
                        Log.d(TAG, "success " + bookingStatus.getMessage());
                        ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                        llDiration.setVisibility(View.VISIBLE);
                        btnCancelMechanic.setVisibility(View.VISIBLE);
                        llCointainerOnTheWay.setClickable(false);
                        llDiration.setClickable(true);
                        llDiration.setVisibility(View.VISIBLE);
                        llDiration.setTag("Show Location");
                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanProvider", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanProvider", "Network busy please try after sometime", "OK", BookingStatusMechanic.this);
                }
            }

            @Override
            public void onFailure(Call<B_Status> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanProvider", t.getMessage(), "OK", BookingStatusMechanic.this);
            }
        });

    }

    private void boobkingStatusReachNstart()
    {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<B_Status> call = apiService.mech_booking_status(
                sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID), "2");

        call.enqueue(new Callback<B_Status>() {
            @Override
            public void onResponse(Call<B_Status> call, Response<B_Status> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    B_Status bookingStatus = response.body();
                    if(bookingStatus.getStatus().equals("200"))
                    {
                        routeCount = 2;
                        Log.d(TAG, "success " + bookingStatus.getMessage());
                        ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                        ivReachStart.setImageResource(R.drawable.reach_start);
                        llDiration.setVisibility(View.INVISIBLE);
                        btnCancelMechanic.setVisibility(View.VISIBLE);
                        llCointainerOnTheWay.setClickable(false);
                        llCointainerReachStart.setClickable(false);
                        llDiration.setVisibility(View.VISIBLE);
                        ivDir.setVisibility(View.GONE);
                        tvBookingMechanicDirection.setText("Reached");
                        llDiration.setClickable(false);

                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", BookingStatusMechanic.this);
                }
            }

            @Override
            public void onFailure(Call<B_Status> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", BookingStatusMechanic.this);
            }
        });

    }

    private void bookingStatusServiceDone()
    {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<B_Status> call = apiService.mech_booking_status(
                sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID), "3");

        call.enqueue(new Callback<B_Status>() {
            @Override
            public void onResponse(Call<B_Status> call, Response<B_Status> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    B_Status bookingStatus = response.body();
                    if(bookingStatus.getStatus().equals("200"))
                    {
                        routeCount = 3;
                        ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                        ivReachStart.setImageResource(R.drawable.reach_start);
                        ivServiceDone.setImageResource(R.drawable.service_done_green);
                        Log.d(TAG, "success " + bookingStatus.getMessage());
                        llCointainerOnTheWay.setClickable(false);
                        llCointainerReachStart.setClickable(false);
                        llCointainerServiceDone.setClickable(false);
                        llDiration.setVisibility(View.INVISIBLE);
                        ivDir.setVisibility(View.GONE);
                        btnCancelMechanic.setVisibility(View.INVISIBLE);
                        llDiration.setClickable(false);
                       //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", BookingStatusMechanic.this);
                }
            }

            @Override
            public void onFailure(Call<B_Status> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", BookingStatusMechanic.this);
            }
        });

    }

    private void boobkingStatusComplete()
    {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<B_Status> call = apiService.mech_booking_status(
                sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID),
                "5");

        call.enqueue(new Callback<B_Status>() {
            @Override
            public void onResponse(Call<B_Status> call, Response<B_Status> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    B_Status bookingStatus = response.body();
                    if(bookingStatus.getStatus().equals("200"))
                    {
                        routeCount = 5;
                        Log.e(TAG, "success " + bookingStatus.getMessage());
                        ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                        ivReachStart.setImageResource(R.drawable.reach_start);
                        ivServiceDone.setImageResource(R.drawable.service_done_green);
                        ivComplete.setImageResource(R.drawable.complete_green);
                        llCointainerOnTheWay.setClickable(false);
                        llCointainerReachStart.setClickable(false);
                        llCointainerServiceDone.setClickable(false);
                        llCointainerCompalete.setClickable(false);
                        tvCheckBill.setVisibility(View.VISIBLE);
                        llDiration.setClickable(false);
                        llDiration.setVisibility(View.VISIBLE);
                        ivDir.setVisibility(View.GONE);
                        btnCancelMechanic.setVisibility(View.INVISIBLE);
                        tvBookingMechanicDirection.setText("Completed");

                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", bookingStatus.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", BookingStatusMechanic.this);
                }
            }

            @Override
            public void onFailure(Call<B_Status> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", BookingStatusMechanic.this);
            }
        });
    }

    private void getAllBookingDetail(){

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", sessionManager.getString(SessionManager.PROVIDER_ID));
        params.put("booking_id", sessionManager.getString(SessionManager.BOOKING_ID));

        Log.e(TAG, "getAllBookingDetail: id " +  sessionManager.getString(SessionManager.PROVIDER_ID));
        Log.e(TAG, "getAllBookingDetail: booking id " +  sessionManager.getString(SessionManager.BOOKING_ID));

        Call<BookingDetails> call = apiService.getMechBooking(params);

        call.enqueue(new Callback<BookingDetails>() {
            @Override
            public void onResponse(Call<BookingDetails> call, Response<BookingDetails> response) {

                Util.hideProgressDialog(progressDialog);

                BookingDetails bookingDetails = response.body();

              //  Log.e(TAG, "onResponse: " + bookingDetails.getStatus());

                if(response.isSuccessful())
                {

                    if(bookingDetails.getStatus().equals("200"))
                    {
                        Data data = bookingDetails.getData();

                        List<Billing> billing = data.getDetails().getBilling();
                        billing_url = bookingDetails.getData().getDetails().getBillingUrl();
                        if(billing.size()>0)
                        {
                            String billingAmount = billing.get(0).getBillingAmount();
                            String billingLink = billing.get(0).getBillImageLink();

                            Log.e(TAG, "onResponse: billing " + billingAmount);
                            Log.e(TAG, "onResponse: link " + billingLink);
                            Log.e(TAG, "onResponse: billing_url " + billing_url);
                            billview = bookingDetails.getData().getDetails().getBilling().get(0).getBillImageLink();
                        }

                        UserDetails userDetails = bookingDetails.getData().getUserDetails();
                        PicassoClient.downloadImage(context,userDetails.getProfilePic(),ivMechanicProfile);
                        Details details = data.getDetails();
                        contact = userDetails.getPhone();

                        try{
                            List<Double> coordinates = details.getUserLocation().getCoordinates();
                            Longitude = coordinates.get(0);
                            Latitude = coordinates.get(1);
                        }
                        catch (Exception e){}
                        /*Longitude = coordinates.get(0);
                        Latitude = coordinates.get(1);
                        Log.e(TAG, "onResponse: "+ Longitude+" - " + Latitude);*/
                        String status = details.getEnrouteStatus();
                        OTP =  details.getOtpVerify();
                        Log.e(TAG, "onResponse: otp mechanic" +  OTP);
                        Log.e(TAG, "onResponse: status mechanic" +  status);


                        if(status.equals("0")){
                            btnCancelMechanic.setVisibility(View.VISIBLE);
                            llDiration.setVisibility(View.INVISIBLE);
                        }
                        else if(status.equals("1")){

                            ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                            ivReachStart.setImageResource(R.drawable.reach_start_grey);
                            ivServiceDone.setImageResource(R.drawable.service_done);
                            ivComplete.setImageResource(R.drawable.complete);
                            tvBookingMechanicDirection.setText("Direction");
                            llDiration.setVisibility(View.VISIBLE);
                            btnCancelMechanic.setVisibility(View.VISIBLE);
                            llCointainerOnTheWay.setClickable(false);
                            llCointainerReachStart.setClickable(true);
                            llCointainerServiceDone.setClickable(true);
                            llCointainerCompalete.setClickable(true);
                            routeCount = 1;

                        }
                        else if(status.equals("2")){

                            ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                            ivReachStart.setImageResource(R.drawable.reach_start);
                            ivServiceDone.setImageResource(R.drawable.service_done);
                            ivComplete.setImageResource(R.drawable.complete);
                            tvBookingMechanicDirection.setText("Direction");
                            llDiration.setVisibility(View.INVISIBLE);
                            btnCancelMechanic.setVisibility(View.VISIBLE);
                            llCointainerOnTheWay.setClickable(false);
                            llCointainerReachStart.setClickable(false);
                            llCointainerServiceDone.setClickable(true);
                            llCointainerCompalete.setClickable(true);
                            routeCount = 2;
                        }
                        else if(status.equals("3")){

                            ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                            ivReachStart.setImageResource(R.drawable.reach_start);
                            ivServiceDone.setImageResource(R.drawable.service_done_green);
                            ivComplete.setImageResource(R.drawable.complete);
                            tvBookingMechanicDirection.setText("Direction");
                            llDiration.setVisibility(View.INVISIBLE);
                            btnCancelMechanic.setVisibility(View.INVISIBLE);
                            llCointainerOnTheWay.setClickable(false);
                            llCointainerReachStart.setClickable(false);
                            llCointainerServiceDone.setClickable(false);
                            llCointainerBilling.setClickable(true);
                            routeCount = 3;

                        }
                        else if(status.equals("4")){

                            ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                            ivReachStart.setImageResource(R.drawable.reach_start);
                            ivServiceDone.setImageResource(R.drawable.service_done_green);
                            //ivComplete.setImageResource(R.drawable.complete_green);
                            ivBilling.setImageResource(R.drawable.bill_green_mechanic);
                            tvBookingMechanicDirection.setText("Complete");
                            tvBookingMechanicDirection.setClickable(false);
                            tvCheckBill.setVisibility(View.VISIBLE);
                            llDiration.setVisibility(View.VISIBLE);
                            ivDir.setVisibility(View.GONE);
                            llDiration.setClickable(false);
                            btnCancelMechanic.setVisibility(View.INVISIBLE);
                            llCointainerOnTheWay.setClickable(false);
                            llCointainerReachStart.setClickable(false);
                            llCointainerServiceDone.setClickable(false);
                            llCointainerCompalete.setClickable(true);
                            tvTitle.setText("Completed / Cancelled service");

                            routeCount = 4;
                        }

                        else if(status.equals("5")){

                            ivOnTheWay.setImageResource(R.drawable.on_the_way_green);
                            ivReachStart.setImageResource(R.drawable.reach_start);
                            ivServiceDone.setImageResource(R.drawable.service_done_green);
                            ivComplete.setImageResource(R.drawable.complete_green);
                            ivBilling.setImageResource(R.drawable.bill_green_mechanic);
                            tvBookingMechanicDirection.setText("Complete");
                            tvBookingMechanicDirection.setClickable(false);
                            tvCheckBill.setVisibility(View.VISIBLE);
                            llDiration.setVisibility(View.VISIBLE);
                            ivDir.setVisibility(View.GONE);
                            llDiration.setClickable(false);
                            btnCancelMechanic.setVisibility(View.INVISIBLE);
                            llCointainerOnTheWay.setClickable(false);
                            llCointainerReachStart.setClickable(false);
                            llCointainerServiceDone.setClickable(false);
                            llCointainerCompalete.setClickable(false);
                            tvTitle.setText("Completed / Cancelled service");
                            routeCount = 5;

                        }


                        tvBookingMechanicName.setText(userDetails.getFullname());
                        tvBookingMechanicMobile.setText(contact);
                        if(bookingDetails.getData().getDetails().getBookingStatus().getUser().getStatus().equals("2"))
                        {
                            llDiration.setVisibility(View.VISIBLE);
                            llDiration.setClickable(false);
                            ivDir.setVisibility(View.GONE);
                            tvBookingMechanicDirection.setText("Cancelled By User");
                            tvBookingMechanicDirection.setTextColor(Color.parseColor("#FF0000"));
                            llCointainerOnTheWay.setClickable(false);
                            llCointainerReachStart.setClickable(false);
                            llCointainerServiceDone.setClickable(false);
                            llCointainerCompalete.setClickable(false);
                            btnCancelMechanic.setVisibility(View.INVISIBLE);
                            tvTitle.setText("Completed / Cancelled service");
                        }
                        else if(bookingDetails.getData().getDetails().getBookingStatus().getMechanic().getStatus().equals("2"))
                        {
                            llDiration.setVisibility(View.VISIBLE);
                            llDiration.setClickable(false);
                            ivDir.setVisibility(View.GONE);
                            tvBookingMechanicDirection.setTextColor(Color.parseColor("#FF0000"));
                            tvBookingMechanicDirection.setText("Cancelled By You");
                            llCointainerOnTheWay.setClickable(false);
                            llCointainerReachStart.setClickable(false);
                            llCointainerServiceDone.setClickable(false);
                            llCointainerCompalete.setClickable(false);
                            btnCancelMechanic.setVisibility(View.INVISIBLE);
                            tvTitle.setText("Completed / Cancelled service");
                        }
                        // CountryAdapter adapter = new CountryAdapter(ProfileActivity.this, list);
                    }

                    else if(bookingDetails.getStatus().equals("404"))
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire","No booking found", "OK", BookingStatusMechanic.this);
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", bookingDetails.getMessage(), "OK", BookingStatusMechanic.this);
                    }

                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", bookingDetails.getMessage(), "OK", BookingStatusMechanic.this);
                }

                Log.e("service", response.body().getStatus());
            }

            @Override
            public void onFailure(Call<BookingDetails> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e("service failier", t.getMessage());
                t.printStackTrace();
                //ActionForAll.alertUserWithCloseActivity("VahanWire", "No active bookin found", "OK", BookingStatusMechanic.this);

            }
        });

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void getreason(){

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", sessionManager.getString(SessionManager.PROVIDER_ID));
        params.put("booking_id", sessionManager.getString(SessionManager.BOOKING_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelReason> call = apiService.cancelReasonBooking(params);
        call.enqueue(new Callback<CancelReason>() {
            @Override
            public void onResponse(Call<CancelReason> call, Response<CancelReason> response) {

                Util.hideProgressDialog(progressDialog);

                CancelReason cancelReason = response.body();

                if(response.isSuccessful())
                {

                    if(cancelReason.getStatus().equals("200"))
                    {
                        data = cancelReason.getData();
                        for(Datum datum : data)
                        {
                            String name = datum.getName();
                            reason.add(name);
                            Log.e(TAG, "onResponse: "+ name);
                        }
                    }
                    else
                    {

                    }
                }
                else
                {

                }

                Log.e("service", response.body().getStatus());

            }

            @Override
            public void onFailure(Call<CancelReason> call, Throwable t) {
                Log.e("service failier", t.getMessage());
            }
        });

    }

    public void cancelPopup() {

        cancelDiolog = new Dialog(BookingStatusMechanic.this);
        cancelDiolog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDiolog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancelDiolog.setContentView(R.layout.cancel_popup_lay);
        cancelDiolog.getWindow().getAttributes().windowAnimations = R.style.diologIntertnet;
        cancelDiolog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        /*RelativeLayout layNoCustomer = cancelDiolog.findViewById(R.id.layNoCustomer);
        RelativeLayout layvichelBroke = cancelDiolog.findViewById(R.id.layvichelBroke);
        RelativeLayout layResNoList = cancelDiolog.findViewById(R.id.layResNoList);
        RelativeLayout layForgotTools = cancelDiolog.findViewById(R.id.layForgotTools);*/
        RelativeLayout laySubmit = cancelDiolog.findViewById(R.id.laySubmit);

        /*final RadioButton radioNoCustomer = cancelDiolog.findViewById(R.id.radioNoCustomer);
        final RadioButton radioVichelBroker = cancelDiolog.findViewById(R.id.radioVichelBroker);
        final RadioButton radioNoRes =  cancelDiolog.findViewById(R.id.radioNoRes);
        final RadioButton radioForgotTools =  cancelDiolog.findViewById(R.id.radioForgotTools);

        final TextView txtNoCustomer =  cancelDiolog.findViewById(R.id.txtNoCustomer);
        final TextView txtVehicelBroke =  cancelDiolog.findViewById(R.id.txtVehicelBroke);
        final TextView txtNoRes =  cancelDiolog.findViewById(R.id.txtNoRes);
        final TextView txtForgotTools =  cancelDiolog.findViewById(R.id.txtForgotTools);*/
        cancelReason = "";

        ListView list = cancelDiolog.findViewById(R.id.listReason);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(BookingStatusMechanic.this,android.R.layout.simple_list_item_single_choice, reason);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cancelReason = parent.getItemAtPosition(position).toString();
                Log.e(TAG, "onItemClick: " + cancelReason);

            }
        });

       /* layNoCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioNoCustomer.setChecked(true);
                radioVichelBroker.setChecked(false);
                radioNoRes.setChecked(false);
                radioForgotTools.setChecked(false);
                cancelReason = txtNoCustomer.getText().toString();
            }
        });
        layvichelBroke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioNoCustomer.setChecked(false);
                radioVichelBroker.setChecked(true);
                radioNoRes.setChecked(false);
                radioForgotTools.setChecked(false);
                cancelReason = txtVehicelBroke.getText().toString();
            }
        });
        layResNoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioNoCustomer.setChecked(false);
                radioVichelBroker.setChecked(false);
                radioNoRes.setChecked(true);
                radioForgotTools.setChecked(false);
                cancelReason = txtNoRes.getText().toString();
            }
        });
        layForgotTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioNoCustomer.setChecked(false);
                radioVichelBroker.setChecked(false);
                radioNoRes.setChecked(false);
                radioForgotTools.setChecked(true);
                cancelReason = txtForgotTools.getText().toString();
            }
        });*/


        laySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancelReason.equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Please select reason for cancel", Toast.LENGTH_LONG).show();
                } else {
                    if (ActionForAll.isNetworkAvailable(context)) {
                        requestCancel(cancelReason);
                    }
                }
            }
        });
        cancelDiolog.show();
    }

    private void requestCancel(String reason) {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelRequest> call = apiService.mech_req_cancel(sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID),reason);

        call.enqueue(new Callback<CancelRequest>() {
            @Override
            public void onResponse(Call<CancelRequest> call, Response<CancelRequest> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    CancelRequest cancelRequest = response.body();
                    if(cancelRequest.getStatus().equals("200"))
                    {
                        sessionManager.setString(SessionManager.BOOKING_STATUS, "");
                        Log.d(TAG, "success " + cancelRequest.getMessage());
                        ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", BookingStatusMechanic.this);

                        if(cancelDiolog.isShowing())
                        {
                            cancelDiolog.dismiss();
                        }

                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", cancelRequest.getMessage(), "OK", BookingStatusMechanic.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", BookingStatusMechanic.this);
                }
            }

            @Override
            public void onFailure(Call<CancelRequest> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", BookingStatusMechanic.this);
            }
        });

    }

    public void verifyOtpPopup() {

        final Dialog verifyDiolog = new Dialog(BookingStatusMechanic.this);
        verifyDiolog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyDiolog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyDiolog.setContentView(R.layout.verify_otp_popup);
        verifyDiolog.getWindow().getAttributes().windowAnimations = R.style.diologIntertnet;
        verifyDiolog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        final CustomEditText etVerifyOtpBooking = verifyDiolog.findViewById(R.id.etVerifyOtpBooking);
        CustomButton btnVerifyOtpBookingCancel = verifyDiolog.findViewById(R.id.btnVerifyOtpBookingCancel);
        CustomButton btnVerifyOtpBookingVerify = verifyDiolog.findViewById(R.id.btnVerifyOtpBookingVerify);

        btnVerifyOtpBookingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyDiolog.dismiss();
            }
        });

        btnVerifyOtpBookingVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etVerifyOtpBooking.getText().toString().trim().equals(OTP))
                {
                    verifyDiolog.dismiss();
                    boobkingStatusReachNstart();
                }
                else
                {
                    etVerifyOtpBooking.setText("");
                    ActionForAll.alertUser("VahanWire", "OTP not matched", "OK", BookingStatusMechanic.this);
                }

            }
        });

        verifyDiolog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == event.KEYCODE_BACK)
        {
            Intent logout = new Intent(getApplicationContext(), BookingHistoryMechanic.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logout);
        }
        return super.onKeyDown(keyCode, event);
    }

}
