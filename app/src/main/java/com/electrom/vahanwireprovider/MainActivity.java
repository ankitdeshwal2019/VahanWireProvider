package com.electrom.vahanwireprovider;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.location_service.GPSTracker;
import com.electrom.vahanwireprovider.models.detail.Detail;
import com.electrom.vahanwireprovider.models.detail.Offer;
import com.electrom.vahanwireprovider.models.update_profile.Datum;
import com.electrom.vahanwireprovider.models.update_profile.Update;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CodeMinimisations;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomTextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = SessionManager.getInstance(this);
        setUpLayoutWithToolbar();
        initView();
        startLocationUpdate();

    }

    private void initView() {
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
        CustomTextView title = findViewById(R.id.tvToolClick);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionForAll.alertUser("VahanWire", "Work in Progress", "OK", MainActivity.this);
            }
        });

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        View navHeader = mNavigationView.getHeaderView(0);
        CircleImageView img = (CircleImageView) navHeader.findViewById(R.id.ivNavProfile);
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
                Log.d(TAG, detail.getStatus());
                if (detail.getStatus().equals("200")) {
                    Util.hideProgressDialog(progressDialog);
                    Log.d(TAG, detail.getData().getMobile() + "-pin-" + detail.getData().getMobilePin());
                    count_navitaion_user = detail.getData().getNavigatedUsers().size();
                    tvTotalVisitorsCount.setText(String.valueOf(count_navitaion_user));
                    List<Offer> offers = detail.getData().getOffers();
                    if (offers.size() > 0) {
                        Offer offer = offers.get(offers.size() - 1);
                        SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(offer);
                        prefsEditor.putString("Offer", json);
                        prefsEditor.apply();
                        //sessionManager.setObject("Myobj", offer);
                    }
                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", MainActivity.this);
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "error" + t.getMessage());
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
                ActionForAll.alertUserWithCloseActivity("Location", "Please enable location Permission", "OK", MainActivity.this);
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

}
