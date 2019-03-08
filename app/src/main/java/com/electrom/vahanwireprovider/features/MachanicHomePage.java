package com.electrom.vahanwireprovider.features;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.cancel_request.CancelRequest;
import com.electrom.vahanwireprovider.models.mech_status.MechanicStatus;
import com.electrom.vahanwireprovider.models.request_accept.RequestAccept;
import com.electrom.vahanwireprovider.models.status_user.StatusUser;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CodeMinimisations;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.MyHandler;
import com.electrom.vahanwireprovider.utility.PicassoClient;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachanicHomePage extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MachanicHomePage.class.getSimpleName();
    Context context = MachanicHomePage.this;
    SessionManager sessionManager;
    CustomButton banStatusOnMechanic, banStatusOffMechanic;
    CustomTextView tvStatusMechanic, tvTotalVisitorsCountMechanic;
    String loginStatus = "0";
    String ACTIVE_STATUS_ON = "You are LogIN";
    String ACTIVE_STATUS_OFF = "You are LogOut";
    MyHandler handler;
    String faltuCheck = "auto", cancelReason;
    Dialog requestPopup,cancelDiolog;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machanic_home_page_nav);
        initView();
        try{
            setUpLayoutWithToolbar();
        }
        catch (IllegalArgumentException e){}



    }

    private void initView() {
        handler = new MyHandler();
        sessionManager = SessionManager.getInstance(this);
        banStatusOnMechanic = findViewById(R.id.banStatusOnMechanic);
        banStatusOffMechanic = findViewById(R.id.banStatusOffMechanic);
        tvTotalVisitorsCountMechanic = findViewById(R.id.tvTotalVisitorsCountMechanic);
        banStatusOffMechanic.setOnClickListener(this);
        banStatusOnMechanic.setOnClickListener(this);
        tvStatusMechanic = findViewById(R.id.tvStatusMechanic);
        loginStatus = sessionManager.getString(SessionManager.ACTIVE_STATUS);
        Log.e(TAG, sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));
        Log.e(TAG, "initView: pro_id " + sessionManager.getString(SessionManager.PROVIDER_ID));


        if(loginStatus.equals("1"))
        {
            banStatusOnMechanic.setVisibility(View.GONE);
            banStatusOffMechanic.setVisibility(View.VISIBLE);
            tvStatusMechanic.setText(ACTIVE_STATUS_ON);
        }
        else
        {
            banStatusOnMechanic.setVisibility(View.VISIBLE);
            banStatusOffMechanic.setVisibility(View.GONE);
            tvStatusMechanic.setText(ACTIVE_STATUS_OFF);
        }

        if(sessionManager.getString(SessionManager.BOOKING_ID).length() > 0)
        {
            Log.e(TAG, "initView: "+sessionManager.getString(SessionManager.BOOKING_ID));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestPopup();
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
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        CodeMinimisations.navListener(nav, mNavigationView, mDrawerLayout, this);
        CodeMinimisations.navigationItemListener(mNavigationView, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.banStatusOnMechanic:
                updateserviceInfo("1");
                break;

            case R.id.banStatusOffMechanic:

                updateserviceInfo("0");
                break;
        }

    }

    private void updateserviceInfo(final String status_id)
    {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));
        Log.e(TAG, "status id " +status_id);

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MechanicStatus> call = apiService.update_status_mechanic(sessionManager.getString(SessionManager.PROVIDER_ID), status_id);

        call.enqueue(new Callback<MechanicStatus>() {
            @Override
            public void onResponse(Call<MechanicStatus> call, Response<MechanicStatus> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    MechanicStatus statusUser = response.body();
                    if(statusUser.getStatus().equals("200"))
                    {
                        Log.d(TAG, "success " + statusUser.getMessage());
                        if(status_id.equals("1"))
                        {
                            tvStatusMechanic.setText(ACTIVE_STATUS_ON);
                            banStatusOnMechanic.setVisibility(View.GONE);
                            banStatusOffMechanic.setVisibility(View.VISIBLE);
                            sessionManager.setString(SessionManager.ACTIVE_STATUS, "1");
                        }
                        else
                        {
                            tvStatusMechanic.setText(ACTIVE_STATUS_OFF);
                            banStatusOnMechanic.setVisibility(View.VISIBLE);
                            banStatusOffMechanic.setVisibility(View.GONE);
                            sessionManager.setString(SessionManager.ACTIVE_STATUS, "0");
                        }
                    }
                    else
                    {
                        ActionForAll.alertUser("VahanWire", statusUser.getMessage(), "OK", MachanicHomePage.this);
                    }
                }
                else
                {
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
        //handler.removeCallbacks(n);
//         handler.removeCallbacksAndMessages(null);
//         pDialog = new ProgressDialog(getActivity());
//         //Showing progress dialog before making http request
//         pDialog.setMessage("Loading...");
//         pDialog.setCancelable(false);
//         pDialog.setCanceledOnTouchOutside(false);
//         pDialog.show();
        requestPopup = new Dialog(MachanicHomePage.this);
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

                requestAdd();
               /* startActivity(new Intent(getApplicationContext(), BookingStatusActivity.class));
                requestPopup.dismiss();
                finish();*/
            }
        });

        btnPopupRequestReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelPopup();

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

        if(sessionManager.getString(SessionManager.BOOKING_ID).length()>0)
        {
            timer = new CountDownTimer(120000, 1000) {
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    //pBar2.setProgress((int) (millisUntilFinished));
                    tv_popup_request_timer.setText(millisUntilFinished / 1000 + " \tseconds left");
                }
                public void onFinish() {
                    requestPopup.dismiss();
                    if (!ActionForAll.isNetworkAvailable(getApplicationContext())) {
                        //diologInternet.show();
                    } else {
                        if (faltuCheck.equalsIgnoreCase("auto")) {
                            requestPopup.dismiss();
                            cancelReason = "auto";
                            handler.removeCallbacks(null);
                            requestCancelAuto(cancelReason);
                        } else {
                        }
                    }
                }
            }.start();
            requestPopup.show();
        }
    }

    private void requestAdd()
    {
        Log.e(TAG, "provider id " + sessionManager.getString(SessionManager.PROVIDER_ID));

        final ProgressDialog progressDialog = Util.showProgressDialog(context);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<RequestAccept> call = apiService.mech_req_accept(sessionManager.getString(SessionManager.PROVIDER_ID),
                sessionManager.getString(SessionManager.BOOKING_ID));

        call.enqueue(new Callback<RequestAccept>() {
            @Override
            public void onResponse(Call<RequestAccept> call, Response<RequestAccept> response) {

                Util.hideProgressDialog(progressDialog);

                if(response.isSuccessful())
                {
                    RequestAccept requestAccept = response.body();
                    if(requestAccept.getStatus().equals("200"))
                    {
                        Log.d(TAG, "success " + requestAccept.getMessage());
                        ActionForAll.alertUserWithCloseActivity("VahanWire","Request accepted ","OK", MachanicHomePage.this);
                        timer.cancel();
                        faltuCheck = "accepted";
                        if(requestPopup.isShowing())
                        {
                            requestPopup.dismiss();
                        }

                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", requestAccept.getMessage(), "OK", MachanicHomePage.this);
                    }
                }
                else
                {
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

    private void requestCancel(String reason)
    {
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
                        Log.d(TAG, "success " + cancelRequest.getMessage());
                        ActionForAll.alertUserWithCloseActivity("VahanWire","Request cancelled !","OK", MachanicHomePage.this);
                        timer.cancel();
                        faltuCheck = "cancelled_by_provider";
                        if(requestPopup.isShowing())
                        {
                            requestPopup.dismiss();

                        }

                        if(cancelDiolog.isShowing())
                        {
                            cancelDiolog.dismiss();
                        }

                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", cancelRequest.getMessage(), "OK", MachanicHomePage.this);
                    }
                }
                else
                {
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

    private void requestCancelAuto(String reason)
    {
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
                        Log.d(TAG, "success " + cancelRequest.getMessage());
                        ActionForAll.myFlash(getApplicationContext(), "Request cancelled automatically");
                        timer.cancel();
                        faltuCheck = "auto";
                        if(requestPopup.isShowing())
                        {
                            requestPopup.dismiss();

                        }

                        if(cancelDiolog!=null && cancelDiolog.isShowing())
                        {
                            cancelDiolog.dismiss();
                        }

                        finish();

                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", cancelRequest.getMessage(), "OK", MachanicHomePage.this);
                    }
                }
                else
                {
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
        RelativeLayout layNoCustomer = cancelDiolog.findViewById(R.id.layNoCustomer);
        RelativeLayout layvichelBroke = cancelDiolog.findViewById(R.id.layvichelBroke);
        RelativeLayout layResNoList = cancelDiolog.findViewById(R.id.layResNoList);
        RelativeLayout layForgotTools = cancelDiolog.findViewById(R.id.layForgotTools);
        RelativeLayout laySubmit = cancelDiolog.findViewById(R.id.laySubmit);

        final RadioButton radioNoCustomer = cancelDiolog.findViewById(R.id.radioNoCustomer);
        final RadioButton radioVichelBroker = cancelDiolog.findViewById(R.id.radioVichelBroker);
        final RadioButton radioNoRes =  cancelDiolog.findViewById(R.id.radioNoRes);
        final RadioButton radioForgotTools =  cancelDiolog.findViewById(R.id.radioForgotTools);

        final TextView txtNoCustomer =  cancelDiolog.findViewById(R.id.txtNoCustomer);
        final TextView txtVehicelBroke =  cancelDiolog.findViewById(R.id.txtVehicelBroke);
        final TextView txtNoRes =  cancelDiolog.findViewById(R.id.txtNoRes);
        final TextView txtForgotTools =  cancelDiolog.findViewById(R.id.txtForgotTools);
        cancelReason = "";
        layNoCustomer.setOnClickListener(new View.OnClickListener() {
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
        sessionManager.setString(SessionManager.BOOKING_ID, "");
    }

}
