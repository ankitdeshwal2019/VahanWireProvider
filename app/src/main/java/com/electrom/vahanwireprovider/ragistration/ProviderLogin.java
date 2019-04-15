package com.electrom.vahanwireprovider.ragistration;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.PetrolPumpHomePage;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.features.AmbulanceHomePage;
import com.electrom.vahanwireprovider.features.MachanicHomePage;
import com.electrom.vahanwireprovider.location_service.GPSTracker;
import com.electrom.vahanwireprovider.models.login.LoginPP;
import com.electrom.vahanwireprovider.models.login_ambulance.Data;
import com.electrom.vahanwireprovider.models.login_ambulance.LoginAmbulance;
import com.electrom.vahanwireprovider.models.mechanic.MechanicLogin;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderLogin extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ProviderLogin.class.getSimpleName();
    CustomButton btnRegister, btnLogin;
    CustomEditText etLoginMobile, etLoginPassword;
    SessionManager sessionManager;
    CustomTextView tvForgotPin,extra;
    String service = "";
    ImageView ivBackLogin;
    LocationManager manager;

    boolean isGPSEnabled = false;

    // Flag for network status
    boolean isNetworkEnabled = false;

    // Flag for GPS status
    boolean canGetLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {

        sessionManager = SessionManager.getInstance(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        etLoginMobile = findViewById(R.id.etLoginMobile);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        ivBackLogin = findViewById(R.id.ivBackLogin);
        extra = findViewById(R.id.extra);
        tvForgotPin = findViewById(R.id.tvForgotPin);
        tvForgotPin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        ivBackLogin.setOnClickListener(this);
        service = sessionManager.getString(SessionManager.SERVICE);
        Log.e(TAG, "service: provider login " + service );

        if(service.equals(Constant.SERVICE_AMBULANCE))
        {
            btnRegister.setVisibility(View.INVISIBLE);
            tvForgotPin.setVisibility(View.INVISIBLE);
            extra.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        isGPSEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        switch (v.getId()) {

            case R.id.btnLogin:
                if (ActionForAll.validMobileEditText(etLoginMobile, "mobile number", ProviderLogin.this) &&
                        ActionForAll.validEditText(etLoginPassword, "valid pin", ProviderLogin.this)) {

                    Log.e(TAG, "onClick: service  " + service );

                    if(service.contains("Petrol_Pump"))
                    {
                        login();
                    }
                    else if(service.contains("Ambulance"))
                    {
                        Dexter.withActivity(this).withPermissions(
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
                                            sessionManager.setString(SessionManager.SERVICE, service);

                                            loginAmbulance();
                                        }

                                        //check for permanent denial of any permission
                                        if (report.isAnyPermissionPermanentlyDenied()) {
                                            showSettingsDialog();
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

                    else if(service.contains("MechanicPro"))
                    {

                        loginMecanic();
                    }
                    else
                    {
                        ActionForAll.myFlash(getApplicationContext(), "service choice not found");
                    }
                }
                //startActivity(new Intent(getApplicationContext(), PetrolPumpHomePage.class));
                break;

            case R.id.btnRegister:
                if(service.contains(Constant.SERVICE_MECHNIC_PRO))
                startActivity(new Intent(getApplicationContext(), RegisterMobile.class));
                else if(service.contains(Constant.SERVICE_AMBULANCE))
                    ActionForAll.myFlash(getApplicationContext(), "Abmulance user can't register by app");
                else if(service.contains(Constant.SERVICE_PETROL_PUMP))
                    startActivity(new Intent(getApplicationContext(), RegisterMobile.class));
                break;

                case R.id.tvForgotPin:
                    if(service.contains(Constant.SERVICE_MECHNIC_PRO))
                        startActivity(new Intent(getApplicationContext(), RegisterMobileForPin.class));
                    else if(service.contains(Constant.SERVICE_AMBULANCE))
                        ActionForAll.myFlash(getApplicationContext(), "Please contact administrator");
                    else if(service.contains(Constant.SERVICE_PETROL_PUMP))
                        startActivity(new Intent(getApplicationContext(), RegisterMobileForPin.class));
                    break;
                 case R.id.ivBackLogin:
                finish();
                break;

        }
    }

    private void loginMecanic() {

        Log.d(TAG, "loginMecanic: device_id " + sessionManager.getString(SessionManager.DEVICE_ID));
        Log.d(TAG, "loginMecanic: device_type " + "1");
        Log.d(TAG, "loginMecanic: notification_id " + sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));
        Log.d(TAG, "loginMecanic: mobile " + etLoginMobile.getText().toString());

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<MechanicLogin> call = apiService.login_mechanic(etLoginMobile.getText().toString(),
                etLoginPassword.getText().toString(),
                sessionManager.getString(SessionManager.DEVICE_ID),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));


        call.enqueue(new Callback<MechanicLogin>() {
            @Override
            public void onResponse(Call<MechanicLogin> call, Response<MechanicLogin> response) {
                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.body().getStatus());
                    Util.hideProgressDialog(progressDialog);
                    MechanicLogin login = response.body();
                    if (login.getStatus().equals("200"))
                    {
                        Log.d(TAG, "onResponse: " + "  message " + login.getMessage());
                        Log.d(TAG, "onResponse: " + "  mobile " + login.getData().getMobile());
                        Log.d(TAG, "onResponse: " + "  pin " + "--" + login.getData().getMobilePin());
                        Log.d(TAG, "onResponse: " + " first address " + "--" + login.getData().getOrganisation().getRegAddress().getFirstAddress());
                        Log.d(TAG, "onResponse: " + "contact person " + "--" + login.getData().getOrganisation().getContactPerson());
                        Log.d(TAG, "onResponse: " + "phone number" + "--" + login.getData().getOrganisation().getMobile());

                        sessionManager.setString(SessionManager.MAIN_PROVIDER, String.valueOf(login.getData().getMainproviderStatus()));
                        List<Double> list = login.getData().getLocation().getCoordinates();
                        Double longitude = list.get(0).doubleValue();
                        Double latitude = list.get(1).doubleValue();
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + longitude);
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + latitude);
                        Log.d(TAG, "onResponse: " + " image " + "--" + login.getData().getProfilePic());

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, login.getData().getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, login.getData().getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, login.getData().getName());
                        sessionManager.setString(SessionManager.EMAIL, login.getData().getOrganisation().getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, login.getData().getOrganisation().getRegAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.CONTACT_PERSON,login.getData().getOrganisation().getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, login.getData().getOrganisation().getPhone());
                        sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                        sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, login.getData().getProfilePic());
                        sessionManager.setString(SessionManager.PROVIDER_ID, login.getData().getId());


                        Intent intent= new Intent(getApplicationContext(), MachanicHomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.putExtra("count", login.getData().getRequestedUsers().size());
                        startActivity(intent);
                        finish();
                    }

                    else if(login.getStatus().equals("404"))
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                    else {
                        Util.hideProgressDialog(progressDialog);
                        ActionForAll.alertUserWithCloseActivity("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<MechanicLogin> call, Throwable t) {
                etLoginPassword.setText("");
                t.printStackTrace();
                Log.e(TAG, "onFailure: " + t.getMessage());
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanProvider", "Invalid credentials", "OK", ProviderLogin.this);
            }
        });


    }

    private void login() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<LoginPP> call = apiService.login(etLoginMobile.getText().toString(),
                etLoginPassword.getText().toString(),
                "1",
                sessionManager.getString(sessionManager.NOTIFICATION_TOKEN));

        call.enqueue(new Callback<LoginPP>() {
            @Override
            public void onResponse(Call<LoginPP> call, Response<LoginPP> response) {
                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.body().getStatus());
                    Util.hideProgressDialog(progressDialog);
                    LoginPP login = response.body();
                    if (login.getStatus().equals("200")) {

                        Log.d(TAG, "onResponse: " + "  message " + login.getMessage());
                        Log.d(TAG, "onResponse: " + "  mobile " + login.getData().getMobile());
                        Log.d(TAG, "onResponse: " + "  pin " + "--" + login.getData().getMobilePin());
                        Log.d(TAG, "onResponse: " + " first address " + "--" + login.getData().getAddress().getFirstAddress());
                        Log.d(TAG, "onResponse: " + "contact person " + "--" + login.getData().getContactPerson());
                        Log.d(TAG, "onResponse: " + "phone number" + "--" + login.getData().getPhone());
                        List<Double> list = login.getData().getAddress().getLocation().getCoordinates();
                        Double longitude = list.get(0).doubleValue();
                        Double latitude = list.get(1).doubleValue();
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + longitude);
                        Log.d(TAG, "onResponse: " + "longitude" + "--" + latitude);
                        Log.d(TAG, "onResponse: " + " image " + "--" + login.getData().getProfilePic());

                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, login.getData().getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, login.getData().getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, login.getData().getRegisteredName());
                        sessionManager.setString(SessionManager.EMAIL, login.getData().getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, login.getData().getAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.CONTACT_PERSON,login.getData().getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, login.getData().getPhone());
                        sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                        sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, login.getData().getProfilePic());
                        sessionManager.setString(SessionManager.SERVICE, service);

                        sessionManager.setString(SessionManager.CITY, login.getData().getAddress().getCity());
                        sessionManager.setString(SessionManager.STATE, login.getData().getAddress().getState());
                        sessionManager.setString(SessionManager.COUNRTY, login.getData().getAddress().getCountry());
                        sessionManager.setString(SessionManager.PINCODE, login.getData().getAddress().getPincode());
                        sessionManager.setString(SessionManager.PROVIDER_ID, login.getData().getId());

                        Intent logout= new Intent(getApplicationContext(), PetrolPumpHomePage.class);
                        logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logout);
                        finish();
                    }

                    else if(login.getStatus().equalsIgnoreCase("401"))
                    {
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }
                    else if(login.getStatus().equalsIgnoreCase("400"))
                    {
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                    else if(login.getStatus().equalsIgnoreCase("500"))
                    {
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }
                    else
                    {
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after sometime", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<LoginPP> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.e(TAG, "error::: " + t.getMessage());
                etLoginPassword.setText("");
                ActionForAll.alertUser("VahanWire", "Invalid credentials", "OK", ProviderLogin.this);
            }
        });
    }

    private void loginAmbulance() {

        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Log.e(TAG, "loginAmbulance: "+ etLoginMobile.getText().toString() );
        Log.e(TAG, "loginAmbulance: "+ sessionManager.getString(SessionManager.DEVICE_ID) );
        Log.e(TAG, "loginAmbulance: "+ sessionManager.getString(SessionManager.NOTIFICATION_TOKEN) );

        Call<LoginAmbulance> call = apiService.login_ambulance(etLoginMobile.getText().toString(),
                etLoginPassword.getText().toString(),
                sessionManager.getString(SessionManager.DEVICE_ID),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));


        call.enqueue(new Callback<LoginAmbulance>() {
            @Override
            public void onResponse(Call<LoginAmbulance> call, Response<LoginAmbulance> response) {
                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "onResponse: "+ response.body().getStatus());
                    Util.hideProgressDialog(progressDialog);
                    LoginAmbulance login = response.body();
                    if (login.getStatus().equals("200"))
                    {
                        sessionManager.setString(SessionManager.SERVICE, service);
                        Data data = login.getData();
                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, data.getContact());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, data.getMobilePin());
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, data.getOrganisation().getProfilePic());
                        sessionManager.setString(SessionManager.REGISTER_NAME, data.getOrganisation().getOrganisationName());
                        sessionManager.setString(SessionManager.EMAIL, data.getOrganisation().getEmail());
                        sessionManager.setString(SessionManager.PROVIDER_ID, data.getId());
                        sessionManager.setString(SessionManager.PROVIDER_VEHICLE, data.getVehicleNumber());
                        Log.e(TAG, "onResponse: image " + sessionManager.getString(SessionManager.PROVIDER_IMAGE));


                        Intent intent= new Intent(getApplicationContext(), AmbulanceHomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //intent.putExtra("count", login.getData().getRequestedUsers().size());
                        //Log.e(TAG, "onResponse: "+ login.getData().getRequestedUsers().size());
                        startActivity(intent);
                        finish();


                    }

                    else {
                        Util.hideProgressDialog(progressDialog);
                        ActionForAll.alertUser("VahanWire", login.getMessage(), "OK", ProviderLogin.this);
                    }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", ProviderLogin.this);
                }
            }

            @Override
            public void onFailure(Call<LoginAmbulance> call, Throwable t) {
                etLoginPassword.setText("");
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUser("VahanWire", "Invalid credentials", "OK", ProviderLogin.this);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            ActionForAll.alertChoiseCloseActivity(this);
        }
        return true;
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProviderLogin.this);
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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
