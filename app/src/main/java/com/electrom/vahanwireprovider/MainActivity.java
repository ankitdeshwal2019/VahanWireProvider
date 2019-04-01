package com.electrom.vahanwireprovider;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.electrom.vahanwireprovider.features.BookingStatusActivity;
import com.electrom.vahanwireprovider.location_service.GPSTracker;
import com.electrom.vahanwireprovider.models.detail.Detail;
import com.electrom.vahanwireprovider.models.detail.Friday;
import com.electrom.vahanwireprovider.models.detail.Offer;
import com.electrom.vahanwireprovider.models.detail.WorkingHours;
import com.electrom.vahanwireprovider.models.update_profile.Datum;
import com.electrom.vahanwireprovider.models.update_profile.Update;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CodeMinimisations;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.MyHandler;
import com.electrom.vahanwireprovider.utility.PicassoClient;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    GPSTracker gps;
    private static final String TAG = MainActivity.class.getSimpleName();
    CustomTextView tvTotalVisitorsCount;
    SessionManager sessionManager;
    private int count_navitaion_user = 0;
    CustomButton btnLocationUpdate;
    Double Latitude, Longitude;
    Handler handler;
    String faltuCheck = "auto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = SessionManager.getInstance(this);
        setUpLayoutWithToolbar();
        initView();
        //startLocationUpdate();
        //requestPopup();

    }

    private void initView() {
        handler = new MyHandler();
        tvTotalVisitorsCount = findViewById(R.id.tvTotalVisitorsCount);
        btnLocationUpdate = findViewById(R.id.btnLocationUpdate);
        btnLocationUpdate.setOnClickListener(this);
        getDetail();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);
    }

    private void setUpLayoutWithToolbar() {

        initToolbar();

        ImageView nav = findViewById(R.id.ivDrawer);
        /*CustomTextView title = findViewById(R.id.tvToolClick);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionForAll.alertUser("VahanWire", "Work in Progress", "OK", MainActivity.this);
            }
        });*/

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        View navHeader = mNavigationView.getHeaderView(0);
        CircleImageView img =  navHeader.findViewById(R.id.ivNavProfile);
        CustomTextView nav_name = navHeader.findViewById(R.id.tvNavName);
        CustomTextView nav_email = navHeader.findViewById(R.id.tvNavEmail);
        //Picasso.with(this).load(sessionManager.getString(SessionManager.PROVIDER_IMAGE)).into(img);
       /* nav_name.setText("ANKIT");
        nav_email.setText("A@GMAIL.COM");*/
        PicassoClient.downloadImage(this, sessionManager.getString(SessionManager.PROVIDER_IMAGE), img);
        nav_name.setText(sessionManager.getString(SessionManager.REGISTER_NAME));
        nav_email.setText(sessionManager.getString(SessionManager.EMAIL));


        Menu nav_Menu = mNavigationView.getMenu();
        if(sessionManager.getString(SessionManager.SERVICE).equalsIgnoreCase(Constant.SERVICE_PETROL_PUMP))
        {
            nav_Menu.findItem(R.id.nav_booking).setVisible(false);
            nav_Menu.findItem(R.id.nav_Serviceable_brand).setVisible(false);
            nav_Menu.findItem(R.id.nav_Service).setVisible(false);
        }
        else
        {
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);
        }

        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        CodeMinimisations.navListener(nav, mNavigationView, mDrawerLayout, this);
        CodeMinimisations.navigationItemListener(mNavigationView, this);
    }

    private void getDetail() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        Call<Detail> call = apiService.getUpdatedDetail(params);
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                Detail detail = response.body();
                Log.e(TAG, detail.getStatus());
                if (detail.getStatus().equals("200")) {
                    Util.hideProgressDialog(progressDialog);
                    Log.d(TAG, detail.getData().getMobile() + "-pin-" + detail.getData().getMobilePin());
                    count_navitaion_user = detail.getData().getNavigatedUsers().size();
                    tvTotalVisitorsCount.setText(String.valueOf(count_navitaion_user));

                    Log.d(TAG, "onResponse: " + "  message " + detail.getMessage());
                    Log.d(TAG, "onResponse: " + "  mobile " + detail.getData().getMobile());
                    Log.d(TAG, "onResponse: " + "  pin " + "--" + detail.getData().getMobilePin());
                    Log.d(TAG, "onResponse: " + " first address " + "--" + detail.getData().getAddress().getFirstAddress());
                    Log.d(TAG, "onResponse: " + "contact person " + "--" + detail.getData().getContactPerson());
                    Log.d(TAG, "onResponse: " + "phone number" + "--" + detail.getData().getPhone());
                    List<Double> list = detail.getData().getAddress().getLocation().getCoordinates();
                    Double longitude = list.get(0).doubleValue();
                    Double latitude = list.get(1).doubleValue();
                    Log.d(TAG, "onResponse: " + "longitude" + "--" + longitude);
                    Log.d(TAG, "onResponse: " + "longitude" + "--" + latitude);
                    Log.d(TAG, "onResponse: " + " image " + "--" + detail.getData().getProfilePic());

                    sessionManager.setString(SessionManager.PROVIDER_MOBILE, detail.getData().getMobile());
                    sessionManager.setString(SessionManager.PROVIDER_PIN, detail.getData().getMobilePin());
                    sessionManager.setString(SessionManager.REGISTER_NAME, detail.getData().getContactPerson());
                    sessionManager.setString(SessionManager.EMAIL, detail.getData().getEmail());
                    sessionManager.setString(SessionManager.ADDRESS, detail.getData().getAddress().getFirstAddress());
                    sessionManager.setString(SessionManager.CONTACT_PERSON,detail.getData().getRegisteredName());

                    //sessionManager.setString(SessionManager.CONTACT_PERSON, list.get(0).getRegisteredName());
                    sessionManager.setString(SessionManager.LANDLINE, detail.getData().getPhone());
                    sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                    sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                    sessionManager.setString(SessionManager.PROVIDER_IMAGE, detail.getData().getProfilePic());
                    sessionManager.setString(SessionManager.CITY, detail.getData().getAddress().getCity());
                    sessionManager.setString(SessionManager.STATE, detail.getData().getAddress().getState());
                    sessionManager.setString(SessionManager.COUNRTY, detail.getData().getAddress().getCountry());
                    sessionManager.setString(SessionManager.PINCODE, detail.getData().getAddress().getPincode());

                    WorkingHours workingHours = detail.getData().getWorkingHours();
                    sessionManager.setString(SessionManager.WORK_FROM,workingHours.getFrom());
                    sessionManager.setString(SessionManager.WORK_TO,workingHours.getTo());

                    List<Offer> offers = detail.getData().getOffers();
                     Offer offer = null;
                    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (offers.size() > 0) {
                        offer = offers.get(offers.size() - 1);

                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(offer);
                        prefsEditor.putString("Offer", json);
                        prefsEditor.apply();
                        //sessionManager.setObject("Myobj", offer);
                    }
                    else
                    {
                        appSharedPrefs.edit().remove("Offer").commit();

                    }
                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e(TAG, "error" + t.getMessage());
            }
        });
    }

    private void startLocationUpdate() {

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            if (!isMyServiceRunning(GPSTracker.class))
                                startService(new Intent(getApplicationContext(), GPSTracker.class));
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnLocationUpdate) {
            gps = new GPSTracker(getApplicationContext(), MainActivity.this);
            // Check if GPS enabled
            if (gps.canGetLocation()) {
                Latitude = gps.getLatitude();
                Longitude = gps.getLongitude();
                Log.d("onClick: ", Latitude.toString());
                Log.d("onClick: ", Longitude.toString());

                if(Latitude > 0.1 && Longitude > 0.1)
                {
                    Log.d(TAG, "onClick: " + "location found " + Longitude + "/" + Longitude);
                    sessionManager.setString(SessionManager.LONGITUDE, Longitude+"");
                    sessionManager.setString(SessionManager.LATITUDE, Latitude+"");
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses = null;

                    try {
                        addresses = geocoder.getFromLocation(Latitude, Longitude, 1);
                        if (addresses != null && addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0);

                            new AlertDialog.Builder(this).setTitle("Current Location")
                                    .setMessage(address+ "\n\n" + "Are you sure to do update your location?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Log.d(TAG, "onClick: " + "updated");
                                            compaleteRegistration();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            Log.d(TAG, "onClick: " + "no update");
                                        }
                                    }).create().show();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("Location", "Please enable GPS", "OK", MainActivity.this);
                }
            }
            else
            {
               gps.showSettingsAlert(this);
            }
        }
    }

    private void compaleteRegistration() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Update> call = apiService.registrationUpdate(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                sessionManager.getString(SessionManager.PROVIDER_PIN),
                sessionManager.getString(SessionManager.REGISTER_NAME),
                sessionManager.getString(SessionManager.CONTACT_PERSON),
                sessionManager.getString(SessionManager.LANDLINE),
                sessionManager.getString(SessionManager.EMAIL),"","","",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN),"",
                sessionManager.getString(SessionManager.ADDRESS),
                "","","","",
                sessionManager.getString(SessionManager.LATITUDE),
                sessionManager.getString(SessionManager.LONGITUDE),
                "",
                "","","","","",
                "","","","","","","","","","" +
                        "","","","","","","","");

        call.enqueue(new Callback<Update>() {
            @Override
            public void onResponse(Call<Update> call, Response<Update> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    Update detail = response.body();
                        if(detail.getStatus().equals("200"))
                        {
                            List<Datum> list = detail.getData();
                            sessionManager.setString(SessionManager.PROVIDER_MOBILE, list.get(0).getMobile());
                            sessionManager.setString(SessionManager.PROVIDER_PIN, list.get(0).getMobilePin());
                            sessionManager.setString(SessionManager.REGISTER_NAME, list.get(0).getRegisteredName());
                            sessionManager.setString(SessionManager.EMAIL, list.get(0).getEmail());
                            sessionManager.setString(SessionManager.ADDRESS, list.get(0).getAddress().getFirstAddress());
                            sessionManager.setString(SessionManager.CONTACT_PERSON,list.get(0).getContactPerson());
                            sessionManager.setString(SessionManager.LANDLINE, list.get(0).getPhone());
                            sessionManager.setString(SessionManager.PROVIDER_IMAGE, list.get(0).getProfilePic());
                            List<Double> listLngLat = list.get(0).getAddress().getLocation().getCoordinates();
                            Log.d(TAG, listLngLat.get(1).doubleValue()+" latitude");
                            Log.d(TAG, listLngLat.get(0).doubleValue()+" longitude");
                            sessionManager.setString(SessionManager.LATITUDE, listLngLat.get(1).doubleValue()+"");
                            sessionManager.setString(SessionManager.LONGITUDE, listLngLat.get(0).doubleValue()+"");
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", detail.getMessage(), "OK", MainActivity.this);
                        }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Update> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(getApplicationContext(), GPSTracker.class));
    }

    public void getLocationFromAddress(String strAddress)
    {
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress,5);

            //check for null
            if (address == null) {
                return;
            }

            //Lets take first possibility from the all possibilities.
            Address location=address.get(0);
            Log.d(TAG, "d_lat" + location.getLatitude());
            Log.d(TAG, "d_lng" + location.getLongitude());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sessionManager.setString(SessionManager.NOTIFICATION_TOKEN, token);
                        Log.d(TAG, "token " + token);
                        //ActionForAll.myFlash(getApplicationContext(), token);
                    }
                });
    }


    public void requestPopup() {
        // handler.removeCallbacks(n);
//        handler.removeCallbacksAndMessages(null);
//        pDialog = new ProgressDialog(getActivity());
//        // Showing progress dialog before making http request
//        pDialog.setMessage("Loading...");
//        pDialog.setCancelable(false);
//        pDialog.setCanceledOnTouchOutside(false);
//        pDialog.show();
        final Dialog requestPopup = new Dialog(MainActivity.this);
        // faltuCheck = "auto";
        requestPopup.setContentView(R.layout.dialog_request_popup);
        //requestPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestPopup.getWindow().getAttributes().windowAnimations = R.style.diologIntertnet;
        requestPopup.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        requestPopup.setCanceledOnTouchOutside(false);
        requestPopup.setCancelable(false);
        final CustomTextView tv_popup_request_timer = requestPopup.findViewById(R.id.tv_popup_request_timer);
        final CustomButton btnPopupRequestReject = requestPopup.findViewById(R.id.btnPopupRequestReject);
        final CustomButton btnPopupRequestAccept = requestPopup.findViewById(R.id.btnPopupRequestAccept);
        btnPopupRequestAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookingStatusActivity.class));
                requestPopup.dismiss();
                finish();
            }
        });

        btnPopupRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionForAll.myFlash(getApplicationContext(), "reject button click");
                requestPopup.dismiss();
            }
        });

        /* TextView btnAccpet = requestPopup.findViewById(R.id.txtAccept);
        TextView btnReject =  requestPopup.findViewById(R.id.txtReject);
        txtCount =  requestPopup.findViewById(R.id.countdown_text);
        txtAddress = requestPopup.findViewById(R.id.txtAddress);
        txtAccept =  requestPopup.findViewById(R.id.txtAccept);
        txtReject =  requestPopup.findViewById(R.id.txtReject);
        pBar2 =  requestPopup.findViewById(R.id.ProgressBar);*/

        /* if(sessionManager.getString(SessionManager.AMB_LOGIN_STATUS).equals("1"))
        {
            HomeFragment.btnEnable.setVisibility(View.INVISIBLE);
            HomeFragment.btnDisable.setVisibility(View.VISIBLE);
        }
        else
        {
            HomeFragment.btnEnable.setVisibility(View.VISIBLE);
            HomeFragment.btnDisable.setVisibility(View.INVISIBLE);
        }*/

        /*
        btnAccpet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPopup.dismiss();
            }
        });
        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPopup.dismiss();

            }
        });
        txtAddress.setText(cust_search_area);
        txtAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ConnectionDetector.networkStatus(getApplicationContext())) {
                    diologInternet.show();
                } else {
                    acceptByProvider();
                    HomeFragment.layCustomerDetails.setVisibility(View.VISIBLE);
                    HomeFragment.txtUSerReq.setText("EMERGENCY - AMBULANCE REQUEST");
                    HomeFragment.txtReqStatus.setText("Accepted");
                    HomeFragment.txtUSerName.setText(sessionManager.getString(SessionManager.BOOKING_CUSTOMER));
                    HomeFragment.txtUSerAddress.setText(sessionManager.getString(SessionManager.BOOKING_ADDRESS));
                    HomeFragment.txtUSerJobID.setText("Booking Id - " + sessionManager.getString(SessionManager.BOOKING_ID));
                    HomeFragment.btnDisable.setVisibility(View.INVISIBLE);
                    HomeFragment.btnEnable.setVisibility(View.INVISIBLE);
                    HomeFragment.layCustomerDet.setVisibility(View.VISIBLE);
                    HomeFragment.layUser.setVisibility(View.VISIBLE);
                    requestPopup.dismiss();
                }
            }
        });
        txtReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ConnectionDetector.networkStatus(getApplicationContext())) {
                    diologInternet.show();
                } else {
                    cancelByProvider();
                    HomeFragment.layCustomerDetails.setVisibility(View.VISIBLE);
                    HomeFragment.txtUSerReq.setText("EMERGENCY - AMBULANCE REQUEST");
                    HomeFragment.txtUSerName.setText(sessionManager.getString(SessionManager.BOOKING_CUSTOMER));
                    HomeFragment.txtUSerAddress.setText(sessionManager.getString(SessionManager.BOOKING_ADDRESS));
                    HomeFragment.txtUSerJobID.setText("Booking Id - " + sessionManager.getString(SessionManager.BOOKING_ID));
                    HomeFragment.txtReqStatus.setText("Canceled");
                    HomeFragment.btnDisable.setVisibility(View.INVISIBLE);
                    HomeFragment.btnEnable.setVisibility(View.INVISIBLE);
                    HomeFragment.layCustomerDet.setVisibility(View.VISIBLE);
                    HomeFragment.layUser.setVisibility(View.VISIBLE);
                    requestPopup.dismiss();
                }
            }
        });*/

        if(!(MainActivity.this.isFinishing()))
        {
           CountDownTimer timer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    //pBar2.setProgress((int) (millisUntilFinished));
                    tv_popup_request_timer.setText(millisUntilFinished / 1000 + " seconds left");
                }
                public void onFinish() {
                    requestPopup.dismiss();
                    if (!ActionForAll.isNetworkAvailable(getApplicationContext())) {
                        //diologInternet.show();
                    } else {
                        if (faltuCheck.equalsIgnoreCase("auto")) {
                            ActionForAll.myFlash(getApplicationContext(), "cancel");
                            //rejectReequest();
                            requestPopup.dismiss();
                            handler.removeCallbacks(null);
                            finish();
                        }
                        else {
                        }
                    }
                }
            }.start();
            requestPopup.show();
        }
    }

}
