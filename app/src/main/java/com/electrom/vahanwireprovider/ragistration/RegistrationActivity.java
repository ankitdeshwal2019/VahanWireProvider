package com.electrom.vahanwireprovider.ragistration;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.PetrolPumpHomePage;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.features.MachanicHomePage;
import com.electrom.vahanwireprovider.location_service.GPSTracker;
import com.electrom.vahanwireprovider.models.new_petrol_pump_detail.NewPetrolDetail;
import com.electrom.vahanwireprovider.models.pro_update_mech.ProfileUpdateMech;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegistrationActivity.class.getSimpleName() ;
    CustomButton btnRegisterFinal;
    SessionManager sessionManager;
    CustomEditText et_resister_com_name,etContactPerson,etRegisterLandLine,etRegisterEmail,
            etRegisterPassword,etRegisterAddress;
    GPSTracker gps;
    Double Latitude, Longitude;
    String service;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();
    }

    private void getLocation() {
        gps = new GPSTracker(getApplicationContext(), RegistrationActivity.this);
        //Check if GPS enabled
        if (gps.canGetLocation()) {
            Latitude = gps.getLatitude();
            Longitude = gps.getLongitude();
            Log.d("onClick: ", Latitude.toString());
            Log.d("onClick: ", Longitude.toString());
            if (Latitude > 0.1 && Longitude > 0.1) {
                Log.d(TAG, "location found " + Longitude + "/" + Latitude);
                sessionManager.setString(SessionManager.LONGITUDE, Longitude + "");
                sessionManager.setString(SessionManager.LATITUDE, Latitude + "");
            }

        }
        else {
            //gps.showSettingsAlert(this);
        }
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        btnRegisterFinal = findViewById(R.id.btnRegisterFinal);
        et_resister_com_name = findViewById(R.id.et_resister_com_name);
        etContactPerson = findViewById(R.id.etContactPerson);
        etRegisterLandLine = findViewById(R.id.etRegisterLandLine);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        back = findViewById(R.id.back);
        etRegisterAddress = findViewById(R.id.etRegisterAddress);
        service = sessionManager.getString(SessionManager.SERVICE);

        Log.e(TAG, "service: registration Act " + service );
        //getLocation();
        btnRegisterFinal.setOnClickListener(this);
        back.setOnClickListener(this);
        etRegisterAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(sessionManager.getString(SessionManager.LONGITUDE).length() == 0 &&
                        sessionManager.getString(SessionManager.LATITUDE).length() == 0)
                {
                    try {
                        if(s.length() > 2)
                            getLocationFromAddress(s.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{}

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnRegisterFinal:

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
                                    getLocation();

                                    if(ActionForAll.isNetworkAvailable(RegistrationActivity.this))
                                    isNotEmptyFields();
                                }

                                // check for permanent denial of any permission
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

                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void compaleteRegistration() {
        Log.e(TAG, "service: regstration for petrol ");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<NewPetrolDetail> call = apiService.registrationUpdateNew(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etRegisterPassword.getText().toString().trim(), et_resister_com_name.getText().toString(),
                etContactPerson.getText().toString(),etRegisterLandLine.getText().toString(),
                 etRegisterEmail.getText().toString(),
                "", "", "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN),"",
                etRegisterAddress.getText().toString(), "", "","","",
                sessionManager.getString(SessionManager.LATITUDE),sessionManager.getString(SessionManager.LONGITUDE),
                "", "", "","","","","",
                "", "","","");

        call.enqueue(new Callback<NewPetrolDetail>() {
            @Override
            public void onResponse(Call<NewPetrolDetail> call, Response<NewPetrolDetail> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    final NewPetrolDetail update = response.body();
                        Log.d(TAG, update.getStatus());
                        if(update.getStatus().equals("200"))
                        {
                            List<Double> list = update.getData().get(0).getAddress().getLocation().getCoordinates();
                            Double longitude = list.get(0).doubleValue();
                            Double latitude = list.get(1).doubleValue();
                            sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                            sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                            sessionManager.setString(SessionManager.PROVIDER_MOBILE, update.getData().get(0).getMobile());
                            sessionManager.setString(SessionManager.PROVIDER_PIN, update.getData().get(0).getMobilePin());
                            sessionManager.setString(SessionManager.REGISTER_NAME, update.getData().get(0).getRegisteredName());
                            sessionManager.setString(SessionManager.EMAIL, update.getData().get(0).getEmail());
                            sessionManager.setString(SessionManager.ADDRESS, update.getData().get(0).getAddress().getFirstAddress());
                            sessionManager.setString(SessionManager.CONTACT_PERSON,update.getData().get(0).getContactPerson());
                            sessionManager.setString(SessionManager.LANDLINE, update.getData().get(0).getPhone());
                            sessionManager.setString(SessionManager.PROVIDER_IMAGE, update.getData().get(0).getProfilePic());
                            sessionManager.setString(SessionManager.PROVIDER_ID, update.getData().get(0).getId());
                            sessionManager.setString(SessionManager.SERVICE, service);

                            sessionManager.setString(SessionManager.CITY, update.getData().get(0).getAddress().getCity());
                            sessionManager.setString(SessionManager.STATE, update.getData().get(0).getAddress().getState());
                            sessionManager.setString(SessionManager.COUNRTY, update.getData().get(0).getAddress().getCountry());
                            sessionManager.setString(SessionManager.PINCODE, update.getData().get(0).getAddress().getPincode());
                            //sessionManager.setString(SessionManager.PROVIDER_PIN, etRegisterPassword.getText().toString());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent logout= new Intent(RegistrationActivity.this, PetrolPumpHomePage.class);
                                    logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(logout);
                                }
                            }, 300);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", update.getMessage(), "OK", RegistrationActivity.this);
                        }

                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", RegistrationActivity.this);
                }

            }

            @Override
            public void onFailure(Call<NewPetrolDetail> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void compaleteRegistrationMechanic() {
        Log.e(TAG, "service: regstration for mechanic ");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ProfileUpdateMech> call = apiService.registrationUpdateMechanic(
                sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etContactPerson.getText().toString(),
                etRegisterPassword.getText().toString(),
                et_resister_com_name.getText().toString(),
                sessionManager.getString(SessionManager.LATITUDE),
                sessionManager.getString(SessionManager.LONGITUDE),
                etRegisterEmail.getText().toString(),
                "",
                "",
                etRegisterLandLine.getText().toString(),
                etRegisterAddress.getText().toString(),
                "", "", "", "");


        call.enqueue(new Callback<ProfileUpdateMech>() {
            @Override
            public void onResponse(Call<ProfileUpdateMech> call, Response<ProfileUpdateMech> response) {

                Util.hideProgressDialog(progressDialog);
                ProfileUpdateMech responseBody = response.body();

                if(response.isSuccessful())
                {
                    if(responseBody.getStatus().equals("200"))
                    {

                        sessionManager.setString(SessionManager.MAIN_PROVIDER, String.valueOf(responseBody.getData().getMainproviderStatus()));
                        List<Double> list = responseBody.getData().getLocation().getCoordinates();
                        Double longitude = list.get(0).doubleValue();
                        Double latitude = list.get(1).doubleValue();
                        sessionManager.setString(SessionManager.LATITUDE, latitude+"");
                        sessionManager.setString(SessionManager.LONGITUDE, longitude+"");
                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, responseBody.getData().getMobile());
                        sessionManager.setString(SessionManager.PROVIDER_PIN, responseBody.getData().getMobilePin());
                        sessionManager.setString(SessionManager.REGISTER_NAME, responseBody.getData().getName());
                        sessionManager.setString(SessionManager.EMAIL, responseBody.getData().getOrganisation().getEmail());
                        sessionManager.setString(SessionManager.ADDRESS, responseBody.getData().getOrganisation().getRegAddress().getFirstAddress());
                        sessionManager.setString(SessionManager.CONTACT_PERSON,responseBody.getData().getOrganisation().getContactPerson());
                        sessionManager.setString(SessionManager.LANDLINE, responseBody.getData().getOrganisation().getPhone());
                        sessionManager.setString(SessionManager.PROVIDER_IMAGE, responseBody.getData().getProfilePic());
                        sessionManager.setString(SessionManager.PROVIDER_ID, responseBody.getData().getId());
                        sessionManager.setString(SessionManager.ACTIVE_STATUS, responseBody.getData().getMechanicActiveStatus().toString());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                sessionManager.setString(SessionManager.PROVIDER_PIN, etRegisterPassword.getText().toString());
                                Intent logout= new Intent(RegistrationActivity.this, MachanicHomePage.class);
                                logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(logout);
                            }
                        }, 300);
                    }
                    else
                    {
                        ActionForAll.alertUser("VahanWire", responseBody.getMessage(), "OK", RegistrationActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileUpdateMech> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUserWithCloseActivity("VahanWire", t.getMessage(), "OK", RegistrationActivity.this);
            }
        });
    }

    private void isNotEmptyFields(){
        if(ActionForAll.validEditText(et_resister_com_name, "company name", RegistrationActivity.this) &&
                ActionForAll.validEditText(etContactPerson, "name", RegistrationActivity.this) &&
                //ActionForAll.isValidEmail(etRegisterEmail, RegistrationActivity.this) &&
                ActionForAll.validEditText(etRegisterPassword, "password", RegistrationActivity.this) &&
                ActionForAll.validEditText(etRegisterAddress, "address", RegistrationActivity.this))
        {
            if(sessionManager.getString(SessionManager.SERVICE).equalsIgnoreCase("Petrol_Pump"))
            {

                compaleteRegistration();
            }
            else if (sessionManager.getString(SessionManager.SERVICE).equalsIgnoreCase("MechanicPro"))
            {
                compaleteRegistrationMechanic();
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            ActionForAll.alertChoiseCloseActivity(this);
        }
        return true;
    }

    public void getLocationFromAddress(String strAddress) throws Exception
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
            Address location = address.get(0);
            Log.d(TAG, "d_lat" + location.getLatitude());
            Log.d(TAG, "d_lng" + location.getLongitude());
            sessionManager.setString(SessionManager.LONGITUDE, location.getLongitude() + "");
            sessionManager.setString(SessionManager.LATITUDE, location.getLatitude()+ "");

        } catch (IOException e)
        {
            e.printStackTrace();
        }
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


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
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
}
