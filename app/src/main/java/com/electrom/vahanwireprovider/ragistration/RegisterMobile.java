package com.electrom.vahanwireprovider.ragistration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.models.mechanic_registration.Mechanic;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterMobile extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterMobile.class.getSimpleName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    CustomButton btnSendOtp;
    CustomEditText etRegisterMobile;
    SessionManager sessionManager;
    String service;
    ImageView ivBackLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_mobile);
        initView();
    }

    private void initView() {
        sessionManager =  SessionManager.getInstance(this);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        etRegisterMobile = findViewById(R.id.etRegisterMobile);
        ivBackLogin = findViewById(R.id.ivBackLogin);
        btnSendOtp.setOnClickListener(this);
        ivBackLogin.setOnClickListener(this);
        service = sessionManager.getString(SessionManager.SERVICE);
        Log.e(TAG, "service: register mobile " + service );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnSendOtp:
                if(ActionForAll.isNetworkAvailable(this))
                {
                    if(ActionForAll.validMobileEditText(etRegisterMobile, "10 digit mobile number", RegisterMobile.this)){

                        if(service.equalsIgnoreCase("Ambulance"))
                        {
                            ActionForAll.alertUserWithCloseActivity("VahanWire", "Ambulance Registration not valid", "OK", RegisterMobile.this);
                        }
                        else {
                            requestGetOtp();
                        }
                    }
                }
                //startActivity(new Intent(getApplicationContext(), VerifyOtp.class));
                break;

            case R.id.ivBackLogin:
                finish();
                break;
        }

    }

    //petrol pump registration
    private void getRegisterMobile(){

        Log.e(TAG, "service: regst mobile for petrol ");

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.registerProvider(
                etRegisterMobile.getText().toString(),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response!=null && response.isSuccessful()){
                    Util.hideProgressDialog(progressDialog);
                    sessionManager.setString(SessionManager.PROVIDER_MOBILE, etRegisterMobile.getText().toString());
                    try {

                        String jsonResponse = response.body().string();
                        Log.d(TAG, jsonResponse);
                        JSONObject object = new JSONObject(jsonResponse);
                        Log.d(TAG, object.getString("status"));

                        if(object.getString("status").equals("200"))
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), VerifyOtp.class));
                                }
                            }, 300);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", RegisterMobile.this);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", RegisterMobile.this);
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());

            }
        });



    }

    //mechanic registration
    private void getRegisterMobileMechanic(){

        Log.e(TAG, "service: regst for MechanicPro ");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Mechanic> call = apiService.registration_mechanic(
                sessionManager.getString(SessionManager.DEVICE_ID),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN),
                etRegisterMobile.getText().toString());

        call.enqueue(new Callback<Mechanic>() {
            @Override
            public void onResponse(Call<Mechanic> call, Response<Mechanic> response) {

                Util.hideProgressDialog(progressDialog);

                if(response!=null && response.isSuccessful())
                {
                    Mechanic mechanic = response.body();
                    if(mechanic.getStatus().equals("200"))
                    {
                        sessionManager.setString(SessionManager.PROVIDER_MOBILE, etRegisterMobile.getText().toString());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), VerifyOtp.class).putExtra("mobile", etRegisterMobile.getText().toString()));
                            }
                        }, 300);
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", mechanic.getMessage(), "OK", RegisterMobile.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", RegisterMobile.this);
                }
            }

            @Override
            public void onFailure(Call<Mechanic> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime \n"+t.getMessage(), "OK", RegisterMobile.this);
            }
        });
    }

    private void requestGetOtp() {

        if(service.equalsIgnoreCase("Petrol_Pump"))
            getRegisterMobile();
        else if(service.equalsIgnoreCase("MechanicPro"))
            getRegisterMobileMechanic();

    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterMobile.this);
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
}
