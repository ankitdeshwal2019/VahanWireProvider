package com.electrom.vahanwireprovider.ragistration;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.electrom.vahanwireprovider.BuildConfig;
import com.electrom.vahanwireprovider.PetrolPumpHomePage;
import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.features.AmbulanceHomePage;
import com.electrom.vahanwireprovider.features.MachanicHomePage;
import com.electrom.vahanwireprovider.models.version.Version;
import com.electrom.vahanwireprovider.new_app_driver.DriverHomePage;
import com.electrom.vahanwireprovider.new_app_driver.DriverLogin;
import com.electrom.vahanwireprovider.new_app_tow.ChoiseLogin;
import com.electrom.vahanwireprovider.new_app_tow.TowActivityLogin;
import com.electrom.vahanwireprovider.new_app_tow.TowHomePage;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.Constant;
import com.electrom.vahanwireprovider.utility.CustomTextBold;
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.UrlConstants;
import com.electrom.vahanwireprovider.utility.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeforeLogin extends AppCompatActivity implements View.OnClickListener {

    CustomTextBold tvPetrolPump, tvMechanic, tvAbmulance, tvCheckAppMode,tvPreAppointment, tvTow, tvPickDrop;
    SessionManager sessionManager;
    String service = "";
    String version="";
    int versionCode = BuildConfig.VERSION_CODE;
    String versionName = BuildConfig.VERSION_NAME;
    Version body;

    public static final String TAG =BeforeLogin.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_login);
        initView();
    }

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        service = sessionManager.getString(SessionManager.SERVICE);
        Log.e(TAG, "service: before login " + service );
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "onCreate: android id :: " + android_id);
        if(android_id.length() > 0)
            sessionManager.setString(SessionManager.DEVICE_ID, android_id);

        tvPetrolPump = findViewById(R.id.tvPetrolPump);
        tvMechanic = findViewById(R.id.tvMechanic);
        tvAbmulance = findViewById(R.id.tvAmbulance);
        tvCheckAppMode = findViewById(R.id.tvCheckAppMode);
        tvPreAppointment = findViewById(R.id.tvPreAppointment);
        tvTow = findViewById(R.id.tvTow);
        tvPickDrop = findViewById(R.id.tvPickDrop);
        tvPetrolPump.setOnClickListener(this);
        tvAbmulance.setOnClickListener(this);
        tvMechanic.setOnClickListener(this);
        tvPreAppointment.setOnClickListener(this);
        tvTow.setOnClickListener(this);
        tvPickDrop.setOnClickListener(this);

        if(!UrlConstants.BASE_URL.contains("http://3.0.231.209/api/"))
            tvCheckAppMode.setText("STAGING : " + BuildConfig.VERSION_NAME);

        Log.e(TAG, "mobile: " + sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        Log.e(TAG, "pin: " +  sessionManager.getString(SessionManager.PROVIDER_PIN) );
        Log.e(TAG, "service: " +  sessionManager.getString(SessionManager.SERVICE) );
        Log.e(TAG, "initView: D_ID "+ sessionManager.getString(SessionManager.DEVICE_ID));
        Log.e(TAG, "initView: NOTIFICATION "+ sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));

        check_version();

        if(sessionManager.getString(SessionManager.PROVIDER_MOBILE).length() > 0 &&
                sessionManager.getString(SessionManager.PROVIDER_PIN).length() > 0)
        {
            if(service.contains("Petrol_Pump")){
                startActivity(new Intent(getApplicationContext(), PetrolPumpHomePage.class));
                finish();
            }
            else if(service.contains("Ambulance")){
                startActivity(new Intent(getApplicationContext(), AmbulanceHomePage.class));
                finish();
            }
            else if(service.contains("MechanicPro")){
                startActivity(new Intent(getApplicationContext(), MachanicHomePage.class));
                finish();
            }
            else if(service.contains(Constant.SERVICE_TOW)){
                startActivity(new Intent(getApplicationContext(), TowHomePage.class));
                finish();
            }
            else if(service.contains(Constant.SERVICE_DRIVER)){
                startActivity(new Intent(getApplicationContext(), DriverHomePage.class));
                finish();
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {

            case R.id.tvPetrolPump:

                service = Constant.SERVICE_PETROL_PUMP;
                sessionManager.setString(SessionManager.SERVICE, service);
                startActivity(new Intent(getApplicationContext(), ProviderLogin.class));
                break;

            case R.id.tvAmbulance:

                service = Constant.SERVICE_AMBULANCE;
                startActivity(new Intent(getApplicationContext(), ProviderLogin.class));
                sessionManager.setString(SessionManager.SERVICE, service);
                break;

            case R.id.tvMechanic:

                sessionManager.setString(SessionManager.SERVICE, Constant.SERVICE_MECHNIC_PRO);
                startActivity(new Intent(getApplicationContext(), ProviderLogin.class));
                Log.e(TAG, "onClick: " + sessionManager.getString(SessionManager.SERVICE));
                //finish();
                break;

            case R.id.tvTow:

                sessionManager.setString(SessionManager.SERVICE, Constant.SERVICE_TOW);
                startActivity(new Intent(getApplicationContext(), TowActivityLogin.class));
                Log.e(TAG, "onClick: " + sessionManager.getString(SessionManager.SERVICE));
                //finish();
                break;

            case R.id.tvPickDrop:

                sessionManager.setString(SessionManager.SERVICE, Constant.SERVICE_DRIVER);
                startActivity(new Intent(getApplicationContext(), DriverLogin.class));
                Log.e(TAG, "onClick: " + sessionManager.getString(SessionManager.SERVICE));
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseMessaging.getInstance().subscribeToTopic("versionprovider");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sessionManager.setString(SessionManager.NOTIFICATION_TOKEN, token);
                        Log.d(TAG, "token" + token);
                        //ActionForAll.myFlash(getApplicationContext(), token);
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


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BeforeLogin.this);
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



    private void check_version()
    {
        final ProgressDialog progressDialog = Util.showProgressDialog(this);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Map<String, String> params = new HashMap<String, String>();
        params.put("device", "android");
        params.put("app_type", "provider");

        Call<Version> call = apiService.check_version(params);
        call.enqueue(new Callback<Version>() {

            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {

                if(response!=null && response.isSuccessful())
                {
                    body = response.body();
                    if(body.getStatus().equals("200"))
                    {
                        Log.e(TAG, "onResponse: check_version " + body.getStatus());


                        if(body.getData().size() > 0)
                        {
                            int apiVersionCode = Integer.parseInt(body.getData().get(0).getLatestVersion());
                            if(apiVersionCode > versionCode)
                            {

                                if(!(BeforeLogin.this.isFinishing()))
                                {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            new AlertDialog.Builder(BeforeLogin.this)
                                                    .setTitle("Update version")
                                                    .setMessage("New update available please update the app")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Util.hideProgressDialog(progressDialog);
                                                            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                                                                    .setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                                                            startActivity(goToMarket);
                                                            dialog.dismiss();
                                                            finish();

                                                        }
                                                    }).create().show();
                                        }
                                    }, 300);
                                }
                            }
                            else
                            {
                                try{
                                    Util.hideProgressDialog(progressDialog);
                                }catch (Exception e){}
                            }
                        }
                    }
                }
                else
                {
                    Util.hideProgressDialog(progressDialog);
                    //ActionForAll.alertUserWithCloseActivity("VahanWire", body.getMessage(), "OK", BeforeLogin.this);
                }
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {

            }
        });
    }

}
