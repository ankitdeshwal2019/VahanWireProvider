package com.electrom.vahanwireprovider.ragistration;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.electrom.vahanwireprovider.R;
import com.electrom.vahanwireprovider.retrofit_lib.ApiClient;
import com.electrom.vahanwireprovider.retrofit_lib.ApiInterface;
import com.electrom.vahanwireprovider.utility.ActionForAll;
import com.electrom.vahanwireprovider.utility.CustomButton;
import com.electrom.vahanwireprovider.utility.CustomEditText;
import com.electrom.vahanwireprovider.utility.SessionManager;
import com.electrom.vahanwireprovider.utility.Util;
import com.google.gson.Gson;

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
                            String pin =  matcher.group(1);
                            etVerifyOtp.setText(pin);
                            Util.hideProgressDialog(pd);
                        }
                    },2000);

                }
                else{
                    //ActionForAll.myFlash(context, "Not found please resend OTP");
                }
            }
        }
    };

    private void initView() {
        sessionManager = SessionManager.getInstance(this);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        etVerifyOtp = findViewById(R.id.etVerifyOtp);
        btnVerifyOtp.setOnClickListener(this);
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
        switch (v.getId())
        {
            case R.id.btnVerifyOtp:
                if( ActionForAll.validEditText(etVerifyOtp, "4 digit otp", VerifyOtp.this))
                {
                  verifyOTP();
                }
        }
    }

    private void verifyOTP() {

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

                        if(object.getString("status").equals("200"))
                        {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
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
