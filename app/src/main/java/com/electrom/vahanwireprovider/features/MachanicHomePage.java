package com.electrom.vahanwireprovider.features;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.location_service_update_track.MyForeGroundService;
import com.electrom.vahanwireprovider.models.booking_detail_new.BookingDetails;
import com.electrom.vahanwireprovider.models.booking_detail_new.Data;
import com.electrom.vahanwireprovider.models.cancel_reason_mech.CancelReason;
import com.electrom.vahanwireprovider.models.cancel_reason_mech.Datum;
import com.electrom.vahanwireprovider.models.cancel_request.CancelRequest;
import com.electrom.vahanwireprovider.models.mech_status.MechanicStatus;
import com.electrom.vahanwireprovider.models.mechanic_new.MechNewDetail;
import com.electrom.vahanwireprovider.models.mechanic_new.Myimages;
import com.electrom.vahanwireprovider.models.mechanic_new.Offer;
import com.electrom.vahanwireprovider.models.mechanic_new.PersonalDetails;
import com.electrom.vahanwireprovider.models.mechanic_new.WorkingHours;
import com.electrom.vahanwireprovider.models.request_accept.RequestAccept;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CodeMinimisations;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomTextBold;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.MyHandler;
import com.electrom.vahanwireprovider.utility.PicassoClient;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachanicHomePage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MachanicHomePage.class.getSimpleName();
    Context context = MachanicHomePage.this;
    SessionManager sessionManager;
    CustomButton banStatusOnMechanic, banStatusOffMechanic;
    CustomTextView tvStatusMechanic;
    CustomTextBold tvTotalVisitorsCountMechanic;
    String loginStatus = "0";
    String ACTIVE_STATUS_ON = "ON";
    String ACTIVE_STATUS_OFF = "OFF";
    MyHandler handler;
    String faltuCheck = "auto", cancelReason;
    Dialog requestPopup, cancelDiolog;
    CountDownTimer timer;
    List<Datum> data;
    ArrayList<String> reason = new ArrayList();
    public static String id1 = "provider_location_update";
    LocationManager manager;
    boolean isGPSEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machanic_home_page_nav);
        initView();
        getDetail();
        try {
            setUpLayoutWithToolbar();
        } catch (IllegalArgumentException e) {
        }
    }

    private void initView() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        handler = new MyHandler();
        sessionManager = SessionManager.getInstance(this);
        banStatusOnMechanic = findViewById(R.id.banStatusOnMechanic);
        banStatusOffMechanic = findViewById(R.id.banStatusOffMechanic);
        tvTotalVisitorsCountMechanic = findViewById(R.id.tvTotalVisitorsCountMechanic);
        banStatusOffMechanic.setOnClickListener(this);
        banStatusOnMechanic.setOnClickListener(this);
        tvStatusMechanic = findViewById(R.id.tvStatusMechanic);
        loginStatus = sessionManager.getString(SessionManager.ACTIVE_STATUS);
        createchannel();
        Log.e(TAG, sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));
        Log.e(TAG, "initView: pro_id " + sessionManager.getString(SessionManager.PROVIDER_ID));
        getreason();

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //requestPopup();

        if (sessionManager.getString(SessionManager.BOOKING_STATUS).equals("0") &&
                !sessionManager.getString(SessionManager.BOOKING_STATUS_USER).equals("2") &&
                !sessionManager.getString(SessionManager.NOTI_ISSUE).equalsIgnoreCase("Pre Request")) {
                 Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.BOOKING_ID));
                 Log.e(TAG, "initView: " + sessionManager.getString(SessionManager.BOOKING_STATUS));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestPopup();
                }
            }, 500);
        } else if (sessionManager.getString(SessionManager.BOOKING_STATUS_USER).equals("2")) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Request cancelled by user.", "OK", MachanicHomePage.this);
                }
            }, 500);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarCustom);
        setSupportActionBar(toolbar);
    }

    private void setUpLayoutWithToolbar() {

        initToolbar();
        ImageView nav = findViewById(R.id.ivDrawer);/*CustomTextView title = findViewById(R.id.tvToolClick);

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionForAll.alertUser("VahanWire", "Work in Progress", "OK", PetrolPumpHomePage.this);
            }
        });*/

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        View navHeader = mNavigationView.getHeaderView(0);
        CircleImageView img = navHeader.findViewById(R.id.ivNavProfile);
        CustomTextView nav_name = navHeader.findViewById(R.id.tvNavName);
        CustomTextView nav_email = navHeader.findViewById(R.id.tvNavEmail);
        //Picasso.with(this).load(sessionManager.getString(SessionManager.PROVIDER_IMAGE)).into(img);
        /* nav_name.setText("ANKIT");
        nav_email.setText("A@GMAIL.COM");*/
        PicassoClient.downloadImage(this, sessionManager.getString(SessionManager.PROVIDER_IMAGE), img);
        nav_name.setText(sessionManager.getString(SessionManager.NAV_NAME_MECH));
        nav_email.setText(sessionManager.getString(SessionManager.EMAIL));
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        CodeMinimisations.navListener(nav, mNavigationView, mDrawerLayout, this);
        CodeMinimisations.navigationItemListener(mNavigationView, this);

        Menu nav_Menu = mNavigationView.getMenu();

        if (sessionManager.getString(SessionManager.MAIN_PROVIDER).equals("1")) {

            nav_Menu.findItem(R.id.nav_service_charge_mech).setVisible(true);
            nav_Menu.findItem(R.id.nav_Service_pre_service).setVisible(true);
            /*nav_Menu.findItem(R.id.nav_Service).setVisible(false);
            nav_Menu.findItem(R.id.nav_Serviceable_brand).setVisible(false);*/
        } else {
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_offer).setVisible(false);
            nav_Menu.findItem(R.id.nav_time_n_schedule).setVisible(false);
            nav_Menu.findItem(R.id.nav_payment_method).setVisible(false);
            nav_Menu.findItem(R.id.nav_time_n_schedule).setVisible(false);
            nav_Menu.findItem(R.id.nav_Serviceable_brand).setVisible(false);
            nav_Menu.findItem(R.id.nav_my_introduction).setVisible(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.banStatusOnMechanic:

                Dexter.withActivity(MachanicHomePage.this)
                        .withPermissions(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {

                                    isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                                    if (isGPSEnabled) {
                                        updateserviceInfo("1");
                                    } else {
                                        showSettingsAlert();
                                    }
                                }
                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied()) {

                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(new PermissionRequestErrorListener() {


                            @Override
                            public void onError(DexterError error) {
                                Log.d(TAG, "onError: " + error);
                            }
                        })
                        .onSameThread()
                        .check();


                break;

            case R.id.banStatusOffMechanic:
                updateserviceInfo("0");
                break;

        }

    }

    private void updateserviceInfo(final String status_id) {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));
        Log.e(TAG, "status id " + status_id);

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MechanicStatus> call = apiService.update_status_mechanic(sessionManager.getString(SessionManager.PROVIDER_ID), status_id);

        call.enqueue(new Callback<MechanicStatus>() {
            @Override
            public void onResponse(Call<MechanicStatus> call, Response<MechanicStatus> response) {

                Util.hideProgressDialog(progressDialog);

                if (response.isSuccessful()) {
                    MechanicStatus statusUser = response.body();
                    if (statusUser.getStatus().equals("200")) {
                        Log.d(TAG, "success " + statusUser.getMessage());
                        if (status_id.equals("1")) {
                            tvStatusMechanic.setText(ACTIVE_STATUS_ON);
                            banStatusOnMechanic.setVisibility(View.GONE);
                            banStatusOffMechanic.setVisibility(View.VISIBLE);
                            tvStatusMechanic.setTextColor(Color.parseColor("#00A600"));

                            sessionManager.setString(SessionManager.ACTIVE_STATUS, "1");

                            Intent locationIntent = new Intent(getBaseContext(), MyForeGroundService.class);
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                                startForegroundService(locationIntent);
                            } else {
                                //lower then Oreo, just start the service.
                                startService(locationIntent);
                            }

                        } else {
                            tvStatusMechanic.setText(ACTIVE_STATUS_OFF);
                            banStatusOnMechanic.setVisibility(View.VISIBLE);
                            banStatusOffMechanic.setVisibility(View.GONE);
                            tvStatusMechanic.setTextColor(Color.RED);
                            sessionManager.setString(SessionManager.ACTIVE_STATUS, "0");
                        }
                    } else {
                        ActionForAll.alertUser("VahanWire", statusUser.getMessage(), "OK", MachanicHomePage.this);
                    }
                } else {
                    ActionForAll.alertUser("VahanWire", "Network busy please try after sometime", "OK", MachanicHomePage.this);
                }
            }

            @Override
            public void onFailure(Call<MechanicStatus> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", MachanicHomePage.this);
            }
        });

    }

    public void requestPopup() {

        requestPopup = new Dialog(MachanicHomePage.this);
        // faltuCheck = "auto";
        requestPopup.setContentView(R.layout.dialog_request_popup);
        //requestPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestPopup.getWindow().getAttributes().windowAnimations = R.style.diologIntertnet;
        requestPopup.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        requestPopup.getWindow().setGravity(Gravity.CENTER);
        requestPopup.setCanceledOnTouchOutside(false);
        requestPopup.setCancelable(false);
        final CustomTextView tv_popup_request_timer = requestPopup.findViewById(R.id.tv_popup_request_timer);
        final CustomButton btnPopupRequestReject = requestPopup.findViewById(R.id.btnPopupRequestReject);
        final CustomButton btnPopupRequestAccept = requestPopup.findViewById(R.id.btnPopupRequestAccept);
        final CustomTextView tvMessegePopup = requestPopup.findViewById(R.id.tvMessegePopup);
        tvMessegePopup.setText("You have new request for " + sessionManager.getString(SessionManager.NOTI_ISSUE) + " by " +
                sessionManager.getString(SessionManager.NOTI_NAME));

        btnPopupRequestAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sessionManager.getString(SessionManager.NOTI_ISSUE).contains("Pre Request"))
                    startActivity(new Intent(getApplicationContext(), BookingHistoryMechanic.class));
                else
                    requestAdd();
            }
        });

        btnPopupRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPopup();
            }
        });


        if (sessionManager.getString(SessionManager.BOOKING_ID).length() > 0) {
            timer = new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    //pBar2.setProgress((int) (millisUntilFinished));
                    tv_popup_request_timer.setText(millisUntilFinished / 1000 + " \tseconds left");
                }

                public void onFinish() {
                    if(requestPopup.isShowing())
                    requestPopup.dismiss();

                    if (!ActionForAll.isNetworkAvailable(getApplicationContext())) {

                    } else {

                        if (faltuCheck.equalsIgnoreCase("auto")) {
                            requestPopup.dismiss();
                            cancelReason = "auto";
                            handler.removeCallbacks(null);
                            //getAllBookingDetail();
                            //requestCancelAuto();

                            sessionManager.setString(SessionManager.BOOKING_STATUS, "");
                            if (requestPopup.isShowing()) {
                                requestPopup.dismiss();
                            }

                            //ActionForAll.myFlash(getApplicationContext(), "Request cancelled!");

                        } else {

                        }
                    }
                }
            }.start();
            requestPopup.show();
        }
    }

    private void requestAdd() {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<RequestAccept> call = apiService.mech_req_accept(sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID));

        call.enqueue(new Callback<RequestAccept>() {
            @Override
            public void onResponse(Call<RequestAccept> call, Response<RequestAccept> response) {

                Util.hideProgressDialog(progressDialog);

                Log.d(TAG, "success " + response.body().getMessage());

                if (response.isSuccessful()) {
                    RequestAccept requestAccept = response.body();
                    if (requestAccept.getStatus().equals("200")) {
                        sessionManager.setString(SessionManager.BOOKING_STATUS, "");

                        new AlertDialog.Builder(context)
                                .setTitle("VahanWire")
                                .setMessage("Request Accepted")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(context, BookingHistory.class));
                                    }
                                })
                                .create().show();

                        timer.cancel();
                        faltuCheck = "accepted";
                        if (requestPopup.isShowing()) {
                            requestPopup.dismiss();
                        }
                    }
                    else if(requestAccept.getStatus().equals("409"))
                    {
                        sessionManager.setString(SessionManager.BOOKING_STATUS, "");
                        timer.cancel();
                        faltuCheck = "accepted";
                        if (requestPopup.isShowing()) {
                            requestPopup.dismiss();
                        }
                        ActionForAll.alertUserWithCloseActivity("VahanWire", requestAccept.getMessage(), "OK", MachanicHomePage.this);
                    }

                    else {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", requestAccept.getMessage(), "OK", MachanicHomePage.this);
                    }
                } else {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", MachanicHomePage.this);
                }
            }

            @Override
            public void onFailure(Call<RequestAccept> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", MachanicHomePage.this);
            }
        });

    }

    private void requestCancel(String reason) {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelRequest> call = apiService.mech_req_cancel(sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID), reason);

        call.enqueue(new Callback<CancelRequest>() {
            @Override
            public void onResponse(Call<CancelRequest> call, Response<CancelRequest> response) {

                Util.hideProgressDialog(progressDialog);

                if (response.isSuccessful()) {
                    CancelRequest cancelRequest = response.body();
                    if (cancelRequest.getStatus().equals("200")) {
                        sessionManager.setString(SessionManager.BOOKING_STATUS, "");
                        Log.d(TAG, "success " + cancelRequest.getMessage());
                        ActionForAll.alertUserWithCloseActivity("VahanWire", "Request cancelled !", "OK", MachanicHomePage.this);
                        timer.cancel();
                        faltuCheck = "cancelled_by_provider";
                        if (requestPopup.isShowing()) {
                            requestPopup.dismiss();

                        }

                        if (cancelDiolog.isShowing()) {
                            cancelDiolog.dismiss();
                        }

                    } else {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", cancelRequest.getMessage(), "OK", MachanicHomePage.this);
                    }
                } else {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", MachanicHomePage.this);
                }
            }

            @Override
            public void onFailure(Call<CancelRequest> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", MachanicHomePage.this);
            }
        });

    }

    private void requestCancelAuto(String reason) {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelRequest> call = apiService.mech_req_cancel(sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID), reason);

        call.enqueue(new Callback<CancelRequest>() {
            @Override
            public void onResponse(Call<CancelRequest> call, Response<CancelRequest> response) {

                Util.hideProgressDialog(progressDialog);

                if (response.isSuccessful()) {
                    CancelRequest cancelRequest = response.body();
                    if (cancelRequest.getStatus().equals("200")) {
                        Log.d(TAG, "success " + cancelRequest.getMessage());
                        ActionForAll.myFlash(getApplicationContext(), "Request cancelled automatically");
                        timer.cancel();
                        faltuCheck = "auto";
                        if (requestPopup.isShowing()) {
                            requestPopup.dismiss();

                        }

                        if (cancelDiolog != null && cancelDiolog.isShowing()) {
                            cancelDiolog.dismiss();
                        }

                        finish();

                    } else {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", cancelRequest.getMessage(), "OK", MachanicHomePage.this);
                    }
                } else {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", MachanicHomePage.this);
                }
            }

            @Override
            public void onFailure(Call<CancelRequest> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", MachanicHomePage.this);
            }
        });

    }

    public void cancelPopup() {

        cancelDiolog = new Dialog(MachanicHomePage.this);
        cancelDiolog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cancelDiolog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancelDiolog.setContentView(R.layout.cancel_popup_lay);
        cancelDiolog.getWindow().getAttributes().windowAnimations = R.style.diologIntertnet;
        cancelDiolog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        RelativeLayout laySubmit = cancelDiolog.findViewById(R.id.laySubmit);
        cancelReason = "";

        ListView list = cancelDiolog.findViewById(R.id.listReason);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MachanicHomePage.this, android.R.layout.simple_list_item_single_choice, reason);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                cancelReason = parent.getItemAtPosition(position).toString();
                Log.e(TAG, "onItemClick: " + cancelReason);

            }
        });


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sessionManager.setString(SessionManager.BOOKING_STATUS_USER, "");
        sessionManager.setString(SessionManager.BOOKING_STATUS, "");
    }

    /**
     * for API 26+ create notification channels
     */
    private void createchannel() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel mChannel = new NotificationChannel(id1,
                    getString(R.string.channel_name),  //name of the channel
                    NotificationManager.IMPORTANCE_LOW);   //importance level
            //important level: default is is high on the phone.  high is urgent on the phone.  low is medium, so none is low?
            // Configure the notification channel.
            mChannel.setDescription(getString(R.string.channel_description));
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this channel, if the device supports this feature.
            mChannel.setShowBadge(true);
            nm.createNotificationChannel(mChannel);
        }
    }

    private void getreason() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelReason> call = apiService.cancelReason();
        call.enqueue(new Callback<CancelReason>() {
            @Override
            public void onResponse(Call<CancelReason> call, Response<CancelReason> response) {

                Util.hideProgressDialog(progressDialog);

                CancelReason cancelReason = response.body();

                if (response.isSuccessful()) {

                    if (cancelReason.getStatus().equals("200")) {
                        data = cancelReason.getData();
                        for (Datum datum : data) {
                            String name = datum.getName();
                            reason.add(name);
                            Log.e(TAG, "onResponse: " + name);
                        }
                    } else {

                    }
                } else {

                }

                Log.e("service", response.body().getStatus());

            }

            @Override
            public void onFailure(Call<CancelReason> call, Throwable t) {
                Log.e("service failier", t.getMessage());
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try{
            getDetail();
        }
        catch (Exception e){}
    }

    private void getDetail() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        Call<MechNewDetail> call = apiService.getUpdatedDetailMechanic(params);
        call.enqueue(new Callback<MechNewDetail>() {
            @Override
            public void onResponse(Call<MechNewDetail> call, Response<MechNewDetail> response) {
                MechNewDetail detail = response.body();
                //            Log.d(TAG, detail.getStatus());
                if (detail.getStatus().equals("200")) {
                    Util.hideProgressDialog(progressDialog);
                    Log.e(TAG, detail.getData().getMobile() + "-pin-" + detail.getData().getMobilePin());

                    int count_navitaion_user = detail.getData().getRequestedUsers().size();
                    tvTotalVisitorsCountMechanic.setText(String.valueOf(count_navitaion_user));
                    List<Offer> offers = detail.getData().getOrganisation().getOffers();
                    SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    if (offers.size() > 0) {
                        com.electrom.vahanwireprovider.models.mechanic_new.Offer offer = offers.get(0);
                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(offer);
                        prefsEditor.putString("Offer", json);
                        prefsEditor.apply();
                    } else {
                        appSharedPrefs.edit().remove("Offer").commit();
                    }
                    //sessionManager.setObject("Myobj", offer);
                    try {

                       /* Log.e(TAG, "onResponse: dob " + detail.getData().getPersonalDetails().getDob());
                        Log.e(TAG, "onResponse: email " + detail.getData().getPersonalDetails().getEmail());
                        Log.e(TAG, "onResponse: highQul " + detail.getData().getPersonalDetails().getHighestQualification());
                        Log.e(TAG, "onResponse: marital " + detail.getData().getPersonalDetails().getMaritalStatus());
                        Log.e(TAG, "onResponse: tot exp " + detail.getData().getPersonalDetails().getTotalExp());
                        Log.e(TAG, "onResponse: website " + detail.getData().getPersonalDetails().getWebsite());
                        Log.e(TAG, "onResponse: gst num " + detail.getData().getOrganisation().getOrgDetails().getGstNumber());
                        Log.e(TAG, "onResponse: feedback  " + detail.getData().getPersonalDetails().getSocialMedia().getFacebook());
                        Log.e(TAG, "onResponse: facebook " + detail.getData().getPersonalDetails().getSocialMedia().getTwitter());
                        Log.e(TAG, "onResponse: insta " + detail.getData().getPersonalDetails().getSocialMedia().getInstagram());
                        Log.e(TAG, "onResponse: name " + detail.getData().getName());
                        Log.e(TAG, "onResponse: org name " + detail.getData().getOrganisation().getOrganisationName());*/


                        sessionManager.setString(SessionManager.PRO_DOB, detail.getData().getPersonalDetails().getDob());
                        sessionManager.setString(SessionManager.EMAIL, detail.getData().getPersonalDetails().getEmail());
                        sessionManager.setString(SessionManager.PRO_HIGH_QULALIFICATION, detail.getData().getPersonalDetails().getHighestQualification());
                        sessionManager.setString(SessionManager.PRO_MARRITAL_STATUS, detail.getData().getPersonalDetails().getMaritalStatus());
                        sessionManager.setString(SessionManager.PRO_TOTAL_EXP, detail.getData().getPersonalDetails().getTotalExp());
                        sessionManager.setString(SessionManager.PRO_WEB, detail.getData().getPersonalDetails().getWebsite());
                        //Log.e(TAG, "onResponse: gst " +  detail.getData().getOrganisation().getOrgDetails().getGstNumber());
                        sessionManager.setString(SessionManager.PRO_GST, detail.getData().getOrganisation().getOrgDetails().getGstNumber());
                        sessionManager.setString(SessionManager.PRO_FACEBOOK, detail.getData().getPersonalDetails().getSocialMedia().getFacebook());
                        sessionManager.setString(SessionManager.PRO_TWEET, detail.getData().getPersonalDetails().getSocialMedia().getTwitter());
                        sessionManager.setString(SessionManager.PRO_INSTA, detail.getData().getPersonalDetails().getSocialMedia().getInstagram());
                        sessionManager.setString(SessionManager.SERVICE_CHARGE,detail.getData().getService_charge());
                        //Log.e(TAG, "onResponse: service_charge " + detail.getData().getService_charge());
                        sessionManager.setString(SessionManager.PRO_CERTIFICTION_NAME, detail.getData().getPersonalDetails().getCertification());
                        sessionManager.setString(SessionManager.PRO_PERSONAL_PAN, detail.getData().getPersonalDetails().getPanNumber());
                        sessionManager.setString(SessionManager.PRO_SPECIAL_TALENT, detail.getData().getPersonalDetails().getSpecialTalent());
                        sessionManager.setString(SessionManager.PRO_ORG_PAN, detail.getData().getOrganisation().getOrgDetails().getPanNumber());
                         sessionManager.setString(SessionManager.PRO_ORG_ID, detail.getData().getOrganisationId());

                        sessionManager.setString(SessionManager.ACTIVE_STATUS, detail.getData().getMechanicActiveStatus().toString());
                        Log.e(TAG, "onResponse: " + detail.getData().getMechanicActiveStatus().toString());

                        if (sessionManager.getString(SessionManager.ACTIVE_STATUS).equals("1")) {
                            banStatusOnMechanic.setVisibility(View.GONE);
                            banStatusOffMechanic.setVisibility(View.VISIBLE);
                            tvStatusMechanic.setText(ACTIVE_STATUS_ON);
                            tvStatusMechanic.setTextColor(Color.parseColor("#00A600"));
                            if (!isMyServiceRunning(MyForeGroundService.class)) {
                                Intent locationIntent = new Intent(getBaseContext(), MyForeGroundService.class);
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                                    startForegroundService(locationIntent);
                                } else {
                                    //lower then Oreo, just start the service.
                                    startService(locationIntent);
                                }
                            }
                        } else {
                            banStatusOnMechanic.setVisibility(View.VISIBLE);
                            banStatusOffMechanic.setVisibility(View.GONE);
                            tvStatusMechanic.setText(ACTIVE_STATUS_OFF);
                            tvStatusMechanic.setTextColor(Color.RED);
                        }

                        PersonalDetails personalDetails = detail.getData().getPersonalDetails();
                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(personalDetails);
                        prefsEditor.putString("personalDetails", json);
                        prefsEditor.apply();

                    } catch (Exception e) {

                    }

                    sessionManager.setString(SessionManager.PROVIDER_MOBILE, detail.getData().getMobile());
                    sessionManager.setString(SessionManager.PROVIDER_PIN, detail.getData().getMobilePin());
                    sessionManager.setString(SessionManager.REGISTER_NAME, detail.getData().getName());
                    sessionManager.setString(SessionManager.EMAIL, detail.getData().getOrganisation().getEmail());
                    sessionManager.setString(SessionManager.ADDRESS, detail.getData().getOrganisation().getRegAddress().getFirstAddress());
                    sessionManager.setString(SessionManager.CONTACT_PERSON, detail.getData().getOrganisation().getOrganisationName());
                    sessionManager.setString(SessionManager.LANDLINE, detail.getData().getOrganisation().getPhone());
                    sessionManager.setString(SessionManager.PROVIDER_IMAGE, detail.getData().getProfilePic());
                    sessionManager.setString(SessionManager.PROVIDER_ID, detail.getData().getId());
                    sessionManager.setString(SessionManager.COUNRTY, detail.getData().getOrganisation().getRegAddress().getCountry());
                    sessionManager.setString(SessionManager.STATE, detail.getData().getOrganisation().getRegAddress().getState());
                    sessionManager.setString(SessionManager.CITY, detail.getData().getOrganisation().getRegAddress().getCity());
                    sessionManager.setString(SessionManager.PINCODE, detail.getData().getOrganisation().getRegAddress().getPincode());
                    WorkingHours workingHours = detail.getData().getOrganisation().getWorkingHours();
                    sessionManager.setString(SessionManager.WORK_TO, workingHours.getTo());
                    sessionManager.setString(SessionManager.WORK_FROM, workingHours.getFrom());


                    if(sessionManager.getString(SessionManager.MAIN_PROVIDER).equalsIgnoreCase("1"))
                    {
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, detail.getData().getProfilePic());
                        sessionManager.setString(SessionManager.NAV_NAME_MECH, detail.getData().getName());

                    }
                    else
                    {
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, detail.getData().getOrganisation().getProfilePic());
                        sessionManager.setString(SessionManager.NAV_NAME_MECH, detail.getData().getOrganisation().getOrganisationName());
                    }

                    try {
                        setUpLayoutWithToolbar();
                    } catch (IllegalArgumentException e) {
                    }

                    Log.e(TAG, "onResponse: done ");

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", MachanicHomePage.this);
                }
            }

            @Override
            public void onFailure(Call<MechNewDetail> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e(TAG, "error detail " + t.getMessage());
            }
        });
    }

    private void getAllBookingDetail() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", sessionManager.getString(SessionManager.PROVIDER_ID));
        params.put("booking_id", sessionManager.getString(SessionManager.BOOKING_ID));

        Call<BookingDetails> call = apiService.getMechBooking(params);

        call.enqueue(new Callback<BookingDetails>() {
            @Override
            public void onResponse(Call<BookingDetails> call, Response<BookingDetails> response) {

                Util.hideProgressDialog(progressDialog);

                BookingDetails bookingDetails = response.body();

                Log.e(TAG, "onResponse: " + bookingDetails.getStatus());

                if (response.isSuccessful()) {

                    if (bookingDetails.getStatus().equals("200")) {
                        Data data = bookingDetails.getData();

                        String status = data.getDetails().getBookingStatus().getMechanic().getStatus();
                        if (status.contains("0") || status.contains("2")) {

                        } else {
                            new AlertDialog.Builder(context)
                                    .setTitle("VahanProvider")
                                    .setMessage("")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e(TAG, "done");
                                        }
                                    }).create().show();
                        }
                    } else if (bookingDetails.getStatus().equals("404")) {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", "No booking found", "OK", MachanicHomePage.this);
                    } else {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", bookingDetails.getMessage(), "OK", MachanicHomePage.this);
                    }

                } else {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", bookingDetails.getMessage(), "OK", MachanicHomePage.this);
                }

                Log.e("service", response.body().getStatus());

            }

            @Override
            public void onFailure(Call<BookingDetails> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e("service failier", t.getMessage());
                t.printStackTrace();
                ActionForAll.alertUserWithCloseActivity("VahanWire", "No active bookin found", "OK", MachanicHomePage.this);
            }
        });
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

    private void requestCancelAuto() {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CancelRequest> call = apiService.mechanic_req_cancel_auto(
                sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID));

        call.enqueue(new Callback<CancelRequest>() {
            @Override
            public void onResponse(Call<CancelRequest> call, Response<CancelRequest> response) {

                Util.hideProgressDialog(progressDialog);

                Log.e(TAG, "onResponse: " + response.body().getStatus());

                if (response.isSuccessful()) {
                    CancelRequest body = response.body();
                    if (body.getStatus().equals("200")) {
                        sessionManager.setString(SessionManager.BOOKING_STATUS, "");
                        if (requestPopup.isShowing()) {
                            requestPopup.dismiss();
                        }

                        ActionForAll.myFlash(getApplicationContext(), "Request cancelled!");

                    } else {
                        ActionForAll.myFlash(getApplicationContext(), body.getMessage());
                    }
                } else {
                    ActionForAll.myFlash(getApplicationContext(), "error found api not responding...");
                }

                //handler.removeCallbacks(null);
                //finish();

               /* if(response.isSuccessful())
                {
                    CancelRequest cancelRequest = response.body();
                    if(cancelRequest.getStatus().equals("200"))
                    {
                        sessionManager.setString(SessionManager.BOOKING_STATUS, "");
                        Log.d(TAG, "success " + cancelRequest.getMessage());
                        //ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", AmbulanceHomePage.this);
                        timer.cancel();
                        faltuCheck = "cancelled_by_provider";
                        if(requestPopup.isShowing())
                        {
                            requestPopup.dismiss();
                        }
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", cancelRequest.getMessage(), "OK", AmbulanceHomePage.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", AmbulanceHomePage.this);
                }*/
            }

            @Override
            public void onFailure(Call<CancelRequest> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", t.getMessage(), "OK", MachanicHomePage.this);
            }
        });
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. This feature need enable GPS setting. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

}
