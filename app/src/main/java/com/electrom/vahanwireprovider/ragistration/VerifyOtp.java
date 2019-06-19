package com.electrom.vahanwireprovider.ragistration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
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
import com.electrom.vahanwireprovider.utility.CustomTextView;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtp extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = VerifyOtp.class.getSimpleName();
    CustomButton btnVerifyOtp;
    CustomEditText etVerifyOtp;
    SessionManager sessionManager;
    String service;
    ImageView ivBackLogin;
    CustomTextView tvResendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        initView();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                Pattern pattern = Pattern.compile("(\\d{4})");
                final Matcher matcher = pattern.matcher(message);

                if (matcher.find()) {
                    final ProgressDialog pd = Util.showProgressDialog(VerifyOtp.this);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String pin = matcher.group(1);
                            etVerifyOtp.setText(pin);
                            Util.hideProgressDialog(pd);
                        }
                    }, 2000);

                } else {
                    //ActionForAll.myFlash(context, "Not found please resend OTP");
                }
            }
        }
    };

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        service = sessionManager.getString(SessionManager.SERVICE);
        Log.e(TAG, "service: verify otp " + service );
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        etVerifyOtp = findViewById(R.id.etVerifyOtp);
        ivBackLogin = findViewById(R.id.ivBackLogin);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        btnVerifyOtp.setOnClickListener(this);
        ivBackLogin.setOnClickListener(this);
        tvResendOtp.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnVerifyOtp:
                if (ActionForAll.validEditText(etVerifyOtp, "otp", VerifyOtp.this)) {
                    switch (service) {
                        case "Petrol_Pump":
                            verifyOTP();
                            break;

                        case "MechanicPro":
                            verifyOTPMecanic();
                            break;

                        default:
                            ActionForAll.myFlash(getApplicationContext(), "no service found");
                            break;
                    }

                }
                break;
            case R.id.ivBackLogin:
                finish();
                break;

                case R.id.tvResendOtp:

                    etVerifyOtp.setText("");
                    requestGetOtp();
                break;
        }
    }

    private void verifyOTP() {
        Log.e(TAG, "service: for provider");
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.verifyProvider(sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                etVerifyOtp.getText().toString().trim());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    try {

                        String jsonResponse = response.body().string();
                        Log.d(TAG, jsonResponse);
                        JSONObject object = new JSONObject(jsonResponse);
                        Log.d(TAG, object.getString("status"));

                        if (object.getString("status").equals("200")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                                }
                            }, 300);
                        } else {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", VerifyOtp.this);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", VerifyOtp.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }
    String mobile;
    private void verifyOTPMecanic() {
        Log.e(TAG, "service: for mechanic");

        if(getIntent()!=null)
        {
            mobile = getIntent().getStringExtra("mobile");
        }


        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.verifyProviderMecanic
                (mobile, etVerifyOtp.getText().toString().trim());

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null && response.isSuccessful()) {
                    Util.hideProgressDialog(progressDialog);
                    try {

                        String jsonResponse = response.body().string();
                        Log.d(TAG, jsonResponse);
                        JSONObject object = new JSONObject(jsonResponse);
                        Log.d(TAG, object.getString("status"));

                        if (object.getString("status").equals("200")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sessionManager.setString(SessionManager.PROVIDER_MOBILE, mobile);
                                    startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
                                }
                            }, 300);
                        } else {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", VerifyOtp.this);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Util.hideProgressDialog(progressDialog);
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", VerifyOtp.this);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //replaces the default 'Back' button action
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ActionForAll.alertChoiseCloseActivity(this);
        }
        return true;
    }

    private void requestGetOtp() {

        if(service.equalsIgnoreCase("Petrol_Pump"))
            getRegisterMobile();
        else if(service.equalsIgnoreCase("MechanicPro"))
            getRegisterMobileMechanic();
    }

    //petrol pump registration
    private void getRegisterMobile(){

        Log.e(TAG, "service: regst mobile for petrol ");

        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.registerProvider(
               sessionManager.getString(SessionManager.PROVIDER_MOBILE),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response!=null && response.isSuccessful()){
                    Util.hideProgressDialog(progressDialog);
                    sessionManager.setString(SessionManager.PROVIDER_MOBILE, sessionManager.getString(SessionManager.PROVIDER_MOBILE));
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
                                   ActionForAll.myFlash(getApplicationContext(), "Otp resend successfully");
                                }
                            }, 300);
                        }
                        else
                        {
                            ActionForAll.alertUser("VahanWire", object.getString("message"), "OK", VerifyOtp.this);
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy, Please try after some time", "OK", VerifyOtp.this);
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

        Log.e(TAG, "service: regst for MechanicPro " + sessionManager.getString(SessionManager.DEVICE_ID));
        //Log.e(TAG, "device id " + sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));
        Log.e(TAG, "token "+ sessionManager.getString(SessionManager.NOTIFICATION_TOKEN));
        Log.e(TAG, "mobile "+ sessionManager.getString(SessionManager.PROVIDER_MOBILE));
        final ProgressDialog progressDialog = Util.showProgressDialog(this);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Mechanic> call = apiService.registration_mechanic(
                sessionManager.getString(SessionManager.DEVICE_ID),
                "1",
                sessionManager.getString(SessionManager.NOTIFICATION_TOKEN),
                sessionManager.getString(SessionManager.PROVIDER_MOBILE));

        call.enqueue(new Callback<Mechanic>() {
            @Override
            public void onResponse(Call<Mechanic> call, Response<Mechanic> response) {

                Util.hideProgressDialog(progressDialog);

                if(response!=null && response.isSuccessful())
                {
                    Mechanic mechanic = response.body();
                    if(mechanic.getStatus().equals("200"))
                    {
                       ActionForAll.myFlash(getApplicationContext(), "Resend otp successfully");
                    }
                    else
                    {
                        ActionForAll.alertUserWithCloseActivity("VahanWire", mechanic.getMessage(), "OK", VerifyOtp.this);
                    }
                }
                else
                {
                    ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime", "OK", VerifyOtp.this);
                }
            }

            @Override
            public void onFailure(Call<Mechanic> call, Throwable t) {
                Util.hideProgressDialog(progressDialog);
                ActionForAll.alertUserWithCloseActivity("VahanWire", "Network busy please try after sometime \n"+t.getMessage(), "OK", VerifyOtp.this);
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VerifyOtp.this);
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
