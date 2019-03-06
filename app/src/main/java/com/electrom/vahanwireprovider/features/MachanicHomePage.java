package com.electrom.vahanwireprovider.features;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.mech_status.MechanicStatus;
import com.electrom.vahanwireprovider.models.status_user.StatusUser;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CodeMinimisations;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomTextView;
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
        sessionManager = SessionManager.getInstance(this);
        banStatusOnMechanic = findViewById(R.id.banStatusOnMechanic);
        banStatusOffMechanic = findViewById(R.id.banStatusOffMechanic);
        tvTotalVisitorsCountMechanic = findViewById(R.id.tvTotalVisitorsCountMechanic);
        banStatusOffMechanic.setOnClickListener(this);
        banStatusOnMechanic.setOnClickListener(this);
        tvStatusMechanic = findViewById(R.id.tvStatusMechanic);
        loginStatus = sessionManager.getString(SessionManager.ACTIVE_STATUS);

        if(loginStatus.equals("1"))
        {
            //requestPopup();
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
}
