package com.electrom.vahanwireprovider.ragistration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        btnSendOtp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btnSendOtp:
                if(ActionForAll.isNetworkAvailable(this))
                {
                    if(ActionForAll.validMobileEditText(etRegisterMobile, "10 digit mobile number", RegisterMobile.this)){
                        if(checkAndRequestPermissions()){}
                        getRegisterMobile();

                    }
                }
                //startActivity(new Intent(getApplicationContext(), VerifyOtp.class));
                break;
        }

    }

    private void getRegisterMobile(){

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

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
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
